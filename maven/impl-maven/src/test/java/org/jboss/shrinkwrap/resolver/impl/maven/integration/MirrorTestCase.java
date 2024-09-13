package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests mirror setting in Maven
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MirrorTestCase {

    /**
     * Tests a resolution of an artifact a repository specified in settings.xml within activeProfiles mirrored
     *
     */
    @Test
    void enabledMirror() {
        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings-mirror.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().asSingle(File.class);

        Assertions.assertEquals("test-deps-c-1.0.0.jar", file.getName(), "The file is packaged as test-deps-c-1.0.0.jar");
    }

}
