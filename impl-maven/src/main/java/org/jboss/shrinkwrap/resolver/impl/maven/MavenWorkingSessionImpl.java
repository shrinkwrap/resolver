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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import org.apache.maven.model.profile.ProfileActivationContext;
import org.apache.maven.model.profile.ProfileSelector;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.repository.RemoteRepository;
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
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.util.repository.DefaultMirrorSelector;
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
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveExclusionPolicy;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenRepositorySystem;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.MavenModelResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.SettingsXmlProfileSelector;
import org.jboss.shrinkwrap.resolver.impl.maven.logging.LogModelProblemCollector;
import org.jboss.shrinkwrap.resolver.impl.maven.pom.ParsedPomFileImpl;

/**
 * Implementation of a {@link MavenWorkingSession}, encapsulating Maven/Aether backend
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class MavenWorkingSessionImpl implements MavenWorkingSession {

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

    private static final String MAVEN_CENTRAL_NAME = "central";
    // creates a link to Maven Central Repository
    private static final RemoteRepository MAVEN_CENTRAL = new RemoteRepository.Builder(MAVEN_CENTRAL_NAME, "default",
            "http://repo1.maven.org/maven2").build();

    private final MavenRepositorySystem system;
    private Settings settings;

    private DefaultRepositorySystemSession session;

    private Model model;

    private final List<RemoteRepository> remoteRepositories;

    private final List<RemoteRepository> additionalRemoteRepositories;

    private boolean useMavenCentralRepository = true;

    // make sure that programmatic call to offline method is always preserved
    private Boolean programmaticOffline;

    public MavenWorkingSessionImpl() {
        this.system = new MavenRepositorySystem();
        this.settings = new MavenSettingsBuilder().buildDefaultSettings();
        this.remoteRepositories = new ArrayList<RemoteRepository>();
        this.additionalRemoteRepositories = new ArrayList<RemoteRepository>();
        // get session to spare time
        this.session = system.getSession(settings);
        this.dependencies = new ArrayList<MavenDependency>();
        this.dependencyManagement = new HashSet<MavenDependency>();
        this.declaredDependencies = new HashSet<MavenDependency>();
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

        final DefaultModelBuildingRequest request = new DefaultModelBuildingRequest()
                .setSystemProperties(SecurityActions.getProperties()).setProfiles(this.getSettingsDefinedProfiles())
                .setPomFile(pomFile).setActiveProfileIds(SettingsXmlProfileSelector.explicitlyActivatedProfiles(profiles))
                .setInactiveProfileIds(SettingsXmlProfileSelector.explicitlyDisabledProfiles(profiles));

        ModelBuilder builder = new DefaultModelBuilderFactory().newInstance();
        ModelBuildingResult result;
        try {
            request.setModelResolver(new MavenModelResolver(system, session, getRemoteRepositories()));
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
    public MavenWorkingSession configureSettingsFromFile(File globalSettings, File userSettings)
            throws InvalidConfigurationFileException {

        SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
        if (globalSettings != null) {
            request.setGlobalSettingsFile(globalSettings);
        }
        if (userSettings != null) {
            request.setUserSettingsFile(userSettings);
        }
        request.setSystemProperties(SecurityActions.getProperties());

        MavenSettingsBuilder builder = new MavenSettingsBuilder();
        this.settings = builder.buildSettings(request);

        // ensure we keep offline(boolean) if previously set
        if (programmaticOffline != null) {
            this.settings.setOffline(programmaticOffline.booleanValue());
        }

        return regenerateSession();
    }

    @Override
    public Collection<MavenResolvedArtifact> resolveDependencies(final MavenResolutionStrategy strategy)
            throws ResolutionException {

        final List<MavenDependency> depsForResolution = Collections.unmodifiableList(new ArrayList<MavenDependency>(
                this.getDependenciesForResolution()));
        final List<MavenDependency> depManagement = new ArrayList<MavenDependency>(this.getDependencyManagement());

        final List<RemoteRepository> repos = this.getRemoteRepositories();

        final CollectRequest request = new CollectRequest(MavenConverter.asDependencies(depsForResolution),
                MavenConverter.asDependencies(depManagement), repos);

        Collection<ArtifactResult> results = Collections.emptyList();

        // Set the dependency selector used in resolving transitive dependencies based on our transitive exclusion
        // policy abstraction
        final Set<DependencySelector> dependencySelectors = new HashSet<DependencySelector>(3);
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
        session.setDependencySelector(dependencySelector);

        try {
            results = system.resolveDependencies(session, this, request,
                    strategy.getResolutionFilters());
        } catch (DependencyResolutionException e) {
            throw wrapException(e);
        }

        final Collection<MavenResolvedArtifact> resolvedArtifacts = new ArrayList<MavenResolvedArtifact>(results.size());

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
        final Artifact artifact = MavenConverter.asArtifact(coordinate);
        final VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, this.getRemoteRepositories(), null);

        try {
            final VersionRangeResult versionRangeResult = system.resolveVersionRange(session, versionRangeRequest);

            final List<Exception> exceptions = versionRangeResult.getExceptions();
            if (!exceptions.isEmpty()) {
                for (final Exception exception : exceptions) {
                    log.log(Level.SEVERE, "Version range request failed", exception);
                }
                throw new VersionResolutionException("Version range request failed", exceptions.get(0));
            }

            return new MavenVersionRangeResultImpl(artifact, versionRangeResult);
        } catch (VersionRangeResolutionException vrre) {
            throw new VersionResolutionException("Version range request failed", vrre);
        }
    }

    @Override
    public ParsedPomFile getParsedPomFile() {
        return new ParsedPomFileImpl(model, session.getArtifactTypeRegistry());
    }

    @Override
    public MavenWorkingSession regenerateSession() {
        this.session = system.getSession(settings);
        return this;
    }

    @Override
    public void setOffline(final boolean offline) {
        if (log.isLoggable(Level.FINER)) {
            log.finer("Set offline mode programatically to: " + offline);
        }

        this.programmaticOffline = new Boolean(offline);
        this.settings.setOffline(offline);
        regenerateSession();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession#disableClassPathWorkspaceReader()
     */
    @Override
    public void disableClassPathWorkspaceReader() {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Disabling ClassPath resolution");
        }
        ((DefaultRepositorySystemSession) session).setWorkspaceReader(null);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession#disableMavenCentral()
     */
    @Override
    public void disableMavenCentral() {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Disabling Maven Central");
        }
        this.useMavenCentralRepository = false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession#addMavenRemoteRepo()
     */
    @Override
    public void addMavenRemoteRepo(String name, String url, String layout) {
        boolean found = false;
        for (RemoteRepository r : this.additionalRemoteRepositories) {
            if (r.getId().equals(name)) {
                found = true;
                break;
            }
        }

        if (! found) {
            this.additionalRemoteRepositories.add(new RemoteRepository(name, layout, url));
        }
    }


    // ------------------------------------------------------------------------
    // local implementation methods

    private List<RemoteRepository> getRemoteRepositories() throws IllegalStateException {
        // disable repositories if working offline
        if (isOffline()) {
            log.log(Level.FINE, "No remote repositories will be available, working in offline mode");
            return Collections.emptyList();
        }

        Set<RemoteRepository> enhancedRepos = new LinkedHashSet<RemoteRepository>();
        ProfileSelector selector = new SettingsXmlProfileSelector();
        LogModelProblemCollector problems = new LogModelProblemCollector();
        List<Profile> activeProfiles = selector.getActiveProfiles(MavenConverter.asProfiles(settings.getProfiles()),
                new ProfileActivationContext() {

                    @Override
                    public Map<String, String> getUserProperties() {
                        return Collections.emptyMap();
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public Map<String, String> getSystemProperties() {
                        return new HashMap<String, String>((Map) SecurityActions.getProperties());
                    }

                    @Override
                    public File getProjectDirectory() {
                        return new File(SecurityActions.getProperty("user.dir"));
                    }

                    @Override
                    public Map<String, String> getProjectProperties() {
                        // TODO can we put here other values?
                        return Collections.emptyMap();
                    }

                    @Override
                    public List<String> getInactiveProfileIds() {
                        return Collections.emptyList();
                    }

                    @Override
                    public List<String> getActiveProfileIds() {
                        return settings.getActiveProfiles();
                    }
                }, problems);

        if (problems.hasSevereFailures()) {
            throw new IllegalStateException("Unable to get active profiles from Maven settings.");
        }

        for (Profile p : activeProfiles) {
            for (Repository repository : p.getRepositories()) {
                enhancedRepos.add(MavenConverter.asRemoteRepository(repository));
            }
        }

        // add repositories from model
        enhancedRepos.addAll(remoteRepositories);

        // add repositories explicitly given through the API
        enhancedRepos.addAll(additionalRemoteRepositories);

        // add maven central if selected
        if (useMavenCentralRepository) {
            enhancedRepos.add(MAVEN_CENTRAL);
        } else {
            RemoteRepository repoToRemove = null;
            // Attempt a remove

            for (final RemoteRepository repo : enhancedRepos) {
                // Because there are a lot of aliases for Maven Central, we have to approximate that anything named
                // "central" with URL containing "maven" is what we're looking to ban. For instance Central could be
                // http://repo.maven.apache.org/maven2 or http://repo1.maven.org/maven2
                final String repoUrl = repo.getUrl();
                if ((repoUrl.contains("maven.org") || repoUrl.contains("apache.org"))
                        && repo.getId().equalsIgnoreCase(MAVEN_CENTRAL_NAME)) {
                    repoToRemove = repo;
                }
            }
            // We have to search on URL criteria, because .equals on RemoteRepository is too strict for us to call a
            // simple remove operation on the enhancedRepos Collection
            if (repoToRemove != null) {
                enhancedRepos.remove(repoToRemove);
            }
        }

        // use mirrors if any to do the mirroring stuff
        DefaultMirrorSelector dms = new DefaultMirrorSelector();
        // fill in mirrors
        for (Mirror mirror : settings.getMirrors()) {
            // Repository manager flag is set to false
            // Maven does not support specifying it in the settings.xml
            dms.add(mirror.getId(), mirror.getUrl(), mirror.getLayout(), false, mirror.getMirrorOf(),
                    mirror.getMirrorOfLayouts());
        }

        Set<RemoteRepository> mirroredRepos = new LinkedHashSet<RemoteRepository>();
        for (RemoteRepository repository : enhancedRepos) {
            RemoteRepository mirror = dms.getMirror(repository);
            if (mirror != null) {
                mirroredRepos.add(mirror);
            } else {
                mirroredRepos.add(repository);
            }
        }

        final Set<RemoteRepository> authorizedRepos = new LinkedHashSet<RemoteRepository>();
        for (RemoteRepository remoteRepository : mirroredRepos) {
            final RemoteRepository.Builder builder = new RemoteRepository.Builder(remoteRepository);

            // add authentication if <server> was provided in settings.xml file
            Server server = settings.getServer(remoteRepository.getId());
            if (server != null) {
                final AuthenticationBuilder authenticationBuilder = new AuthenticationBuilder()
                        .addUsername(server.getUsername())
                        .addPassword(server.getPassword())
                        .addPrivateKey(server.getPrivateKey(), server.getPassphrase());
                builder.setAuthentication(authenticationBuilder.build());
            }

            authorizedRepos.add(builder.build());
        }

        if (log.isLoggable(Level.FINER)) {
            for (RemoteRepository repository : authorizedRepos) {
                log.finer("Repository " + repository.getUrl() + " have been made available for artifact resolution");
            }
        }

        return new ArrayList<RemoteRepository>(authorizedRepos);
    }

    private boolean isOffline() {
        if (programmaticOffline != null) {
            return programmaticOffline.booleanValue();
        }
        return settings.isOffline();
    }

    private List<Profile> getSettingsDefinedProfiles() {
        return MavenConverter.asProfiles(settings.getProfiles());
    }

    // utility methods

    private static ResolutionException wrapException(DependencyResolutionException e) {
        Throwable cause = (Throwable) e;
        Throwable nextCause = null;
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
