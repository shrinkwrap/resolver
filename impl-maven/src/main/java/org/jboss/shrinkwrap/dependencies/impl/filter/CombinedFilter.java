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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jboss.shrinkwrap.dependencies.DependencyException;
import org.jboss.shrinkwrap.dependencies.DependencyFilter;
import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;
import org.sonatype.aether.graph.DependencyNode;

/**
 * A combinator for multiple filters.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class CombinedFilter implements DependencyFilter<MavenDependencies>
{
   private List<DependencyFilter<MavenDependencies>> filters;

   private static final Class<?> NEEDLE = MavenDependencies.class;

   /**
    * Combines multiple filters in a such way that all must pass.
    * 
    * Implementation note: The varargs arguments cannot have a type bound, because
    * this leads to an unchecked cast while invoked
    * 
    * @param filters The filters to be combined
    * @throws DependencyException If any of the filter cannot be used to filter MavenDependencies
    * @see MavenDependencies
    */
   @SuppressWarnings("unchecked")
   public CombinedFilter(DependencyFilter<?>... filters) throws DependencyException
   {
      this.filters = new ArrayList<DependencyFilter<MavenDependencies>>();
      // 
      for (DependencyFilter<?> f : filters)
      {
         boolean added = false;
         for (Type type : f.getClass().getGenericInterfaces())
         {
            if (type instanceof ParameterizedType)
            {
               ParameterizedType ptype = (ParameterizedType) type;
               if (DependencyFilter.class.equals(ptype.getRawType()))
               {
                  // DependencyFilter interface has only one parameter type possible
                  if (NEEDLE.isAssignableFrom((Class<?>) ptype.getActualTypeArguments()[0]))
                  {
                     // this cast is unchecked
                     this.filters.add((DependencyFilter<MavenDependencies>) f);
                     added = true;
                     break;
                  }
               }
            }
         }
         if (added == false)
         {
            throw new DependencyException("Unable to add dependency filter for a class of type " + NEEDLE.getName() + ", because the filter " + f.getClass().getName() + " is incompatible");
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyFilter#configure(org.jboss.shrinkwrap.dependencies.DependencyBuilder)
    */
   public DependencyFilter<MavenDependencies> configure(MavenDependencies dependencyBuilder)
   {
      for (DependencyFilter<MavenDependencies> f : filters)
      {
         f.configure(dependencyBuilder);
      }
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sonatype.aether.graph.DependencyFilter#accept(org.sonatype.aether.graph.DependencyNode, java.util.List)
    */
   public boolean accept(DependencyNode node, List<DependencyNode> parents)
   {
      for (DependencyFilter<MavenDependencies> f : filters)
      {
         if (f.accept(node, parents) == false)
         {
            return false;
         }
      }

      return true;
   }

}
