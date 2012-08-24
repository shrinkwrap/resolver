package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.StrictFilter;
import org.junit.Test;

/**
 * Tests mirror setting in Maven
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MirrorTestCase {

    /**
     * Tests a resolution of an artifact from JBoss repository specified in settings.xml within activeProfiles mirrored
     *
     * @throws ResolutionException
     */
    @Test
    public void enabledMirror() throws ResolutionException {
        File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadSettings("target/settings/profiles/settings-mirror.xml").disableMavenCentral()
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAsFiles(new StrictFilter());

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar",
            files[0].getName());
    }

}
