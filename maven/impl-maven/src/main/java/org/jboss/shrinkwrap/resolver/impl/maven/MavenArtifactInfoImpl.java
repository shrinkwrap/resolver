/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactProperties;
import org.eclipse.aether.graph.DependencyNode;
import org.jboss.shrinkwrap.resolver.api.maven.MavenArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;

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
    protected final boolean optional;

    protected final MavenArtifactInfo[] dependencies;

    protected MavenArtifactInfoImpl(final MavenCoordinate mavenCoordinate, final String resolvedVersion,
        final boolean snapshotVersion, final String extension, final ScopeType scopeType,
        final MavenArtifactInfo[] dependencies, final boolean optional) {
        this.mavenCoordinate = mavenCoordinate;
        this.resolvedVersion = resolvedVersion;
        this.snapshotVersion = snapshotVersion;
        this.extension = extension;
        this.scopeType = scopeType;
        this.dependencies = dependencies.clone();
        this.optional = optional;
    }

    protected MavenArtifactInfoImpl(final Artifact artifact, final ScopeType scopeType,
                                    final List<DependencyNode> children, boolean optional) {

        final PackagingType packaging = PackagingType.of(artifact.getProperty(ArtifactProperties.TYPE, artifact.getExtension()));
        final String classifier = artifact.getClassifier().length() == 0 ? packaging.getClassifier() : artifact.getClassifier();

        this.mavenCoordinate = MavenCoordinates.createCoordinate(artifact.getGroupId(), artifact.getArtifactId(),
            artifact.getBaseVersion(), packaging, classifier);
        this.resolvedVersion = artifact.getVersion();
        this.snapshotVersion = artifact.isSnapshot();
        this.extension = artifact.getExtension();
        this.dependencies = parseDependencies(children);
        this.scopeType = scopeType;
        this.optional = optional;
    }

    /**
     * Creates MavenArtifactInfo based on DependencyNode.
     *
     * @param dependencyNode
     *            dependencyNode
     * @return The new {@link MavenArtifactInfo> instance.
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
        final boolean optional = dependencyNode.getDependency().isOptional();
        return new MavenArtifactInfoImpl(artifact, scopeType, children, optional);
    }

    /**
     * Produces MavenArtifactInfo array from List of DependencyNode's.
     *
     * @param children A list of DependencyNode's
     * @return A {@link MavenArtifactInfo} array from {@link List} of DependencyNode's.
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
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact#getCoordinate()
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
    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return "MavenArtifactInfoImpl [mavenCoordinate=" + mavenCoordinate + ", resolvedVersion=" + resolvedVersion
            + ", snapshotVersion=" + snapshotVersion + ", extension=" + extension + ", scope=" + scopeType
            + ", dependencies=" + Arrays.toString(dependencies) + "]";
    }

}
