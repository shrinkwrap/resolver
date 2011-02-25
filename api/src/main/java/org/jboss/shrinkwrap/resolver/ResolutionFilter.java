/**
 * 
 */
package org.jboss.shrinkwrap.resolver;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public interface ResolutionFilter<F extends ResolutionFilter<F, E>, E extends ResolutionElement<E>>
{
   boolean accept(E element);
}
