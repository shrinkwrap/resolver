package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests as(URL) and asSingle(ResolvedArtifactInfo) methods.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 */
public class AsURLTestCase {

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    /**
     * Tests MavenCoordinate resolution
     */
    @Test
    public void asURLs() throws Exception {
        // given
        final String artifactCanonicalFormA = "org.jboss.shrinkwrap.test:test-parent:pom:1.0.0";

        // when
        final List<URL> coordinates = Maven.resolver().resolve(artifactCanonicalFormA)
                .withTransitivity().asList(URL.class);

        URL target = new File(
                System.getProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION),
                "org/jboss/shrinkwrap/test/test-deps-i/1.0.0/test-deps-i-1.0.0.jar").toURI().toURL();

        assertThat(coordinates, hasItem(target));
    }
}
