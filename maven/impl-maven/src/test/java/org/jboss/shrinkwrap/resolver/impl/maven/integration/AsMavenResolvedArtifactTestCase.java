package org.jboss.shrinkwrap.resolver.impl.maven.integration;

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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests as(ResolvedArtifactInfo) and asSingle(ResolvedArtifactInfo) methods.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
class AsMavenResolvedArtifactTestCase {

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
     * Tests asResolvedArtifact().
     */
    @Test
    void asMavenResolvedArtifact() {
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

        Assertions.assertEquals("jar", resolvedArtifactInfos[0].getExtension());
        Assertions.assertEquals("1.0.0", resolvedArtifactInfos[0].getResolvedVersion());
        Assertions.assertFalse(resolvedArtifactInfos[0].isSnapshotVersion());
        Assertions.assertEquals("jar", resolvedArtifactInfos[0].getExtension());
        Assertions.assertFalse(resolvedArtifactInfos[0].isOptional());
        Assertions.assertEquals(originalCoordinateA, resolvedArtifactInfos[0].getCoordinate());

        new ValidationUtil("test-deps-b-1.0.0.jar").validate(resolvedArtifactInfos[1].as(File.class));

        Assertions.assertEquals("jar", resolvedArtifactInfos[1].getExtension());
        Assertions.assertEquals("1.0.0", resolvedArtifactInfos[1].getResolvedVersion());
        Assertions.assertFalse(resolvedArtifactInfos[1].isSnapshotVersion());
        Assertions.assertEquals("jar", resolvedArtifactInfos[1].getExtension());
        Assertions.assertFalse(resolvedArtifactInfos[1].isOptional());
        Assertions.assertEquals(originalCoordinateB, resolvedArtifactInfos[1].getCoordinate());
    }

    /**
     * Tests .asSingle(MavenResolvedArtifact.class);
     */
    @Test
    void asSingleResolvedArtifactInfo() {
        // given
        final String artifactCanonicalForm = "org.jboss.shrinkwrap.test:test-deps-a:jar:1.0.0";
        final MavenCoordinate originalCoordinate = MavenCoordinates.createCoordinate(artifactCanonicalForm);

        // when
        final MavenResolvedArtifact resolvedArtifact = Maven.resolver().loadPomFromFile("target/poms/test-parent.xml")
                .resolve(artifactCanonicalForm).withoutTransitivity().asSingleResolvedArtifact();

        // then
        new ValidationUtil("test-deps-a-1.0.0.jar").validate(resolvedArtifact.as(File.class));

        Assertions.assertEquals("jar", resolvedArtifact.getExtension());
        Assertions.assertEquals("1.0.0", resolvedArtifact.getResolvedVersion());
        Assertions.assertFalse(resolvedArtifact.isSnapshotVersion());
        Assertions.assertEquals("jar", resolvedArtifact.getExtension());
        Assertions.assertFalse(resolvedArtifact.isOptional());
        Assertions.assertEquals(originalCoordinate, resolvedArtifact.getCoordinate());
    }

    /**
     * Poms are not filtered out when using the withoutTransitivity().
     */
    @Test
    void emptyResolvedListFromPom() {
        // given
        final String artifactCanonicalForm = "org.jboss.shrinkwrap.test:test-parent:pom:1.0.0";

        final MavenDependency dependency = MavenDependencies.createDependency(artifactCanonicalForm, ScopeType.TEST,
                false);

        // when
        final MavenResolvedArtifact[] resolvedArtifactInfos = Maven.resolver()
                .loadPomFromFile("target/poms/test-parent.xml").addDependencies(dependency).resolve().withoutTransitivity()
                .asResolvedArtifact();

        // then
        Assertions.assertEquals(1, resolvedArtifactInfos.length, "resolved artifact infos list should be empty for pom dependency");
    }

    /**
     * Tests getDependencies of resolved artifact.
     */
    @Test
    void resolvedArtifactInfoDependencies() {
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
        Assertions.assertEquals(1, resolvedArtifactInfos.length, "MavenResolvedArtifact list should have one element");

        final MavenResolvedArtifact resolvedArtifact = resolvedArtifactInfos[0];
        Assertions.assertEquals(2, resolvedArtifact.getDependencies().length, "Resolved artifact should have children");

        new ValidationUtil("test-dependency-1.0.0.jar").validate(resolvedArtifact.as(File.class));

        Assertions.assertEquals("jar", resolvedArtifact.getExtension());
        Assertions.assertEquals("1.0.0", resolvedArtifact.getResolvedVersion());
        Assertions.assertFalse(resolvedArtifact.isSnapshotVersion());
        Assertions.assertEquals("jar", resolvedArtifact.getExtension());
        Assertions.assertFalse(resolvedArtifact.isOptional());
        Assertions.assertEquals(originalCoordinate, resolvedArtifact.getCoordinate());

        final MavenArtifactInfo child1 = resolvedArtifact.getDependencies()[0];
        Assertions.assertEquals("jar", child1.getExtension());
        Assertions.assertEquals("1.0.0", child1.getResolvedVersion());
        Assertions.assertFalse(child1.isSnapshotVersion());
        Assertions.assertEquals("jar", child1.getExtension());
        Assertions.assertFalse(child1.isOptional());
        Assertions.assertEquals(child1Coordinate, child1.getCoordinate());
        Assertions.assertEquals(ScopeType.COMPILE, child1.getScope());

        final MavenArtifactInfo child2 = resolvedArtifact.getDependencies()[1];
        Assertions.assertEquals("jar", child2.getExtension());
        Assertions.assertEquals("1.0.0", child2.getResolvedVersion());
        Assertions.assertFalse(child2.isSnapshotVersion());
        Assertions.assertEquals("jar", child2.getExtension());
        Assertions.assertFalse(child2.isOptional());
        Assertions.assertEquals(child2Coordinate, child2.getCoordinate());
        Assertions.assertEquals(ScopeType.RUNTIME, child2.getScope());
    }

    /**
     * Tests getDependencies of with optional dependency and that this flag is preserved
     */
    @Test
    void resolvedArtifactOptionalDependencies() {
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
        Assertions.assertEquals(2, resolvedArtifactInfos.length, "MavenResolvedArtifact list should have two elements");

        final MavenResolvedArtifact resolvedArtifact = resolvedArtifactInfos[0];
        Assertions.assertEquals(1, resolvedArtifact.getDependencies().length, "Resolved artifact should have one child");

        new ValidationUtil("test-deps-optional-1.0.0.jar").validate(resolvedArtifact.as(File.class));

        Assertions.assertEquals("jar", resolvedArtifact.getExtension());
        Assertions.assertEquals("1.0.0", resolvedArtifact.getResolvedVersion());
        Assertions.assertFalse(resolvedArtifact.isSnapshotVersion());
        Assertions.assertEquals("jar", resolvedArtifact.getExtension());
        Assertions.assertFalse(resolvedArtifact.isOptional());
        Assertions.assertEquals(originalCoordinate, resolvedArtifact.getCoordinate());

        final MavenArtifactInfo child1 = resolvedArtifact.getDependencies()[0];
        Assertions.assertEquals("jar", child1.getExtension());
        Assertions.assertEquals("1.0.0", child1.getResolvedVersion());
        Assertions.assertFalse(child1.isSnapshotVersion());
        Assertions.assertEquals("jar", child1.getExtension());
        Assertions.assertTrue(child1.isOptional());
        Assertions.assertEquals(child1Coordinate, child1.getCoordinate());
        Assertions.assertEquals(ScopeType.COMPILE, child1.getScope());
    }
}
