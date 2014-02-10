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
package org.jboss.shrinkwrap.resolver.impl.maven.bootstrap;

import java.io.File;

import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.jboss.shrinkwrap.resolver.impl.maven.aether.ClasspathWorkspaceReader;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.logging.LogRepositoryListener;
import org.jboss.shrinkwrap.resolver.impl.maven.logging.LogTransferListener;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.eclipse.aether.RepositoryListener;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.util.graph.manager.ClassicDependencyManager;
import org.eclipse.aether.util.repository.DefaultMirrorSelector;
import org.eclipse.aether.util.repository.DefaultProxySelector;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;

/**
 * Builds objects required for Maven session execution
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MavenManagerBuilder {
    private final Settings settings;
    private final RepositorySystem system;

    private static enum LocalRepositoryType {
        SIMPLE("simple"), ENHANCED("default");

        private final String type;

        private LocalRepositoryType(String type) {
            this.type = type;
        }

        public String contentType() {
            return type;
        }
    }

    /**
     * Creates a builder which has access to Maven system and current settings
     *
     * @param system
     * the Maven system
     * @param settings
     * Maven and resolver settings
     */
    public MavenManagerBuilder(RepositorySystem system, Settings settings) {
        this.system = system;
        this.settings = settings;
    }

    /**
     * Gets the transfer listener
     *
     * @return the listener
     */
    public TransferListener transferListerer() {
        return new LogTransferListener();
    }

    /**
     * Get the repository listener
     *
     * @return the listener
     */
    public RepositoryListener repositoryListener() {
        return new LogRepositoryListener();
    }

    /**
     * Gets manager for local repository
     *
     * @return the manager
     */
    public LocalRepositoryManager localRepositoryManager(final RepositorySystemSession session) {
        Validate.notNull(session, "session must be specified");
        String localRepositoryPath = settings.getLocalRepository();
        Validate.notNullOrEmpty(localRepositoryPath, "Path to a local repository must be defined");

        LocalRepositoryType repositoryType = settings.isOffline() ? LocalRepositoryType.SIMPLE
                : LocalRepositoryType.ENHANCED;
        return system.newLocalRepositoryManager(session, new LocalRepository(new File(localRepositoryPath), repositoryType
                .contentType()));
    }

    /**
     * Gets mirror selector
     *
     * @return the selector
     */
    public MirrorSelector mirrorSelector() {

        DefaultMirrorSelector dms = new DefaultMirrorSelector();

        // fill in mirrors
        for (Mirror mirror : settings.getMirrors()) {
            // Repository manager flag is set to false
            // Maven does not support specifying it in the settings.xml
            dms.add(mirror.getId(), mirror.getUrl(), mirror.getLayout(), false, mirror.getMirrorOf(),
                    mirror.getMirrorOfLayouts());
        }

        return dms;
    }

    /**
     * Gets proxy selector
     *
     * @return the selector
     */
    public ProxySelector proxySelector() {
        DefaultProxySelector dps = new DefaultProxySelector();

        for (Proxy proxy : settings.getProxies()) {
            dps.add(MavenConverter.asProxy(proxy), proxy.getNonProxyHosts());
        }

        return dps;
    }

    /**
     * Gets workspace reader
     *
     */
    public WorkspaceReader workspaceReader() {
        return new ClasspathWorkspaceReader();
    }

    /**
     * Gets dependency manager. This class handles propagation of dependencyManagement sections while
     * resolving artifact descriptors
     *
     * @return
     */
    public DependencyManager dependencyManager() {
        return new ClassicDependencyManager();
    }

    /**
     * Gets artifact descriptor repository policy. Ignore errors when artifact descriptor is not available - this handles
     * situation when resolving from classpath or local repository. Additionally, ignore invalid artifact descriptors, as they
     * might be available in public Maven repositories
     *
     * @return
     */
    public ArtifactDescriptorPolicy artifactRepositoryPolicy() {
        return new SimpleArtifactDescriptorPolicy(true, true);
    }
}
