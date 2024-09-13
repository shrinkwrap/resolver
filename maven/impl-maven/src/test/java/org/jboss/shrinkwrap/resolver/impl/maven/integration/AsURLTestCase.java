package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests as(URL) and asSingle(ResolvedArtifactInfo) methods.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 */
class AsURLTestCase {

    @BeforeAll
    static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterAll
    static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    /**
     * Tests MavenCoordinate resolution
     */
    @Test
    void asURLs() throws Exception {
        // given
        final String artifactCanonicalFormA = "org.jboss.shrinkwrap.test:test-parent:pom:1.0.0";

        // when
        final List<URL> coordinates = Maven.resolver().resolve(artifactCanonicalFormA)
                .withTransitivity().asList(URL.class);

        URL target = new File(
                System.getProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION),
                "org/jboss/shrinkwrap/test/test-deps-i/1.0.0/test-deps-i-1.0.0.jar").toURI().toURL();

        Assertions.assertTrue(coordinates.contains(target),
                "Expected coordinates to contain the target URL.");
    }
}
