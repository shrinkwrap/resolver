package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.MavenNonTransitiveStrategyImpl;
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

        File[] files = resolver.importTestDependencies(new MavenNonTransitiveStrategyImpl()).as(File.class);

        new ValidationUtil("shrinkwrap-impl-base", "junit", "commons-codec", "jetty").validate(files);
    }

}
