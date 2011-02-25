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
package org.jboss.shrinkwrap.dependencies.impl;

import java.util.Collections;
import java.util.List;

import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;

/**
 * A wrapper which allows filters accessing internals of MavenDependencies.
 * This way interface is not polluted with getters and setters and filters can live
 * in a different package.
 * 
 * This class is intended for internal use only.
 * 
 * @see MavenBuilderImpl
 * @see DependencyFilter
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenDependencyFilterWrap
{
   private MavenBuilderImpl dependencies;

   /**
    * Wraps MavenDependecies so its internals can be accessed
    * @param dependencies The dependencies
    */
   public MavenDependencyFilterWrap(MavenBuilderImpl dependencies)
   {
      this.dependencies = dependencies;
   }

   /**
    * A list of currently defined dependencies
    * @return The read-only list of dependencies defined up to this point
    */
   public List<Dependency> getDefinedDependencies()
   {
      return Collections.unmodifiableList(dependencies.dependencies);
   }
}
