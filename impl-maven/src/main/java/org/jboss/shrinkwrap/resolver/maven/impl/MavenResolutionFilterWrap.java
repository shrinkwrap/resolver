/**
 * 
 */
package org.jboss.shrinkwrap.resolver.maven.impl;

import java.util.List;

import org.jboss.shrinkwrap.resolver.maven.MavenResolutionFilter;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public class MavenResolutionFilterWrap implements org.sonatype.aether.graph.DependencyFilter
{
   private MavenResolutionFilter delegate;

   public MavenResolutionFilterWrap(MavenResolutionFilter filter)
   {
      delegate = filter;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.sonatype.aether.graph.DependencyFilter#accept(org.sonatype.aether.
    * graph.DependencyNode, java.util.List)
    */
   public boolean accept(DependencyNode node, List<DependencyNode> parents)
   {
      Dependency dependency = node.getDependency();
      if (dependency == null)
      {
         return false;
      }

      return delegate.accept(MavenConverter.fromDependency(dependency));
   }

}
