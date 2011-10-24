package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.resolver.api.Child;
import org.jboss.shrinkwrap.resolver.api.DependencyResolver;

public interface EffectivePomMavenDependencyResolver extends Child<MavenDependencyResolver>,
        DependencyResolver<MavenResolutionFilter, MavenDependency> {

    /**
     * Adds all dependencies defined by a pom file in scope test.
     *
     * @return The modified archive
     */
    EffectivePomMavenDependencyResolver importTestDependencies();

    /**
     * Adds any dependencies defined by a pom file. User have to use a filter to filter the dependencies.
     *
     * @param filter the filter to be applied
     * @return The modified archive
     */
    EffectivePomMavenDependencyResolver importAnyDependencies(MavenResolutionFilter filter);

    EffectivePomMavenDependencyResolver importAllDependencies();

}
