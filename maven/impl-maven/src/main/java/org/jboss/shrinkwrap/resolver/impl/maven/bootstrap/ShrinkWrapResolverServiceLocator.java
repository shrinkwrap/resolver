/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultModelCacheFactory;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.ModelCacheFactory;
import org.apache.maven.repository.internal.SnapshotMetadataGeneratorFactory;
import org.apache.maven.repository.internal.VersionsMetadataGeneratorFactory;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.DependencyCollector;
import org.eclipse.aether.impl.Deployer;
import org.eclipse.aether.impl.Installer;
import org.eclipse.aether.impl.LocalRepositoryProvider;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.OfflineController;
import org.eclipse.aether.impl.RemoteRepositoryFilterManager;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.RepositoryConnectorProvider;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.RepositorySystemLifecycle;
import org.eclipse.aether.impl.UpdateCheckManager;
import org.eclipse.aether.impl.UpdatePolicyAnalyzer;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.internal.impl.DefaultArtifactResolver;
import org.eclipse.aether.internal.impl.DefaultChecksumPolicyProvider;
import org.eclipse.aether.internal.impl.DefaultDeployer;
import org.eclipse.aether.internal.impl.DefaultFileProcessor;
import org.eclipse.aether.internal.impl.DefaultInstaller;
import org.eclipse.aether.internal.impl.DefaultLocalPathComposer;
import org.eclipse.aether.internal.impl.DefaultLocalRepositoryProvider;
import org.eclipse.aether.internal.impl.DefaultMetadataResolver;
import org.eclipse.aether.internal.impl.DefaultOfflineController;
import org.eclipse.aether.internal.impl.DefaultRemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultRepositoryConnectorProvider;
import org.eclipse.aether.internal.impl.DefaultRepositoryEventDispatcher;
import org.eclipse.aether.internal.impl.DefaultRepositoryLayoutProvider;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.internal.impl.DefaultRepositorySystemLifecycle;
import org.eclipse.aether.internal.impl.DefaultTrackingFileManager;
import org.eclipse.aether.internal.impl.DefaultTransporterProvider;
import org.eclipse.aether.internal.impl.DefaultUpdateCheckManager;
import org.eclipse.aether.internal.impl.DefaultUpdatePolicyAnalyzer;
import org.eclipse.aether.internal.impl.EnhancedLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.LocalPathComposer;
import org.eclipse.aether.internal.impl.Maven2RepositoryLayoutFactory;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.TrackingFileManager;
import org.eclipse.aether.internal.impl.collect.DefaultDependencyCollector;
import org.eclipse.aether.internal.impl.filter.DefaultRemoteRepositoryFilterManager;
import org.eclipse.aether.internal.impl.synccontext.DefaultSyncContextFactory;
import org.eclipse.aether.internal.impl.synccontext.named.NamedLockFactoryAdapterFactory;
import org.eclipse.aether.internal.impl.synccontext.named.NamedLockFactoryAdapterFactoryImpl;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutFactory;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.spi.synccontext.SyncContextFactory;
import org.eclipse.aether.transport.wagon.WagonProvider;
import org.eclipse.aether.transport.wagon.WagonTransporterFactory;
import org.jboss.shrinkwrap.resolver.impl.maven.logging.AetherLoggerFactory;

