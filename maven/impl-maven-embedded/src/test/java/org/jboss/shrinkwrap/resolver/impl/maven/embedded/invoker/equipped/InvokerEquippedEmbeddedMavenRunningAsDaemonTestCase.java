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
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.getPropertiesWithSkipTests;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;

public class InvokerEquippedEmbeddedMavenRunningAsDaemonTestCase {

    @Rule
    public final TestWorkDirRule workDirRule = new TestWorkDirRule();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testWhenDaemonIsUsedEndOfTheBuildIsNotReached() {

        final InvocationRequest request = new DefaultInvocationRequest();
        Invoker invoker = new DefaultInvoker();

        request.setPomFile(workDirRule.prepareProject(pathToJarSamplePom));
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

    @After
    public void cleanUpStreams() {
        System.setOut(System.out);
    }
}
