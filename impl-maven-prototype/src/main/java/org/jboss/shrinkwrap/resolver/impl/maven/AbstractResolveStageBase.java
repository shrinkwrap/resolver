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
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclarationBuilderBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderBase;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <COORDINATEBUILDERTYPE>
 * @param <EXCLUSIONBUILDERTYPE>
 * @param <RESOLVESTAGETYPE>
 */
public abstract class AbstractResolveStageBase<COORDINATEBUILDERTYPE extends DependencyDeclarationBuilderBase<DependencyDeclaration, COORDINATEBUILDERTYPE, MavenResolutionFilter, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, MavenStrategyStage, MavenFormatStage, MavenResolutionStrategy>, EXCLUSIONBUILDERTYPE extends DependencyExclusionBuilderBase<EXCLUSIONBUILDERTYPE>, RESOLVESTAGETYPE extends MavenResolveStageBase<DependencyDeclaration, COORDINATEBUILDERTYPE, MavenResolutionFilter, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, MavenStrategyStage, MavenFormatStage, MavenResolutionStrategy>>
    implements
    MavenResolveStageBase<DependencyDeclaration, COORDINATEBUILDERTYPE, MavenResolutionFilter, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, MavenStrategyStage, MavenFormatStage, MavenResolutionStrategy>,
    MavenWorkingSessionRetrieval {

    protected MavenWorkingSession session;

    public AbstractResolveStageBase(MavenWorkingSession session) {
        Validate.stateNotNull(session, "Maven Working session must not be null");
        this.session = session;
    }

    protected MavenStrategyStage resolve(@SuppressWarnings("rawtypes") DependencyDeclarationBuilderBase builder,
        String coordinate) throws IllegalArgumentException {
        return (MavenStrategyStage) builder.and(coordinate).resolve();
    }

    protected MavenStrategyStage resolve(@SuppressWarnings("rawtypes") DependencyDeclarationBuilderBase builder,
        String... coordinates) throws IllegalArgumentException {
        Validate.notNullAndNoNullValues(coordinates, "Coordinates for resolution must not be null nor empty.");
        for (String coords : coordinates) {
            builder.and(coords);
        }
        return (MavenStrategyStage) builder.resolve();
    }

    protected MavenStrategyStage resolve(@SuppressWarnings("rawtypes") DependencyDeclarationBuilderBase builder,
        DependencyDeclaration coordinate) throws IllegalArgumentException {
        Validate.notNull(coordinate, "Coordinates for resolution must not be null nor empty.");
        builder.and(coordinate.getAddress());
        return (MavenStrategyStage) builder.resolve();
    }

    protected MavenStrategyStage resolve(@SuppressWarnings("rawtypes") DependencyDeclarationBuilderBase builder,
        DependencyDeclaration... coordinates) throws IllegalArgumentException {
        Validate.notNullAndNoNullValues(coordinates, "Coordinates for resolution must not be null nor empty.");
        for (DependencyDeclaration coords : coordinates) {
            builder.and(coords.getAddress());
        }
        return (MavenStrategyStage) builder.resolve();
    }

    @Override
    public MavenWorkingSession getMavenWorkingSession() {
        return session;
    }

}
