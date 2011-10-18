package org.jboss.shrinkwrap.resolver.impl.maven;

import org.apache.maven.model.Model;

public interface EffectivePomMavenDependencyResolverInternal {
    Model getModel();

    MavenDependencyResolverInternal getDelegate();
}
