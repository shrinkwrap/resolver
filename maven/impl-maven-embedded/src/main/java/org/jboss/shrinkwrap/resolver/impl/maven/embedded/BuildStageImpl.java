package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuildStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.StandardBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuildTrigger;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.daemon.DaemonBuildTriggerImpl;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public abstract class BuildStageImpl<NEXT_STEP extends BuildStage<DAEMON_TRIGGER_TYPE>, DAEMON_TRIGGER_TYPE extends DaemonBuildTrigger>
    extends DistributionStageImpl<NEXT_STEP, DAEMON_TRIGGER_TYPE> implements BuildStage<DAEMON_TRIGGER_TYPE> {

    private boolean ignoreFailure = false;

    @Override
    public BuiltProject build() {
        return createBuildTrigger().build(null, null);
    }

    @Override
    public StandardBuilder ignoreFailure(boolean ignoreFailure) {
        this.ignoreFailure = ignoreFailure;
        return this;
    }

    @Override
    public StandardBuilder ignoreFailure() {
        ignoreFailure = true;
        return this;
    }

    @Override
    public DAEMON_TRIGGER_TYPE useAsDaemon() {
        return (DAEMON_TRIGGER_TYPE) new DaemonBuildTriggerImpl(createBuildTrigger());
    }

    private BuildTrigger createBuildTrigger(){
        return new BuildTrigger(
            getSetMavenInstallation(),
            getInvocationRequest(),
            getInvoker(),
            getLogBuffer(),
            isQuiet(),
            ignoreFailure);
    }

    protected abstract InvocationRequest getInvocationRequest();

    protected abstract Invoker getInvoker();

    protected abstract StringBuffer getLogBuffer();

    protected abstract boolean isQuiet();
}
