package org.jboss.shrinkwrap.resolver.impl.maven;

import static org.jboss.shrinkwrap.resolver.api.maven.MavenConfigurationTypes.ENVIRONMENT;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.StrictFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Verified plugin integration on current project
 *
 * This tests are expected to fail in IDE until integration with IDE is done.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class PluginIntegrationTestCase {

    @Test
    public void loadCurrentProject() {
        EffectivePomMavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).configureFrom(
                ENVIRONMENT);
        Assert.assertNotNull("Resolver was retrieved from environment", resolver);
    }

    @Test
    public void loadCurrentVersion() {
        EffectivePomMavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).configureFrom(
                ENVIRONMENT);

        File[] files = resolver.artifact("org.sonatype.aether:aether-api").resolveAsFiles();
        new ValidationUtil("aether-api").validate(files);
    }

    @Test
    public void strictlyLoadTestDependencies() {
        EffectivePomMavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).configureFrom(
                ENVIRONMENT);

        File[] files = resolver.importTestDependencies().resolveAsFiles(new StrictFilter());
        new ValidationUtil("shrinkwrap-impl-base", "junit", "commons-codec", "jetty").validate(files);
    }

}
