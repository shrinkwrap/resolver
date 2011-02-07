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
package org.jboss.shrinkwrap.dependencies.impl.filter;

import java.util.List;

import org.jboss.shrinkwrap.dependencies.DependencyFilter;
import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;
import org.jboss.shrinkwrap.dependencies.impl.MavenDependencyFilterWrap;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;

/**
 * A filter which accepts only dependencies which are directly specified in the builder
 * All transitive dependencies are omitted.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class StrictFilter implements DependencyFilter<MavenDependencies>
{
   private List<Dependency> allowedDependencies;

   /**
    * Creates a filter which accepts dependencies marked as allowed
    */
   public StrictFilter()
   {
   }

   /**
    * @return the allowedDependencies
    */
   public List<Dependency> getAllowedDependencies()
   {
      return allowedDependencies;
   }

   /**
    * @param allowedDependencies the allowedDependencies to set
    */
   public void setAllowedDependencies(List<Dependency> allowedDependencies)
   {
      this.allowedDependencies = allowedDependencies;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyFilter#configure(org.jboss.shrinkwrap.dependencies.DependencyBuilder)
    */
   public DependencyFilter<MavenDependencies> configure(MavenDependencies dependencyBuilder)
   {
      MavenDependencyFilterWrap wrap = new MavenDependencyFilterWrap(dependencyBuilder);
      this.allowedDependencies = wrap.getDefinedDependencies();
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sonatype.aether.graph.DependencyFilter#accept(org.sonatype.aether.graph.DependencyNode, java.util.List)
    */
   public boolean accept(DependencyNode node, List<DependencyNode> parents)
   {
      Dependency dependency = node.getDependency();

      if (dependency == null)
      {
         return false;
      }

      for (Dependency allowed : allowedDependencies)
      {
         if (allowed.getArtifact().equals(dependency.getArtifact()) && allowed.getScope().equals(dependency.getScope()))
         {
            return true;
         }
      }
      return false;
   }

}
