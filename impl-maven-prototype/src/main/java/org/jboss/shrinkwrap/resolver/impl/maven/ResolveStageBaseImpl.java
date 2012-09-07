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

import java.text.MessageFormat;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencyExclusion;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Base implementation providing support for operations defined by {@link MavenResolveStageBase}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @param <RESOLVESTAGETYPE>
 */
abstract class ResolveStageBaseImpl<RESOLVESTAGETYPE extends MavenResolveStageBase<RESOLVESTAGETYPE, MavenStrategyStage, MavenFormatStage>>
    implements MavenResolveStageBase<RESOLVESTAGETYPE, MavenStrategyStage, MavenFormatStage>,
    MavenWorkingSessionContainer {

    private static final MavenDependencyExclusion[] TYPESAFE_EXCLUSIONS_ARRAY = new MavenDependencyExclusion[] {};

    private final MavenWorkingSession session;

    protected MavenWorkingSession getSession() {
        return session;
    }

    public ResolveStageBaseImpl(final MavenWorkingSession session) {
        Validate.stateNotNull(session, "Maven Working session must not be null");
        this.session = session;
    }

    private void clearDependenciesFromSession() {
        this.session.getDependencies().clear();
    }

    @Override
    public MavenWorkingSession getMavenWorkingSession() {
        return session;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve()
     */
    @Override
    public final MavenStrategyStage resolve() throws IllegalStateException {
        return new MavenStrategyStageImpl(getMavenWorkingSession());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(java.lang.String)
     */
    @Override
    public final MavenStrategyStage resolve(final String coordinate) throws IllegalArgumentException {
        this.clearDependenciesFromSession();
        this.addDependency(coordinate);
        return this.resolve();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(java.lang.String[])
     */
    @Override
    public final MavenStrategyStage resolve(final String... coordinates) throws IllegalArgumentException {
        this.clearDependenciesFromSession();
        this.addDependencies(coordinates);
        return this.resolve();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(org.jboss.shrinkwrap.resolver.api.Coordinate)
     */
    @Override
    public final MavenStrategyStage resolve(final MavenDependency dependency) throws IllegalArgumentException {
        this.clearDependenciesFromSession();
        this.addDependency(dependency);
        return this.resolve();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#resolve(COORDINATETYPE[])
     */
    @Override
    public final MavenStrategyStage resolve(final MavenDependency... coordinates) throws IllegalArgumentException {
        this.clearDependenciesFromSession();
        this.addDependencies(coordinates);
        return this.resolve();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependency(org.jboss.shrinkwrap.resolver.api.Coordinate)
     */
    @Override
    public final RESOLVESTAGETYPE addDependency(final MavenDependency dependency) throws IllegalArgumentException {
        if (dependency == null) {
            throw new IllegalArgumentException("dependency must be specified");
        }
        final MavenDependency resolved = this.resolveDependency(dependency);
        this.session.getDependencies().add(resolved);
        return this.covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependency(java.lang.String)
     */
    @Override
    public final RESOLVESTAGETYPE addDependency(final String coordinate) throws CoordinateParseException,
        IllegalArgumentException {
        if (coordinate == null || coordinate.length() == 0) {
            throw new IllegalArgumentException("Coordinate must be specified");
        }
        final MavenDependency declared = MavenDependencies.createDependency(coordinate, null, false);
        final MavenDependency resolved = this.resolveDependency(declared);
        this.session.getDependencies().add(resolved);
        return this.covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependencies(COORDINATETYPE[])
     */
    @Override
    public final RESOLVESTAGETYPE addDependencies(final MavenDependency... dependencies)
        throws IllegalArgumentException {
        if (dependencies == null || dependencies.length == 0) {
            throw new IllegalArgumentException("At least one coordinate must be specified");
        }
        for (final MavenDependency dependency : dependencies) {
            if (dependency == null) {
                throw new IllegalArgumentException("null dependency not permitted");
            }
            final MavenDependency resolved = this.resolveDependency(dependency);
            this.session.getDependencies().add(resolved);
        }
        return this.covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.ResolveStage#addDependencies(java.lang.String[])
     */
    @Override
    public final RESOLVESTAGETYPE addDependencies(final String... coordinates) throws CoordinateParseException,
        IllegalArgumentException {
        if (coordinates == null || coordinates.length == 0) {
            throw new IllegalArgumentException("At least one coordinate must be specified");
        }
        for (final String coordinate : coordinates) {
            if (coordinate == null || coordinate.length() == 0) {
                throw new IllegalArgumentException("null dependency not permitted");
            }
            final MavenDependency dependency = this.resolveDependency(coordinate);
            this.session.getDependencies().add(dependency);
        }
        return this.covarientReturn();
    }

    private MavenDependency resolveDependency(final String coordinate) {
        assert coordinate != null && coordinate.length() > 0 : "Coordinate is required";
        final MavenCoordinate newCoordinate = MavenCoordinates.createCoordinate(coordinate);
        final MavenDependency declared = MavenDependencies.createDependency(newCoordinate, null, false);
        final MavenDependency resolved = this.resolveDependency(declared);
        return resolved;
    }

    private MavenDependency resolveDependency(final MavenDependency declared) {
        final String resolvedVersion = this.resolveVersion(declared);
        final MavenCoordinate newCoordinate = MavenCoordinates.createCoordinate(declared.getGroupId(),
            declared.getArtifactId(), resolvedVersion, declared.getPackaging(), declared.getClassifier());
        final MavenDependency dependency = MavenDependencies.createDependency(newCoordinate, declared.getScope(),
            declared.isOptional(), declared.getExclusions().toArray(TYPESAFE_EXCLUSIONS_ARRAY));
        return dependency;
    }

    /**
     * Use available information to resolve the version for the specified {@link MavenDependency}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.ResolveStageBaseImpl#resolveVersion(org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency)
     */
    protected String resolveVersion(final MavenDependency dependency) throws IllegalArgumentException {
        final String declaredVersion = dependency.getVersion();
        if (Validate.isNullOrEmpty(declaredVersion)) {
            throw new ResolutionException(MessageFormat.format(
                "Unable to get version for dependency specified by {0}:, it was either null or empty.",
                dependency.toCanonicalForm()));
        }
        return declaredVersion;
    }

    /**
     * Returns the next invokable resolve stage with the currently-configured session
     *
     * @return
     */
    private RESOLVESTAGETYPE covarientReturn() {
        return this.getActualClass().cast(this);
    }

    protected abstract Class<RESOLVESTAGETYPE> getActualClass();

}
