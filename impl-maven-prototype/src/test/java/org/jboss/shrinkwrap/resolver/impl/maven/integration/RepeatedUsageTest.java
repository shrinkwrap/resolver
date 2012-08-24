package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;

public class RepeatedUsageTest {

    /**
     * Tests a resolution of an artifact from local repository specified in settings.xml as active profile
     *
     * @throws ResolutionException
     */
    @Test
    public void reuseBOM() {

        ConfiguredResolveStage resolver = Resolvers.use(MavenResolverSystem.class)
            .configureSettings("target/settings/profiles/settings.xml").configureFromPom("target/poms/test-bom.xml");

        File[] files = resolver.resolve("org.jboss.shrinkwrap.test:test-deps-a").withTransitivity().as(File.class);

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-a-1.0.0.jar", "test-deps-a-1.0.0.jar",
            files[0].getName());

        files = resolver.resolve("org.jboss.shrinkwrap.test:test-deps-d").withTransitivity().as(File.class);

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-d-1.0.0.jar", "test-deps-d-1.0.0.jar",
            files[0].getName());

    }
}
