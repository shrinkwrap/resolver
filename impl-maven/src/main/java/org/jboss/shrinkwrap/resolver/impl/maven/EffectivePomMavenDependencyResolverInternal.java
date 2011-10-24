package org.jboss.shrinkwrap.resolver.impl.maven;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;

public interface EffectivePomMavenDependencyResolverInternal extends EffectivePomMavenDependencyResolver {
    Model getModel();

    MavenDependencyResolverInternal getDelegate();
}
