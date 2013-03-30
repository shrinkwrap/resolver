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
package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.resolver.api.FormatStage;
import org.jboss.shrinkwrap.resolver.api.ResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;

/**
 * Defines the contract for operations denoting a {@link ResolverStage} has been configured via POM (Project Object
 * Model) metadata
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface PomEquippedResolveStageBase<RESOLVESTAGETYPE extends MavenResolveStageBase<RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
    extends MavenResolveStageBase<RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, MavenResolveVersionsStageBase {
    /**
     * Resolves dependencies in all scopes as defined by the POM metadata.
     *
     * @return The next {@link FormatStage}
     */
    FORMATSTAGETYPE importRuntimeAndTestDependencies();

    /**
     * Resolves dependencies in all scopes as defined by the POM metadata, using the additional
     * {@link ResolutionStrategy}.
     *
     * @param strategy
     * @return
     * @throws IllegalArgumentException
     *             If no strategy is specified
     */
    FORMATSTAGETYPE importRuntimeAndTestDependencies(MavenResolutionStrategy strategy) throws IllegalArgumentException;

    /**
     * Resolves all runtime dependencies defined by the POM metadata. Amounts to scopes: {@link ScopeType#COMPILE},
     * {@link ScopeType#IMPORT}, {@link ScopeType#RUNTIME}, {@link ScopeType#SYSTEM}
     *
     * @return The next {@link FormatStage}
     */
    FORMATSTAGETYPE importRuntimeDependencies();

    /**
     * Resolves all dependencies defined by the POM metadata, using the additional {@link ResolutionStrategy}. Amounts
     * to scopes: {@link ScopeType#COMPILE}, {@link ScopeType#IMPORT}, {@link ScopeType#RUNTIME},
     * {@link ScopeType#SYSTEM}
     *
     * @param strategy
     * @return
     * @throws IllegalArgumentException
     *             If no strategy is specified
     */
    FORMATSTAGETYPE importRuntimeDependencies(MavenResolutionStrategy strategy) throws IllegalArgumentException;
}
