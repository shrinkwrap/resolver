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

import org.jboss.shrinkwrap.resolver.api.DependencyType;
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
public interface MavenDependencyResolver extends DependencyType<MavenDependencyResolver> {

    /**
     * Configures Maven from a settings.xml file
     *
     * @param path A path to a settings.xml configuration file
     * @return A dependency builder with a configuration from given file
     */
    MavenDependencyResolver configureFrom(String path);

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
    EffectivePomMavenDependencyResolver loadEffectiveFromPom(String path, String... profiles) throws ResolutionException;

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

    MavenDependencyBuilder artifact(String coordinates) throws ResolutionException;

    MavenDependencyBuilder artifacts(String... coordinates) throws ResolutionException;
}
