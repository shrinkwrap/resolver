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
 * Contract for an exclusion-based filter applied during resolution of a {@link Coordinate}. Applies a selective passthrough
 * mechanism. May be chained alongside other {@link ResolutionFilter}s to comprise a {@link ResolutionStrategy}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface ResolutionFilter<COORDINATETYPE extends Coordinate> {

    /**
     * Determines whether or not a {@link Coordinate} is accepted by this filter.
     *
     * @param coordinate
     * @return
     * @throws IllegalArgumentException
     */
    boolean accepts(COORDINATETYPE coordinate) throws IllegalArgumentException;
}
