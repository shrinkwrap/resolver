/**
 * 
 */
package org.jboss.shrinkwrap.dependencies.impl;

import java.util.List;

import org.sonatype.aether.graph.DependencyNode;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
public class MavenDependencyFilter implements org.sonatype.aether.graph.DependencyFilter
{

   /* (non-Javadoc)
    * @see org.sonatype.aether.graph.DependencyFilter#accept(org.sonatype.aether.graph.DependencyNode, java.util.List)
    */
   public boolean accept(DependencyNode node, List<DependencyNode> parents)
   {
      // TODO Auto-generated method stub
      return false;
   }

}
