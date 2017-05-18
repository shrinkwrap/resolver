package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped.ConfigurationStageImpl;
import org.junit.Assert;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class ConfigurationStageTestCase {

    String[] goals = new String[] { "clean", "test", "package", "install" };
    String[] includes = new String[] { "include1", "include2" };
    String[] excludes = new String[] { "exclude1", "exclude2" };
    Properties properties = new Properties() {{
        put("propertyKey1", "propertyValue1");
        put("propertyKey2", "propertyValue2");
    }};
    Map<String, String> shellEnvironments = new HashMap() {{
        put("shellEnvName1", "shellEnvValue1");
        put("shellEnvName2", "shellEnvValue2");
    }};
    String failureBehavior = "failureBehavior";
    String globalChecksumPolicy = "globalChecksumPolicy";
    InputStream inputStream = new ByteArrayInputStream(new byte[] {});
    File globalSettingFile = new File("globalSettingFile");
    File javaHome = new File("javaHome");
    File localRepositoryDirectory = new File("localRepositoryDirectory");
    InvokerLogger invokerLogger = new DummyInvokerLogger();
    String mavenOpts = "--maven --opts";
    String[] profiles = new String[] { "profile1", "profile2" };
    String[] projects = new String[] { "project1", "project2" };
    String resumeFrom = "resumeFrom";
    String threads = "8.0C";
    File toolChainsFile = new File("toolChainsFile");
    File userSettingFile = new File("userSettingFile");
    File workingDirectory = new File("workingDirectory");

    @Test
    public void runTest() {
        ConfigurationStageImpl configurationStageImpl = getConfigurationStageImpl();

        // invocation request validation
        InvocationRequest invocationRequest = configurationStageImpl.getInvocationRequest();

        properties.put("skipTests", "true");
        assertEquals(properties, invocationRequest.getProperties());
        assertEquals(Arrays.asList(profiles), invocationRequest.getProfiles());
        assertEquals(excludes, invocationRequest.getActivatedReactorExcludes());
        assertEquals(includes, invocationRequest.getActivatedReactorIncludes());
        assertEquals(failureBehavior, invocationRequest.getFailureBehavior());
        assertEquals(globalChecksumPolicy, invocationRequest.getGlobalChecksumPolicy());
        assertEquals(globalSettingFile, invocationRequest.getGlobalSettingsFile());
        assertEquals(Arrays.asList(goals), invocationRequest.getGoals());
        assertEquals(inputStream, invocationRequest.getInputStream(null));
        assertEquals(javaHome, invocationRequest.getJavaHome());
        assertEquals(localRepositoryDirectory, invocationRequest.getLocalRepositoryDirectory(null));
        assertEquals(mavenOpts, invocationRequest.getMavenOpts());
        File jarSamplePom = new File(pathToJarSamplePom);
        assertEquals(jarSamplePom.getAbsoluteFile(), invocationRequest.getPomFile());
        assertEquals(Arrays.asList(profiles), invocationRequest.getProfiles());
        assertEquals(Arrays.asList(projects), invocationRequest.getProjects());
        assertEquals(resumeFrom, invocationRequest.getResumeFrom());
        assertEquals(shellEnvironments, invocationRequest.getShellEnvironments());
        assertEquals(threads, invocationRequest.getThreads());
        assertEquals(toolChainsFile, invocationRequest.getToolchainsFile());
        assertEquals(userSettingFile, invocationRequest.getUserSettingsFile());
        assertEquals(true, invocationRequest.isActivatedReactor());
        assertEquals(true, invocationRequest.isAlsoMake());
        assertEquals(true, invocationRequest.isAlsoMakeDependents());
        assertEquals(true, invocationRequest.isDebug());
        assertEquals(true, invocationRequest.isInteractive());
        assertEquals(true, invocationRequest.isNonPluginUpdates());
        assertEquals(true, invocationRequest.isOffline());
        assertEquals(true, invocationRequest.isRecursive());
        assertEquals(true, invocationRequest.isShellEnvironmentInherited());
        assertEquals(true, invocationRequest.isShowErrors());
        assertEquals(true, invocationRequest.isShowVersion());
        assertEquals(true, invocationRequest.isUpdateSnapshots());

        // invoker validation
        Invoker invoker = configurationStageImpl.getInvoker();

        assertEquals(invokerLogger, invoker.getLogger());
        assertEquals(localRepositoryDirectory, invoker.getLocalRepositoryDirectory());
        assertEquals(workingDirectory, invoker.getWorkingDirectory());

        boolean hasFailed = false;
        try {
            configurationStageImpl.ignoreFailure().build();
        } catch (Exception e) {
            hasFailed = true;
        }
        if (!hasFailed) {
            Assert.fail("Maven build execution should fail as the local repository location is NOT a directory");
        }

        assertNotNull(invoker.getMavenHome());
        assertEquals("apache-maven-3.3.9", invoker.getMavenHome().getName());
    }

    private ConfigurationStageImpl getConfigurationStageImpl() {
        ConfigurationStage configurationStage =
            EmbeddedMaven.forProject(pathToJarSamplePom)
                .useMaven3Version("3.3.9")
                .setGoals(goals)
                .activateReactor(includes, excludes)
                .addProperty("propertyKey1", properties.getProperty("propertyKey1"))
                .addProperty("propertyKey2", properties.getProperty("propertyKey2"))
                .addShellEnvironment("shellEnvName1", shellEnvironments.get("shellEnvName1"))
                .addShellEnvironment("shellEnvName2", shellEnvironments.get("shellEnvName2"))
                .setAlsoMake(true)
                .setAlsoMakeDependents(true)
                .setDebug(true)
                .setFailureBehavior(failureBehavior)
                .setGlobalChecksumPolicy(globalChecksumPolicy)
                .setInputStream(inputStream)
                .setGlobalSettingsFile(globalSettingFile)
                .setInteractive(true)
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
                .setUpdateSnapshots(true)
                .setRecursive(true)
                .setUserSettingsFile(userSettingFile)
                .setWorkingDirectory(workingDirectory);

        return (ConfigurationStageImpl) configurationStage;
    }

    class DummyInvokerLogger implements InvokerLogger {
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
        }

        @Override public int getThreshold() {
            return 0;
        }
    }
}
