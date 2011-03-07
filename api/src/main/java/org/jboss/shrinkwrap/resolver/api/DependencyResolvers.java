/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.api;

import org.jboss.shrinkwrap.api.Archive;

/**
 * Client entry point to resolve {@link Archive}s
 * from a specified {@link DependencyBuilder}
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class DependencyResolvers
{
   /**
   * Creates a new implementation of a dependency builder based on passed class.
   * This allows to switch builder in the test suite dynamically
   * @param <T> The type of class which extends {@link DependencyBuilder}
   * @param clazz the class
   * @return The new instance of dependency builder backed by passed implementation
   */
   public static <T extends DependencyBuilder<T>> T use(final Class<T> clazz)
   {
      return DependencyBuilderInstantiator.createFromUserView(clazz);
   }
}
