/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.api.maven;

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.DependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.DependencyResolver;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

/**
 * An artifact builder is object which holds and construct dependencies and it is able to resolve them into an array of
 * ShrinkWrap archives.
 * 
 * Artifact builder allows chaining of artifacts, that is specifying a new artifact. In this case, currently constructed
 * artifact is stored as a dependency and user is allowed to specify parameters for another artifact.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="http://community.jboss.org/people/spinner)">Jose Rodolfo freitas</a>
 */
public interface MavenDependencyResolver extends DependencyBuilder<MavenDependencyResolver>,
        DependencyResolver<MavenResolutionFilter, MavenDependency> {
    /**
     * Configures Maven from a settings.xml file
     * 
     * @param path A path to a settings.xml configuration file
     * @return A dependency builder with a configuration from given file
     */
    MavenDependencyResolver configureFrom(String path);

    /**
     * Configures Maven from a settings.xml file
     * 
     * @param path A path to a settings.xml configuration file
     * @return A dependency builder with a configuration from given file
     */
    MavenDependencyResolver configureFromFileInClassPath(String path);

    /**
     * Loads remote repositories for a POM file. If repositories are defined in the parent of the POM file and there are
     * accessible via local file system, they are set as well.
     * 
     * These remote repositories are used to resolve the artifacts during dependency resolution.
     * 
     * Additionally, it loads dependencies defined in the POM file model in an internal cache, which can be later used to
     * resolve an artifact without explicitly specifying its version.
     * 
     * @param pathx A path to the POM file, must not be {@code null} or empty
     * @return A dependency builder with remote repositories set according to the content of POM file.
     * @throws Exception
     */
    MavenDependencyResolver loadMetadataFromPom(String path) throws ResolutionException;

    /**
     * Loads remote repositories for a POM file. If repositories are defined in the parent of the POM file and there are
     * accessible via local file system, they are set as well.
     * 
     * These remote repositories are used to resolve the artifacts during dependency resolution.
     * 
     * Additionally, it loads dependencies defined in the POM file model in an internal cache, which can be later used to
     * resolve an artifact without explicitly specifying its version.
     * 
     * @param path A path to the POM file, must not be {@code null} or empty
     * @return A dependency builder with remote repositories set according to the content of POM file.
     * @throws Exception
     * @deprecated please use {@link #loadMetadataFromPom(String)} instead
     */
    @Deprecated
    MavenDependencyResolver loadReposFromPom(String path) throws ResolutionException;

    /**
     * Sets a scope of dependency
     * 
     * @param scope A scope, for example @{code compile}, @{code test} and others
     * @return Artifact builder with scope set
     */
    MavenDependencyResolver scope(String scope);

    /**
     * Sets dependency as optional. If dependency is marked as optional, it is always resolved, however, the dependency graph
     * can later be filtered based on {@code optional} flag
     * 
     * @param optional Optional flag
     * @return Artifact builder with optional flag set
     */
    MavenDependencyResolver optional(boolean optional);

    /**
     * Adds an exclusion for current dependency.
     * 
     * @param exclusion the exclusion to be added to list of artifacts to be excluded, specified in the format
     *        {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will match all
     *        exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
     * @return Artifact builder with added exclusion
     */
    MavenDependencyResolver exclusion(String exclusion);

    /**
     * Adds multiple exclusions for current dependency
     * 
     * @param exclusions the exclusions to be added to the list of artifacts to be excluded, specified in the format
     *        {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will match all
     *        exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
     * @return Artifact builder with added exclusions
     */
    MavenDependencyResolver exclusions(String... exclusions);

    /**
     * Adds multiple exclusions for current dependency
     * 
     * @param exclusions the exclusions to be added to the list of artifacts to be excluded, specified in the format
     *        {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will match all
     *        exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
     * @return Artifact builder with added exclusions
     */
    MavenDependencyResolver exclusions(Collection<String> exclusions);

    /**
     * Resolves based upon dependencies declared in the POM at the specified path
     * 
     * @param path
     * @return
     * @throws ResolutionException
     */
    MavenDependencyResolver includeDependenciesFromPom(final String path) throws ResolutionException;

    /**
     * Resolves based upon dependencies declared in the POM at the classpath
     * 
     * @param path
     * @return
     * @throws ResolutionException
     */
    MavenDependencyResolver includeDependenciesFromPomInClassPath(final String path) throws ResolutionException;

    /**
     * Resolves based upon dependencies declared in the POM at the specified path
     * 
     * @param path
     * @return
     * @throws ResolutionException
     * @deprecated please use {@link #includeDependenciesFromPom(String)} instead
     */
    @Deprecated
    MavenDependencyResolver loadDependenciesFromPom(final String path) throws ResolutionException;

    /**
     * Resolves based upon dependencies declared in the POM at the specified path
     * 
     * @param path
     * @param filter
     * @return
     * @throws ResolutionException
     * @deprecated please use {@link #includeDependenciesFromPom(String)} or
     *             {@link #includeDependenciesFromClassPathPominstead(String)} instead
     */
    @Deprecated
    MavenDependencyResolver loadDependenciesFromPom(final String path, final MavenResolutionFilter filter)
            throws ResolutionException;

    /**
     * Sets the resolver to either consider (or not) Maven Central in resolution
     * 
     * @param useCentral a flag whether to use Maven central
     * @return
     */
    MavenDependencyResolver useCentralRepo(final boolean useCentral);

    /**
     * Disables touching remote repositories at all, rely on local repository only
     * 
     * @return Modified MavenDependencyResolution
     */
    MavenDependencyResolver goOffline();
}
