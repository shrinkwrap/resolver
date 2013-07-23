/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystemBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

import java.io.File;
import java.util.Collection;

/**
 * Support for implementations of {@link MavenResolverSystem}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public abstract class MavenResolverSystemBaseImpl<UNCONFIGURABLERESOLVERSYSTEMTYPE extends MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, CONFIGURABLERESOLVERSYSTEMTYPE extends MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, UNEQUIPPEDRESOLVESTAGETYPE extends PomlessResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
    implements
    MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    private final UNEQUIPPEDRESOLVESTAGETYPE delegate;
    private final MavenWorkingSessionContainer sessionContainer;

    /**
     * Creates a new instance using the specified delegate, which is required and must also implement the
     * {@link MavenWorkingSessionContainer} SPI, else {@link IllegalArgumentException} will be thrown.
     *
     * @param delegate
     */
    public MavenResolverSystemBaseImpl(final UNEQUIPPEDRESOLVESTAGETYPE delegate) throws IllegalArgumentException {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must be specified");
        }
        if (!(delegate instanceof MavenWorkingSessionContainer)) {
            throw new IllegalArgumentException("delegate must adhere to SPI contract "
                + MavenWorkingSessionContainer.class.getName());
        }
        this.delegate = delegate;
        this.sessionContainer = (MavenWorkingSessionContainer) delegate;
    }

    /**
     * Returns the {@link MavenWorkingSession} associated with this {@link MavenResolverSystem}
     *
     * @return
     */
    protected MavenWorkingSession getSession() {
        return sessionContainer.getMavenWorkingSession();
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
    public STRATEGYSTAGETYPE resolve() throws IllegalStateException, ResolutionException {
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
    public EQUIPPEDRESOLVESTAGETYPE loadPomFromFile(File pomFile) throws IllegalArgumentException,
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
    public STRATEGYSTAGETYPE resolve(String coordinate) throws IllegalArgumentException, ResolutionException {
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
    public EQUIPPEDRESOLVESTAGETYPE loadPomFromFile(File pomFile, String... profiles) throws IllegalArgumentException,
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
    public STRATEGYSTAGETYPE resolve(String... coordinates) throws IllegalArgumentException, ResolutionException {
        return delegate.resolve(coordinates);
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinate coordinate in canonical form
     * @return
     * @throws IllegalArgumentException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveWithRangeSupportStage#resolveVersionRange(String)
     */
    @Override
    public MavenVersionRangeResult resolveVersionRange(String coordinate) throws IllegalArgumentException {
        return delegate.resolveVersionRange(coordinate);
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
    public EQUIPPEDRESOLVESTAGETYPE loadPomFromFile(String pathToPomFile) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        return delegate.loadPomFromFile(pathToPomFile);
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
    public EQUIPPEDRESOLVESTAGETYPE loadPomFromFile(String pathToPomFile, String... profiles)
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
    public UNEQUIPPEDRESOLVESTAGETYPE addDependency(MavenDependency coordinate) throws IllegalArgumentException {
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
    public EQUIPPEDRESOLVESTAGETYPE loadPomFromClassLoaderResource(String pathToPomResource)
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
    public UNEQUIPPEDRESOLVESTAGETYPE addDependencies(MavenDependency... coordinates) throws IllegalArgumentException {
        return delegate.addDependencies(coordinates);
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
    public EQUIPPEDRESOLVESTAGETYPE loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl)
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
    public EQUIPPEDRESOLVESTAGETYPE loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl,
        String... profiles) throws IllegalArgumentException, InvalidConfigurationFileException {
        return delegate.loadPomFromClassLoaderResource(pathToPomResource, cl, profiles);
    }

    /**
     * {@inheritDoc}
     *
     * @param canonicalForms
     * @return
     * @throws IllegalArgumentException
     * @throws ResolutionException
     * @throws CoordinateParseException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(java.util.Collection)
     */
    @Override
    public STRATEGYSTAGETYPE resolve(Collection<String> canonicalForms) throws IllegalArgumentException,
        ResolutionException, CoordinateParseException {
        return delegate.resolve(canonicalForms);
    }

    /**
     * {@inheritDoc}
     *
     * @param dependencies
     * @return
     * @throws IllegalArgumentException
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependencies(java.util.Collection)
     */
    @Override
    public UNEQUIPPEDRESOLVESTAGETYPE addDependencies(Collection<MavenDependency> dependencies)
        throws IllegalArgumentException {
        return delegate.addDependencies(dependencies);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.MavenResolverSystemBaseImpl#offline(boolean)
     */
    @Override
    public UNEQUIPPEDRESOLVESTAGETYPE offline(final boolean offline) {
        this.getSession().setOffline(offline);
        return delegate;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.MavenResolverSystemBaseImpl#offline()
     */
    @Override
    public UNEQUIPPEDRESOLVESTAGETYPE offline() {
        return this.offline(true);
    }
}
