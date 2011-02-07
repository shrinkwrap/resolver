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
import org.sonatype.aether.graph.DependencyNode;

/**
 * A filter which accept all dependencies. This is the default behavior
 * is no other filter is specified.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class AcceptAllFilter implements DependencyFilter<MavenDependencies>
{
   /*
    * (non-Javadoc)
    * 
    * @see org.sonatype.aether.graph.DependencyFilter#accept(org.sonatype.aether.graph.DependencyNode, java.util.List)
    */
   public boolean accept(DependencyNode node, List<DependencyNode> parents)
   {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyFilter#configure(org.jboss.shrinkwrap.dependencies.DependencyBuilder)
    */
   public AcceptAllFilter configure(MavenDependencies dependencyBuilder)
   {
      return this;
   }

}
