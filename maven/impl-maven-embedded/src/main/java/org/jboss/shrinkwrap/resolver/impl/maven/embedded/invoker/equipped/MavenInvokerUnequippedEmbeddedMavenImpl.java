package org.jboss.shrinkwrap.resolver.impl.maven.embedded.invoker.equipped;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.invoker.equipped.MavenInvokerEquippedEmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.invoker.equipped.MavenInvokerUnequippedEmbeddedMaven;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class MavenInvokerUnequippedEmbeddedMavenImpl implements MavenInvokerUnequippedEmbeddedMaven {

    @Override
    public MavenInvokerEquippedEmbeddedMaven setMavenInvoker(InvocationRequest request, Invoker invoker) {
        return new MavenInvokerEquippedEmbeddedMavenImpl(request, invoker);
    }
}