/**
 * A service locator for bootstrapping repository system
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ShrinkWrapResolverServiceLocator implements ServiceLocator {
    private static final Logger log = Logger.getLogger(ShrinkWrapResolverServiceLocator.class.getName());

    private final Map<Class<?>, CacheItem> cache;

    /**
     * Representation of either implementation class or instance for given service to allow lazy-loading of the services
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    class CacheItem {
        Class<?> type;
        List<Class<?>> implementations;
        List<Object> instances;

        CacheItem(Class<?> type) {
            this.type = type;
            this.implementations = new ArrayList<Class<?>>();
            this.instances = new ArrayList<Object>();
        }

        void addImplementation(Class<?> classImpl) {
            implementations.add(classImpl);
        }

        synchronized List<Object> instantiate() {

            for (Class<?> impl : implementations) {
                try {
                    Object instance = SecurityActions.newInstance(impl, new Class<?>[0], new Object[0]);

                    // lazy load other services if required
                    if (instance instanceof Service) {
                        ((Service) instance).initService(ShrinkWrapResolverServiceLocator.this);
                    }
                    instances.add(type.cast(instance));
                } catch (Exception e) {
                    log.log(Level.SEVERE,
                            MessageFormat.format("Failed instantiating {0}, implementation of {1}", impl.getName(),
                                    type.getName()), e);
                }
            }
            return instances;
        }

        synchronized void replaceInstances(Object... newInstaces) {
            this.implementations.clear();
            this.instances.clear();
            this.instances.addAll(Arrays.asList(newInstaces));
        }

    }

    ShrinkWrapResolverServiceLocator() {

        this.cache = new HashMap<Class<?>, CacheItem>();

        addService(RepositorySystem.class, DefaultRepositorySystem.class);
        addService(ArtifactResolver.class, DefaultArtifactResolver.class);
        addService(DependencyCollector.class, DefaultDependencyCollector.class);
        addService(Deployer.class, DefaultDeployer.class);
        addService(Installer.class, DefaultInstaller.class);
        addService(MetadataResolver.class, DefaultMetadataResolver.class);
        addService(RepositoryConnectorProvider.class, DefaultRepositoryConnectorProvider.class);
        addService(RemoteRepositoryManager.class, DefaultRemoteRepositoryManager.class);
        addService(UpdateCheckManager.class, DefaultUpdateCheckManager.class);
        addService(UpdatePolicyAnalyzer.class, DefaultUpdatePolicyAnalyzer.class);
        addService(FileProcessor.class, DefaultFileProcessor.class);
        addService(SyncContextFactory.class, DefaultSyncContextFactory.class);
        addService(RepositoryEventDispatcher.class, DefaultRepositoryEventDispatcher.class);
        addService(OfflineController.class, DefaultOfflineController.class);
        addService(LocalRepositoryProvider.class, DefaultLocalRepositoryProvider.class);
        addService(LocalRepositoryManagerFactory.class, SimpleLocalRepositoryManagerFactory.class);
        addService(LocalRepositoryManagerFactory.class, EnhancedLocalRepositoryManagerFactory.class);

        // add Maven supported services, we are not using MavenServiceLocator as it should not be used from
        // Maven plugins, however we need to do that for dependency tree output
        // class names for internal aether classes we need to register implementations for
        addService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
        addService(VersionResolver.class, DefaultVersionResolver.class);
        addService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
        addService(MetadataGeneratorFactory.class, SnapshotMetadataGeneratorFactory.class);
        addService(MetadataGeneratorFactory.class, VersionsMetadataGeneratorFactory.class);

        // add our own services
        setServices(ModelBuilder.class, new DefaultModelBuilderFactory().newInstance());
        setServices(WagonProvider.class, new ManualWagonProvider());

        // add default services introduced after aether 0.9.0.M2
        addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        addService(TransporterProvider.class, DefaultTransporterProvider.class);
        addService(TransporterFactory.class, WagonTransporterFactory.class);
        addService(RepositoryLayoutProvider.class, DefaultRepositoryLayoutProvider.class);
        addService(RepositoryLayoutFactory.class, Maven2RepositoryLayoutFactory.class);
        addService(ChecksumPolicyProvider.class, DefaultChecksumPolicyProvider.class);
        addService(TrackingFileManager.class, DefaultTrackingFileManager.class);
        addService(RemoteRepositoryFilterManager.class, DefaultRemoteRepositoryFilterManager.class);
        addService(NamedLockFactoryAdapterFactory.class, NamedLockFactoryAdapterFactoryImpl.class);
        addService(RepositorySystemLifecycle.class, DefaultRepositorySystemLifecycle.class);
        addService(ModelCacheFactory.class, DefaultModelCacheFactory.class);
        addService(LocalPathComposer.class, DefaultLocalPathComposer.class);

        // to avoid problems with SLF4J, we are having a JUL bridge
        setServices(LoggerFactory.class, new AetherLoggerFactory());
    }

    private <T> ShrinkWrapResolverServiceLocator addService(Class<T> type, Class<? extends T> implementationType) {

        CacheItem item = cache.get(type);
        if (item == null) {
            item = new CacheItem(type);
        }

        item.addImplementation(implementationType);
        cache.put(type, item);
        return this;
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
    private <T> ShrinkWrapResolverServiceLocator setServices(Class<T> type, T... services) {

        CacheItem item = cache.get(type);
        if (item == null) {
            item = new CacheItem(type);
        }

        item.replaceInstances(services);
        cache.put(type, item);
        return this;

    }

    @Override
    public <T> T getService(Class<T> serviceType) {

        List<T> services = getServices(serviceType);
        if (services.size() == 1) {
            return services.iterator().next();
        }

        if (services.size() > 1) {
            throw new IllegalStateException(MessageFormat.format(
                    "Unable to identify service for {0}, multiple ({1}) services implementations were registered.",
                    serviceType.getName(),
                    services.size()));
        }

        // this represents and exception we can't recover from
        if (serviceType.isAssignableFrom(RepositorySystem.class)) {
            // zero services available
            throw new IllegalStateException(
                    "Unable to boostrap Aether repository system, missing RepositoryService "
                            + serviceType.getName()
                            + ", probably due to missing or invalid Aether dependencies. "
                            + " You are either running from within Maven plugin with Maven 3.0.x version (make sure to update to Maven 3.1.0 or newer) or "
                            + " you have org.apache.maven:maven-aether-provider:3.0.x on classpath shading required binding (make sure to update dependencies in your project).");
        }

        // there might be some services, however we can live with them being null
        return null;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getServices(Class<T> serviceType) {

        CacheItem item = cache.get(serviceType);
        if (item == null) {
            // this represents and exception we can't recover from
            if (serviceType.isAssignableFrom(RepositorySystem.class)) {

                throw new IllegalStateException(
                        "Unable to boostrap Aether repository system, missing RepositoryService "
                                + serviceType.getName()
                                + ", probably due to missing or invalid Aether dependencies. "
                                + " You are either running from within Maven plugin with Maven 3.0.x version (make sure to update to Maven 3.1.0 or newer) or "
                                + " you have org.apache.maven:maven-aether-provider:3.0.x on classpath shading required binding (make sure to update dependencies in your project).");
            }

            return Collections.emptyList();

        }

        // classes were not yet instantiated
        if (item.instances.isEmpty()) {
            return (List<T>) item.instantiate();
        }

        // we already have instances, so let's return them
        return (List<T>) item.instances;
    }
}
