/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.bootstrap;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtimes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.settings.Settings;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;

/**
 * Abstraction of the repository system for purposes of dependency resolution used by Maven
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class MavenRepositorySystem {

    private final Context context;

    /**
     * Creates a Maven repository system
     */
    public MavenRepositorySystem() {
        this.context = getContext();

    }

    /**
     * Spawns a working session from the repository system. This is used to as environment for execution of Maven
     * commands
     *
     * @param settings
     * A configuration of current session
     * @param legacyLocalRepository
     * Whether to ignore origin of artifacts in local repository; defaults to false
     * @return A working session spawned from the repository system.
     */
    public DefaultRepositorySystemSession getSession(final Settings settings, boolean legacyLocalRepository) {
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();

        MavenManagerBuilder builder = new MavenManagerBuilder(context.repositorySystem(), settings);

        session.setLocalRepositoryManager(builder.localRepositoryManager(session, legacyLocalRepository));
        session.setWorkspaceReader(builder.workspaceReader());
        session.setTransferListener(builder.transferListerer());
        session.setRepositoryListener(builder.repositoryListener());
        session.setOffline(settings.isOffline());
        session.setMirrorSelector(builder.mirrorSelector());
        session.setProxySelector(builder.proxySelector());
        session.setDependencyManager(builder.dependencyManager());
        session.setArtifactDescriptorPolicy(builder.artifactRepositoryPolicy());

        session.setDependencyTraverser(builder.dependencyTraverser());
        session.setDependencyGraphTransformer(builder.dependencyGraphTransformer());

        // set artifact stereotypes
        session.setArtifactTypeRegistry(builder.artifactTypeRegistry());

        // set system properties for interpolation
        session.setSystemProperties(SecurityActions.getProperties());
        session.setConfigProperties(SecurityActions.getProperties());

        return session;
    }

    /**
     * Resolves artifact dependencies.
     *
     * The {@link ArtifactResult} contains a reference to a file in Maven local repository.
     *
     * @param repoSession The current Maven session
     * @param swrSession SWR Aether session abstraction
     * @param request The request to be computed
     * @param filters The filters of dependency results
     * @return A collection of artifacts which have built dependency tree from {@code request}
     * @throws DependencyResolutionException If a dependency could not be computed or collected
     */
    public Collection<ArtifactResult> resolveDependencies(final RepositorySystemSession repoSession,
            final MavenWorkingSession swrSession, final CollectRequest request, final MavenResolutionFilter[] filters)
            throws DependencyResolutionException {
        final DependencyRequest depRequest = new DependencyRequest(request, new MavenResolutionFilterWrap(filters,
                Collections.unmodifiableList(new ArrayList<MavenDependency>(swrSession.getDependenciesForResolution()))));

        DependencyResult result = context.repositorySystem().resolveDependencies(repoSession, depRequest);
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
    public ArtifactResult resolveArtifact(final RepositorySystemSession session, final ArtifactRequest request)
            throws ArtifactResolutionException {
        return context.repositorySystem().resolveArtifact(session, request);
    }

    /**
     * Resolves versions range
     *
     * @param session The current Maven session
     * @param request The request to be computed
     * @return version range result
     * @throws VersionRangeResolutionException
     * If the requested range could not be parsed. Note that an empty range does not raise an exception.
     *
     */
    public VersionRangeResult resolveVersionRange(final RepositorySystemSession session, final VersionRangeRequest request)
            throws VersionRangeResolutionException {
        return context.repositorySystem().resolveVersionRange(session, request);
    }

    /**
     * Finds a current implementation of repository system. A {@link RepositorySystem} is an entry point to dependency
     * resolution
     *
     * @return Context
     */
    private Context getContext() throws UnsupportedOperationException {
        eu.maveniverse.maven.mima.context.Runtime runtime = Runtimes.INSTANCE.getRuntime();
        return runtime.create(ContextOverrides.create().checksumPolicy(ContextOverrides.ChecksumPolicy.WARN).build());
    }
}

class MavenResolutionFilterWrap implements org.eclipse.aether.graph.DependencyFilter {
    private static final Logger log = Logger.getLogger(MavenResolutionFilterWrap.class.getName());

    private final MavenResolutionFilter[] filters;
    private final List<MavenDependency> dependenciesForResolution;

    MavenResolutionFilterWrap(final MavenResolutionFilter[] filters,
            final List<MavenDependency> dependenciesForResolution) {
        assert filters != null : "filters must be specified, even if empty";
        assert dependenciesForResolution != null : "declaredDependencies must be specified";
        this.dependenciesForResolution = dependenciesForResolution;
        this.filters = filters;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.aether.graph.DependencyFilter#accept(org.eclipse.aether.graph.DependencyNode, java.util.List)
     */
    @Override
    public boolean accept(final DependencyNode node, List<DependencyNode> parents) {
        Dependency dependency = node.getDependency();

        if (dependency == null) {
            return false;
        }

        List<MavenDependency> ancestors = new ArrayList<MavenDependency>();
        for (DependencyNode parent : parents) {
            Dependency parentDependency = parent.getDependency();
            if (parentDependency != null) {
                ancestors.add(MavenConverter.fromDependency(parentDependency));
            }
        }

        if (log.isLoggable(Level.FINER)) {
            log.log(Level.FINER, "Filtering {0} using {1} filters", new Object[] { dependency, filters.length });
        }

        for (final MavenResolutionFilter filter : filters) {
            if (!filter.accepts(MavenConverter.fromDependency(dependency), dependenciesForResolution, ancestors)) {
                if (log.isLoggable(Level.FINER)) {
                    log.log(Level.FINER, "Dependency {0} rejected by {1}", new Object[] { dependency, filter });
                }
                return false;
            }
        }

        // All filters passed
        if (log.isLoggable(Level.FINER)) {
            log.log(Level.FINER, "Dependency {0} was accepted.", dependency);
        }
        return true;
    }

}
