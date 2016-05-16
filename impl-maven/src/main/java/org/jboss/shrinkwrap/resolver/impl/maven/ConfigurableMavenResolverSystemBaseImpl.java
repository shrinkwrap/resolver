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
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystemBase;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidEnvironmentException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystemBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsFromFileTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsFromPluginTask;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Base support for implementations of {@link ConfigurableMavenResolverSystem}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public abstract class ConfigurableMavenResolverSystemBaseImpl<UNCONFIGURABLERESOLVERSYSTEMTYPE extends MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, CONFIGURABLEBERESOLVERSYSTEMTYPE extends ConfigurableMavenResolverSystemBase<UNCONFIGURABLERESOLVERSYSTEMTYPE, CONFIGURABLEBERESOLVERSYSTEMTYPE, EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, CONFIGURABLEBERESOLVERSYSTEMTYPE>, EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, UNEQUIPPEDRESOLVESTAGETYPE extends PomlessResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
    extends
    MavenResolverSystemBaseImpl<UNCONFIGURABLERESOLVERSYSTEMTYPE, CONFIGURABLEBERESOLVERSYSTEMTYPE, EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>
    implements
    ConfigurableMavenResolverSystemBase<UNCONFIGURABLERESOLVERSYSTEMTYPE, CONFIGURABLEBERESOLVERSYSTEMTYPE, EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, CONFIGURABLEBERESOLVERSYSTEMTYPE> {

    /**
     * Creates a new instance using the specified delegate, which is required and must also implement the
     * {@link MavenWorkingSessionContainer} SPI, else {@link IllegalArgumentException} will be thrown..
     *
     * @param delegate The delegate
     * @throws IllegalArgumentException
     *          If the {@code delegate} is either null or doesn't implement the {@link MavenWorkingSessionContainer}
     */
    public ConfigurableMavenResolverSystemBaseImpl(final UNEQUIPPEDRESOLVESTAGETYPE delegate)
        throws IllegalArgumentException {
        super(delegate);
    }


    @Override
    public final EQUIPPEDRESOLVESTAGETYPE configureViaPlugin() throws InvalidEnvironmentException {
        final MavenWorkingSession session = this.getSession();
        ConfigureSettingsFromPluginTask.INSTANCE.execute(session);
        return this.createPomEquippedResolveStage();
    }

    @Override
    public UNCONFIGURABLERESOLVERSYSTEMTYPE fromClassloaderResource(String path) throws IllegalArgumentException,
            InvalidConfigurationFileException {
            return this.configureFromClassloaderResource(path, SecurityActions.getThreadContextClassLoader());
    }

    @Override
    public UNCONFIGURABLERESOLVERSYSTEMTYPE fromClassloaderResource(String path, ClassLoader loader)
            throws IllegalArgumentException, InvalidConfigurationFileException {
        return configureFromClassloaderResource(path, loader);
    }

    @Override
    public UNCONFIGURABLERESOLVERSYSTEMTYPE fromFile(File file) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        Validate.notNull(file, "settings file must be specified");
        Validate.readable(file, "settings file is not readable: " + file.getAbsolutePath());
        new ConfigureSettingsFromFileTask(file).execute(this.getSession());
        return this.getUnconfigurableView();
    }

    @Override
    public UNCONFIGURABLERESOLVERSYSTEMTYPE fromFile(String pathToFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        Validate.isNullOrEmpty(pathToFile);
        return fromFile(new File(pathToFile));
    }

    private UNCONFIGURABLERESOLVERSYSTEMTYPE configureFromClassloaderResource(final String path,
        final ClassLoader loader) throws IllegalArgumentException, UnsupportedOperationException,
        InvalidConfigurationFileException {
        Validate.isNullOrEmpty(path);
        Validate.notNull(loader, "ClassLoader is required");
        final File file = FileUtil.INSTANCE.fileFromClassLoaderResource(path, loader);
        return this.fromFile(file);
    }

    /**
     * Returns the UNCONFIGURABLERESOLVERSYSTEMTYPE view type of this {@link ConfigurableMavenResolverSystemBase}
     *
     * @return The UNCONFIGURABLERESOLVERSYSTEMTYPE view type of this {@link ConfigurableMavenResolverSystemBase}
     */
    protected abstract UNCONFIGURABLERESOLVERSYSTEMTYPE getUnconfigurableView();

    /**
     * Returns a new EQUIPPEDRESOLVESTAGETYPE for the current session
     *
     * @return A new EQUIPPEDRESOLVESTAGETYPE for the current session
     */
    protected abstract EQUIPPEDRESOLVESTAGETYPE createPomEquippedResolveStage();
}