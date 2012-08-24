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
 * Fluent factory base for creating new {@link Coordinate} instances, forwarding control to the relevant
 * {@link ResolveStage} when {@link CoordinateBuilder#resolve()} is called.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface CoordinateBuilder<COORDINATETYPE extends Coordinate, COORDINATEBUILDERTYPE extends CoordinateBuilder<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, RESOLUTIONFILTERTYPE extends ResolutionFilter<COORDINATETYPE>, RESOLVESTAGETYPE extends ResolveStage<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, STRATEGYSTAGETYPE extends StrategyStage<COORDINATETYPE, RESOLUTIONFILTERTYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, FORMATSTAGETYPE extends FormatStage, RESOLUTIONSTRATEGYTYPE extends ResolutionStrategy<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>> {

    /**
     * Creates new {@link Coordinate} instance(s) from the properties specified in this builder, forwarding control to
     * the next {@link StrategyStage}
     *
     * @return
     * @throws CoordinateBuildException
     *             If the properties in this builder violate the rules governing a complete {@link Coordinate}
     *             definition
     */
    STRATEGYSTAGETYPE resolve() throws CoordinateBuildException;

    /**
     * Creates new {@link Coordinate} instance(s) from the properties specified in <code>this</code> builder, returning
     * a new builder such that another {@link Coordinate} instance may be created
     *
     * @return
     */
    COORDINATEBUILDERTYPE and();

    /**
     * Creates new {@link Coordinate} instance(s) from the properties specified in <code>this</code> builder, returning
     * a new builder such that another {@link Coordinate} instance may be created from the specified canonical form
     *
     * @param coordinate
     *            The canonical form of the coordinate to parse into the the builder
     * @return
     * @throws CoordinateParseException
     *             If the canonical form cannot be parsed into builder properties
     * @throws IllegalArgumentException
     *             If no coordinate was specified
     */
    COORDINATEBUILDERTYPE and(String coordinate) throws CoordinateParseException, IllegalArgumentException;

}
