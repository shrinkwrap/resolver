package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.io.InputStream;

import org.jboss.shrinkwrap.resolver.api.formatprocessor.FileFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.InputStreamFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.sonatype.aether.artifact.Artifact;

/**
 * Immutable implementation of {@link ResolvedArtifactInfo}.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class ResolvedArtifactInfoImpl implements ResolvedArtifactInfo {

    private final MavenCoordinate mavenCoordinate;
    private final String resolvedVersion;
    private final boolean snapshotVersion;
    private final String extension;
    private final File file;

    private ResolvedArtifactInfoImpl(final MavenCoordinate mavenCoordinate, final String resolvedVersion,
        final boolean snapshotVersion, final String extension, final File file) {
        this.mavenCoordinate = mavenCoordinate;
        this.resolvedVersion = resolvedVersion;
        this.snapshotVersion = snapshotVersion;
        this.extension = extension;
        this.file = file;
    }

    /**
     * Creates ResolvedArtifactInfo based on Artifact.
     *
     * @param artifact
     *            artifact
     * @param file
     *            file contained in artifact
     * @return
     */
    static ResolvedArtifactInfo fromArtifact(final Artifact artifact, final File file) {
        final MavenCoordinate mavenCoordinate = MavenCoordinates.createCoordinate(artifact.getGroupId(),
            artifact.getArtifactId(), artifact.getBaseVersion(),
            PackagingType.fromPackagingType(artifact.getExtension()), artifact.getClassifier());

        return new ResolvedArtifactInfoImpl(mavenCoordinate, artifact.getVersion(), artifact.isSnapshot(),
            artifact.getExtension(), file);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfoImpl#getCoordinate()
     */
    @Override
    public MavenCoordinate getCoordinate() {
        return mavenCoordinate;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo#getResolvedVersion()
     */
    @Override
    public String getResolvedVersion() {
        return resolvedVersion;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo#isSnapshotVersion()
     */
    @Override
    public boolean isSnapshotVersion() {
        return snapshotVersion;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo#getExtension()
     */
    @Override
    public String getExtension() {
        return extension;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo#getArtifact(FormatProcessor)
     */
    @Override
    public <FORMATTERTYPE extends FormatProcessor<RETURNTYPE>, RETURNTYPE> RETURNTYPE getArtifact(
        FORMATTERTYPE formatter) {
        return formatter.process(getArtifact(File.class));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo#getArtifact(Class)
     */
    @Override
    public File getArtifact(Class<File> clazz) {
        return FileFormatProcessor.INSTANCE.process(file);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo#getArtifact(Class)
     */
    @Override
    public InputStream getArtifact(Class<InputStream> clazz) {
        return InputStreamFormatProcessor.INSTANCE.process(file);
    }

}
