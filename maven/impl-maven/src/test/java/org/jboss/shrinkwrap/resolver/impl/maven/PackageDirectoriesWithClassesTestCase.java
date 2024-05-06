package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactProperties;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * This test case simulates behavior of an IDE - in IDE an artifact, that is also another module loaded in the IDE,
 * is not fetched from local/remote repository, but is referenced to the location of the module's directory.
 * The module's pom.xml file is taken as the artifact's pom and the subdirectories containing the compiled classes
 * are packaged into a zip archive. This zip archive is then returned as a resulting artifact file.
 * <p>
 * NOTE: this testcase is in the {@code util} package because of visibility of
 * {@link MavenResolvedArtifactImpl#fromArtifactResult(ArtifactResult)} method
 *
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 * @author <a href="mailto:olivts@free.fr">Olivier Spieser</a>
 *
 */
public class PackageDirectoriesWithClassesTestCase {

    /**
     * Test zip archive creation from directory located in. Check if directory entries are added to the archive.
     */
    @Test
    public void packageDirectoriesWithClasses() throws IOException {
        File artifactFile = new File(
            System.getProperty("user.dir") + "/target/repository/org/jboss/shrinkwrap/test/test-pom/1.0.0/pom.xml");

        Artifact testPomArtifactMock = Mockito.mock(Artifact.class);
        Mockito.when(testPomArtifactMock.getGroupId()).thenReturn("org.jboss.shrinkwrap.test");
        Mockito.when(testPomArtifactMock.getArtifactId()).thenReturn("test-pom");
        Mockito.when(testPomArtifactMock.getExtension()).thenReturn("xml");
        Mockito.when(testPomArtifactMock.getClassifier()).thenReturn("");
        Mockito.when(testPomArtifactMock.getVersion()).thenReturn("1.0.0");
        Mockito.when(testPomArtifactMock.getFile()).thenReturn(artifactFile);
        Mockito.when(testPomArtifactMock.getProperty(ArtifactProperties.TYPE, testPomArtifactMock.getExtension()))
            .thenReturn("pom");

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setDependencyNode(new DefaultDependencyNode(new Dependency(testPomArtifactMock, "test")));
        ArtifactResult mockedArtResult = new ArtifactResult(artifactRequest);
        mockedArtResult.setArtifact(testPomArtifactMock);

        MavenResolvedArtifact mavenResolvedArtifact = MavenResolvedArtifactImpl.fromArtifactResult(mockedArtResult);
        ZipFile outputZipFile = new ZipFile(mavenResolvedArtifact.asFile());

        //Check if existing files are in zip.
        Assert.assertNotNull(outputZipFile.getEntry("a/a.file"));

        //Check if directories are not separately contained
        Assert.assertNull(outputZipFile.getEntry("b/c" + File.separator));

        //Check if non existing items are null !
        Assert.assertNull(outputZipFile.getEntry("a/non-exist" + File.separator));

    }

    /**
     * Test special logic when resolving a war dependency.
     */
    @Test
    public void packageWar() throws IOException {
        File artifactFile = new File(
                System.getProperty("user.dir") + "/target/repository/org/jboss/shrinkwrap/test/test-war-with-resources/1.0.0/pom.xml");

        Artifact testPomArtifactMock = Mockito.mock(Artifact.class);
        Mockito.when(testPomArtifactMock.getGroupId()).thenReturn("org.jboss.shrinkwrap.test");
        Mockito.when(testPomArtifactMock.getArtifactId()).thenReturn("test-war");
        Mockito.when(testPomArtifactMock.getExtension()).thenReturn("war");
        Mockito.when(testPomArtifactMock.getClassifier()).thenReturn("");
        Mockito.when(testPomArtifactMock.getVersion()).thenReturn("1.0.0");
        Mockito.when(testPomArtifactMock.getFile()).thenReturn(artifactFile);
        Mockito.when(testPomArtifactMock.getProperty(eq(ArtifactProperties.TYPE), any())).thenReturn("war");

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setDependencyNode(new DefaultDependencyNode(new Dependency(testPomArtifactMock, "compile")));
        ArtifactResult mockedArtResult = new ArtifactResult(artifactRequest);
        mockedArtResult.setArtifact(testPomArtifactMock);

        MavenResolvedArtifact mavenResolvedArtifact = MavenResolvedArtifactImpl.fromArtifactResult(mockedArtResult);
        ZipFile outputZipFile = new ZipFile(mavenResolvedArtifact.asFile());

        //Check if the included files were taken from the "target/Artifact.artifactId-Artifact.version" directory
        Assert.assertNotNull(outputZipFile.getEntry("special/a.file"));

        //Check if the default "target/classes" directory was not included
        Assert.assertNull(outputZipFile.getEntry("a/a.file"));

    }
}