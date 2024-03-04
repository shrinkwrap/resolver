package org.jboss.shrinkwrap.resolver.impl.maven.embedded.invoker.equipped;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuildStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.WithoutTimeoutDaemonBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.invoker.equipped.MavenInvokerEquippedEmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.BuildStageImpl;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class MavenInvokerEquippedEmbeddedMavenImpl
    extends BuildStageImpl<BuildStage<WithoutTimeoutDaemonBuilder>, WithoutTimeoutDaemonBuilder>
    implements MavenInvokerEquippedEmbeddedMaven {

    private final InvocationRequest request;
    private final Invoker invoker;

    public MavenInvokerEquippedEmbeddedMavenImpl(InvocationRequest request, Invoker invoker){
        this.request = request;
        this.invoker = invoker;
    }

    @Override
    protected InvocationRequest getInvocationRequest() {
        return request;
    }

    @Override
    protected Invoker getInvoker() {
        return invoker;
    }

    @Override
    protected StringBuffer getLogBuffer() {
        return null;
    }

    @Override
    protected boolean isQuiet() {
        return false;
    }

    @Override
    protected BuildStage returnNextStepType() {
        return this;
    }
}
