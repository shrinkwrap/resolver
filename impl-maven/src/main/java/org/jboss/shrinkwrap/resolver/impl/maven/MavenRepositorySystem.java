/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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

import java.util.Collection;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;

/**
 * Abstraction of the repository system for purposes of dependency resolution used by Maven
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MavenRepositorySystem {

    private final RepositorySystem system;

    /**
     * Creates a Maven repository system
     */
    public MavenRepositorySystem() {
        this.system = getRepositorySystem();
    }

    /**
     * Spawns a working session from the repository system. This is used to as environment for execution of Maven commands
     *
     * @param settings A configuration of current session
     */
    public RepositorySystemSession getSession(Settings settings) {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();

        MavenManagerBuilder builder = new MavenManagerBuilder(system, settings);

        session.setLocalRepositoryManager(builder.localRepositoryManager());
        session.setWorkspaceReader(builder.workspaceReader());
        session.setTransferListener(builder.transferListerer());
        session.setRepositoryListener(builder.repositoryListener());
        session.setOffline(settings.isOffline());
        session.setMirrorSelector(builder.mirrorSelector());
        session.setProxySelector(builder.proxySelector());

        return session;
    }

    /**
     * Resolves artifact dependencies.
     *
     * The {@see ArtifactResult} contains a reference to a file in Maven local repository.
     *
     * @param session The current Maven session
     * @param request The request to be computed
     * @param filter The filter of dependency results
     * @return A collection of artifacts which have built dependency tree from {@link request}
     * @throws DependencyCollectionException If a dependency could not be computed or collected
     * @throws ArtifactResolutionException If an artifact could not be fetched
     */
    public Collection<ArtifactResult> resolveDependencies(RepositorySystemSession session, CollectRequest request,
            MavenResolutionFilter filter) throws DependencyResolutionException {

        DependencyRequest depRequest = new DependencyRequest(request, new MavenResolutionFilterWrap(filter));
        DependencyResult result = system.resolveDependencies(session, depRequest);
        return result.getArtifactResults();

    }

    /**
     * Resolves an artifact
     *
     * @param session The current Maven session
     * @param request The request to be computed
     * @return The artifact
     * @throws ArtifactResolutionException If the artifact could not be fetched
     */
    public ArtifactResult resolveArtifact(RepositorySystemSession session, ArtifactRequest request)
            throws ArtifactResolutionException {
        return system.resolveArtifact(session, request);
    }

    /**
     * Finds a current implementation of repository system. A {@link RepositorySystem} is an entry point to dependency
     * resolution
     *
     * @return A repository system
     */
    private RepositorySystem getRepositorySystem() {
        try {
            return new DefaultPlexusContainer().lookup(RepositorySystem.class);
        } catch (ComponentLookupException e) {
            throw new RuntimeException(
                    "Unable to lookup component RepositorySystem, cannot establish Aether dependency resolver.", e);
        } catch (PlexusContainerException e) {
            throw new RuntimeException(
                    "Unable to load RepositorySystem component by Plexus, cannot establish Aether dependency resolver.", e);
        }
    }

}

class MavenResolutionFilterWrap implements org.sonatype.aether.graph.DependencyFilter {
    private MavenResolutionFilter delegate;

    public MavenResolutionFilterWrap(MavenResolutionFilter filter) {
        delegate = filter;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sonatype.aether.graph.DependencyFilter#accept(org.sonatype.aether. graph.DependencyNode, java.util.List)
     */
    public boolean accept(DependencyNode node, List<DependencyNode> parents) {
        Dependency dependency = node.getDependency();
        if (dependency == null) {
            return false;
        }

        return delegate.accept(MavenConverter.fromDependency(dependency));
    }

}
