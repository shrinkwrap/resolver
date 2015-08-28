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
 * Defines the stage of resolution in which the user may supply a {@link ResolutionStrategy}
 *
 * @param <FORMATSTAGETYPE>
 *            Type of {@link FormatStage} after this {@link StrategyStage}
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface StrategyStage<COORDINATETYPE extends Coordinate, RESOLUTIONFILTERTYPE extends ResolutionFilter, RESOLVEDTYPE extends ResolvedArtifact<RESOLVEDTYPE>, FORMATSTAGETYPE extends FormatStage<RESOLVEDTYPE>, RESOLUTIONSTRATEGYTYPE extends ResolutionStrategy<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>> {

    /**
     * Defines the {@link ResolutionStrategy} to be used in resolution, returning the next {@link FormatStage}.
     *
     * @param strategy The {@link ResolutionStrategy} to be used in resolution.
     * @return The next {@link FormatStage}.
     * @throws IllegalArgumentException
     *             If the {@link ResolutionStrategy} is not specified
     */
    FORMATSTAGETYPE using(RESOLUTIONSTRATEGYTYPE strategy) throws IllegalArgumentException;

}
