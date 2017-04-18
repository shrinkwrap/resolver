package org.jboss.shrinkwrap.resolver.plugin.test;
import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.junit.Ignore;
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
        PomEquippedResolveStage resolver = Maven.configureResolverViaPlugin();
        Assert.assertNotNull("Resolver was retrieved from environment", resolver);
    }

    @Test
    public void loadCurrentVersion() {
        PomEquippedResolveStage resolver = Maven.configureResolverViaPlugin();

        File[] files = resolver.resolve("junit:junit").withTransitivity().as(File.class);
        new ValidationUtil("junit", "hamcrest-core").validate(files);
    }
    
    @Test
    public void strictlyLoadTestDependencies() {
        PomEquippedResolveStage resolver = Maven.configureResolverViaPlugin();

        final File[] files = resolver.importCompileAndRuntimeDependencies()
                .resolve().withoutTransitivity().as(File.class);

        new ValidationUtil("junit").validate(files);
    }

}
