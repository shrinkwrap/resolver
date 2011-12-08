package org.jboss.shrinkwrap.resolver.api;

/**
 * A marker for an entry point which can be instantiated using {@link DependencyResolvers#use(Class)} call.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <T> Type of the entry point
 */
public interface ResolverEntryPoint<T extends ResolverEntryPoint<T>> {

}
