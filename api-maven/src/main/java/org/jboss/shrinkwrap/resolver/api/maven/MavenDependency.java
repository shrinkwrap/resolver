/**
 * 
 */
package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.resolver.api.ResolutionElement;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public interface MavenDependency extends ResolutionElement<MavenDependency>
{

   MavenDependency setCoordinates(String coordinates);

   String getCoordinates();

   MavenDependency setScope(String scope);

   String getScope();

   MavenDependency addExclusions(String... exclusion);

   String[] getExclusions();

   MavenDependency setOptional(boolean optional);

   boolean isOptional();

}
