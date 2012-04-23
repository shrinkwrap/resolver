package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;

public class RepeatedUsageTestCase {

    /**
     * Tests a resolution of an artifact from local repository specified in settings.xml as active profile
     *
     * @throws ResolutionException
     */
    @Test
    public void reuseBOM() throws ResolutionException {
        EffectivePomMavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
                .loadSettings("target/settings/profiles/settings.xml").loadEffectivePom("target/poms/test-bom.xml");

        File[] files = resolver.artifact("org.jboss.shrinkwrap.test:test-deps-a").resolveAsFiles();

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-a-1.0.0.jar", "test-deps-a-1.0.0.jar", files[0].getName());

        files = resolver.artifact("org.jboss.shrinkwrap.test:test-deps-d").resolveAsFiles();

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-d-1.0.0.jar", "test-deps-d-1.0.0.jar", files[0].getName());

    }
}
