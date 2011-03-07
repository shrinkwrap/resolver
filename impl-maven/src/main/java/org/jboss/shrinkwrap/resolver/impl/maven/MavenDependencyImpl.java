/**
 * 
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

   public MavenDependency setCoordinates(String coordinates)
   {
      this.coordinates = coordinates;
      return this;
   }

   public String getScope()
   {
      return scope;
   }

   public MavenDependency setScope(String scope)
   {
      this.scope = scope;
      return this;
   }

   public boolean isOptional()
   {
      return optional;
   }

   public MavenDependency setOptional(boolean optional)
   {
      this.optional = optional;
      return this;
   }

   public String[] getExclusions()
   {
      return exclusions.toArray(new String[0]);
   }

   public String getCoordinates()
   {
      return coordinates;
   }

   public MavenDependency addExclusions(String... exclusions)
   {
      if (exclusions.length == 0)
      {
         return this;
      }

      this.exclusions.addAll(Arrays.asList(exclusions));
      return this;
   }



}
