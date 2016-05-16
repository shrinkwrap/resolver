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
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomTask;

/**
 * Base support for implementations of a {@link PomlessResolveStage}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public abstract class PomlessResolveStageBaseImpl<EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, UNEQUIPPEDRESOLVESTAGETYPE extends PomlessResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
        extends MavenResolveStageBaseImpl<UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> implements
        PomlessResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    public PomlessResolveStageBaseImpl(final MavenWorkingSession session) {
        super(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.io.File, java.lang.String[])
     */
    @Override
    public final EQUIPPEDRESOLVESTAGETYPE loadPomFromFile(final File pomFile, final String... profiles)
            throws IllegalArgumentException {
        final MavenWorkingSession session = this.getMavenWorkingSession();
        LoadPomTask.loadPomFromFile(pomFile, profiles).execute(session);
        return this.createNewPomEquippedResolveStage();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.lang.String,
     * java.lang.String[])
     */
    @Override
    public final EQUIPPEDRESOLVESTAGETYPE loadPomFromFile(final String pathToPomFile, final String... profiles)
            throws IllegalArgumentException {
        final MavenWorkingSession session = this.getMavenWorkingSession();
        LoadPomTask.loadPomFromFile(pathToPomFile, profiles).execute(session);
        return this.createNewPomEquippedResolveStage();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.io.File)
     */
    @Override
    public final EQUIPPEDRESOLVESTAGETYPE loadPomFromFile(final File pomFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        final MavenWorkingSession session = this.getMavenWorkingSession();
        LoadPomTask.loadPomFromFile(pomFile).execute(session);
        return this.createNewPomEquippedResolveStage();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.lang.String)
     */
    @Override
    public final EQUIPPEDRESOLVESTAGETYPE loadPomFromFile(final String pathToPomFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        final MavenWorkingSession session = this.getMavenWorkingSession();
        LoadPomTask.loadPomFromFile(pathToPomFile).execute(session);
        return this.createNewPomEquippedResolveStage();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String)
     */
    @Override
    public final EQUIPPEDRESOLVESTAGETYPE loadPomFromClassLoaderResource(final String pathToPomResource)
            throws IllegalArgumentException, InvalidConfigurationFileException {
        final MavenWorkingSession session = this.getMavenWorkingSession();
        LoadPomTask.loadPomFromClassLoaderResource(pathToPomResource).execute(session);
        return this.createNewPomEquippedResolveStage();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String,
     * java.lang.ClassLoader)
     */
    @Override
    public final EQUIPPEDRESOLVESTAGETYPE loadPomFromClassLoaderResource(final String pathToPomResource,
            final ClassLoader cl) throws IllegalArgumentException, InvalidConfigurationFileException {
        final MavenWorkingSession session = this.getMavenWorkingSession();
        LoadPomTask.loadPomFromClassLoaderResource(pathToPomResource, cl).execute(session);
        return this.createNewPomEquippedResolveStage();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String,
     * java.lang.ClassLoader, java.lang.String[])
     */
    @Override
    public final EQUIPPEDRESOLVESTAGETYPE loadPomFromClassLoaderResource(final String pathToPomResource,
            final ClassLoader cl, final String... profiles) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        final MavenWorkingSession session = this.getMavenWorkingSession();
        LoadPomTask.loadPomFromClassLoaderResource(pathToPomResource, cl, profiles).execute(session);
        return this.createNewPomEquippedResolveStage();
    }

    /**
     * Obtains a new {@link PomEquippedResolveStageBase} instance for the current {@link MavenWorkingSession}
     *
     * @return A new {@link PomEquippedResolveStageBase} instance for the current {@link MavenWorkingSession}
     */
    protected abstract EQUIPPEDRESOLVESTAGETYPE createNewPomEquippedResolveStage();

}
