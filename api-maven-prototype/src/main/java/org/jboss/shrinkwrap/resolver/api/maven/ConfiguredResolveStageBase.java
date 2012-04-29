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

/**
 * Defines the contract for operations denoting a {@link ResolverStage} has been configured via POM (Project Object
 * Model) metadata
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface ConfiguredResolveStageBase<FORMATSTAGETYPE extends MavenFormatStage> {
    /**
     * Resolves all dependencies defined by the POM metadata in "test" scope
     *
     * @return The next {@link FormatStage}
     */
    FORMATSTAGETYPE importTestDependencies();

    /**
     * Resolves all dependencies defined by the POM metadata in "test" scope, using the additional
     * {@link ResolutionStrategy}
     *
     * TODO: Resolution Strategy here to define filtering? Is it possible that this would conflict with the Resolution
     * Strategy under the hood here which grabs everything in test scope? Do we put in place some ResolutionStrategy
     * Chain? This should be worked out when we figure the Resolution Strategy API.
     *
     * @param strategy
     * @return
     * @throws IllegalArgumentException
     *             If no strategy is specified
     */
    FORMATSTAGETYPE importTestDependencies(ResolutionStrategy strategy) throws IllegalArgumentException;

    /**
     * Resolves all dependencies defined by the POM metadata
     *
     * @return The next {@link FormatStage}
     */
    FORMATSTAGETYPE importDefinedDependencies();

    /**
     * Resolves all dependencies defined by the POM metadata, using the additional {@link ResolutionStrategy}
     *
     * TODO: Resolution Strategy here to define filtering? Is it possible that this would conflict with the Resolution
     * Strategy under the hood here which grabs everything in test scope? Do we put in place some ResolutionStrategy
     * Chain? This should be worked out when we figure the Resolution Strategy API.
     *
     * @param strategy
     * @return
     * @throws IllegalArgumentException
     *             If no strategy is specified
     */
    FORMATSTAGETYPE importDefinedDependencies(ResolutionStrategy strategy) throws IllegalArgumentException;
}
