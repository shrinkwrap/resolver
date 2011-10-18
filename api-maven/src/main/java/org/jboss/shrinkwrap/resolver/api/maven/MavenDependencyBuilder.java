package org.jboss.shrinkwrap.resolver.api.maven;

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.DependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.DependencyBuilderChild;
import org.jboss.shrinkwrap.resolver.api.DependencyResolver;

public interface MavenDependencyBuilder extends DependencyBuilderChild<MavenDependencyResolver>,
        DependencyBuilder<MavenDependencyBuilder>, DependencyResolver<MavenResolutionFilter, MavenDependency> {

    /**
     * Sets a scope of dependency/dependencies
     *
     * @param scope A scope, for example @{code compile}, @{code test} and others
     * @return Artifact builder with scope set
     */
    MavenDependencyBuilder scope(String scope);

    /**
     * Sets dependency/dependencies as optional. If dependency is marked as optional, it is always resolved, however, the
     * dependency graph can later be filtered based on {@code optional} flag
     *
     * @param optional Optional flag
     * @return Artifact builder with optional flag set
     */
    MavenDependencyBuilder optional(boolean optional);

    /**
     * Adds an exclusion for current dependency.
     *
     * @param exclusion the exclusion to be added to list of artifacts to be excluded, specified in the format
     *        {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will match all
     *        exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
     * @return Artifact builder with added exclusion
     */
    MavenDependencyBuilder exclusion(String exclusion);

    /**
     * Adds multiple exclusions for current dependency/dependencies
     *
     * @param exclusions the exclusions to be added to the list of artifacts to be excluded, specified in the format
     *        {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will match all
     *        exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
     * @return Artifact builder with added exclusions
     */
    MavenDependencyBuilder exclusions(String... exclusions);

    /**
     * Adds multiple exclusions for current dependency/dependencies
     *
     * @param exclusions the exclusions to be added to the list of artifacts to be excluded, specified in the format
     *        {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will match all
     *        exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
     * @return Artifact builder with added exclusions
     */
    MavenDependencyBuilder exclusions(Collection<String> exclusions);

}
