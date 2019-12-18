package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuild;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.RuleChain;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleContainsOnlyOneJar;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleSimpleBuild;

public class PomEquippedEmbeddedMavenRunningAsDaemonTestCase {

    private final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    private final TestWorkDirRule workDirRule = new TestWorkDirRule();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(systemOutRule).around(workDirRule);

    @Test
    public void testDaemonShouldWaitForBuildSuccess() throws TimeoutException, InterruptedException {
        final DaemonBuild daemonBuild = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
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

        final DaemonBuild daemonBuild = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "package")
            .useAsDaemon()
            .build();

        Awaitility.await("Wait till maven build is started").atMost(5, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return systemOutRule.getLog().contains("Embedded Maven build started");
            }
        });
        Assertions.assertThat(systemOutRule.getLog()).doesNotContain("Embedded Maven build stopped");
        Assertions.assertThat(systemOutRule.getLog()).doesNotContain("Embedded Maven build stopped");
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
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "package")
            .useAsDaemon()
            .withWaitUntilOutputLineMathes(".*BUILD SUCCESS.*", 1, TimeUnit.SECONDS)
            .build();
    }

    @Test(expected = TimeoutException.class)
    public void testDaemonShouldThrowTimeoutExceptionBecauseOfWrongRegex() throws TimeoutException {
        EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
            .setGoals("package")
            .useAsDaemon()
            .withWaitUntilOutputLineMathes("blabla", 5, TimeUnit.SECONDS)
            .build();
    }
}
