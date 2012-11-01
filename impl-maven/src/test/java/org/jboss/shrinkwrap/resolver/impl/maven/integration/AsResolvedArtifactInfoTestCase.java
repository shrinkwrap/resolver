package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests as(ResolvedArtifactInfo) and asSingle(ResolvedArtifactInfo) methods.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class AsResolvedArtifactInfoTestCase {

    @Test
    public void asResolvedArtifactInfo() {

        String artifactCanonicalFormA = "org.jboss.shrinkwrap.test:test-deps-a:jar:1.0.0";
        String artifactCanonicalFormB = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";

        final MavenDependency dependencyA = MavenDependencies.createDependency(artifactCanonicalFormA, ScopeType.TEST,
            false);
        final MavenDependency dependencyB = MavenDependencies.createDependency(artifactCanonicalFormB, ScopeType.TEST,
            false);

        MavenCoordinate originalCoordinateA = MavenCoordinates.createCoordinate(artifactCanonicalFormA);
        MavenCoordinate originalCoordinateB = MavenCoordinates.createCoordinate(artifactCanonicalFormB);

        ResolvedArtifactInfo[] resolvedArtifactInfos = Maven.resolver().loadPomFromFile("target/poms/test-child.xml")
            .addDependencies(dependencyA, dependencyB).resolve().withoutTransitivity().as(ResolvedArtifactInfo.class);

        new ValidationUtil("test-deps-a-1.0.0.jar").validate(resolvedArtifactInfos[0].getArtifact(File.class));

        Assert.assertEquals("jar", resolvedArtifactInfos[0].getExtension());
        Assert.assertEquals("1.0.0", resolvedArtifactInfos[0].getResolvedVersion());
        Assert.assertEquals(false, resolvedArtifactInfos[0].isSnapshotVersion());
        Assert.assertEquals("jar", resolvedArtifactInfos[0].getExtension());
        Assert.assertEquals(originalCoordinateA, resolvedArtifactInfos[0].getCoordinate());

        new ValidationUtil("test-deps-b-1.0.0.jar").validate(resolvedArtifactInfos[1].getArtifact(File.class));

        Assert.assertEquals("jar", resolvedArtifactInfos[1].getExtension());
        Assert.assertEquals("1.0.0", resolvedArtifactInfos[1].getResolvedVersion());
        Assert.assertEquals(false, resolvedArtifactInfos[1].isSnapshotVersion());
        Assert.assertEquals("jar", resolvedArtifactInfos[1].getExtension());
        Assert.assertEquals(originalCoordinateB, resolvedArtifactInfos[1].getCoordinate());
    }

    @Test
    public void asSingleResolvedArtifactInfo() {
        String artifactCanonicalForm = "org.jboss.shrinkwrap.test:test-deps-a:jar:1.0.0";
        MavenCoordinate originalCoordinate = MavenCoordinates.createCoordinate(artifactCanonicalForm);

        ResolvedArtifactInfo resolvedArtifactInfo = Maven.resolver().loadPomFromFile("target/poms/test-parent.xml")
            .resolve(artifactCanonicalForm).withoutTransitivity().asSingle(ResolvedArtifactInfo.class);

        new ValidationUtil("test-deps-a-1.0.0.jar").validate(resolvedArtifactInfo.getArtifact(File.class));

        Assert.assertEquals("jar", resolvedArtifactInfo.getExtension());
        Assert.assertEquals("1.0.0", resolvedArtifactInfo.getResolvedVersion());
        Assert.assertEquals(false, resolvedArtifactInfo.isSnapshotVersion());
        Assert.assertEquals("jar", resolvedArtifactInfo.getExtension());
        Assert.assertEquals(originalCoordinate, resolvedArtifactInfo.getCoordinate());
    }
}
