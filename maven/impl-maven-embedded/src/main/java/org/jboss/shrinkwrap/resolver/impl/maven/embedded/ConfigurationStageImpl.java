package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.maven.shared.invoker.InvokerLogger;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.ConfigurationStage;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public abstract class ConfigurationStageImpl extends BuildStageImpl<ConfigurationStage> implements ConfigurationStage {

    private boolean skipTests = true;

    @Override
    public ConfigurationStage setInteractive(boolean interactive) {
        getInvocationRequest().setInteractive(interactive);
        return this;
    }

    @Override
    public ConfigurationStage setOffline(boolean offline) {
        getInvocationRequest().setOffline(offline);
        return this;
    }

    @Override
    public ConfigurationStage setDebug(boolean debug) {
        getInvocationRequest().setDebug(debug);
        return this;
    }

    @Override
    public ConfigurationStage setShowErrors(boolean showErrors) {
        getInvocationRequest().setShowErrors(showErrors);
        return this;
    }

    @Override
    public ConfigurationStage setUpdateSnapshots(boolean updateSnapshots) {
        getInvocationRequest().setUpdateSnapshots(updateSnapshots);
        return this;
    }

    @Override
    public ConfigurationStage setFailureBehavior(String failureBehavior) {
        getInvocationRequest().setFailureBehavior(failureBehavior);
        return this;
    }

    @Override
    public ConfigurationStage activateReactor(String[] includes, String[] excludes) {
        getInvocationRequest().activateReactor(includes, excludes);
        return this;
    }

    @Override
    public ConfigurationStage setLocalRepositoryDirectory(File localRepositoryDirectory) {
        getInvocationRequest().setLocalRepositoryDirectory(localRepositoryDirectory);
        return this;
    }

    @Override
    public ConfigurationStage setLogger(InvokerLogger invokerLogger) {
        getInvoker().setLogger(invokerLogger);
        return this;
    }

    @Override
    public ConfigurationStage setWorkingDirectory(File workingDirectory) {
        getInvoker().setWorkingDirectory(workingDirectory);
        return this;
    }

    @Override
    public ConfigurationStage setJavaHome(File javaHome) {
        getInvocationRequest().setJavaHome(javaHome);
        return this;
    }

    @Override
    public ConfigurationStage setProperties(Properties properties) {
        getInvocationRequest().getProperties().putAll(properties);
        getInvocationRequest().getProperties().put("skipTests", String.valueOf(skipTests));
        return this;
    }

    @Override
    public ConfigurationStage addProperty(String key, String value){
        getInvocationRequest().getProperties().put(key, value);
        return this;
    }

    @Override
    public ConfigurationStage skipTests(boolean skipTests){
        this.skipTests = skipTests;
        getInvocationRequest().getProperties().put("skipTests", String.valueOf(skipTests));
        return this;
    }

    @Override
    public ConfigurationStage setGoals(List<String> goals) {
        getInvocationRequest().setGoals(goals);
        return this;
    }

    @Override
    public ConfigurationStage setGoals(String... goals) {
        getInvocationRequest().setGoals(Arrays.asList(goals));
        return this;
    }

    @Override
    public ConfigurationStage setProfiles(List<String> profiles) {
        getInvocationRequest().setProfiles(profiles);
        return this;
    }

    @Override
    public ConfigurationStage setProfiles(String... profiles) {
        getInvocationRequest().setProfiles(Arrays.asList(profiles));
        return this;
    }

    @Override
    public ConfigurationStage setShellEnvironmentInherited(boolean shellEnvironmentInherited) {
        getInvocationRequest().setShellEnvironmentInherited(shellEnvironmentInherited);
        return this;
    }

    @Override
    public ConfigurationStage setUserSettingsFile(File userSettingsFile) {
        getInvocationRequest().setUserSettingsFile(userSettingsFile);
        return this;
    }

    @Override
    public ConfigurationStage setGlobalSettingsFile(File globalSettingsFile) {
        getInvocationRequest().setGlobalSettingsFile(globalSettingsFile);
        return this;
    }

    @Override
    public ConfigurationStage setToolchainsFile(File toolchainsFile) {
        getInvocationRequest().setToolchainsFile(toolchainsFile);
        return this;
    }

    @Override
    public ConfigurationStage setGlobalChecksumPolicy(String globalChecksumPolicy) {
        getInvocationRequest().setGlobalChecksumPolicy(globalChecksumPolicy);
        return this;
    }

    @Override
    public ConfigurationStage setNonPluginUpdates(boolean nonPluginUpdates) {
        getInvocationRequest().setNonPluginUpdates(nonPluginUpdates);
        return this;
    }

    @Override
    public ConfigurationStage setRecursive(boolean recursive) {
        getInvocationRequest().setRecursive(recursive);
        return this;
    }

    @Override
    public ConfigurationStage addShellEnvironment(String name, String value) {
        getInvocationRequest().addShellEnvironment(name, value);
        return this;
    }

    @Override
    public ConfigurationStage setMavenOpts(String mavenOpts) {
        getInvocationRequest().setMavenOpts(mavenOpts);
        return this;
    }

    @Override
    public ConfigurationStage setShowVersion(boolean showVersion) {
        getInvocationRequest().setShowVersion(showVersion);
        return this;
    }

    @Override
    public ConfigurationStage setThreads(String threads) {
        getInvocationRequest().setThreads(threads);
        return this;
    }

    @Override
    public ConfigurationStage setProjects(List<String> projects) {
        getInvocationRequest().setProjects(projects);
        return this;
    }

    @Override
    public ConfigurationStage setAlsoMake(boolean alsoMake) {
        getInvocationRequest().setAlsoMake(alsoMake);
        return this;
    }

    @Override
    public ConfigurationStage setAlsoMakeDependents(boolean alsoMakeDependents) {
        getInvocationRequest().setAlsoMakeDependents(alsoMakeDependents);
        return this;
    }

    @Override
    public ConfigurationStage setResumeFrom(String resumeFrom) {
        getInvocationRequest().setResumeFrom(resumeFrom);
        return this;
    }
}
