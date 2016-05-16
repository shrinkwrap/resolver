package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import static org.hamcrest.CoreMatchers.hasItem;

import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests as(ResolvedArtifactInfo) and asSingle(ResolvedArtifactInfo) methods.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class AsMavenCoordinateTestCase {

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
    public void asMavenCoordinates() {
        // given
        final String artifactCanonicalFormA = "org.jboss.shrinkwrap.test:test-parent:pom:1.0.0";

        // when
        final List<MavenCoordinate> coordinates = Maven.resolver().resolve(artifactCanonicalFormA)
                .withTransitivity().asList(MavenCoordinate.class);

        Assert.assertThat(coordinates,
                hasItem(MavenCoordinates.createCoordinate("org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0")));
    }
}
