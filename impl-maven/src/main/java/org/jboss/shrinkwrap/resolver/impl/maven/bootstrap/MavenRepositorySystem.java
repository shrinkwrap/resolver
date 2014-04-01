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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.SnapshotMetadataGeneratorFactory;
import org.apache.maven.repository.internal.VersionsMetadataGeneratorFactory;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.DefaultServiceLocator.ErrorHandler;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.locator.ServiceLocator;
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

    private final RepositorySystem system;

    /**
     * Creates a Maven repository system
     */
    public MavenRepositorySystem() {
        this.system = getRepositorySystem();

    }

    /**
     * Spawns a working session from the repository system. This is used to as environment for execution of Maven
     * commands
     *
     * @param settings
     * A configuration of current session
     */
    public DefaultRepositorySystemSession getSession(final Settings settings) {
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();

        MavenManagerBuilder builder = new MavenManagerBuilder(system, settings);

        session.setLocalRepositoryManager(builder.localRepositoryManager(session));
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

        // set system properties for interpolation
        session.setSystemProperties(SecurityActions.getProperties());
        session.setConfigProperties(SecurityActions.getProperties());

        return session;
    }

    /**
     * Resolves artifact dependencies.
     *
     * The {@see ArtifactResult} contains a reference to a file in Maven local repository.
     *
     * @param repoSession
     * The current Maven session
     * @param swrSession
     * SWR Aether session abstraction
     * @param request
     * The request to be computed
     * @param filters
     * The filters of dependency results
     * @return A collection of artifacts which have built dependency tree from {@link request}
     * @throws DependencyCollectionException
     * If a dependency could not be computed or collected
     * @throws ArtifactResolutionException
     * If an artifact could not be fetched
     */
    public Collection<ArtifactResult> resolveDependencies(final RepositorySystemSession repoSession,
            final MavenWorkingSession swrSession, final CollectRequest request, final MavenResolutionFilter[] filters)
            throws DependencyResolutionException {
        final DependencyRequest depRequest = new DependencyRequest(request, new MavenResolutionFilterWrap(filters,
                Collections.unmodifiableList(new ArrayList<MavenDependency>(swrSession.getDependenciesForResolution()))));

        DependencyResult result = system.resolveDependencies(repoSession, depRequest);
        return result.getArtifactResults();
    }

    /**
     * Resolves an artifact
     *
     * @param session
     * The current Maven session
     * @param request
     * The request to be computed
     * @return The artifact
     * @throws ArtifactResolutionException
     * If the artifact could not be fetched
     */
    public ArtifactResult resolveArtifact(final RepositorySystemSession session, final ArtifactRequest request)
            throws ArtifactResolutionException {
        return system.resolveArtifact(session, request);
    }

    /**
     * Resolves versions range
     *
     * @param session The current Maven session
     * @param request The request to be computed
     * @return version range result
     * @throws VersionRangeResolutionException
     *
     */
    public VersionRangeResult resolveVersionRange(final RepositorySystemSession session, final VersionRangeRequest request)
            throws VersionRangeResolutionException {
        return system.resolveVersionRange(session, request);
    }

    /**
     * Finds a current implementation of repository system. A {@link RepositorySystem} is an entry point to dependency
     * resolution
     *
     * @return A repository system
     * @throws UnsupportedOperationException if {@link RepositorySystem} was not bootstrapped correctly
     */
    private RepositorySystem getRepositorySystem() throws UnsupportedOperationException {

        final ShrinkWrapResolverServiceLocator locator = new ShrinkWrapResolverServiceLocator();

        // add Maven supported services, we are not using MavenServiceLocator as it should not be used from
        // Maven plugins, however we need to do that for dependency tree output
        // class names for internal aether classes we need to register implementations for
        locator.addService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
        locator.addService(VersionResolver.class, DefaultVersionResolver.class);
        locator.addService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
        locator.addService(MetadataGeneratorFactory.class, SnapshotMetadataGeneratorFactory.class);
        locator.addService(MetadataGeneratorFactory.class, VersionsMetadataGeneratorFactory.class);

        // add our own services
        locator.setServices(ModelBuilder.class, new DefaultModelBuilderFactory().newInstance());
        locator.setServices(WagonProvider.class, new ManualWagonProvider());
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);

        final RepositorySystem repositorySystem = locator.getService(RepositorySystem.class);

        // if running from inside plugin, we required Maven 3.1.0 or newer
        // that happens because Maven handles Aether dependencies in plugins a special way so we can't provide our own
        // implementation, it is simply removed from classpath and not available at all
        //
        // note, this exception can be thrown if maven-aether-provider in older version is on class path as well
        if (repositorySystem == null) {
            throw ShrinkWrapResolverServiceLocator.unableToBootstrapMavenResolverSystem();
        }
        return repositorySystem;
    }
}

class ShrinkWrapResolverServiceLocator implements ServiceLocator {
    private static final Logger log = Logger.getLogger(ShrinkWrapResolverServiceLocator.class.getName());

    private DefaultServiceLocator delegate;

    public ShrinkWrapResolverServiceLocator() {
        this.delegate = new DefaultServiceLocator();
        delegate.setErrorHandler(new ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                log.log(Level.SEVERE, "Unable to create service {0} to fullfile {1} due to {2}", new Object[] { type.getName(),
                        impl.getName(), exception.getMessage() });
            }
        });
    }

    /**
     * Sets the instances for a service.
     *
     * @param <T> The service type.
     * @param type The interface describing the service, must not be {@code null}.
     * @param services The instances of the service, may be {@code null} but must not contain {@code null} elements.
     * @return This locator for chaining, never {@code null}.
     */
    @SuppressWarnings("unchecked")
    public <T> ShrinkWrapResolverServiceLocator setServices(Class<T> type, T... services)
    {
        delegate.setServices(type, services);
        return this;
    }

    @Override
    public <T> T getService(Class<T> arg0) {
        return delegate.getService(arg0);
    }

    @Override
    public <T> List<T> getServices(Class<T> arg0) {
        return delegate.getServices(arg0);
    }

    public <T> ShrinkWrapResolverServiceLocator addService(Class<T> type, Class<? extends T> impl)
    {
        delegate.addService(type, impl);
        return this;
    }

    public static UnsupportedOperationException unableToBootstrapMavenResolverSystem() {
        return new UnsupportedOperationException(
                "Unable to boostrap Aether repository system due to missing or invalid Aether dependencies. "
                        + " You are either running from within Maven plugin with Maven 3.0.x version (make sure to update to Maven 3.1.0 or newer) or "
                        + " you have org.apache.maven:maven-aether-provider:3.0.x on classpath shading required binding (make sure to update dependencies in your project).");
    }

}

class MavenResolutionFilterWrap implements org.eclipse.aether.graph.DependencyFilter {
    private static final Logger log = Logger.getLogger(MavenResolutionFilterWrap.class.getName());

    private final MavenResolutionFilter[] filters;
    private final List<MavenDependency> dependenciesForResolution;

    public MavenResolutionFilterWrap(final MavenResolutionFilter[] filters,
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
