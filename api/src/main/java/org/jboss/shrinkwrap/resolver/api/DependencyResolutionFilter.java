/**
 * 
 */
package org.jboss.shrinkwrap.resolver.api;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public interface DependencyResolutionFilter<F extends DependencyResolutionFilter<F, E>, E extends ResolutionElement<E>>
{
   boolean accept(E element);
}
