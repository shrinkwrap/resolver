package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.assertj.core.api.JUnitSoftAssertions;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.WithTimeoutDaemonBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationDistributionStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationStage;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped.ConfigurationStageImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class ConfigurationStageTestCase {

    final String[] goals = new String[] { "clean", "test", "package", "install" };
    String[] includes = new String[] { "include1", "include2" };
    String[] excludes = new String[] { "exclude1", "exclude2" };
    final Properties properties = new Properties() {{
        put("propertyKey1", "propertyValue1");
        put("propertyKey2", "propertyValue2");
    }};
    final Map<String, String> shellEnvironments = new HashMap<String, String>() {{
        put("shellEnvName1", "shellEnvValue1");
        put("shellEnvName2", "shellEnvValue2");
    }};
    final InvocationRequest.ReactorFailureBehavior failureBehavior = InvocationRequest.ReactorFailureBehavior.FailNever;
    final InvocationRequest.CheckSumPolicy globalChecksumPolicy = InvocationRequest.CheckSumPolicy.Warn;
    final InputStream inputStream = new ByteArrayInputStream(new byte[] {});
    final File globalSettingFile = new File("globalSettingFile");
    final File javaHome = new File("javaHome");
    final File localRepositoryDirectory = new File("localRepositoryDirectory");
    final InvokerLogger invokerLogger = new DummyInvokerLogger();
    final String mavenOpts = "--maven --opts";
    final String[] profiles = new String[] { "profile1", "profile2" };
    final String[] projects = new String[] { "project1", "project2" };
    final String resumeFrom = "resumeFrom";
    final String threads = "8.0C";
    final String builderId = "builderId";
    final File toolChainsFile = new File("toolChainsFile");
    final File globalToolChainsFile = new File("globalToolChainsFile");
    final File userSettingFile = new File("userSettingFile");
    final File workingDirectory = new File("workingDirectory");

    private final TestWorkDirRule workDirRule = new TestWorkDirRule();
    private final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(workDirRule).around(softly);

    @Test
    public void runTest() {
        File jarSamplePom = workDirRule.prepareProject(pathToJarSamplePom);
        ConfigurationStageImpl configurationStageImpl = getConfigurationStageImpl(jarSamplePom);

        // invocation request validation
        InvocationRequest invocationRequest = configurationStageImpl.getInvocationRequest();

        properties.put("skipTests", "true");
        softly.assertThat(invocationRequest.getProperties()).isEqualTo(properties);
        softly.assertThat(invocationRequest.getProfiles()).containsExactly(profiles);
        softly.assertThat(invocationRequest.getReactorFailureBehavior()).isEqualTo(failureBehavior);
        softly.assertThat(invocationRequest.getGlobalChecksumPolicy()).isEqualTo(globalChecksumPolicy);
        softly.assertThat(invocationRequest.getGlobalSettingsFile()).isEqualTo(globalSettingFile);
        softly.assertThat(invocationRequest.getGoals()).containsExactly(goals);
        softly.assertThat(invocationRequest.getInputStream(null)).isEqualTo(inputStream);
        softly.assertThat(invocationRequest.getJavaHome()).isEqualTo(javaHome);
        softly.assertThat(invocationRequest.getLocalRepositoryDirectory(null)).isEqualTo(localRepositoryDirectory);
        softly.assertThat(invocationRequest.getMavenOpts()).isEqualTo(mavenOpts);
        softly.assertThat(invocationRequest.getPomFile()).isEqualTo(jarSamplePom.getAbsoluteFile());
        softly.assertThat(invocationRequest.getProfiles()).containsExactly(profiles);
        softly.assertThat(invocationRequest.getProjects()).containsExactly(projects);
        softly.assertThat(invocationRequest.getResumeFrom()).isEqualTo(resumeFrom);
        softly.assertThat(invocationRequest.getShellEnvironments()).isEqualTo(shellEnvironments);
        softly.assertThat(invocationRequest.getThreads()).isEqualTo(threads);
        softly.assertThat(invocationRequest.getToolchainsFile()).isEqualTo(toolChainsFile);
        softly.assertThat(invocationRequest.getGlobalToolchainsFile()).isEqualTo(globalToolChainsFile);
        softly.assertThat(invocationRequest.getUserSettingsFile()).isEqualTo(userSettingFile);
        softly.assertThat(invocationRequest.getBuilder()).isEqualTo(builderId);
        softly.assertThat(invocationRequest.isAlsoMake()).isTrue();
        softly.assertThat(invocationRequest.isAlsoMakeDependents()).isTrue();
        softly.assertThat(invocationRequest.isDebug()).isTrue();
        softly.assertThat(invocationRequest.isBatchMode()).isTrue();
        softly.assertThat(invocationRequest.isNonPluginUpdates()).isTrue();
        softly.assertThat(invocationRequest.isOffline()).isTrue();
        softly.assertThat(invocationRequest.isRecursive()).isTrue();
        softly.assertThat(invocationRequest.isShellEnvironmentInherited()).isTrue();
        softly.assertThat(invocationRequest.isShowErrors()).isTrue();
        softly.assertThat(invocationRequest.isShowVersion()).isTrue();
        softly.assertThat(invocationRequest.isUpdateSnapshots()).isTrue();

        // invoker validation
        Invoker invoker = configurationStageImpl.getInvoker();

        softly.assertThat(invoker.getLogger()).isEqualTo(invokerLogger);
        softly.assertThat(invoker.getLocalRepositoryDirectory()).isEqualTo(localRepositoryDirectory);
        softly.assertThat(invoker.getWorkingDirectory()).isEqualTo(workingDirectory);
        softly.assertThat(invoker.getLogger().getThreshold()).isEqualTo(InvokerLogger.DEBUG);

        boolean hasFailed = false;
        try {
            configurationStageImpl.ignoreFailure().build();
        } catch (Exception e) {
            hasFailed = true;
        }
        if (!hasFailed) {
            Assert.fail("Maven build execution should fail as the local repository location is NOT a directory");
        }

        softly.assertThat(invoker.getMavenHome()).isNotNull();
        softly.assertThat(invoker.getMavenHome().getName()).isEqualTo("apache-maven-3.3.9");
    }

    private ConfigurationStageImpl getConfigurationStageImpl(File jarSamplePom) {
        ConfigurationStage<ConfigurationDistributionStage, WithTimeoutDaemonBuilder> configurationStage =
            EmbeddedMaven.forProject(jarSamplePom)
                .useMaven3Version("3.3.9")
                .setGoals(goals)
                .addProperty("propertyKey1", properties.getProperty("propertyKey1"))
                .addProperty("propertyKey2", properties.getProperty("propertyKey2"))
                .addShellEnvironment("shellEnvName1", shellEnvironments.get("shellEnvName1"))
                .addShellEnvironment("shellEnvName2", shellEnvironments.get("shellEnvName2"))
                .setAlsoMake(true)
                .setAlsoMakeDependents(true)
                .setDebug(true)
                .setReactorFailureBehavior(failureBehavior)
                .setGlobalChecksumPolicy(globalChecksumPolicy)
                .setInputStream(inputStream)
                .setGlobalSettingsFile(globalSettingFile)
                .setBatchMode(true)
                .setJavaHome(javaHome)
                .setLocalRepositoryDirectory(localRepositoryDirectory)
                .setLogger(invokerLogger)
                .setMavenOpts(mavenOpts)
                .setNonPluginUpdates(true)
                .setOffline(true)
                .setProfiles(profiles)
                .setProjects(projects)
                .setRecursive(true)
                .setResumeFrom(resumeFrom)
                .setShellEnvironmentInherited(true)
                .setShowErrors(true)
                .setShowVersion(true)
                .setThreads(threads)
                .setToolchainsFile(toolChainsFile)
                .setGlobalToolchainsFile(globalToolChainsFile)
                .setUpdateSnapshots(true)
                .setRecursive(true)
                .setUserSettingsFile(userSettingFile)
                .setWorkingDirectory(workingDirectory)
                .setBuilder(builderId)
                .setDebugLoggerLevel();

        return (ConfigurationStageImpl) configurationStage;
    }

    class DummyInvokerLogger implements InvokerLogger {
        private int threshold = 0;

        @Override public void debug(String s) {
        }

        @Override public void debug(String s, Throwable throwable) {
        }

        @Override public boolean isDebugEnabled() {
            return false;
        }

        @Override public void info(String s) {
        }

        @Override public void info(String s, Throwable throwable) {
        }

        @Override public boolean isInfoEnabled() {
            return false;
        }

        @Override public void warn(String s) {
        }

        @Override public void warn(String s, Throwable throwable) {
        }

        @Override public boolean isWarnEnabled() {
            return false;
        }

        @Override public void error(String s) {
        }

        @Override public void error(String s, Throwable throwable) {
        }

        @Override public boolean isErrorEnabled() {
            return false;
        }

        @Override public void fatalError(String s) {
        }

        @Override public void fatalError(String s, Throwable throwable) {
        }

        @Override public boolean isFatalErrorEnabled() {
            return false;
        }

        @Override public void setThreshold(int i) {
            threshold = i;
        }

        @Override public int getThreshold() {
            return threshold;
        }
    }
}
