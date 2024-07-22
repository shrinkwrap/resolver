package org.jboss.shrinkwrap.resolver.impl.maven.embedded.invoker.equipped;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuild;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.getPropertiesWithSkipTests;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;

class InvokerEquippedEmbeddedMavenRunningAsDaemonTestCase {

    @RegisterExtension
    final TestWorkDirExtension workDirExtension = new TestWorkDirExtension();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testWhenDaemonIsUsedEndOfTheBuildIsNotReached() {

        final InvocationRequest request = new DefaultInvocationRequest();
        Invoker invoker = new DefaultInvoker();

        request.setPomFile(workDirExtension.prepareProject(pathToJarSamplePom));
        request.setGoals(Arrays.asList("clean", "package"));

        request.setProperties(getPropertiesWithSkipTests());

        final DaemonBuild daemonBuild = EmbeddedMaven
            .withMavenInvokerSet(request, invoker)
            .useAsDaemon()
            .build();

        Awaitility.await("Wait till maven build is started").atMost(5, TimeUnit.SECONDS)
                .until(() -> outContent.toString().contains("Embedded Maven build started"));
        Assertions.assertThat(outContent.toString()).doesNotContain("Embedded Maven build stopped");

        Awaitility.await("Wait till project is not be null").atMost(20, TimeUnit.SECONDS)
                .until(() -> daemonBuild.getBuiltProject() != null);

        Assertions.assertThat(daemonBuild.isAlive()).isFalse();
    }

    @AfterEach
    void cleanUpStreams() {
        System.setOut(System.out);
    }
}
