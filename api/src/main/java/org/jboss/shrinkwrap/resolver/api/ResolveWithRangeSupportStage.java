/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
 * Represents the stage in resolution in which the user supplies {@link Coordinate} address for version resolution in a
 * repository-based {@link ResolverSystem}.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface ResolveWithRangeSupportStage<
        DEPENDENCYTYPE extends Coordinate,
        COORDINATETYPE extends Coordinate,
        RESOLUTIONFILTERTYPE extends ResolutionFilter,
        RESOLVESTAGETYPE extends ResolveStage<DEPENDENCYTYPE, RESOLUTIONFILTERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, RESOLVEDTYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>,
        STRATEGYSTAGETYPE extends StrategyStage<DEPENDENCYTYPE, RESOLUTIONFILTERTYPE, RESOLVEDTYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>,
        RESOLVEDTYPE extends ResolvedArtifact<RESOLVEDTYPE>,
        FORMATSTAGETYPE extends FormatStage<RESOLVEDTYPE>,
        RESOLUTIONSTRATEGYTYPE extends ResolutionStrategy<DEPENDENCYTYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>,
        VERSIONRANGERESULTTYPE extends VersionRangeResult<COORDINATETYPE>
        > extends ResolveStage<DEPENDENCYTYPE, RESOLUTIONFILTERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, RESOLVEDTYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE> {

    /**
     * Performs version range resolution of specified artifact defined in canonical form. Returns info about available versions.
     *
     * @param coordinate coordinate in canonical form containing version range:
     *                   <a href="http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html">Maven doc</a>}
     * @return Info about available versions.StrategyStage
     * @throws IllegalArgumentException If no coordinate is supplied
     */
    VERSIONRANGERESULTTYPE resolveVersionRange(String coordinate) throws IllegalArgumentException;
}
