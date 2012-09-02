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

import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidEnvironmentException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureFromPluginTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureFromPomTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsTask;

/**
 * Implementation of {@link MavenResolverSystem}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MavenResolverSystemImpl extends AbstractResolveStageBase<ConfigurableResolveStage> implements
    MavenResolverSystem {

    public MavenResolverSystemImpl() {
        this(new MavenWorkingSessionImpl());
    }

    public MavenResolverSystemImpl(final MavenWorkingSession session) {
        super(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase#configureSettings(java.io.File)
     */
    @Override
    public ConfigurableResolveStage configureSettings(final File settingsXmlFile) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        if (settingsXmlFile == null) {
            throw new IllegalArgumentException("settings file must be specified");
        }
        if (!settingsXmlFile.exists()) {
            throw new IllegalArgumentException("settings file specified does not exist: "
                + settingsXmlFile.getAbsolutePath());
        }
        this.session = new ConfigureSettingsTask(settingsXmlFile).execute(session);
        return new ConfigurableResolveStageImpl(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase#configureSettings(java.lang.String)
     */
    @Override
    public ConfigurableResolveStage configureSettings(final String pathToSettingsXmlFile)
        throws IllegalArgumentException, InvalidConfigurationFileException {
        if (pathToSettingsXmlFile == null || pathToSettingsXmlFile.length() == 0) {
            throw new IllegalArgumentException("settings file path must be specified");
        }
        this.session = new ConfigureSettingsTask(pathToSettingsXmlFile).execute(session);
        return new ConfigurableResolveStageImpl(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ConfigurableResolveStageBase#configureFromPom(java.io.File,
     *      java.lang.String[])
     */
    @Override
    public ConfiguredResolveStage configureFromPom(final File pomFile, final String... profiles)
        throws IllegalArgumentException {
        if (pomFile == null) {
            throw new IllegalArgumentException("POM file must be specified");
        }
        if (!pomFile.exists()) {
            throw new IllegalArgumentException("POM file specified does not exist: " + pomFile.getAbsolutePath());
        }
        this.session = new ConfigureFromPomTask(pomFile, profiles).execute(session);
        return new ConfiguredResolveStageImpl(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ConfigurableResolveStageBase#configureFromPom(java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public ConfiguredResolveStage configureFromPom(String pathToPomFile, String... profiles)
        throws IllegalArgumentException {
        if (pathToPomFile == null || pathToPomFile.length() == 0) {
            throw new IllegalArgumentException("POM file path must be specified");
        }
        this.session = new ConfigureFromPomTask(pathToPomFile, profiles).execute(session);
        return new ConfiguredResolveStageImpl(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ConfigurableResolveStageBase#configureFromPlugin()
     */
    @Override
    public ConfiguredResolveStage configureFromPlugin() throws InvalidEnvironmentException {
        this.session = new ConfigureFromPluginTask().execute(session);
        return new ConfiguredResolveStageImpl(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.AbstractResolveStageBase#covarientReturn()
     */
    @Override
    protected ConfigurableResolveStage covarientReturn() {
        return new ConfigurableResolveStageImpl(session);
    }
}
