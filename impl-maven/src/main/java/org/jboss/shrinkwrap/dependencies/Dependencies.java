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

import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;

/**
 * This utility provides a way how to add dependencies to an archive in ShrinkWrap.
 * 
 * It can use arbitrary implementation of dependency resolution and artifact storage,
 * default implementation uses Maven.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class Dependencies
{

   /**
    * Creates a new implementation of a dependency builder based on passed class.
    * This allows to switch builder in the test suite dynamically
    * @param <T> The type of class which extends {@link DependencyBuilder}
    * @param clazz the class
    * @return The new instance of dependency builder backed by passed implementation
    */
   public static <T extends DependencyBuilder<T>> T use(Class<T> clazz)
   {
      return SecurityActions.newInstance(clazz.getName(), new Class<?>[0], new Object[0], clazz);
   }

   /**
    * Creates a new instance of an artifact builder based on default implementation.
    * @param coordinates The artifact coordinates in the format {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>},
    *        must not be {@code null} or empty.
    * @return A new instance of artifact builder
    */
   public static DependencyBuilder.ArtifactBuilder<MavenDependencies> artifact(String coordinates)
   {
      return new MavenDependencies().artifact(coordinates);
   }
   
   /**
    * Creates a new instance of an artifacts builder based on default implementation
    * 
    * @param coordinates A list of artifact coordinates in the format {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>},
    *        must not be {@code null} or empty.
    * @return A new instance of artifacts builder
    */
   public static DependencyBuilder.ArtifactsBuilder<MavenDependencies> artifacts(String...coordinates) {
      return new MavenDependencies().artifacts(coordinates);
   }

}
