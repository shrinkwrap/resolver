/*
d * JBoss, Home of Professional Open Source
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

import org.jboss.shrinkwrap.resolver.api.DependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.ResolverEntryPoint;
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
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 * @author <a href="http://community.jboss.org/people/spinner">Jose Rodolfo Freitas</a>
 */
public interface MavenDependencyResolver extends ResolverEntryPoint<MavenDependencyResolver>,
        DependencyBuilder<MavenDependencyBuilder>, ConfiguredMavenDependencyResolver {

    <T extends ConfiguredMavenDependencyResolver> T configureFrom(MavenConfigurationType<T> configurationType);

    /**
     * Configures Maven from a settings.xml file
     *
     * @param userSettings A path to a user settings.xml configuration file
     * @return A dependency builder with a configuration from given file
     * @throws IllegalArgumentException If userSettings are not supplied
     */
    MavenDependencyResolver loadSettings(String userSettings);

    /**
     * Constructs an effective POM loading a POM file from a given resource, which can be either a path to file or a class path
     * resource.
     *
     * It grabs definitions of dependencies, dependencies in dependencyManagement and repositories. This are cached and can be
     * later used to simplify the way how user specifies dependencies, e.g. allows user to omit versions which are already
     * present in the POM file.
     *
     * @param path A path to the POM file, must not be {@code null} or empty
     * @return A dependency builder with remote repositories set according to the content of POM file.
     * @throws ResolutionException If an effective POM cannot be resolved
     */
    EffectivePomMavenDependencyResolver loadEffectivePom(String path) throws ResolutionException;

    /**
     * Adds a repository specified by given URL
     *
     * @param url the url representing a Maven repository
     * @return {@link MavenDependencyBuilder} to allow specify more information about the repository
     */
    MavenRepositoryBuilder repository(String url);

    /**
     * Adds a list of repositories specified by given URL
     *
     * @param url a list of urls represention a Maven repository
     * @return {@link MavenDependencyBuilder} to allow specify more information about the repositories
     */
    MavenRepositoryBuilder repositories(String... url);

    /**
     * Disables touching of Maven Central repository. This repository is enabled by default in Maven
     *
     * @return Modified {@link MavenDependencyResolver}
     */
    MavenDependencyResolver disableMavenCentral();

    /**
     * Disables touching remote repositories at all, rely on local repository only
     *
     * @return Modified {@link MavenDependencyResolver}
     */
    MavenDependencyResolver goOffline();

}
