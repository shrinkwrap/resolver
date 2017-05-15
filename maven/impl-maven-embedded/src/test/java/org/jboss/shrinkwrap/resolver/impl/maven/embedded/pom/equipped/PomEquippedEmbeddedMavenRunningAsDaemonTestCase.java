package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.After;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;

public class PomEquippedEmbeddedMavenRunningAsDaemonTestCase {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Test
    public void testDaemonShouldWaitForBuildSuccess() throws TimeoutException {
        EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("package")
            .useAsDaemon()
            .withWaitUntilOutputLineMathes(".*BUILD SUCCESS.*")
            .build();
    }

    @Test
    public void testDaemonWithoutWaitShouldNotReachTheEndOfTheBuild() throws TimeoutException, InterruptedException {
        System.setOut(new PrintStream(outContent));

        EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("clean", "package")
            .useAsDaemon()
            .build();

        Thread.sleep(900);
        Assertions.assertThat(outContent.toString()).contains("Embedded Maven build started");
        Assertions.assertThat(outContent.toString()).doesNotContain("Embedded Maven build stopped");
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
