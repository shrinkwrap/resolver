/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat Middleware LLC, and individual contributors
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

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtime;
import eu.maveniverse.maven.mima.context.Runtimes;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelProblem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RemoteRepository.Builder;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.VersionResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepository;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveExclusionPolicy;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.MavenModelResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.SettingsXmlProfileSelector;
import org.jboss.shrinkwrap.resolver.impl.maven.pom.ParsedPomFileImpl;

/**
 * Implementation of a {@link MavenWorkingSession}, encapsulating Maven/Aether backend
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class MavenWorkingSessionImpl extends ConfigurableMavenWorkingSessionImpl {

    private static final Logger log = Logger.getLogger(MavenWorkingSessionImpl.class.getName());

    /**
     * <code><dependencyManagement></code> metadata
     */
    private final Set<MavenDependency> dependencyManagement;
    /**
     * Dependencies for resolution during this session
     */
    private final List<MavenDependency> dependencies;
    /**
     * <code><dependencies></code> metadata
     */
    private final Set<MavenDependency> declaredDependencies;
    private Model model;

    private final List<RemoteRepository> remoteRepositories;

    private final List<RemoteRepository> additionalRemoteRepositories;

    private boolean useMavenCentralRepository = true;

    public MavenWorkingSessionImpl() {
        super();
        this.remoteRepositories = new ArrayList<>();
        this.additionalRemoteRepositories = new ArrayList<>();

        this.dependencies = new ArrayList<>();
        this.dependencyManagement = new LinkedHashSet<>();
        this.declaredDependencies = new LinkedHashSet<>();
    }

    @Override
    public Set<MavenDependency> getDependencyManagement() {
        return dependencyManagement;
    }

    @Override
    public List<MavenDependency> getDependenciesForResolution() {
        return dependencies;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession#getDeclaredDependencies()
     */
    @Override
    public Set<MavenDependency> getDeclaredDependencies() {
        return declaredDependencies;
    }

    @Override
    public MavenWorkingSession loadPomFromFile(File pomFile, String... profiles) throws InvalidConfigurationFileException {
        loadPomFromFile(pomFile, null, profiles);
        return this;
    }

    public MavenWorkingSession loadPomFromFile(File pomFile, Properties userProperties, String... profiles)
        throws InvalidConfigurationFileException {

        final DefaultModelBuildingRequest request = new DefaultModelBuildingRequest()
                .setSystemProperties(SecurityActions.getProperties()).setProfiles(this.getSettingsDefinedProfiles())
                .setPomFile(pomFile).setActiveProfileIds(SettingsXmlProfileSelector.explicitlyActivatedProfiles(profiles))
                .setInactiveProfileIds(SettingsXmlProfileSelector.explicitlyDisabledProfiles(profiles));

        if (userProperties != null){
            request.setUserProperties(userProperties);
        }

        ModelBuilder builder = new DefaultModelBuilderFactory().newInstance();
        ModelBuildingResult result;
        try {
            request.setModelResolver(new MavenModelResolver(getSystem(), getSession(), getRemoteRepositories()));
            result = builder.build(request);
        }
        // wrap exception message
        catch (ModelBuildingException e) {
            String pomPath = request.getPomFile().getAbsolutePath();
            StringBuilder sb = new StringBuilder("Found ").append(e.getProblems().size())
                    .append(" problems while building POM model from ").append(pomPath).append("\n");

            int counter = 1;
            for (ModelProblem problem : e.getProblems()) {
                sb.append(counter++).append("/ ").append(problem).append("\n");
            }

            throw new InvalidConfigurationFileException(sb.toString());
        }

        // get and update model
        Model model = result.getEffectiveModel();
        this.model = model;

        // update model repositories
        for (Repository repository : model.getRepositories()) {
            remoteRepositories.add(MavenConverter.asRemoteRepository(repository));
        }

        return this;
    }

    @Override
    public Collection<MavenResolvedArtifact> resolveDependencies(final MavenResolutionStrategy strategy)
            throws ResolutionException {

        final List<MavenDependency> depsForResolution = Collections.unmodifiableList(new ArrayList<>(
                this.getDependenciesForResolution()));
        final List<MavenDependency> depManagement = new ArrayList<>(this.getDependencyManagement());

        final List<RemoteRepository> repos = this.getRemoteRepositories();

        final CollectRequest request = new CollectRequest(MavenConverter.asDependencies(depsForResolution,
            getSession().getArtifactTypeRegistry()),
            MavenConverter.asDependencies(depManagement, getSession().getArtifactTypeRegistry()), repos);

        Collection<ArtifactResult> results;

        // Set the dependency selector used in resolving transitive dependencies based on our transitive exclusion
        // policy abstraction
        final Set<DependencySelector> dependencySelectors = new LinkedHashSet<>(3);
        final TransitiveExclusionPolicy transitiveExclusionPolicy = strategy.getTransitiveExclusionPolicy();
        final ScopeType[] filteredScopes = transitiveExclusionPolicy.getFilteredScopes();
        final int numFilteredScopes = filteredScopes.length;
        final String[] filteredScopeStrings = new String[numFilteredScopes];
        for (int i = 0; i < numFilteredScopes; i++) {
            filteredScopeStrings[i] = filteredScopes[i].toString();
        }
        if (numFilteredScopes > 0) {
            dependencySelectors.add(new ScopeDependencySelector(filteredScopeStrings));
        }
        if (!transitiveExclusionPolicy.allowOptional()) {
            dependencySelectors.add(new OptionalDependencySelector());
        }
        dependencySelectors.add(new ExclusionDependencySelector());
        final DependencySelector dependencySelector = new AndDependencySelector(dependencySelectors);
        getSession().setDependencySelector(dependencySelector);

        try {
            results = getSystem().resolveDependencies(getSession(), this, request,
                strategy.getResolutionFilters());
        } catch (DependencyResolutionException e) {
            throw wrapException(e);
        }

        final Collection<MavenResolvedArtifact> resolvedArtifacts = new ArrayList<>(results.size());

        for (final ArtifactResult result : results) {
            resolvedArtifacts.add(MavenResolvedArtifactImpl.fromArtifactResult(result));
        }

        // Clear dependencies to be resolved (for the next request); we've already sent this request
        this.getDependenciesForResolution().clear();

        // apply post filtering
        return PostResolutionFilterApplicator.postFilter(resolvedArtifacts);
    }

    @Override
    public MavenVersionRangeResult resolveVersionRange(final MavenCoordinate coordinate) throws VersionResolutionException {
        final Artifact artifact = MavenConverter.asArtifact(coordinate, getSession().getArtifactTypeRegistry());
        final VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, this.getRemoteRepositories(), null);

        try {
            final VersionRangeResult versionRangeResult = getSystem().resolveVersionRange(getSession(), versionRangeRequest);
            if (!versionRangeResult.getVersions().isEmpty()) {
                return new MavenVersionRangeResultImpl(artifact, versionRangeResult);
            }
            final List<Exception> exceptions = versionRangeResult.getExceptions();
            if (exceptions.isEmpty()) {
                return new MavenVersionRangeResultImpl(artifact, versionRangeResult);
            } else {
                StringBuilder builder = new StringBuilder("Version range request failed with ")
                        .append(exceptions.size()).append(" exceptions.").append("\n");

                int counter = 1;
                for (final Exception exception : exceptions) {
                    log.log(Level.SEVERE, "Version range request failed", exception);
                    builder.append(counter++).append("/ ").append(exception.getLocalizedMessage()).append("\n");
                }
                throw new VersionResolutionException(builder.toString());
            }
        } catch (VersionRangeResolutionException vrre) {
            throw new VersionResolutionException("Version range request failed", vrre);
        }
    }

    @Override
    public ParsedPomFile getParsedPomFile() {
        return new ParsedPomFileImpl(model, getSession().getArtifactTypeRegistry());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession#disableMavenCentral()
     */
    @Override
    public void disableMavenCentral() {
        log.log(Level.FINEST, "Disabling Maven Central");
        this.useMavenCentralRepository = false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession#addRemoteRepo(MavenRemoteRepository)
     */
    @Override
    public void addRemoteRepo(MavenRemoteRepository repository) {
        Builder builder = new Builder(repository.getId(), repository.getType(), repository.getUrl());
        builder.setPolicy(new RepositoryPolicy(true, repository.getUpdatePolicy() == null ? null : repository
                .getUpdatePolicy().apiValue(), repository.getChecksumPolicy() == null ? null : repository
                .getChecksumPolicy().apiValue()));

        this.additionalRemoteRepositories.removeIf(r -> r.getId().equals(repository.getId()));
        this.additionalRemoteRepositories.add(builder.build());
    }

    // ------------------------------------------------------------------------
    // local implementation methods

    private List<RemoteRepository> getRemoteRepositories() throws IllegalStateException {
        // disable repositories if working offline
        if (isOffline()) {
            log.log(Level.FINE, "No remote repositories will be available, working in offline mode");
            return Collections.emptyList();
        }

        ContextOverrides.Builder contextOverridesBuilder = ContextOverrides.Builder.create();
        contextOverridesBuilder.withUserSettings(true);
        contextOverridesBuilder.withEffectiveSettings(getSettings());
        contextOverridesBuilder.appendRepositories(true);
        List<RemoteRepository> ctxrepo = new ArrayList<>();
        if (useMavenCentralRepository
                && remoteRepositories.stream().noneMatch(r -> ContextOverrides.CENTRAL.getId().equals(r.getId()))
                && additionalRemoteRepositories.stream().noneMatch(r -> ContextOverrides.CENTRAL.getId().equals(r.getId()))) {
            ctxrepo.add(ContextOverrides.CENTRAL);
        }
        this.remoteRepositories.forEach(r -> addReplace(ctxrepo, r));
        this.additionalRemoteRepositories.forEach(r -> addReplace(ctxrepo, r));

        contextOverridesBuilder.repositories(ctxrepo);
        Runtime runtime = Runtimes.INSTANCE.getRuntime();
        try (Context context = runtime.create(contextOverridesBuilder.build())) {
            ArrayList<RemoteRepository> result = new ArrayList<>(context.remoteRepositories());
            if (!useMavenCentralRepository) {
                result.removeIf(r -> ContextOverrides.CENTRAL.getId().equals(r.getId()));
            }
            if (log.isLoggable(Level.FINER)) {
                for (RemoteRepository repository : result) {
                    log.finer("Repository " + repository.getUrl() + " have been made available for artifact resolution");
                }
            }
            return new ArrayList<>(result);
        }
    }

    private void addReplace(List<RemoteRepository> remoteRepositories, RemoteRepository repository) {
        remoteRepositories.removeIf(r -> Objects.equals(repository.getId(), r.getId()));
        remoteRepositories.add(repository);
    }

    private List<Profile> getSettingsDefinedProfiles() {
        return MavenConverter.asProfiles(getSettings().getProfiles());
    }

    private static ResolutionException wrapException(DependencyResolutionException e) {
        Throwable cause = e;
        Throwable nextCause;
        while ((nextCause = cause.getCause()) != null) {
            cause = nextCause;
        }

        if (cause instanceof ArtifactResolutionException) {
            throw new NoResolvedResultException("Unable to get artifact from the repository due to: "
                    + e.getMessage() + ", caused by: " + cause.getMessage());
        } else if (cause instanceof DependencyCollectionException) {
            throw new NoResolvedResultException(
                    "Unable to collect dependency tree for given dependencies due to: "
                            + e.getMessage() + ", caused by: " + cause.getMessage(), e);
        }

        throw new NoResolvedResultException(
                "Unable to collect/resolve dependency tree for a resolution due to: "
                        + e.getMessage() + ", caused by: " + cause.getMessage(), e);

    }

}
