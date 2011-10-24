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
public interface MavenDependencyResolver extends ResolverEntryPoint<MavenDependencyResolver> {

    /**
     * Configures Maven from a settings.xml file
     *
     * @param path A path to a settings.xml configuration file
     * @return A dependency builder with a configuration from given file
     */
    MavenDependencyResolver configureFrom(String path);

    /**
     * Constructs an effective POM loading a POM file from a given resource, which can be either a path to file or a class path
     * resource.
     *
     * It grabs definitions of dependencies, dependencies in dependencyManagement and repositories. This are cached and can be
     * later used to simplify the way how user specifies dependencies, e.g. allows user to omit versions which are already
     * present in the POM file.
     *
     * @param path A path to the POM file, must not be {@code null} or empty
     * @param profiles A list of profiles to be activated during effective POM creation
     * @return A dependency builder with remote repositories set according to the content of POM file.
     * @throws Exception
     */
    EffectivePomMavenDependencyResolver loadEffectivePom(String path, String... profiles) throws ResolutionException;

    MavenRepositoryBuilder repository(String url);

    MavenRepositoryBuilder repositories(String... url);

    MavenDependencyResolver useCentralRepo(boolean useCentralRepository);

    /**
     * Disables touching remote repositories at all, rely on local repository only
     *
     * @return Modified MavenDependencyResolution
     */
    MavenDependencyResolver goOffline();

    MavenDependencyBuilder artifact(String coordinates) throws ResolutionException;

    MavenDependencyBuilder artifacts(String... coordinates) throws ResolutionException;
}
