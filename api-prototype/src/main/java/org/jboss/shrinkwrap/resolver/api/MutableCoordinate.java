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
 * Mutable representation of a single coordinate; an address in a repository-based system which may point to an
 * artifact. Systems may each have their own syntax for parsing the {@link String} form of a {@link MutableCoordinate},
 * or supply their own builders for programmatic creation.
 *
 * @param <COORDINATETYPE>
 *            Concrete type of this {@link MutableCoordinate} for covarient return
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface MutableCoordinate<COORDINATETYPE extends MutableCoordinate<COORDINATETYPE>> extends Coordinate {

    /**
     * Parses and returns this {@link MutableCoordinate} based on the specified {@link String} representation
     *
     * @return A coordinate parsed from the specified {@link String} representation
     * @throws IllegalArgumentException
     *             If no {@link String} representation of the coordinate was specified
     * @throws CoordinateBuildException
     *             If the specified argument could not be parsed into a {@link MutableCoordinate}
     */
    COORDINATETYPE address(String address) throws IllegalArgumentException, CoordinateBuildException;

}
