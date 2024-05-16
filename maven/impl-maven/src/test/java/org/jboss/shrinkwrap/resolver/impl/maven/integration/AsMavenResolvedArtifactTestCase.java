package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveExclusionPolicy;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests as(ResolvedArtifactInfo) and asSingle(ResolvedArtifactInfo) methods.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class AsMavenResolvedArtifactTestCase {

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
     * Tests asResolvedArtifact().
     */
    @Test
    public void asMavenResolvedArtifact() {
        // given
        final String artifactCanonicalFormA = "org.jboss.shrinkwrap.test:test-deps-a:jar:1.0.0";
        final String artifactCanonicalFormB = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";

        final MavenDependency dependencyA = MavenDependencies.createDependency(artifactCanonicalFormA, ScopeType.TEST,
                false);
        final MavenDependency dependencyB = MavenDependencies.createDependency(artifactCanonicalFormB, ScopeType.TEST,
                false);

        final MavenCoordinate originalCoordinateA = MavenCoordinates.createCoordinate(artifactCanonicalFormA);
        final MavenCoordinate originalCoordinateB = MavenCoordinates.createCoordinate(artifactCanonicalFormB);

        // when
        final MavenResolvedArtifact[] resolvedArtifactInfos = Maven.resolver().addDependencies(dependencyA, dependencyB)
                .resolve()
                .withoutTransitivity().asResolvedArtifact();

        // then
        new ValidationUtil("test-deps-a-1.0.0.jar").validate(resolvedArtifactInfos[0].as(File.class));

        assertEquals("jar", resolvedArtifactInfos[0].getExtension());
        assertEquals("1.0.0", resolvedArtifactInfos[0].getResolvedVersion());
        assertEquals(false, resolvedArtifactInfos[0].isSnapshotVersion());
        assertEquals("jar", resolvedArtifactInfos[0].getExtension());
        assertEquals(false, resolvedArtifactInfos[0].isOptional());
        assertEquals(originalCoordinateA, resolvedArtifactInfos[0].getCoordinate());

        new ValidationUtil("test-deps-b-1.0.0.jar").validate(resolvedArtifactInfos[1].as(File.class));

        assertEquals("jar", resolvedArtifactInfos[1].getExtension());
        assertEquals("1.0.0", resolvedArtifactInfos[1].getResolvedVersion());
        assertEquals(false, resolvedArtifactInfos[1].isSnapshotVersion());
        assertEquals("jar", resolvedArtifactInfos[1].getExtension());
        assertEquals(false, resolvedArtifactInfos[1].isOptional());
        assertEquals(originalCoordinateB, resolvedArtifactInfos[1].getCoordinate());
    }

    /**
     * Tests .asSingle(MavenResolvedArtifact.class);
     */
    @Test
    public void asSingleResolvedArtifactInfo() {
        // given
        final String artifactCanonicalForm = "org.jboss.shrinkwrap.test:test-deps-a:jar:1.0.0";
        final MavenCoordinate originalCoordinate = MavenCoordinates.createCoordinate(artifactCanonicalForm);

        // when
        final MavenResolvedArtifact resolvedArtifact = Maven.resolver().loadPomFromFile("target/poms/test-parent.xml")
                .resolve(artifactCanonicalForm).withoutTransitivity().asSingleResolvedArtifact();

        // then
        new ValidationUtil("test-deps-a-1.0.0.jar").validate(resolvedArtifact.as(File.class));

        assertEquals("jar", resolvedArtifact.getExtension());
        assertEquals("1.0.0", resolvedArtifact.getResolvedVersion());
        assertEquals(false, resolvedArtifact.isSnapshotVersion());
        assertEquals("jar", resolvedArtifact.getExtension());
        assertEquals(false, resolvedArtifact.isOptional());
        assertEquals(originalCoordinate, resolvedArtifact.getCoordinate());
    }

    /**
     * Poms are not filtered out when using the withoutTransitivity().
     */
    @Test
    public void emptyResolvedListFromPom() {
        // given
        final String artifactCanonicalForm = "org.jboss.shrinkwrap.test:test-parent:pom:1.0.0";

        final MavenDependency dependency = MavenDependencies.createDependency(artifactCanonicalForm, ScopeType.TEST,
                false);

        // when
        final MavenResolvedArtifact[] resolvedArtifactInfos = Maven.resolver()
                .loadPomFromFile("target/poms/test-parent.xml").addDependencies(dependency).resolve().withoutTransitivity()
                .asResolvedArtifact();

        // then
        assertEquals("resolved artifact infos list should be empty for pom dependency", 1, resolvedArtifactInfos.length);
    }

    /**
     * Tests getDependencies of resolved artifact.
     */
    @Test
    public void resolvedArtifactInfoDependencies() {
        // given
        final String artifactCanonicalForm = "org.jboss.shrinkwrap.test:test-dependency:jar:1.0.0";
        final String child1CanonicalForm = "org.jboss.shrinkwrap.test:test-deps-a:jar:1.0.0";
        final String child2CanonicalForm = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final MavenCoordinate originalCoordinate = MavenCoordinates.createCoordinate(artifactCanonicalForm);
        final MavenCoordinate child1Coordinate = MavenCoordinates.createCoordinate(child1CanonicalForm);
        final MavenCoordinate child2Coordinate = MavenCoordinates.createCoordinate(child2CanonicalForm);

        final MavenDependency dependency = MavenDependencies.createDependency(artifactCanonicalForm, ScopeType.TEST,
                false);

        // when
        final MavenResolvedArtifact[] resolvedArtifactInfos = Maven.resolver()
                .loadPomFromFile("target/poms/test-dependency.xml").addDependencies(dependency).resolve()
                .withoutTransitivity().asResolvedArtifact();

        // then
        assertEquals("MavenResolvedArtifact list should have one element", 1, resolvedArtifactInfos.length);

        final MavenResolvedArtifact resolvedArtifact = resolvedArtifactInfos[0];
        assertEquals("Resolved artifact should have children", 2, resolvedArtifact.getDependencies().length);

        new ValidationUtil("test-dependency-1.0.0.jar").validate(resolvedArtifact.as(File.class));

        assertEquals("jar", resolvedArtifact.getExtension());
        assertEquals("1.0.0", resolvedArtifact.getResolvedVersion());
        assertEquals(false, resolvedArtifact.isSnapshotVersion());
        assertEquals("jar", resolvedArtifact.getExtension());
        assertEquals(false, resolvedArtifact.isOptional());
        assertEquals(originalCoordinate, resolvedArtifact.getCoordinate());

        final MavenArtifactInfo child1 = resolvedArtifact.getDependencies()[0];
        assertEquals("jar", child1.getExtension());
        assertEquals("1.0.0", child1.getResolvedVersion());
        assertEquals(false, child1.isSnapshotVersion());
        assertEquals("jar", child1.getExtension());
        assertEquals(false, child1.isOptional());
        assertEquals(child1Coordinate, child1.getCoordinate());
        assertEquals(ScopeType.COMPILE, child1.getScope());

        final MavenArtifactInfo child2 = resolvedArtifact.getDependencies()[1];
        assertEquals("jar", child2.getExtension());
        assertEquals("1.0.0", child2.getResolvedVersion());
        assertEquals(false, child2.isSnapshotVersion());
        assertEquals("jar", child2.getExtension());
        assertEquals(false, child2.isOptional());
        assertEquals(child2Coordinate, child2.getCoordinate());
        assertEquals(ScopeType.RUNTIME, child2.getScope());
    }

    /**
     * Tests getDependencies of with optional dependency and that this flag is preserved
     */
    @Test
    public void resolvedArtifactOptionalDependencies() {
        // given
        final String artifactCanonicalForm = "org.jboss.shrinkwrap.test:test-deps-optional:jar:1.0.0";
        final String child1CanonicalForm = "org.jboss.shrinkwrap.test:test-managed-dependency:jar:1.0.0";
        final MavenCoordinate originalCoordinate = MavenCoordinates.createCoordinate(artifactCanonicalForm);
        final MavenCoordinate child1Coordinate = MavenCoordinates.createCoordinate(child1CanonicalForm);

        final MavenDependency dependency = MavenDependencies.createDependency(artifactCanonicalForm, ScopeType.COMPILE,
                false);

        // when
        final MavenResolvedArtifact[] resolvedArtifactInfos = Maven.resolver()
                .loadPomFromFile("target/poms/test-dependency.xml").addDependencies(dependency).resolve()
                .using(new MavenResolutionStrategy() {

                    @Override
                    public TransitiveExclusionPolicy getTransitiveExclusionPolicy() {
                        return new TransitiveExclusionPolicy() {

                            @Override
                            public ScopeType[] getFilteredScopes() {
                                return new ScopeType[] { ScopeType.PROVIDED, ScopeType.TEST };
                            }

                            @Override
                            public boolean allowOptional() {
                                return true;
                            }
                        };
                    }

                    @Override
                    public MavenResolutionFilter[] getResolutionFilters() {
                        return new MavenResolutionFilter[] { AcceptAllFilter.INSTANCE };
                    }
                }).asResolvedArtifact();

        // then
        assertEquals("MavenResolvedArtifact list should have two elements", 2, resolvedArtifactInfos.length);

        final MavenResolvedArtifact resolvedArtifact = resolvedArtifactInfos[0];
        assertEquals("Resolved artifact should have one child", 1, resolvedArtifact.getDependencies().length);

        new ValidationUtil("test-deps-optional-1.0.0.jar").validate(resolvedArtifact.as(File.class));

        assertEquals("jar", resolvedArtifact.getExtension());
        assertEquals("1.0.0", resolvedArtifact.getResolvedVersion());
        assertEquals(false, resolvedArtifact.isSnapshotVersion());
        assertEquals("jar", resolvedArtifact.getExtension());
        assertEquals(false, resolvedArtifact.isOptional());
        assertEquals(originalCoordinate, resolvedArtifact.getCoordinate());

        final MavenArtifactInfo child1 = resolvedArtifact.getDependencies()[0];
        assertEquals("jar", child1.getExtension());
        assertEquals("1.0.0", child1.getResolvedVersion());
        assertEquals(false, child1.isSnapshotVersion());
        assertEquals("jar", child1.getExtension());
        assertEquals(true, child1.isOptional());
        assertEquals(child1Coordinate, child1.getCoordinate());
        assertEquals(ScopeType.COMPILE, child1.getScope());
    }
}
