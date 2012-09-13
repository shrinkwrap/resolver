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
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.NonUniqueResultException;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor;

/**
 * Represents the formatting stage of Maven-based resolution in which the resolved artifact is returned in the desired
 * format. Supports extensible formats by optionally supplying a {@link FormatProcessor}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface MavenFormatStage extends FormatStage {
    /**
     * Formats the resultant artifacts as an array of {@link ResolvedArtifactInfo}s. If nothing matches resolution, an
     * empty array will be returned.
     *
     * @param type
     * @return
     * @throws IllegalArgumentException
     *             If the type is not specified
     */
    ResolvedArtifactInfo[] as(Class<ResolvedArtifactInfo> type) throws IllegalArgumentException;

    /**
     * Formats the resultant artifact as a {@link ResolvedArtifactInfo}; assumes a single artifact is returned from
     * resolution.
     *
     * @param type
     * @return
     * @throws IllegalArgumentException
     *             If the type is not specified
     * @throws NonUniqueResultException
     *             If the resolution resulted in more than one result
     * @throws NoResolvedResultException
     *             If the resolution did not yield any result
     */
    ResolvedArtifactInfo asSingle(Class<ResolvedArtifactInfo> type) throws IllegalArgumentException,
        NonUniqueResultException, NoResolvedResultException;
}
