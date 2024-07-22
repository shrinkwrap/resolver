package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.awaitility.Awaitility;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuild;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.SystemOutExtension;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.RegisterExtension;


import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleContainsOnlyOneJar;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleSimpleBuild;

class PomEquippedEmbeddedMavenRunningAsDaemonTestCase {

    @RegisterExtension
    final SystemOutExtension systemOutExtension = new SystemOutExtension();

    @RegisterExtension
    final TestWorkDirExtension workDirExtension = new TestWorkDirExtension();


    @Test
    void testDaemonShouldWaitForBuildSuccess() throws TimeoutException {
        final DaemonBuild daemonBuild = EmbeddedMaven
            .forProject(workDirExtension.prepareProject(pathToJarSamplePom))
            .setGoals("package")
            .useAsDaemon()
            .withWaitUntilOutputLineMatches(".*BUILD SUCCESS.*")
            .build();

        Awaitility.await("Wait till thread is not be alive").atMost(20, TimeUnit.SECONDS)
                .until(() -> !daemonBuild.isAlive());

        Assertions.assertNotNull(daemonBuild.getBuiltProject());
        verifyJarSampleSimpleBuild(daemonBuild.getBuiltProject());
        verifyJarSampleContainsOnlyOneJar(daemonBuild.getBuiltProject());
    }

    @Test
    void testDaemonWithoutWaitShouldNotReachTheEndOfTheBuild() {

        final DaemonBuild daemonBuild = EmbeddedMaven
            .forProject(workDirExtension.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "package")
            .useAsDaemon()
            .build();

        Awaitility.await("Wait till maven build is started").atMost(5, TimeUnit.SECONDS)
                .until(() -> systemOutExtension.getLog().contains("Embedded Maven build started"));

        Assertions.assertFalse(systemOutExtension.getLog().contains("Embedded Maven build stopped"));
        Assertions.assertFalse(systemOutExtension.getLog().contains("Embedded Maven build stopped"));
        Assertions.assertTrue(daemonBuild.isAlive());
        Assertions.assertNull(daemonBuild.getBuiltProject());

        Awaitility.await("Wait till thread is not be alive").atMost(20, TimeUnit.SECONDS)
                .until(() -> !daemonBuild.isAlive());

        Assertions.assertFalse(daemonBuild.isAlive());
        verifyJarSampleSimpleBuild(daemonBuild.getBuiltProject());
        verifyJarSampleContainsOnlyOneJar(daemonBuild.getBuiltProject());
    }

    @Test
    void testDaemonShouldThrowTimeoutExceptionBecauseOfLowTimeout() {
        Assertions.assertThrows(TimeoutException.class, () -> {
            EmbeddedMaven
                    .forProject(workDirExtension.prepareProject(pathToJarSamplePom))
                    .setGoals("clean", "package")
                    .useAsDaemon()
                    .withWaitUntilOutputLineMatches(".*BUILD SUCCESS.*", 1, TimeUnit.SECONDS)
                    .build();
        });
    }

    @Test
    void testDaemonShouldThrowTimeoutExceptionBecauseOfWrongRegex() {
        Assertions.assertThrows(TimeoutException.class, () -> {
            EmbeddedMaven
                    .forProject(workDirExtension.prepareProject(pathToJarSamplePom))
                    .setGoals("package")
                    .useAsDaemon()
                    .withWaitUntilOutputLineMatches("blabla", 5, TimeUnit.SECONDS)
                    .build();
        });
    }

}
