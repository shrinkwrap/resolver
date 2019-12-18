/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenRepositorySystem;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;

/**
 * Resolves an artifact even from remote repository during resolution of the model.
 *
 * The repositories are added to the resolution chain as found during processing of the POM file. Repository is added
 * only if there is no other repository with same id already defined.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class MavenModelResolver implements ModelResolver {

    private final List<RemoteRepository> repositories;
    private final Set<String> repositoryIds;

    private final MavenRepositorySystem system;
    private final RepositorySystemSession session;

    /**
     * Creates a new Maven repository resolver. This resolver uses service available to Maven to create an artifact
     * resolution chain
     *
     * @param system
     *        the Maven based implementation of the {@link RepositorySystem}
     * @param session
     *        the current Maven execution session
     * @param remoteRepositories
     *        the list of available Maven repositories
     */
    public MavenModelResolver(MavenRepositorySystem system, RepositorySystemSession session,
        List<RemoteRepository> remoteRepositories) {
        this.system = system;
        this.session = session;

        // RemoteRepository is mutable
        this.repositories = new ArrayList<RemoteRepository>(remoteRepositories.size());
        for (final RemoteRepository remoteRepository : remoteRepositories) {
            this.repositories.add(new RemoteRepository.Builder(remoteRepository).build());
        }

        this.repositoryIds = new HashSet<String>(repositories.size());

        for (final RemoteRepository repository : repositories) {
            repositoryIds.add(repository.getId());
        }
    }

    /**
     * Cloning constructor
     *
     * @param origin
     */
    private MavenModelResolver(MavenModelResolver origin) {
        this(origin.system, origin.session, origin.repositories);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.maven.model.resolution.ModelResolver#addRepository(org.apache.maven.model.Repository)
     */
    @Override
    public void addRepository(Repository repository) throws InvalidRepositoryException {
        addRepository(repository, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.maven.model.resolution.ModelResolver#newCopy()
     */
    @Override
    public ModelResolver newCopy() {
        return new MavenModelResolver(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.maven.model.resolution.ModelResolver#resolveModel(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ModelSource resolveModel(String groupId, String artifactId, String version)
        throws UnresolvableModelException {
        Artifact pomArtifact = new DefaultArtifact(groupId, artifactId, "", "pom", version);
        try {
            final ArtifactRequest request = new ArtifactRequest(pomArtifact, repositories, null);
            pomArtifact = system.resolveArtifact(session, request).getArtifact();

        } catch (ArtifactResolutionException e) {
            throw new UnresolvableModelException("Failed to resolve POM for " + groupId + ":" + artifactId + ":"
                + version + " due to " + e.getMessage(), groupId, artifactId, version, e);
        }

        final File pomFile = pomArtifact.getFile();

        return new FileModelSource(pomFile);

    }

    @Override
    public ModelSource resolveModel(Parent parent) throws UnresolvableModelException {

        Artifact artifact = new DefaultArtifact(parent.getGroupId(), parent.getArtifactId(), "", "pom",
            parent.getVersion());

        VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, repositories, null);

        try {
            VersionRangeResult versionRangeResult =
                system.resolveVersionRange(session, versionRangeRequest);

            if (versionRangeResult.getHighestVersion() == null) {
                throw new UnresolvableModelException(
                    String.format("No versions matched the requested parent version range '%s'",
                        parent.getVersion()),
                    parent.getGroupId(), parent.getArtifactId(), parent.getVersion());

            }

            if (versionRangeResult.getVersionConstraint() != null
                && versionRangeResult.getVersionConstraint().getRange() != null
                && versionRangeResult.getVersionConstraint().getRange().getUpperBound() == null) {
                throw new UnresolvableModelException(
                    String.format("The requested parent version range '%s' does not specify an upper bound",
                        parent.getVersion()),
                    parent.getGroupId(), parent.getArtifactId(), parent.getVersion());

            }

            parent.setVersion(versionRangeResult.getHighestVersion().toString());
        } catch (VersionRangeResolutionException e) {
            throw new UnresolvableModelException(e.getMessage(), parent.getGroupId(), parent.getArtifactId(),
                parent.getVersion(), e);
        }

        return resolveModel(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
    }

    @Override
    public ModelSource resolveModel(Dependency dependency) throws UnresolvableModelException {
        try {
            final Artifact artifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), "",
                "pom", dependency.getVersion());

            final VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, repositories, null);

            final VersionRangeResult versionRangeResult =
                system.resolveVersionRange(session, versionRangeRequest);

            if (versionRangeResult.getHighestVersion() == null) {
                throw new UnresolvableModelException(
                    String.format("No versions matched the requested dependency version range '%s'",
                        dependency.getVersion()),
                    dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
            }

            if (versionRangeResult.getVersionConstraint() != null
                && versionRangeResult.getVersionConstraint().getRange() != null
                && versionRangeResult.getVersionConstraint().getRange().getUpperBound() == null) {
                throw new UnresolvableModelException(
                    String.format("The requested dependency version range '%s' does not specify an upper bound",
                        dependency.getVersion()),
                    dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
            }

            dependency.setVersion(versionRangeResult.getHighestVersion().toString());

            return resolveModel(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
        } catch (VersionRangeResolutionException e) {
            throw new UnresolvableModelException(e.getMessage(), dependency.getGroupId(), dependency.getArtifactId(),
                dependency.getVersion(), e);
        }
    }

    @Override
    public void addRepository(Repository repository, boolean replace) throws InvalidRepositoryException {

        if (session.isIgnoreArtifactDescriptorRepositories()) {
            return;
        }

        if (!repositoryIds.add(repository.getId())) {
            if (!replace) {
                return;
            }

            removeMatchingRepository(repositories, repository.getId());
        }

        repositories.add(MavenConverter.asRemoteRepository(repository));
    }

    private static void removeMatchingRepository(List<RemoteRepository> repositories, final String id) {
        List<RemoteRepository> matchingRepositoriesToRemove = new ArrayList<>();
        for (RemoteRepository remoteRepository : repositories) {
            if (remoteRepository.getId().equals(id)) {
                matchingRepositoriesToRemove.add(remoteRepository);
            }
        }
        repositories.removeAll(matchingRepositoriesToRemove);
    }
}
