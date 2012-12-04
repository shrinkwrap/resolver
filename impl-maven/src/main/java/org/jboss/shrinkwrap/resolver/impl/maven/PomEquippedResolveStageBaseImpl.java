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

import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptAllStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.CombinedStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.task.AddScopedDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ResolveVersionFromMetadataTask;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Base support for implementations of a {@link PomEquippedResolveStage}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public abstract class PomEquippedResolveStageBaseImpl<EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
        extends ResolveStageBaseImpl<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> implements
        PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    public PomEquippedResolveStageBaseImpl(final MavenWorkingSession session) {
        super(LoadPomDependenciesTask.INSTANCE.execute(session));
    }

    @Override
    public final FORMATSTAGETYPE importRuntimeAndTestDependencies() {
        new AddScopedDependenciesTask(ScopeType.values()).execute(this.getMavenWorkingSession());
        return importAnyDependencies(AcceptAllStrategy.INSTANCE);
    }

    @Override
    public final FORMATSTAGETYPE importRuntimeAndTestDependencies(final MavenResolutionStrategy strategy)
            throws IllegalArgumentException {

        Validate.notNull(strategy, "Specified strategy for importing test dependencies must not be null");

        new AddScopedDependenciesTask(ScopeType.values()).execute(this.getMavenWorkingSession());
        return importAnyDependencies(strategy);
    }

    @Override
    public final FORMATSTAGETYPE importRuntimeDependencies() {

        ScopeType[] scopes = new ScopeType[] { ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.RUNTIME, ScopeType.SYSTEM };

        new AddScopedDependenciesTask(ScopeType.values()).execute(this.getMavenWorkingSession());
        return importAnyDependencies(new AcceptScopesStrategy(scopes));
    }

    @Override
    public final FORMATSTAGETYPE importRuntimeDependencies(final MavenResolutionStrategy strategy)
            throws IllegalArgumentException {

        Validate.notNull(strategy, "Specified strategy for importing test dependencies must not be null");

        final ScopeType[] scopes = new ScopeType[] { ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.RUNTIME,
                ScopeType.SYSTEM };

        new AddScopedDependenciesTask(ScopeType.values()).execute(this.getMavenWorkingSession());
        final MavenResolutionStrategy scopeStrategy = new AcceptScopesStrategy(scopes);
        final MavenResolutionStrategy combined = new CombinedStrategy(scopeStrategy, strategy);

        return importAnyDependencies(combined);
    }

    private FORMATSTAGETYPE importAnyDependencies(final MavenResolutionStrategy strategy) {
        // resolve
        return this.createStrategyStage().using(strategy);

    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.ResolveStageBaseImpl#resolveVersion(org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate)
     */
    @Override
    protected String resolveVersion(final MavenDependency dependency) throws IllegalArgumentException {
        return new ResolveVersionFromMetadataTask(dependency).execute(this.getMavenWorkingSession());
    }

}
