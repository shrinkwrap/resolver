/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.settings.Activation;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;

/**
 * An implementation of Maven Environment required for resolver to have a place where to store intermediate data
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MavenEnvironmentImpl implements MavenEnvironment {
    private static final Logger log = Logger.getLogger(MavenEnvironmentImpl.class.getName());

    // creates a link to Maven Central Repository
    private static final RemoteRepository MAVEN_CENTRAL = new RemoteRepository("central", "default",
            "http://repo1.maven.org/maven2");

    private MavenRepositorySystem system;
    private Settings settings;
    // private MavenDependencyResolverSettings settings;
    private RepositorySystemSession session;

    private Stack<MavenDependency> dependencies;

    private Set<MavenDependency> versionManagement;

    private Model model;

    private List<RemoteRepository> remoteRepositories;

    private boolean useMavenCentralRepository;

    /**
     * Constructs a new instance of MavenEnvironment
     */
    public MavenEnvironmentImpl() {
        this.system = new MavenRepositorySystem();
        this.settings = new MavenSettingsBuilder().buildDefaultSettings();
        this.dependencies = new Stack<MavenDependency>();
        this.versionManagement = new LinkedHashSet<MavenDependency>();
        this.remoteRepositories = new ArrayList<RemoteRepository>();
        // get session to spare time
        this.session = system.getSession(settings);
    }

    @Override
    public Set<MavenDependency> getVersionManagement() {
        return versionManagement;
    }

    @Override
    public Stack<MavenDependency> getDependencies() {
        return dependencies;
    }

    @Override
    public MavenEnvironment regenerateSession() {
        this.session = system.getSession(settings);
        return this;
    }

    @Override
    public MavenEnvironment execute(ModelBuildingRequest request) {

        request.setModelResolver(new MavenModelResolver(system, session, getRemoteRepositories()));

        ModelBuilder builder = new DefaultModelBuilderFactory().newInstance();
        ModelBuildingResult result;
        try {
            result = builder.build(request);
        }
        // wrap exception message
        catch (ModelBuildingException e) {
            String pomPath = request.getPomFile().getAbsolutePath();
            StringBuilder sb = new StringBuilder("Found ").append(e.getProblems().size())
                    .append(" problems while building POM model from ").append(pomPath);

            int counter = 1;
            for (ModelProblem problem : e.getProblems()) {
                sb.append(counter++).append("/ ").append(problem).append("\n");
            }

            throw new ResolutionException(sb.toString(), e);
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
    public MavenEnvironment execute(SettingsBuildingRequest request) {
        MavenSettingsBuilder builder = new MavenSettingsBuilder();
        this.settings = builder.buildSettings(request);
        // propagate offline settings from system properties
        return goOffline(settings.isOffline());
    }

    @Override
    public Collection<ArtifactResult> execute(CollectRequest request, MavenResolutionFilter filter)
            throws DependencyResolutionException {
        return system.resolveDependencies(session, request, filter);
    }

    @Override
    public ArtifactResult execute(ArtifactRequest request) throws ArtifactResolutionException {
        return system.resolveArtifact(session, request);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RemoteRepository> getRemoteRepositories() {
        // disable repositories if working offline
        if (settings.isOffline()) {
            return Collections.emptyList();
        }

        List<String> actives = settings.getActiveProfiles();
        Set<RemoteRepository> enhancedRepos = new LinkedHashSet<RemoteRepository>();

        for (Map.Entry<String, Profile> profile : (Set<Map.Entry<String, Profile>>) settings.getProfilesAsMap().entrySet()) {
            Activation activation = profile.getValue().getActivation();
            if (actives.contains(profile.getKey()) || (activation != null && activation.isActiveByDefault())) {
                for (org.apache.maven.settings.Repository repo : profile.getValue().getRepositories()) {
                    enhancedRepos.add(MavenConverter.asRemoteRepository(repo));
                }
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

        for(RemoteRepository remoteRepository : mirroredRepos) {
            Server server = settings.getServer(remoteRepository.getId());
            if(server == null)
               continue;
            Authentication authentication = new Authentication(server.getUsername(),server.getPassword(), server.getPrivateKey(), server.getPassphrase());
            remoteRepository.setAuthentication(authentication);
        }

        return new ArrayList<RemoteRepository>(mirroredRepos);
    }

    /**
     * Gets registry of the known artifact types based on underlying session
     *
     * @return the registry
     */
    @Override
    public ArtifactTypeRegistry getArtifactTypeRegistry() {
        return session.getArtifactTypeRegistry();
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public MavenEnvironment goOffline(boolean value) {
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
    public MavenEnvironment useCentralRepository(boolean useCentralRepository) {
        this.useMavenCentralRepository = useCentralRepository;
        return this;
    }

}
