package org.jboss.shrinkwrap.resolver.plugin.test;
import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Verified plugin integration on current project
 *
 * This tests are expected to fail in IDE until integration with IDE is done.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
class PluginIntegrationTestCase {

    /*
     * NOTE: This test will depend upon information declared in this project's POM. Changes to the POM will impact the
     * test.
     */
    
    @Test
    void loadCurrentProject() {
        PomEquippedResolveStage resolver = Maven.configureResolverViaPlugin();
        Assertions.assertNotNull(resolver, "Resolver was retrieved from environment");
    }

    @Test
    void loadCurrentVersion() {
        PomEquippedResolveStage resolver = Maven.configureResolverViaPlugin();

        File[] files = resolver.resolve("org.junit.jupiter:junit-jupiter").withTransitivity().as(File.class);
        new ValidationUtil("junit-jupiter", "junit-platform", "apiguardian", "opentest4j").validate(files);
    }
    
    @Test
    void strictlyLoadTestDependencies() {
        PomEquippedResolveStage resolver = Maven.configureResolverViaPlugin();

        final File[] files = resolver.importCompileAndRuntimeDependencies()
                .resolve().withoutTransitivity().as(File.class);

        new ValidationUtil("junit-jupiter").validate(files);
    }

}
