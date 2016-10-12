package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.MavenInvokerEquippedEmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.MavenInvokerUnequippedEmbeddedMaven;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class MavenInvokerUnequippedEmbeddedMavenImpl implements MavenInvokerUnequippedEmbeddedMaven {

    @Override
    public MavenInvokerEquippedEmbeddedMaven setMavenInvoker(InvocationRequest request, Invoker invoker) {
        return new MavenInvokerEquippedEmbeddedMavenImpl(request, invoker);
    }
}
