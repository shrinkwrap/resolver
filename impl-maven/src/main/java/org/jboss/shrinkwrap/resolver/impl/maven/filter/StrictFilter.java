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
package org.jboss.shrinkwrap.resolver.impl.maven.filter;

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenConverter;

/**
 * A filter which accepts only dependencies which are directly specified in the
 * builder All transitive dependencies are omitted.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class StrictFilter implements MavenResolutionFilter
{
   private Collection<MavenDependency> allowedDependencies;

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.jboss.shrinkwrap.resolver.maven.MavenResolutionFilter#configure(java
    * .util.Collection)
    */
   public MavenResolutionFilter configure(Collection<MavenDependency> dependencies)
   {
      this.allowedDependencies = dependencies;
      return this;
   }

   public boolean accept(MavenDependency element)
   {

      for (MavenDependency allowed : allowedDependencies)
      {
         if (allowed.getScope().equals(element.getScope()) && hasSameArtifact(allowed, element))
         {
            return true;
         }
      }
      return false;
   }

   private boolean hasSameArtifact(MavenDependency one, MavenDependency two)
   {
      return MavenConverter.asArtifact(one.getCoordinates()).equals(MavenConverter.asArtifact(two.getCoordinates()));
   }

}
