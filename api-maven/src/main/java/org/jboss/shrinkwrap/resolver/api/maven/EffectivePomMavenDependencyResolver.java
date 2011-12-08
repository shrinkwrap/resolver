package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.resolver.api.Child;
import org.jboss.shrinkwrap.resolver.api.DependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.DependencyResolver;

public interface EffectivePomMavenDependencyResolver extends Child<MavenDependencyResolver>,
        DependencyResolver<MavenResolutionFilter, MavenDependency>, DependencyBuilder<MavenDependencyBuilder>,
        ConfiguredMavenDependencyResolver {

    /**
     * Adds all dependencies defined by a pom file in scope test.
     *
     * @return The modified archive
     */
    EffectivePomMavenDependencyResolver importTestDependencies();

    /**
     * Adds all dependencies defined by a pom file in scope test. User has additional possibility to filter the dependencies.
     *
     * @param filter the filter to be applied
     * @return The modified archive
     * @throws IllegalArgumentException If filter is not supplied
     */
    EffectivePomMavenDependencyResolver importTestDependencies(MavenResolutionFilter filter);

    /**
     * Adds any dependencies defined by a pom file. User have to use a filter to filter the dependencies.
     *
     * @param filter the filter to be applied
     * @return The modified archive
     * @throws IllegalArgumentException If filter is not supplied
     */
    EffectivePomMavenDependencyResolver importAnyDependencies(MavenResolutionFilter filter);

    EffectivePomMavenDependencyResolver importAllDependencies();

}
