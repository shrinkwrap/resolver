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
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStage;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomMetadataTask;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Implementation of a {@link PomlessResolveStage}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
class PomlessResolveStageImpl extends ResolveStageBaseImpl<PomlessResolveStage> implements PomlessResolveStage {

    private static final String[] EMPTY_ARRAY = new String[] {};

    public PomlessResolveStageImpl(final MavenWorkingSession session) {
        super(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.io.File,
     *      java.lang.String[])
     */
    @Override
    public PomEquippedResolveStage loadPomFromFile(final File pomFile, final String... profiles)
        throws IllegalArgumentException {
        Validate.notNull(pomFile, "Path to pom.xml file must not be null");
        Validate.isReadable(pomFile, "Path to the POM ('" + pomFile + "') file must be defined and accessible");
        final MavenWorkingSession session = this.getSession();
        new LoadPomMetadataTask(pomFile, profiles).execute(session);
        return new PomEquippedResolveStageImpl(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public PomEquippedResolveStage loadPomFromFile(final String pathToPomFile, final String... profiles)
        throws IllegalArgumentException {
        if (pathToPomFile == null || pathToPomFile.length() == 0) {
            throw new IllegalArgumentException("path to POM file must be specified");
        }
        final MavenWorkingSession session = this.getSession();
        new LoadPomMetadataTask(pathToPomFile, profiles).execute(session);
        return new PomEquippedResolveStageImpl(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.io.File)
     */
    @Override
    public PomEquippedResolveStage loadPomFromFile(final File pomFile) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        return this.loadPomFromFile(pomFile, EMPTY_ARRAY);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.lang.String)
     */
    @Override
    public PomEquippedResolveStage loadPomFromFile(final String pathToPomFile) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        return this.loadPomFromFile(pathToPomFile, EMPTY_ARRAY);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String)
     */
    @Override
    public PomEquippedResolveStage loadPomFromClassLoaderResource(final String pathToPomResource)
        throws IllegalArgumentException, InvalidConfigurationFileException {
        return this.loadPomFromClassLoaderResource(pathToPomResource, SecurityActions.getThreadContextClassLoader());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String,
     *      java.lang.ClassLoader)
     */
    @Override
    public PomEquippedResolveStage loadPomFromClassLoaderResource(final String pathToPomResource, final ClassLoader cl)
        throws IllegalArgumentException, InvalidConfigurationFileException {
        return this.loadPomFromClassLoaderResource(pathToPomResource, SecurityActions.getThreadContextClassLoader(),
            EMPTY_ARRAY);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String,
     *      java.lang.ClassLoader, java.lang.String[])
     */
    @Override
    public PomEquippedResolveStage loadPomFromClassLoaderResource(final String pathToPomResource, final ClassLoader cl,
        final String... profiles) throws IllegalArgumentException, InvalidConfigurationFileException {
        Validate.notNullOrEmpty(pathToPomResource, "path to CL resource must be specified");
        Validate.notNull(cl, "ClassLoader must be specified");
        final File file = FileUtil.INSTANCE.fileFromClassLoaderResource(pathToPomResource, cl);
        return this.loadPomFromFile(file);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.ResolveStageBaseImpl#getActualClass()
     */
    @Override
    protected Class<PomlessResolveStage> getActualClass() {
        return PomlessResolveStage.class;
    }

}
