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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
public class MavenDependencyImpl implements MavenDependency
{

   private String coordinates;
   private String scope;
   private boolean optional;

   private List<String> exclusions;

   public MavenDependencyImpl(String coordinates)
   {
      this.coordinates = coordinates;
      this.scope = "";
      this.optional = false;
      this.exclusions = new ArrayList<String>();
   }

   @Override
   public MavenDependency setCoordinates(String coordinates)
   {
      this.coordinates = coordinates;
      return this;
   }

   @Override
   public String getScope()
   {
      return scope;
   }

   @Override
   public MavenDependency setScope(String scope)
   {
      this.scope = scope;
      return this;
   }

   @Override
   public boolean isOptional()
   {
      return optional;
   }

   @Override
   public MavenDependency setOptional(boolean optional)
   {
      this.optional = optional;
      return this;
   }

   @Override
   public String[] getExclusions()
   {
      return exclusions.toArray(new String[0]);
   }

   @Override
   public String getCoordinates()
   {
      return coordinates;
   }

   @Override
   public MavenDependency addExclusions(String... exclusions)
   {
      if (exclusions.length == 0)
      {
         return this;
      }

      this.exclusions.addAll(Arrays.asList(exclusions));
      return this;
   }

   /*
    * (non-Javadoc)
    *
    * @see org.jboss.shrinkwrap.resolver.api.maven.MavenDependency#hasSameArtifactAs(org.jboss.shrinkwrap.resolver.api.maven.
    * MavenDependency)
    */
   @Override
   public boolean hasSameArtifactAs(MavenDependency other)
   {
      return MavenConverter.asArtifact(getCoordinates()).equals(MavenConverter.asArtifact(other.getCoordinates()));
   }

}
