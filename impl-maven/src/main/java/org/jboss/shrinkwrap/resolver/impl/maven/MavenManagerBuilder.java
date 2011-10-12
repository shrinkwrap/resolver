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

import java.io.File;

import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.RepositoryListener;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.LocalRepositoryManager;
import org.sonatype.aether.repository.MirrorSelector;
import org.sonatype.aether.repository.ProxySelector;
import org.sonatype.aether.transfer.TransferListener;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;
import org.sonatype.aether.util.repository.DefaultProxySelector;

/**
 * Builds objects required for Maven session execution
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MavenManagerBuilder {
    private MavenDependencyResolverSettings settings;
    private RepositorySystem system;

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
     * @param system the Maven system
     * @param settings Maven and resolver settings
     */
    public MavenManagerBuilder(RepositorySystem system, MavenDependencyResolverSettings settings) {
        this.system = system;
        this.settings = settings;
    }

    /**
     * Gets the transfer listener
     *
     * @return the listener
     */
    public TransferListener transferListerer() {
        return new LogTransferListerer();
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
    public LocalRepositoryManager localRepositoryManager() {

        String localRepositoryPath = settings.getSettings().getLocalRepository();
        Validate.notNullOrEmpty(localRepositoryPath, "Path to a local repository must be defined");

        LocalRepositoryType repositoryType = settings.isOffline() ? LocalRepositoryType.SIMPLE : LocalRepositoryType.ENHANCED;
        return system
                .newLocalRepositoryManager(new LocalRepository(new File(localRepositoryPath), repositoryType.contentType()));
    }

    /**
     * Gets mirror selector
     *
     * @return the selector
     */
    public MirrorSelector mirrorSelector() {

        DefaultMirrorSelector dms = new DefaultMirrorSelector();

        // fill in mirrors
        for (Mirror mirror : settings.getSettings().getMirrors()) {
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

        for (Proxy proxy : settings.getSettings().getProxies()) {
            dps.add(MavenConverter.asProxy(proxy), proxy.getNonProxyHosts());
        }

        return dps;
    }
}
