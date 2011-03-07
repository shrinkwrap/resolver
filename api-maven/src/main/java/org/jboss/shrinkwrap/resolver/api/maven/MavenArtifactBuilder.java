package org.jboss.shrinkwrap.resolver.api.maven;

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.DependencyResolver;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

/**
 * An artifact builder is object which holds and construct dependencies and it
 * is able to resolve them into an array of ShrinkWrap archives.
 * 
 * Artifact builder allows chaining of artifacts, that is specifying a new
 * artifact. In this case, currently constructed artifact is stored as a
 * dependency and user is allowed to specify parameters for another artifact.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public interface MavenArtifactBuilder extends DependencyResolver<MavenResolutionFilter, MavenDependency>
{

   /**
    * Creates an artifact builder. You can define additional parameters for the
    * artifact later.
    * 
    * @param coordinates Coordinates specified to a created artifact, specified
    *           in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]}
    *           , must not be {@code null} or empty. If {@code version} is not
    *           specified, is it determined if underlying repository system
    *           supports so.
    * 
    * @return A new artifact builder
    * @throws ResolutionException If artifact coordinates are wrong or if
    *            version cannot be determined.
    */
   MavenArtifactBuilder artifact(String coordinates) throws ResolutionException;

   /**
    * Creates an artifact builder. You can define additional parameters for the
    * artifacts later. Additional parameters will be changed for all artifacts
    * defined by this call.
    * 
    * @param coordinates A list of coordinates specified to the created
    *           artifacts, specified in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]}
    *           , must not be {@code null} or empty. If {@code version} is not
    *           specified, is it determined if underlying repository system
    *           supports so.
    * @return A new artifact builder
    * @throws ResolutionException If artifact coordinates are wrong or if
    *            version cannot be determined.
    */
   MavenArtifactsBuilder artifacts(String... coordinates) throws ResolutionException;

   /**
    * Sets a scope of dependency
    * 
    * @param scope A scope, for example @{code compile}, @{code test} and others
    * @return Artifact builder with scope set
    */
   MavenArtifactBuilder scope(String scope);

   /**
    * Sets dependency as optional. If dependency is marked as optional, it is
    * always resolved, however, the dependency graph can later be filtered based
    * on {@code optional} flag
    * 
    * @param optional Optional flag
    * @return Artifact builder with optional flag set
    */
   MavenArtifactBuilder optional(boolean optional);

   /**
    * Adds an exclusion for current dependency.
    * 
    * @param exclusion the exclusion to be added to list of artifacts to be
    *           excluded, specified in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an
    *           empty string or {@code *} will match all exclusions, you can
    *           pass an {@code *} instead of any part of the coordinates to
    *           match all possible values
    * @return Artifact builder with added exclusion
    */
   MavenArtifactBuilder exclusion(String exclusion);

   /**
    * Adds multiple exclusions for current dependency
    * 
    * @param exclusions the exclusions to be added to the list of artifacts to
    *           be excluded, specified in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an
    *           empty string or {@code *} will match all exclusions, you can
    *           pass an {@code *} instead of any part of the coordinates to
    *           match all possible values
    * @return Artifact builder with added exclusions
    */
   MavenArtifactBuilder exclusions(String... exclusions);

   /**
    * Adds multiple exclusions for current dependency
    * 
    * @param exclusions the exclusions to be added to the list of artifacts to
    *           be excluded, specified in the format
    *           {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an
    *           empty string or {@code *} will match all exclusions, you can
    *           pass an {@code *} instead of any part of the coordinates to
    *           match all possible values
    * @return Artifact builder with added exclusions
    */
   MavenArtifactBuilder exclusions(Collection<String> exclusions);
}