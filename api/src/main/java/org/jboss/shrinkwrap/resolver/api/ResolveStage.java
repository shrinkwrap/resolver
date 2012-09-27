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
package org.jboss.shrinkwrap.resolver.api;

import java.util.Collection;

/**
 * Represents the stage in resolution in which the user supplies (a) {@link Coordinate} address(es) for resolution in a
 * repository-based {@link ResolverSystem}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface ResolveStage<COORDINATETYPE extends Coordinate, RESOLUTIONFILTERTYPE extends ResolutionFilter, RESOLVESTAGETYPE extends ResolveStage<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, STRATEGYSTAGETYPE extends StrategyStage<COORDINATETYPE, RESOLUTIONFILTERTYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, FORMATSTAGETYPE extends FormatStage, RESOLUTIONSTRATEGYTYPE extends ResolutionStrategy<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>> {

    /**
     * Begins resolution of the prior-defined dependencies, returning the next {@link StrategyStage} for the user to
     * define the {@link ResolutionStrategy}
     *
     * @return
     * @throws IllegalStateException
     *             If no dependencies have yet been added
     * @throws ResolutionException
     *             If an error occured in resolution
     */
    STRATEGYSTAGETYPE resolve() throws IllegalStateException, ResolutionException;

    /**
     * Begins resolution by defining the single desired dependency (in canonical form), returning the next
     * {@link StrategyStage} for the user to define the {@link ResolutionStrategy}. Previously-added dependencies will
     * be included in resolution.
     *
     * @param canonicalForm
     * @return
     * @throws IllegalArgumentException
     *             If no coordinate is supplied
     * @throws ResolutionException
     *             If an error occurred in resolution
     * @throws CoordinateParseException
     *             If the specified canonical form is invalid
     */
    STRATEGYSTAGETYPE resolve(String canonicalForm) throws IllegalArgumentException, ResolutionException,
        CoordinateParseException;

    /**
     * Begins resolution by defining a set of desired dependencies (in canonical form), returning the next
     * {@link StrategyStage} for the user to define the {@link ResolutionStrategy}. Previously-added dependencies will
     * be included in resolution.
     *
     * @param canonicalForms
     * @return
     * @throws IllegalArgumentException
     *             If no coordinates are supplied
     * @throws ResolutionException
     *             If an error occurred in resolution
     * @throws CoordinateParseException
     *             If one or more of the specified canonical forms is invalid
     */
    STRATEGYSTAGETYPE resolve(String... canonicalForms) throws IllegalArgumentException, ResolutionException,
        CoordinateParseException;

    /**
     * Begins resolution by defining a {@link Collection} of desired dependencies (in canonical form), returning the
     * next {@link StrategyStage} for the user to define the {@link ResolutionStrategy}. Previously-added dependencies
     * will be included in resolution.
     *
     * @param canonicalForms
     * @return
     * @throws IllegalArgumentException
     *             If no coordinates are supplied
     * @throws ResolutionException
     *             If an error occurred in resolution
     * @throws CoordinateParseException
     *             If one or more of the specified canonical forms is invalid
     *
     */
    STRATEGYSTAGETYPE resolve(Collection<String> canonicalForms) throws IllegalArgumentException, ResolutionException,
        CoordinateParseException;

    /**
     * Adds the specified coordinate to be resolved
     *
     * @return
     * @throws IllegalArgumentException
     *             If no dependency is supplied
     */
    RESOLVESTAGETYPE addDependency(COORDINATETYPE dependency) throws IllegalArgumentException;

    /**
     * Adds the specified coordinates to be resolved
     *
     * @return
     * @throws IllegalArgumentException
     *             If no dependencies are supplied
     */
    RESOLVESTAGETYPE addDependencies(COORDINATETYPE... dependencies) throws IllegalArgumentException;

    /**
     * Adds the specified dependencies to be resolved
     *
     * @return
     * @throws IllegalArgumentException
     *             If the {@link Collection} is null
     */
    RESOLVESTAGETYPE addDependencies(Collection<COORDINATETYPE> dependencies) throws IllegalArgumentException;

}
