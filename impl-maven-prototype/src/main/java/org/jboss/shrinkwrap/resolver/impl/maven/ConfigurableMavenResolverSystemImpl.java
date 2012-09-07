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

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidEnvironmentException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsFromFileTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsFromPluginTask;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * {@link ConfigurableMavenResolverSystem} implementation
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public final class ConfigurableMavenResolverSystemImpl extends MavenResolverSystemImpl implements
    ConfigurableMavenResolverSystem {

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ConfigurableResolverSystem#configureFromFile(java.io.File)
     */
    @Override
    public MavenResolverSystem configureFromFile(final File file) throws IllegalArgumentException,
        UnsupportedOperationException, InvalidConfigurationFileException {
        Validate.notNull(file, "settings file must be specified");
        Validate.isReadable(file, "settings file is not readable: " + file.getAbsolutePath());
        new ConfigureSettingsFromFileTask(file).execute(this.getSession());
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ConfigurableResolverSystem#configureFromFile(java.lang.String)
     */
    @Override
    public MavenResolverSystem configureFromFile(final String pathToFile) throws IllegalArgumentException,
        UnsupportedOperationException, InvalidConfigurationFileException {
        Validate.isNullOrEmpty(pathToFile);
        new ConfigureSettingsFromFileTask(pathToFile).execute(this.getSession());
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ConfigurableResolverSystem#configureFromClassloaderResource(java.lang.String)
     */
    @Override
    public MavenResolverSystem configureFromClassloaderResource(final String path) throws IllegalArgumentException,
        UnsupportedOperationException, InvalidConfigurationFileException {
        return this.configureFromClassloaderResource(path, SecurityActions.getThreadContextClassLoader());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ConfigurableResolverSystem#configureFromClassloaderResource(java.lang.String,
     *      java.lang.ClassLoader)
     */
    @Override
    public MavenResolverSystem configureFromClassloaderResource(final String path, final ClassLoader loader)
        throws IllegalArgumentException, UnsupportedOperationException, InvalidConfigurationFileException {
        Validate.isNullOrEmpty(path);
        Validate.notNull(loader, "ClassLoader is required");
        final File file = FileUtil.INSTANCE.fileFromClassLoaderResource(path, loader);
        return this.configureFromFile(file);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem#configureViaPlugin()
     */
    @Override
    public PomEquippedResolveStage configureViaPlugin() throws InvalidEnvironmentException {
        final MavenWorkingSession session = this.getSession();
        ConfigureSettingsFromPluginTask.INSTANCE.execute(session);
        return new PomEquippedResolveStageImpl(session);
    }
}