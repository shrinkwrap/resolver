package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.WithTimeoutDaemonBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationDistributionStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationStage;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.BuildStageImpl;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public abstract class ConfigurationStageImpl extends
    BuildStageImpl<ConfigurationStage<ConfigurationDistributionStage, WithTimeoutDaemonBuilder>, WithTimeoutDaemonBuilder>
    implements ConfigurationDistributionStage {

    private boolean skipTests = true;

    @Override
    public ConfigurationDistributionStage setInteractive(boolean interactive) {
        getInvocationRequest().setInteractive(interactive);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setOffline(boolean offline) {
        getInvocationRequest().setOffline(offline);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setDebug(boolean debug) {
        getInvocationRequest().setDebug(debug);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setShowErrors(boolean showErrors) {
        getInvocationRequest().setShowErrors(showErrors);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setUpdateSnapshots(boolean updateSnapshots) {
        getInvocationRequest().setUpdateSnapshots(updateSnapshots);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setFailureBehavior(String failureBehavior) {
        getInvocationRequest().setFailureBehavior(failureBehavior);
        return this;
    }

    @Override
    public ConfigurationDistributionStage activateReactor(String[] includes, String[] excludes) {
        getInvocationRequest().activateReactor(includes, excludes);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setLocalRepositoryDirectory(File localRepositoryDirectory) {
        getInvocationRequest().setLocalRepositoryDirectory(localRepositoryDirectory);
        getInvoker().setLocalRepositoryDirectory(localRepositoryDirectory);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setLogger(InvokerLogger invokerLogger) {
        getInvoker().setLogger(invokerLogger);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setWorkingDirectory(File workingDirectory) {
        getInvoker().setWorkingDirectory(workingDirectory);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setInputStream(InputStream inputStream){
        getInvoker().setInputStream(inputStream);
        getInvocationRequest().setInputStream(inputStream);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setJavaHome(File javaHome) {
        getInvocationRequest().setJavaHome(javaHome);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setProperties(Properties properties) {
        getInvocationRequest().getProperties().putAll(properties);
        getInvocationRequest().getProperties().put("skipTests", String.valueOf(skipTests));
        return this;
    }

    @Override
    public ConfigurationDistributionStage addProperty(String key, String value){
        getInvocationRequest().getProperties().put(key, value);
        return this;
    }

    @Override
    public ConfigurationDistributionStage skipTests(boolean skipTests){
        this.skipTests = skipTests;
        getInvocationRequest().getProperties().put("skipTests", String.valueOf(skipTests));
        return this;
    }

    @Override
    public ConfigurationDistributionStage setGoals(List<String> goals) {
        getInvocationRequest().setGoals(goals);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setGoals(String... goals) {
        getInvocationRequest().setGoals(Arrays.asList(goals));
        return this;
    }

    @Override
    public ConfigurationDistributionStage setProfiles(List<String> profiles) {
        getInvocationRequest().setProfiles(profiles);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setProfiles(String... profiles) {
        getInvocationRequest().setProfiles(Arrays.asList(profiles));
        return this;
    }

    @Override
    public ConfigurationDistributionStage setShellEnvironmentInherited(boolean shellEnvironmentInherited) {
        getInvocationRequest().setShellEnvironmentInherited(shellEnvironmentInherited);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setUserSettingsFile(File userSettingsFile) {
        getInvocationRequest().setUserSettingsFile(userSettingsFile);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setGlobalSettingsFile(File globalSettingsFile) {
        getInvocationRequest().setGlobalSettingsFile(globalSettingsFile);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setToolchainsFile(File toolchainsFile) {
        getInvocationRequest().setToolchainsFile(toolchainsFile);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setGlobalChecksumPolicy(String globalChecksumPolicy) {
        getInvocationRequest().setGlobalChecksumPolicy(globalChecksumPolicy);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setNonPluginUpdates(boolean nonPluginUpdates) {
        getInvocationRequest().setNonPluginUpdates(nonPluginUpdates);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setRecursive(boolean recursive) {
        getInvocationRequest().setRecursive(recursive);
        return this;
    }

    @Override
    public ConfigurationDistributionStage addShellEnvironment(String name, String value) {
        getInvocationRequest().addShellEnvironment(name, value);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setMavenOpts(String mavenOpts) {
        getInvocationRequest().setMavenOpts(mavenOpts);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setShowVersion(boolean showVersion) {
        getInvocationRequest().setShowVersion(showVersion);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setThreads(String threads) {
        getInvocationRequest().setThreads(threads);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setProjects(List<String> projects) {
        getInvocationRequest().setProjects(projects);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setProjects(String... projects) {
        getInvocationRequest().setProjects(Arrays.asList(projects));
        return this;
    }

    @Override
    public ConfigurationDistributionStage setAlsoMake(boolean alsoMake) {
        getInvocationRequest().setAlsoMake(alsoMake);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setAlsoMakeDependents(boolean alsoMakeDependents) {
        getInvocationRequest().setAlsoMakeDependents(alsoMakeDependents);
        return this;
    }

    @Override
    public ConfigurationDistributionStage setResumeFrom(String resumeFrom) {
        getInvocationRequest().setResumeFrom(resumeFrom);
        return this;
    }
}
