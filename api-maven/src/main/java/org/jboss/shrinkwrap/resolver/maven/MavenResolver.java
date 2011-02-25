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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.ResolutionException;

/**
 * This utility provides a way how to add dependencies to an archive in
 * ShrinkWrap.
 * 
 * It can use arbitrary implementation of dependency resolution and artifact
 * storage, default implementation uses Maven.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenResolver
{
   private static final String IMPL_CLASS = "org.jboss.shrinkwrap.resolver.maven.impl.MavenBuilderImpl";

   /**
    * Creates a new instance of an artifact builder based on default
    * implementation.
    * 
    * @param coordinates The artifact coordinates in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
    *           , must not be {@code null} or empty.
    * @return A new instance of artifact builder
    */
   public static MavenBuilder.MavenArtifactBuilder artifact(String coordinates)
   {
      return createDefaultInstance().artifact(coordinates);
   }

   /**
    * Creates a new instance of an artifacts builder based on default
    * implementation
    * 
    * @param coordinates A list of artifact coordinates in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
    *           , must not be {@code null} or empty.
    * @return A new instance of artifacts builder
    */
   public static MavenBuilder.MavenArtifactsBuilder artifacts(String... coordinates)
   {
      return createDefaultInstance().artifacts(coordinates);
   }

   /**
    * Configures Maven from a settings.xml file
    * 
    * @param path A path to a settings.xml configuration file
    * @return A dependency builder with a configuration from given file
    */
   public static MavenBuilder configureFrom(String path)
   {
      return createDefaultInstance().configureFrom(path);
   }

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
   public static MavenBuilder loadPom(String path) throws ResolutionException
   {
      return createDefaultInstance().loadPom(path);
   }

   /**
    * Uses dependencies and remote repositories defined in a POM file to and
    * tries to resolve them
    * 
    * @param path A path to the POM file
    * @return An array of ShrinkWrap archives
    * @throws DependencyException If dependencies could not be resolved or the
    *            POM processing failed
    */
   public static Archive<?>[] resolveFrom(String path) throws ResolutionException
   {
      return createDefaultInstance().resolveFrom(path);
   }

   /**
    * Uses dependencies and remote repositories defined in a POM file to and
    * tries to resolve them
    * 
    * @param path A path to the POM file
    * @return An array of ShrinkWrap archives
    * @throws DependencyException If dependencies could not be resolved or the
    *            POM processing failed
    */
   public static Archive<?>[] resolveFrom(String path, MavenResolutionFilter filter) throws ResolutionException
   {
      return createDefaultInstance().resolveFrom(path, filter);
   }

   // helper to create default instance
   private static MavenBuilder createDefaultInstance()
   {
      return SecurityActions.newInstance(IMPL_CLASS, new Class<?>[0], new Object[0], MavenBuilder.class);
   }

}
