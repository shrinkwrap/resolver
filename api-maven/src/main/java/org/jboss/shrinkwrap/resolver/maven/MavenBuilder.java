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
package org.jboss.shrinkwrap.resolver.maven;

import java.util.Collection;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.ResolutionException;
import org.jboss.shrinkwrap.resolver.Resolver;

/**
 * A dependency builder encapsulates access to a repository which is used to
 * resolve dependencies.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public interface MavenBuilder
{
   /**
    * An artifact builder is object which holds and construct dependencies and
    * it is able to resolve them into an array of ShrinkWrap archives.
    * 
    * Artifact builder allows chaining of artifacts, that is specifying a new
    * artifact. In this case, currently constructed artifact is stored as a
    * dependency and user is allowed to specify parameters for another artifact.
    * 
    * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
    * 
    */
   public interface MavenArtifactBuilder extends Resolver<MavenResolutionFilter, MavenResolutionElement>
   {
      /**
       * Sets a scope of dependency
       * 
       * @param scope A scope, for example @{code compile}, @{code test} and
       *           others
       * @return Artifact builder with scope set
       */
      MavenArtifactBuilder scope(String scope);

      /**
       * Sets dependency as optional. If dependency is marked as optional, it is
       * always resolved, however, the dependency graph can later be filtered
       * based on {@code optional} flag
       * 
       * @param optional Optional flag
       * @return Artifact builder with optional flag set
       */
      MavenArtifactBuilder optional(boolean optional);

      /**
       * Adds an exclusion for current dependency.
       * 
       * @param exclusion the exclusion to be added to list of artifacts to be
       *           excluded, specified in the format
       *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]},
       *           an empty string or {@code *} will match all exclusions, you
       *           can pass an {@code *} instead of any part of the coordinates
       *           to match all possible values
       * @return Artifact builder with added exclusion
       */
      MavenArtifactBuilder exclusion(String exclusion);

      /**
       * Adds multiple exclusions for current dependency
       * 
       * @param exclusions the exclusions to be added to the list of artifacts
       *           to be excluded, specified in the format
       *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]},
       *           an empty string or {@code *} will match all exclusions, you
       *           can pass an {@code *} instead of any part of the coordinates
       *           to match all possible values
       * @return Artifact builder with added exclusions
       */
      MavenArtifactBuilder exclusions(String... exclusions);

      /**
       * Adds multiple exclusions for current dependency
       * 
       * @param exclusions the exclusions to be added to the list of artifacts
       *           to be excluded, specified in the format
       *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]},
       *           an empty string or {@code *} will match all exclusions, you
       *           can pass an {@code *} instead of any part of the coordinates
       *           to match all possible values
       * @return Artifact builder with added exclusions
       */
      MavenArtifactBuilder exclusions(Collection<String> exclusions);
   }

   /**
    * An artifacts builder is object which holds and construct dependencies and
    * it is able to resolve them into an array of ShrinkWrap archives.
    * 
    * Artifacts builder allows chaining of artifacts, that is specifying a new
    * artifact. In this case, currently constructed artifact is stored as a
    * dependency and user is allowed to specify parameters for another artifact.
    * 
    * The special ability of this object when compared to
    * {@link MavenArtifactBuilder} is the ability to work in batch, that is
    * allow to define more artifacts at once and modify their scope etc. by a
    * single call.
    * 
    * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
    * @see MavenArtifactBuilder
    */
   public interface MavenArtifactsBuilder extends MavenArtifactBuilder
   {
   }

   /**
    * Creates an artifact builder. You can define additional parameters for the
    * artifact later.
    * 
    * @param coordinates Coordinates specified to a created artifact, specified
    *           in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]}
    *           , must not be {@code null} or empty. If {@code version} is not
    *           specified, is it determined if underlying repository system
    *           supports so.
    * 
    * @return A new artifact builder
    * @throws ResolutionException If artifact coordinates are wrong or if
    *            version cannot be determined.
    */
   MavenArtifactBuilder artifact(String coordinates) throws ResolutionException;

   /**
    * Creates an artifact builder. You can define additional parameters for the
    * artifacts later. Additional parameters will be changed for all artifacts
    * defined by this call.
    * 
    * @param coordinates A list of coordinates specified to the created
    *           artifacts, specified in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]}
    *           , must not be {@code null} or empty. If {@code version} is not
    *           specified, is it determined if underlying repository system
    *           supports so.
    * @return A new artifact builder
    * @throws ResolutionException If artifact coordinates are wrong or if
    *            version cannot be determined.
    */
   MavenArtifactsBuilder artifacts(String... coordinates) throws ResolutionException;

   /**
    * Configures Maven from a settings.xml file
    * 
    * @param path A path to a settings.xml configuration file
    * @return A dependency builder with a configuration from given file
    */
   MavenBuilder configureFrom(String path);

   /**
    * Loads remote repositories for a POM file. If repositories are defined in
    * the parent of the POM file and there are accessible via local file system,
    * they are set as well.
    * 
    * These remote repositories are used to resolve the artifacts during
    * dependency resolution.
    * 
    * Additionally, it loads dependencies defined in the POM file model in an
    * internal cache, which can be later used to resolve an artifact without
    * explicitly specifying its version.
    * 
    * @param path A path to the POM file, must not be {@code null} or empty
    * @return A dependency builder with remote repositories set according to the
    *         content of POM file.
    * @throws Exception
    */
   MavenBuilder loadPom(String path) throws ResolutionException;

   /**
    * Uses dependencies and remote repositories defined in a POM file to and
    * tries to resolve them
    * 
    * @param path A path to the POM file
    * @return An array of ShrinkWrap archives
    * @throws DependencyException If dependencies could not be resolved or the
    *            POM processing failed
    */
   Archive<?>[] resolveFrom(String path) throws ResolutionException;

   /**
    * Uses dependencies and remote repositories defined in a POM file to and
    * tries to resolve them
    * 
    * @param path A path to the POM file
    * @return An array of ShrinkWrap archives
    * @throws DependencyException If dependencies could not be resolved or the
    *            POM processing failed
    */
   Archive<?>[] resolveFrom(String path, MavenResolutionFilter filter) throws ResolutionException;

}
