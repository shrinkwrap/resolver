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

import java.net.URL;

import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepositories;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepository;

/**
 * {@link ConfigurableMavenResolverSystem} implementation
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ConfigurableMavenResolverSystemImpl
        extends
        ConfigurableMavenResolverSystemBaseImpl<MavenResolverSystem, ConfigurableMavenResolverSystem, PomEquippedResolveStage, PomlessResolveStage, MavenStrategyStage, MavenFormatStage>
        implements ConfigurableMavenResolverSystem, MavenWorkingSessionContainer {

    /**
     * Creates a new instance with a new backing {@link MavenWorkingSession}
     *
     * @throws IllegalArgumentException
     */
    public ConfigurableMavenResolverSystemImpl() throws IllegalArgumentException {
        super(new PomlessResolveStageImpl(new MavenWorkingSessionImpl()));
    }

    @Override
    public ConfigurableMavenResolverSystem withClassPathResolution(boolean useClassPathResolution) {
        getMavenWorkingSession().disableClassPathWorkspaceReader();
        return this;
    }

    @Override
    public ConfigurableMavenResolverSystem withRemoteRepo(String name, String url, String layout) {
        getMavenWorkingSession().addRemoteRepo(MavenRemoteRepositories.createRemoteRepository(name, url, layout));
        return this;
    }

    @Override
    public ConfigurableMavenResolverSystem withRemoteRepo(String name, URL url, String layout) {
        getMavenWorkingSession().addRemoteRepo(MavenRemoteRepositories.createRemoteRepository(name, url, layout));
        return this;
    }

    @Override
    public ConfigurableMavenResolverSystem withRemoteRepo(MavenRemoteRepository repository) {
        getMavenWorkingSession().addRemoteRepo(repository);
        return this;
    }

    @Override
    public ConfigurableMavenResolverSystem withMavenCentralRepo(boolean useMavenCentral) {
        if (useMavenCentral == false) {
            getMavenWorkingSession().disableMavenCentral();
        }
        return this;
    }

    @Override
    public ConfigurableMavenResolverSystem workOffline() {
        return workOffline(true);
    }

    @Override
    public ConfigurableMavenResolverSystem workOffline(boolean workOffline) {
        getMavenWorkingSession().setOffline(workOffline);
        return this;
    }

    @Override
    public MavenWorkingSession getMavenWorkingSession() {
        return super.getSession();
    }

    /**
     * {@inheritDoc} (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.ConfigurableMavenResolverSystemBaseImpl#getUnconfigurableView()
     */
    @Override
    protected MavenResolverSystem getUnconfigurableView() {
        return new MavenResolverSystemImpl(new PomlessResolveStageImpl(this.getSession()));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.ConfigurableMavenResolverSystemBaseImpl#createPomEquippedResolveStage()
     */
    @Override
    protected PomEquippedResolveStage createPomEquippedResolveStage() {
        return new PomEquippedResolveStageImpl(getSession());
    }

}