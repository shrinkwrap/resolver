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
 */
public interface ResolveStage<COORDINATETYPE extends Coordinate, COORDINATEBUILDERTYPE extends CoordinateBuilder<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, RESOLVESTAGETYPE extends ResolveStage<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends StrategyStage<FORMATSTAGETYPE>, FORMATSTAGETYPE extends FormatStage> {

    /**
     * Begins resolution by defining the single desired coordinate, returning the next {@link StrategyStage} for the
     * user to define the {@link ResolutionStrategy}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     *             If no coordinate is supplied
     */
    STRATEGYSTAGETYPE resolve(String coordinate) throws IllegalArgumentException;

    /**
     * Begins resolution by defining a set of desired coordinates, returning the next {@link StrategyStage} for the user
     * to define the {@link ResolutionStrategy}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     *             If no coordinates are supplied
     */
    STRATEGYSTAGETYPE resolve(String... coordinates) throws IllegalArgumentException;

    /**
     * Begins resolution by defining the single desired coordinate, returning the next {@link StrategyStage} for the
     * user to define the {@link ResolutionStrategy}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     *             If no coordinate is supplied
     */
    STRATEGYSTAGETYPE resolve(COORDINATETYPE coordinate) throws IllegalArgumentException;

    /**
     * Begins resolution by defining a set of desired coordinates, returning the next {@link StrategyStage} for the user
     * to define the {@link ResolutionStrategy}
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     *             If no coordinates are supplied
     */
    STRATEGYSTAGETYPE resolve(COORDINATETYPE... coordinates) throws IllegalArgumentException;

    /**
     * Begins resolution by returning a {@link CoordinateBuilder} for the user to define explicitly what's to be
     * defined.
     *
     * @return
     */
    COORDINATEBUILDERTYPE addDependency();

    /**
     * Begins resolution by returning a {@link CoordinateBuilder}, initially parsed from the specified coordinate, for
     * the user to define explicitly what's to be defined.
     *
     * @return
     */
    COORDINATEBUILDERTYPE addDependency(String coordinate) throws CoordinateParseException;

}
