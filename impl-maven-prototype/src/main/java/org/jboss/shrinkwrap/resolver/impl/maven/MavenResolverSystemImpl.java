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

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

/**
 * Implementation of {@link MavenResolverSystem}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MavenResolverSystemImpl implements MavenResolverSystem {

    private final PomlessResolveStageImpl delegate;

    public MavenResolverSystemImpl() {
        this.delegate = new PomlessResolveStageImpl(new MavenWorkingSessionImpl());
    }

    /**
     * Returns the {@link MavenWorkingSession} associated with this {@link MavenResolverSystem}
     *
     * @return
     */
    protected MavenWorkingSession getSession() {
        return delegate.getSession();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws IllegalStateException
     * @throws ResolutionException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve()
     */
    @Override
    public MavenStrategyStage resolve() throws IllegalStateException, ResolutionException {
        return delegate.resolve();
    }

    /**
     * {@inheritDoc}
     *
     * @param pomFile
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidConfigurationFileException
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.io.File)
     */
    @Override
    public PomEquippedResolveStage loadPomFromFile(File pomFile) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        return delegate.loadPomFromFile(pomFile);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     * @throws ResolutionException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(java.lang.String)
     */
    @Override
    public MavenStrategyStage resolve(String coordinate) throws IllegalArgumentException, ResolutionException {
        return delegate.resolve(coordinate);
    }

    /**
     * {@inheritDoc}
     *
     * @param pomFile
     * @param profiles
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidConfigurationFileException
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.io.File,
     *      java.lang.String[])
     */
    @Override
    public PomEquippedResolveStage loadPomFromFile(File pomFile, String... profiles) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        return delegate.loadPomFromFile(pomFile, profiles);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinates
     * @return
     * @throws IllegalArgumentException
     * @throws ResolutionException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(java.lang.String[])
     */
    @Override
    public MavenStrategyStage resolve(String... coordinates) throws IllegalArgumentException, ResolutionException {
        return delegate.resolve(coordinates);
    }

    /**
     * {@inheritDoc}
     *
     * @param pathToPomFile
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidConfigurationFileException
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.lang.String)
     */
    @Override
    public PomEquippedResolveStage loadPomFromFile(String pathToPomFile) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        return delegate.loadPomFromFile(pathToPomFile);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     * @throws ResolutionException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(org.jboss.shrinkwrap.resolver.api.Coordinate)
     */
    @Override
    public MavenStrategyStage resolve(MavenDependency coordinate) throws IllegalArgumentException, ResolutionException {
        return delegate.resolve(coordinate);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinates
     * @return
     * @throws IllegalArgumentException
     * @throws ResolutionException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(COORDINATETYPE[])
     */
    @Override
    public MavenStrategyStage resolve(MavenDependency... coordinates) throws IllegalArgumentException,
        ResolutionException {
        return delegate.resolve(coordinates);
    }

    /**
     * {@inheritDoc}
     *
     * @param pathToPomFile
     * @param profiles
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidConfigurationFileException
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromFile(java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public PomEquippedResolveStage loadPomFromFile(String pathToPomFile, String... profiles)
        throws IllegalArgumentException, InvalidConfigurationFileException {
        return delegate.loadPomFromFile(pathToPomFile, profiles);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependency(org.jboss.shrinkwrap.resolver.api.Coordinate)
     */
    @Override
    public PomlessResolveStage addDependency(MavenDependency coordinate) throws IllegalArgumentException {
        return delegate.addDependency(coordinate);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinate
     * @return
     * @throws CoordinateParseException
     * @throws IllegalArgumentException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependency(java.lang.String)
     */
    @Override
    public PomlessResolveStage addDependency(String coordinate) throws CoordinateParseException,
        IllegalArgumentException {
        return delegate.addDependency(coordinate);
    }

    /**
     * {@inheritDoc}
     *
     * @param pathToPomResource
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidConfigurationFileException
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String)
     */
    @Override
    public PomEquippedResolveStage loadPomFromClassLoaderResource(String pathToPomResource)
        throws IllegalArgumentException, InvalidConfigurationFileException {
        return delegate.loadPomFromClassLoaderResource(pathToPomResource);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinates
     * @return
     * @throws IllegalArgumentException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependencies(COORDINATETYPE[])
     */
    @Override
    public PomlessResolveStage addDependencies(MavenDependency... coordinates) throws IllegalArgumentException {
        return delegate.addDependencies(coordinates);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinate
     * @return
     * @throws CoordinateParseException
     * @throws IllegalArgumentException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependencies(java.lang.String[])
     */
    @Override
    public PomlessResolveStage addDependencies(String... coordinate) throws CoordinateParseException,
        IllegalArgumentException {
        return delegate.addDependencies(coordinate);
    }

    /**
     * {@inheritDoc}
     *
     * @param pathToPomResource
     * @param cl
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidConfigurationFileException
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String,
     *      java.lang.ClassLoader)
     */
    @Override
    public PomEquippedResolveStage loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl)
        throws IllegalArgumentException, InvalidConfigurationFileException {
        return delegate.loadPomFromClassLoaderResource(pathToPomResource, cl);
    }

    /**
     * {@inheritDoc}
     *
     * @param pathToPomResource
     * @param cl
     * @param profiles
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidConfigurationFileException
     * @see org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase#loadPomFromClassLoaderResource(java.lang.String,
     *      java.lang.ClassLoader, java.lang.String[])
     */
    @Override
    public PomEquippedResolveStage loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl,
        String... profiles) throws IllegalArgumentException, InvalidConfigurationFileException {
        return delegate.loadPomFromClassLoaderResource(pathToPomResource, cl, profiles);
    }

}
