/**
 * 
 */
package org.jboss.shrinkwrap.resolver.api;

import java.io.File;
import java.util.Collection;

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public interface DependencyResolver<F extends DependencyResolutionFilter<F, E>, E extends ResolutionElement<E>>
{

   /**
    * Resolves dependencies for dependency builder
    * 
    * @param archiveView End-user view of the archive requested (ie. 
    *   {@link GenericArchive} or {@link JavaArchive})
    * @return An array of archives which contains resolved artifacts.
    * @throws ResolutionException If artifacts could not be resolved
    * @throws {@link IllegalArgumentException} If target archive view is not supplied
    */
   <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView)
         throws ResolutionException;

   /**
    * Resolves dependencies for dependency builder. Uses a filter to limit
    * dependency tree
    * 
    * @param archiveView End-user view of the archive requested (ie. 
    *   {@link GenericArchive} or {@link JavaArchive})
    * @param filter The filter to limit the dependencies during resolution
    * @return An array of archive which contains resolved artifacts
    * @throws ResolutionException
    * @throws {@link IllegalArgumentException} If either argument is not supplied
    */
   <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView, F filter)
         throws ResolutionException;

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
