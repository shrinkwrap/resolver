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

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.NonTransitiveStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Base support for implementations of {@link MavenStrategyStage}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public abstract class MavenStrategyStageBaseImpl<STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
        implements MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, MavenWorkingSessionContainer {

    private static final Logger log = Logger.getLogger(MavenStrategyStageBaseImpl.class.getName());

    private final MavenWorkingSession session;

    public MavenStrategyStageBaseImpl(final MavenWorkingSession session) {
        this.session = session;
    }

    @Override
    public FORMATSTAGETYPE withTransitivity() {
        return using(TransitiveStrategy.INSTANCE);
    }

    @Override
    public FORMATSTAGETYPE withoutTransitivity() {
        return using(NonTransitiveStrategy.INSTANCE);
    }

    @Override
    public MavenWorkingSession getMavenWorkingSession() {
        return session;
    }

    @Override
    public FORMATSTAGETYPE using(final MavenResolutionStrategy strategy) throws IllegalArgumentException {
        // first, get dependencies specified for resolution in the session
        Validate.notEmpty(session.getDependenciesForResolution(), "No dependencies were set for resolution");

        final Collection<MavenResolvedArtifact> resolvedArtifacts = session.resolveDependencies(strategy);

        // Proceed to format stage
        return this.createFormatStage(resolvedArtifacts);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase#withClassPathResolution(boolean)
     */
    @Override
    public STRATEGYSTAGETYPE withClassPathResolution(boolean useClassPathResolution) {

        log.log(Level.WARNING, "Using deprecated withClassPathResolution(boolean) method, that might be activated after pom.xml resolution. Please configure classpath resolution via Maven.configureResolver() call instead.");

        if (!useClassPathResolution) {
            this.session.disableClassPathWorkspaceReader();
        }
        return this.covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase#withMavenCentralRepo(boolean)
     */
    @Override
    public STRATEGYSTAGETYPE withMavenCentralRepo(boolean useMavenCentral) {

        log.log(Level.WARNING, "Using deprecated withMavenCentralRepo(boolean) method, that might be activated after pom.xml resolution. Please configure Maven Central repository via Maven.configureResolver() call instead.");

        if (!useMavenCentral) {
            this.session.disableMavenCentral();
        }
        return this.covarientReturn();
    }

    private STRATEGYSTAGETYPE covarientReturn() {
        return this.getActualClass().cast(this);
    }

    protected abstract Class<STRATEGYSTAGETYPE> getActualClass();

    /**
     * Creates a new {@link MavenFormatStage} instance for the current {@link MavenWorkingSession}
     *
     * @param filteredArtifacts Required
     * @return
     */
    protected abstract FORMATSTAGETYPE createFormatStage(final Collection<MavenResolvedArtifact> filteredArtifacts);

}
