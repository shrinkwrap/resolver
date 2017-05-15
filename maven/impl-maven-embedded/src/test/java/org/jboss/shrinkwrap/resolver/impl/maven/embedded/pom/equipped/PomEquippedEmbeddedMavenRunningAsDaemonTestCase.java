package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.assertj.core.api.Assertions;
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
        DaemonBuild daemonBuild = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("package")
            .useAsDaemon()
            .withWaitUntilOutputLineMathes(".*BUILD SUCCESS.*")
            .build();

        int timeout = 0;
        while (daemonBuild.isAlive() && timeout <= 20) {
            Thread.sleep(500);
        }
        Assertions.assertThat(timeout)
            .as("The build should have finish (not to be alive) within 10 seconds")
            .isLessThanOrEqualTo(10);

        Assertions.assertThat(daemonBuild.getBuiltProject()).isNotNull();
        verifyJarSampleSimpleBuild(daemonBuild.getBuiltProject());
        verifyJarSampleContainsOnlyOneJar(daemonBuild.getBuiltProject());
    }

    @Test
    public void testDaemonWithoutWaitShouldNotReachTheEndOfTheBuild() throws InterruptedException {
        System.setOut(new PrintStream(outContent));

        DaemonBuild daemonBuild = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("clean", "package")
            .useAsDaemon()
            .build();

        Thread.sleep(900);
        Assertions.assertThat(outContent.toString()).contains("Embedded Maven build started");
        Assertions.assertThat(outContent.toString()).doesNotContain("Embedded Maven build stopped");
        Assertions.assertThat(daemonBuild.isAlive()).isTrue();
        Assertions.assertThat(daemonBuild.getBuiltProject()).isNull();

        int timeout = 0;
        while (daemonBuild.isAlive() && timeout <= 20) {
            Thread.sleep(500);
        }
        Assertions.assertThat(timeout)
            .as("The build should have finish (built project should not be null) within 10 seconds")
            .isLessThanOrEqualTo(10);

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
