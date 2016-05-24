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

import java.io.File;
import java.util.Collection;
import java.util.logging.Logger;

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

    private static final Logger log = Logger.getLogger(MavenResolverSystemBaseImpl.class.getName());

    private final UNEQUIPPEDRESOLVESTAGETYPE delegate;
    private final MavenWorkingSessionContainer sessionContainer;

    /**
     * Creates a new instance using the specified delegate, which is required and must also implement the
     * {@link MavenWorkingSessionContainer} SPI, else {@link IllegalArgumentException} will be thrown.
     *
     * @param delegate The delegate
     * @throws IllegalArgumentException
     *          If the {@code delegate} is either null or doesn't implement the {@link MavenWorkingSessionContainer}
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
     * @return The {@link MavenWorkingSession} associated with this {@link MavenResolverSystem}
     */
    protected MavenWorkingSession getSession() {
        return sessionContainer.getMavenWorkingSession();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve()
     */
    @Override
    public STRATEGYSTAGETYPE resolve() throws IllegalStateException, ResolutionException {
        return delegate.resolve();
    }

    /**
     * {@inheritDoc}
     *
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
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(java.lang.String)
     */
    @Override
    public STRATEGYSTAGETYPE resolve(String coordinate) throws IllegalArgumentException, ResolutionException {
        return delegate.resolve(coordinate);
    }

    /**
     * {@inheritDoc}
     *
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
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(java.lang.String[])
     */
    @Override
    public STRATEGYSTAGETYPE resolve(String... coordinates) throws IllegalArgumentException, ResolutionException {
        return delegate.resolve(coordinates);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveWithRangeSupportStage#resolveVersionRange(String)
     */
    @Override
    public MavenVersionRangeResult resolveVersionRange(String coordinate) throws IllegalArgumentException {
        return delegate.resolveVersionRange(coordinate);
    }

    /**
     * {@inheritDoc}
     *
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
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependency(org.jboss.shrinkwrap.resolver.api.Coordinate)
     */
    @Override
    public UNEQUIPPEDRESOLVESTAGETYPE addDependency(MavenDependency coordinate) throws IllegalArgumentException {
        return delegate.addDependency(coordinate);
    }

    /**
     * {@inheritDoc}
     *
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
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependencies(Coordinate[])
     */
    @Override
    public UNEQUIPPEDRESOLVESTAGETYPE addDependencies(MavenDependency... coordinates) throws IllegalArgumentException {
        return delegate.addDependencies(coordinates);
    }

    /**
     * {@inheritDoc}
     *
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
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependencies(java.util.Collection)
     */
    @Override
    public UNEQUIPPEDRESOLVESTAGETYPE addDependencies(Collection<MavenDependency> dependencies)
        throws IllegalArgumentException {
        return delegate.addDependencies(dependencies);
    }
}
