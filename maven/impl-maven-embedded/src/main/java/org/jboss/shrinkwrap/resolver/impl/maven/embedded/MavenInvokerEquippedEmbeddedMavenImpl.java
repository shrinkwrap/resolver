package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuildStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.MavenInvokerEquippedEmbeddedMaven;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class MavenInvokerEquippedEmbeddedMavenImpl extends BuildStageImpl<BuildStage> implements
    MavenInvokerEquippedEmbeddedMaven {

    private InvocationRequest request;
    private Invoker invoker;

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
    protected BuildStage returnNextStepType() {
        return this;
    }
}
