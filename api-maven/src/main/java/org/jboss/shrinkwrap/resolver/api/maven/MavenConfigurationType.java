package org.jboss.shrinkwrap.resolver.api.maven;

/**
 * A strategy how to configure Maven session inside of the test.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <T>
 *            The type of the object returned after configuration
 * @see MavenConfigurationTypes for choices distributed in ShrinkWrap Maven resolver
 */
public interface MavenConfigurationType<T extends ConfiguredMavenDependencyResolver> {

    /**
     * Configures a MavenDependencyResolver or returns a different object which can be configured by hand
     *
     * @param resolver
     *            The resolver to be configured
     * @return the configured object
     * @throws IllegalArgumentException
     *             If resolver is not supplied
     */
    T configure(MavenDependencyResolver resolver);
}