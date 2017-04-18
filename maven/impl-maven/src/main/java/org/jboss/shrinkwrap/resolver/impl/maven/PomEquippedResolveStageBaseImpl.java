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
import org.jboss.shrinkwrap.resolver.impl.maven.task.AddScopedDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ResolveVersionFromMetadataTask;

/**
 * Base support for implementations of a {@link PomEquippedResolveStage}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public abstract class PomEquippedResolveStageBaseImpl<EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
        extends MavenResolveStageBaseImpl<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> implements
        PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    public PomEquippedResolveStageBaseImpl(final MavenWorkingSession session) {
        super(LoadPomDependenciesTask.INSTANCE.execute(session));
    }

    @Override
    public EQUIPPEDRESOLVESTAGETYPE importTestDependencies() {
        return importAnyDependencies(ScopeType.TEST);
    }

    @Override
    public EQUIPPEDRESOLVESTAGETYPE importDependencies(ScopeType... scopes) throws IllegalArgumentException {
        if(scopes==null || scopes.length==0) {
            throw new IllegalArgumentException("Scopes must be defined");
        }
        return importAnyDependencies(scopes);
    }

    @Override
    public EQUIPPEDRESOLVESTAGETYPE importRuntimeAndTestDependencies() {
        return importAnyDependencies(ScopeType.RUNTIME, ScopeType.TEST, ScopeType.IMPORT, ScopeType.SYSTEM);
    }

    @Override
    public EQUIPPEDRESOLVESTAGETYPE importRuntimeDependencies() {
        return importAnyDependencies(ScopeType.RUNTIME, ScopeType.IMPORT, ScopeType.SYSTEM);
    }

    @Override
    public EQUIPPEDRESOLVESTAGETYPE importCompileAndRuntimeDependencies() {
        return importAnyDependencies(ScopeType.COMPILE, ScopeType.RUNTIME, ScopeType.IMPORT, ScopeType.SYSTEM);
    }

    @SuppressWarnings("unchecked")
    public EQUIPPEDRESOLVESTAGETYPE importAnyDependencies(ScopeType... scopes) {
        new AddScopedDependenciesTask(scopes).execute(this.getMavenWorkingSession());
        return (EQUIPPEDRESOLVESTAGETYPE) this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.task.ResolveVersionFromMetadataTask
     */
    @Override
    protected String resolveVersion(final MavenDependency dependency) throws IllegalArgumentException {
        return new ResolveVersionFromMetadataTask(dependency).execute(this.getMavenWorkingSession());
    }

}
