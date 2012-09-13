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

/**
 * Represents the stage in resolution in which the user supplies (a) {@link Coordinate} address(es) for resolution in a
 * repository-based {@link ResolverSystem}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface ResolveStage<COORDINATETYPE extends Coordinate, RESOLUTIONFILTERTYPE extends ResolutionFilter<COORDINATETYPE>, RESOLVESTAGETYPE extends ResolveStage<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, STRATEGYSTAGETYPE extends StrategyStage<COORDINATETYPE, RESOLUTIONFILTERTYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, FORMATSTAGETYPE extends FormatStage, RESOLUTIONSTRATEGYTYPE extends ResolutionStrategy<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>> {

    /**
     * Begins resolution of the prior-defined coordinate(s), returning the next {@link StrategyStage} for the user to
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
     * Begins resolution by defining the single desired coordinate, returning the next {@link StrategyStage} for the
     * user to define the {@link ResolutionStrategy}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     *             If no coordinate is supplied
     * @throws ResolutionException
     *             If an error occured in resolution
     */
    STRATEGYSTAGETYPE resolve(String coordinate) throws IllegalArgumentException, ResolutionException;

    /**
     * Begins resolution by defining a set of desired coordinates, returning the next {@link StrategyStage} for the user
     * to define the {@link ResolutionStrategy}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     *             If no coordinates are supplied
     * @throws ResolutionException
     *             If an error occured in resolution
     */
    STRATEGYSTAGETYPE resolve(String... coordinates) throws IllegalArgumentException, ResolutionException;

    /**
     * Begins resolution by defining the single desired coordinate, returning the next {@link StrategyStage} for the
     * user to define the {@link ResolutionStrategy}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     *             If no coordinate is supplied
     * @throws ResolutionException
     *             If an error occured in resolution
     */
    STRATEGYSTAGETYPE resolve(COORDINATETYPE coordinate) throws IllegalArgumentException, ResolutionException;

    /**
     * Begins resolution by defining a set of desired coordinates, returning the next {@link StrategyStage} for the user
     * to define the {@link ResolutionStrategy}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     *             If no coordinates are supplied
     * @throws ResolutionException
     *             If an error occured in resolution
     */
    STRATEGYSTAGETYPE resolve(COORDINATETYPE... coordinates) throws IllegalArgumentException, ResolutionException;

    /**
     * Adds the specified coordinate to be resolved
     *
     * @return
     * @throws IllegalArgumentException
     *             If no coordinate is supplied
     */
    RESOLVESTAGETYPE addDependency(COORDINATETYPE coordinate) throws IllegalArgumentException;

    /**
     * Adds the specified coordinate, initially parsed from the specified canonical form, to be resolved
     *
     * @return
     * @throws IllegalArgumentException
     *             If no coordinate is supplied
     * @throws CoordinateParseException
     *             If the dependency could not be parsed
     */
    RESOLVESTAGETYPE addDependency(String coordinate) throws CoordinateParseException, IllegalArgumentException;

    /**
     * Adds the specified coordinates to be resolved
     *
     * @return
     * @throws IllegalArgumentException
     *             If no coordinates are supplied
     */
    RESOLVESTAGETYPE addDependencies(COORDINATETYPE... coordinates) throws IllegalArgumentException;

    /**
     * Adds the specified coordinates, initially parsed from the specified canonical forms, to be resolved
     *
     * @return
     * @throws IllegalArgumentException
     *             If no coordinates are supplied
     */
    RESOLVESTAGETYPE addDependencies(String... coordinate) throws CoordinateParseException, IllegalArgumentException;

}
