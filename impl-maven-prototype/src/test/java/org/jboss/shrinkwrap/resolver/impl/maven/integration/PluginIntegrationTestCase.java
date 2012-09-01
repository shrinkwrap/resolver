package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.NonTransitiveStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Verified plugin integration on current project
 *
 * This tests are expected to fail in IDE until integration with IDE is done.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public class PluginIntegrationTestCase {

    /*
     * NOTE: This test will depend upon information declared in this project's POM. Changes to the POM will impact the
     * test.
     */

    @Test
    public void loadCurrentProject() {
        ConfiguredResolveStage resolver = Resolvers.use(MavenResolverSystem.class).configureFromPlugin();
        Assert.assertNotNull("Resolver was retrieved from environment", resolver);
    }

    @Test
    public void loadCurrentVersion() {
        ConfiguredResolveStage resolver = Resolvers.use(MavenResolverSystem.class).configureFromPlugin();

        File[] files = resolver.resolve("org.sonatype.aether:aether-api").withTransitivity().as(File.class);
        new ValidationUtil("aether-api").validate(files);
    }

    @Test
    public void strictlyLoadTestDependencies() {
        ConfiguredResolveStage resolver = Resolvers.use(MavenResolverSystem.class).configureFromPlugin();

        final File[] files = resolver.importRuntimeDependencies(new NonTransitiveStrategy()).as(File.class);
        new ValidationUtil("maven-settings-builder", "plexus-interpolation", "maven-settings", "aether-util",
            "aether-spi", "maven-model-builder", "wagon-provider-api", "plexus-cipher", "maven-repository-metadata",
            "shrinkwrap-resolver-api-maven-prototype", "maven-model", "jsoup", "sisu-inject-plexus",
            "maven-aether-provider", "plexus-utils", "wagon-file", "aether-api", "aether-connector-wagon",
            "plexus-classworlds", "wagon-http-lightweight", "plexus-component-annotations", "aether-impl")
            .validate(files);
    }

}
