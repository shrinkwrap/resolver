/**
 * 
 */
package org.jboss.shrinkwrap.resolver;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public interface Resolver<F extends ResolutionFilter<F, E>, E extends ResolutionElement<E>>
{

   /**
    * Resolves dependencies for dependency builder
    * 
    * @return An array of archives which contains resolved artifacts.
    * @throws ResolutionException If artifacts could not be resolved
    */
   Archive<?>[] resolve() throws ResolutionException;

   /**
    * Resolves dependencies for dependency builder. Uses a filter to limit
    * dependency tree
    * 
    * @param filter The filter to limit the dependencies during resolution
    * @return An array of archive which contains resolved artifacts
    * @throws ResolutionException
    */
   Archive<?>[] resolve(F filter) throws ResolutionException;

   /**
    * Resolves dependencies for dependency builder
    * 
    * @return An array of Files which contains resolved artifacts.
    * @throws ResolutionException If artifacts could not be resolved
    */
   File[] resolveAsFiles() throws ResolutionException;

   /**
    * Resolves dependencies for dependency builder. Uses a filter to limit
    * dependency tree
    * 
    * @param filter The filter to limit the dependencies during resolution
    * @return An array of Files which contains resolved artifacts
    * @throws ResolutionException
    */
   File[] resolveAsFiles(F filter) throws ResolutionException;
}
