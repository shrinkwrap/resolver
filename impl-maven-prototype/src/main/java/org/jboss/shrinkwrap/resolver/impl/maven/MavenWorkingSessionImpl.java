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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.apache.maven.model.profile.ProfileSelector;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenRepositorySystem;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.MavenModelResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.SettingsXmlProfileSelector;
import org.jboss.shrinkwrap.resolver.impl.maven.logging.LogModelProblemCollector;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MavenWorkingSessionImpl implements MavenWorkingSession {

    private Set<DependencyDeclaration> dependencyManagement;
    private Stack<DependencyDeclaration> dependencies;

    private static final Logger log = Logger.getLogger(MavenWorkingSessionImpl.class.getName());

    // creates a link to Maven Central Repository
    private static final RemoteRepository MAVEN_CENTRAL = new RemoteRepository("central", "default",
            "http://repo1.maven.org/maven2");

    private MavenRepositorySystem system;
    private Settings settings;

    private RepositorySystemSession session;

    private Model model;

    private List<RemoteRepository> remoteRepositories;

    private boolean useMavenCentralRepository = true;

    public MavenWorkingSessionImpl() {
        this.system = new MavenRepositorySystem();
        this.settings = new MavenSettingsBuilder().buildDefaultSettings();
        this.remoteRepositories = new ArrayList<RemoteRepository>();
        // get session to spare time
        this.session = system.getSession(settings);
        this.dependencies = new Stack<DependencyDeclaration>();
        this.dependencyManagement = new LinkedHashSet<DependencyDeclaration>();
    }

    @Override
    public Set<DependencyDeclaration> getVersionManagement() {
        return dependencyManagement;
    }

    @Override
    public Stack<DependencyDeclaration> getDependencies() {
        return dependencies;
    }

    @Override
    public MavenWorkingSession execute(ModelBuildingRequest request) throws InvalidConfigurationFileException {
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
    public MavenWorkingSession execute(SettingsBuildingRequest request) throws InvalidConfigurationFileException {
        MavenSettingsBuilder builder = new MavenSettingsBuilder();
        this.settings = builder.buildSettings(request);
        // propagate offline settings from system properties
        return goOffline(settings.isOffline());
    }

    // @Override
    public Collection<ArtifactResult> execute(CollectRequest request, MavenResolutionFilter filter)
            throws DependencyResolutionException {
        return system.resolveDependencies(session, request, filter);
    }

    // @Override
    public MavenWorkingSession goOffline(boolean value) {
        String goOffline = SecurityActions.getProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE);
        if (goOffline != null) {
            this.settings.setOffline(Boolean.valueOf(goOffline));
            if (log.isLoggable(Level.FINER)) {
                log.finer("Offline settings is set via a system property. The new offline flag value is: "
                        + settings.isOffline());
            }

        } else {
            settings.setOffline(value);
        }
        return this;
    }

    @Override
    public List<RemoteRepository> getRemoteRepositories() throws IllegalStateException {
        // disable repositories if working offline
        if (settings.isOffline()) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("No remote repositories available, working in offline mode");
            }
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

        // add maven central if selected
        if (useMavenCentralRepository) {
            enhancedRepos.add(MAVEN_CENTRAL);
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

        for (RemoteRepository remoteRepository : mirroredRepos) {
            Server server = settings.getServer(remoteRepository.getId());
            if (server == null) {
                continue;
            }
            Authentication authentication = new Authentication(server.getUsername(), server.getPassword(),
                    server.getPrivateKey(), server.getPassphrase());
            remoteRepository.setAuthentication(authentication);

        }

        if (log.isLoggable(Level.FINER)) {
            for (RemoteRepository repository : mirroredRepos) {
                log.finer("Repository " + repository.getUrl() + " have been made available for artifact resolution");
            }
        }

        return new ArrayList<RemoteRepository>(mirroredRepos);
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public MavenWorkingSession regenerateSession() {
        this.session = system.getSession(settings);
        return this;
    }

    @Override
    public List<Profile> getSettingsDefinedProfiles() {
        return MavenConverter.asProfiles(settings.getProfiles());
    }

    @Override
    public ArtifactTypeRegistry getArtifactTypeRegistry() {
        return session.getArtifactTypeRegistry();
    }

}
