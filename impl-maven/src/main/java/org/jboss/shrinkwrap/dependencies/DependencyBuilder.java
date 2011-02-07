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
package org.jboss.shrinkwrap.dependencies;

import java.io.File;
import java.util.Collection;

import org.jboss.shrinkwrap.api.Archive;

/**
 * A dependency builder encapsulates access to a repository which is used
 * to resolve dependencies.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public interface DependencyBuilder<T extends DependencyBuilder<T>>
{
   /**
    * An artifact builder is object which holds and construct dependencies
    * and it is able to resolve them into an array of ShrinkWrap archives.
    * 
    * Artifact builder allows chaining of artifacts, that is specifying
    * a new artifact. In this case, currently constructed artifact is stored
    * as a dependency and user is allowed to specify parameters for another
    * artifact.
    * 
    * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
    * 
    */
   public interface ArtifactBuilder<T extends DependencyBuilder<T>> extends DependencyBuilder<T>
   {
      /**
       * Sets a scope of dependency
       * @param scope A scope, for example @{code compile}, @{code test} and others
       * @return Artifact builder with scope set
       */
      ArtifactBuilder<T> scope(String scope);

      /**
       * Sets dependency as optional. If dependency is marked as optional, it is
       * always resolved, however, the dependency graph can later be filtered based
       * on {@code optional} flag
       * @param optional Optional flag
       * @return Artifact builder with optional flag set
       */
      ArtifactBuilder<T> optional(boolean optional);

      /**
       * Adds an exclusion for current dependency.
       * @param exclusion the exclusion to be added to list of artifacts to be excluded,
       *        specified in the format {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will
       *        match all exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
       * @return Artifact builder with added exclusion
       */
      ArtifactBuilder<T> exclusion(String exclusion);

      /**
       * Adds multiple exclusions for current dependency
       * @param exclusions the exclusions to be added to the list of artifacts to be excluded,
       *        specified in the format {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will
       *        match all exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
       * @return Artifact builder with added exclusions
       */
      ArtifactBuilder<T> exclusions(String... exclusions);

      /**
       * Adds multiple exclusions for current dependency
       * @param exclusions the exclusions to be added to the list of artifacts to be excluded,
       *        specified in the format {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will
       *        match all exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
       * @return Artifact builder with added exclusions
       */
      ArtifactBuilder<T> exclusions(Collection<String> exclusions);

      /**
       * Resolves dependencies for dependency builder
       * @return An array of archives which contains resolved artifacts.
       * @throws DependencyException If artifacts could not be resolved
       */
      Archive<?>[] resolve() throws DependencyException;

      /**
       * Resolves dependencies for dependency builder.
       * Uses a filter to limit dependency tree
       * @param filter The filter to limit the dependencies during resolution
       * @return An array of archive which contains resolved artifacts
       * @throws DependencyException
       */
      Archive<?>[] resolve(DependencyFilter<T> filter) throws DependencyException;

      /**
       * Resolves dependencies for dependency builder
       * 
       * @return An array of Files which contains resolved artifacts.
       * @throws DependencyException If artifacts could not be resolved
       */
      File[] resolveAsFiles() throws DependencyException;

      /**
       * Resolves dependencies for dependency builder.
       * Uses a filter to limit dependency tree
       * 
       * @param filter The filter to limit the dependencies during resolution
       * @return An array of Files which contains resolved artifacts
       * @throws DependencyException
       */
      File[] resolveAsFiles(DependencyFilter<T> filter) throws DependencyException;

   }

   /**
    * An artifacts builder is object which holds and construct dependencies
    * and it is able to resolve them into an array of ShrinkWrap archives.
    * 
    * Artifacts builder allows chaining of artifacts, that is specifying
    * a new artifact. In this case, currently constructed artifact is stored
    * as a dependency and user is allowed to specify parameters for another
    * artifact.
    * 
    * The special ability of this object when compared to {@link ArtifactBuilder} is
    * the ability to work in batch, that is allow to define more artifacts at once and
    * modify their scope etc. by a single call.
    * 
    * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
    * @see ArtifactBuilder
    */
   public interface ArtifactsBuilder<T extends DependencyBuilder<T>> extends ArtifactBuilder<T>
   {
   }

   /**
    * Creates an artifact builder. You can define additional parameters
    * for the artifact later.
    * 
    * @param coordinates Coordinates specified to a created artifact, specified
    *        in the format {@code <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]}, must not be {@code null} or empty.
    *        If {@code version} is not specified, is it determined if underlying repository system supports so.
    * 
    * @return A new artifact builder
    * @throws DependencyException If artifact coordinates are wrong or if version cannot be determined.
    */
   ArtifactBuilder<T> artifact(String coordinates) throws DependencyException;

   /**
    * Creates an artifact builder. You can define additional parameters
    * for the artifacts later. Additional parameters will be changed for all artifacts
    * defined by this call.
    * 
    * @param coordinates A list of coordinates specified to the created artifacts, specified
    *        in the format {@code <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]}, must not be {@code null} or empty.
    *        If {@code version} is not specified, is it determined if underlying repository system supports so.
    * @return A new artifact builder
    * @throws DependencyException If artifact coordinates are wrong or if version cannot be determined.
    */
   ArtifactsBuilder<T> artifacts(String... coordinates) throws DependencyException;

}
