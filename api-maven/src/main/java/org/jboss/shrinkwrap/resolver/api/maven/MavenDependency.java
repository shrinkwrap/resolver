/**
 * 
 */
package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.resolver.api.ResolutionElement;

/**
 * Describes Maven Resolution Element. 
 * 
 * Contract encapsulates Maven dependency as known from POM files. 
 * 
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public interface MavenDependency extends ResolutionElement<MavenDependency>
{

   /**
    * Sets coordinates.
    * @param coordinates The artifact coordinates in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
    *           , must not be {@code null} or empty.
    * @return Modified instance for chaining
    */
   MavenDependency setCoordinates(String coordinates);

   /**
    * Gets coordinates of the dependency
    * @return The coordinates
    */
   String getCoordinates();

   /**
    * Sets scope of the Maven dependency
    * @param scope The scope to be set
    * @return Modified instance for chaining
    */
   MavenDependency setScope(String scope);

   /**
    * Gets scope of the dependency
    * @return The scope
    */
   String getScope();

   /**
    * Adds one or more exclusions for current dependency 
    * @param exclusion Array of exclusions to be added, in form {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}
    * or {@code *} to exclude all transitive dependencies
    * @return Modified instance for chaining
    */
   MavenDependency addExclusions(String... exclusion);

   /**
    * Gets all exclusions defined on the dependency
    * @return Array of exclusions defined for the dependency
    */
   String[] getExclusions();

   /**
    * Sets dependency as optional. 
    * @param optional The optional flag to set
    * @return Modified instance for chaining
    */
   MavenDependency setOptional(boolean optional);

   /**
    * Gets optional flag.
    * 
    * By default dependency is considered non-optional.
    * @return {@code true} if dependency is optional,{@code false} otherwise
    */
   boolean isOptional();

   /**
    * Checks if other dependency defined the same artifact, 
    * that is Maven will resolve the same artifact from the other dependency.
    * 
    * <p>
    * Coordinates cannot be compared directly, see reason below.
    * </p>
    * 
    * <p>
    * To implement this method, developer must be aware that effectively
    * @{code foo:bar:jar:1.0} and {@code foo:bar:1.0} are the same coordinates,
    * because Maven considers jar as default extension.
    * </p>
    * 
    * @param other The other dependency
    * @return {@code true} if other has the same artifact definition, {@code false} otherwise
    */
   boolean hasSameArtifactAs(MavenDependency other);

}
