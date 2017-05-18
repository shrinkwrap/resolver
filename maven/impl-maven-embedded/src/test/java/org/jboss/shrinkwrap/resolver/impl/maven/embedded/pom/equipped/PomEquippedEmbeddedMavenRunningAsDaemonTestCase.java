package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuild;
import org.junit.After;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleContainsOnlyOneJar;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleSimpleBuild;

public class PomEquippedEmbeddedMavenRunningAsDaemonTestCase {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Test
    public void testDaemonShouldWaitForBuildSuccess() throws TimeoutException, InterruptedException {
        final DaemonBuild daemonBuild = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("package")
            .useAsDaemon()
            .withWaitUntilOutputLineMathes(".*BUILD SUCCESS.*")
            .build();

        Awaitility.await("Wait till thread is not be alive").atMost(20, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !daemonBuild.isAlive();
            }
        });

        Assertions.assertThat(daemonBuild.getBuiltProject()).isNotNull();
        verifyJarSampleSimpleBuild(daemonBuild.getBuiltProject());
        verifyJarSampleContainsOnlyOneJar(daemonBuild.getBuiltProject());
    }

    @Test
    public void testDaemonWithoutWaitShouldNotReachTheEndOfTheBuild() throws InterruptedException {
        System.setOut(new PrintStream(outContent));

        final DaemonBuild daemonBuild = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("clean", "package")
            .useAsDaemon()
            .build();

        Awaitility.await("Wait till maven build is started").atMost(5, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return outContent.toString().contains("Embedded Maven build started");
            }
        });
        Assertions.assertThat(outContent.toString()).doesNotContain("Embedded Maven build stopped");
        Assertions.assertThat(outContent.toString()).doesNotContain("Embedded Maven build stopped");
        Assertions.assertThat(daemonBuild.isAlive()).isTrue();
        Assertions.assertThat(daemonBuild.getBuiltProject()).isNull();

        Awaitility.await("Wait till thread is not be alive").atMost(20, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !daemonBuild.isAlive();
            }
        });

        Assertions.assertThat(daemonBuild.isAlive()).isFalse();
        verifyJarSampleSimpleBuild(daemonBuild.getBuiltProject());
        verifyJarSampleContainsOnlyOneJar(daemonBuild.getBuiltProject());
    }

    @Test(expected = TimeoutException.class)
    public void testDaemonShouldThrowTimeoutExceptionBecauseOfLowTimeout() throws TimeoutException {
        EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("clean", "package")
            .useAsDaemon()
            .withWaitUntilOutputLineMathes(".*BUILD SUCCESS.*", 1, TimeUnit.SECONDS)
            .build();
    }

    @Test(expected = TimeoutException.class)
    public void testDaemonShouldThrowTimeoutExceptionBecauseOfWrongRegex() throws TimeoutException {
        EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("package")
            .useAsDaemon()
            .withWaitUntilOutputLineMathes("blabla", 5, TimeUnit.SECONDS)
            .build();
    }

    @After
    public void cleanUpStreams() {
        System.setOut(System.out);
    }
}
