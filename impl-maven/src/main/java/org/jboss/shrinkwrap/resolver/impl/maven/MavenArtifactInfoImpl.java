package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.maven.MavenArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;

/**
 * Immutable implementation of {@link MavenArtifactInfo}.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class MavenArtifactInfoImpl implements MavenArtifactInfo {
    private static final Logger log = Logger.getLogger(MavenArtifactInfoImpl.class.getName());

    protected final MavenCoordinate mavenCoordinate;
    protected final String resolvedVersion;
    protected final boolean snapshotVersion;
    protected final String extension;
    protected final ScopeType scopeType;

    protected final MavenArtifactInfo[] dependencies;

    protected MavenArtifactInfoImpl(final MavenCoordinate mavenCoordinate, final String resolvedVersion,
        final boolean snapshotVersion, final String extension, final ScopeType scopeType,
        final MavenArtifactInfo[] dependencies) {
        this.mavenCoordinate = mavenCoordinate;
        this.resolvedVersion = resolvedVersion;
        this.snapshotVersion = snapshotVersion;
        this.extension = extension;
        this.scopeType = scopeType;
        this.dependencies = dependencies.clone();
    }

    protected MavenArtifactInfoImpl(final Artifact artifact, final ScopeType scopeType,
        final List<DependencyNode> children) {
        this.mavenCoordinate = MavenCoordinates.createCoordinate(artifact.getGroupId(), artifact.getArtifactId(),
            artifact.getBaseVersion(), PackagingType.of(artifact.getExtension()), artifact.getClassifier());
        this.resolvedVersion = artifact.getVersion();
        this.snapshotVersion = artifact.isSnapshot();
        this.extension = artifact.getExtension();
        this.dependencies = parseDependencies(children);
        this.scopeType = scopeType;
    }

    /**
     * Creates MavenArtifactInfo based on DependencyNode.
     *
     * @param dependencyNode
     *            dependencyNode
     * @return
     */
    static MavenArtifactInfo fromDependencyNode(final DependencyNode dependencyNode) {
        final Artifact artifact = dependencyNode.getDependency().getArtifact();
        final List<DependencyNode> children = dependencyNode.getChildren();

        // SHRINKRES-143 lets ignore invalid scope
        ScopeType scopeType = ScopeType.RUNTIME;
        try {
            scopeType = ScopeType.fromScopeType(dependencyNode.getDependency().getScope());
        } catch (IllegalArgumentException e) {
            // let scope be RUNTIME
            log.log(Level.WARNING, "Invalid scope {0} of retrieved dependency {1} will be replaced by <scope>runtime</scope>",
                    new Object[] { dependencyNode.getDependency().getScope(), dependencyNode.getDependency().getArtifact() });
        }

        return new MavenArtifactInfoImpl(artifact, scopeType, children);
    }

    /**
     * Produces MavenArtifactInfo array from List of DependencyNode's.
     *
     * @param children
     * @return
     */
    protected MavenArtifactInfo[] parseDependencies(final List<DependencyNode> children) {
        final MavenArtifactInfo[] dependecies = new MavenArtifactInfo[children.size()];
        int i = 0;
        for (final DependencyNode child : children) {
            dependecies[i++] = MavenArtifactInfoImpl.fromDependencyNode(child);
        }
        return dependecies;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifactImpl#getCoordinate()
     */
    @Override
    public MavenCoordinate getCoordinate() {
        return mavenCoordinate;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact#getResolvedVersion()
     */
    @Override
    public String getResolvedVersion() {
        return resolvedVersion;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact#isSnapshotVersion()
     */
    @Override
    public boolean isSnapshotVersion() {
        return snapshotVersion;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact#getExtension()
     */
    @Override
    public String getExtension() {
        return extension;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact#getDependencies()
     */
    @Override
    public MavenArtifactInfo[] getDependencies() {
        return dependencies;
    }

    @Override
    public ScopeType getScope() {
        return scopeType;
    }

    @Override
    public String toString() {
        return "MavenArtifactInfoImpl [mavenCoordinate=" + mavenCoordinate + ", resolvedVersion=" + resolvedVersion
            + ", snapshotVersion=" + snapshotVersion + ", extension=" + extension + ", scope=" + scopeType
            + ", dependencies=" + Arrays.toString(dependencies) + "]";
    }

}
