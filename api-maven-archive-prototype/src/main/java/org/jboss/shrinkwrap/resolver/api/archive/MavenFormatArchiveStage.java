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
package org.jboss.shrinkwrap.resolver.api.archive;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.NonUniqueResultException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;

/**
 * Represents the formatting stage of resolution in which the resolved archive is returned as a ShrinkWrap
 * {@link Archive}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface MavenFormatArchiveStage extends MavenFormatStage {
    /**
     * Formats the resultant artifacts as an array of {@link Archive}s. If nothing matches resolution, an empty array
     * will be returned.
     *
     * @param type
     * @return
     * @throws IllegalArgumentException
     *             If the type is not specified
     */
    <ARCHIVETYPE extends Archive<ARCHIVETYPE>> ARCHIVETYPE[] as(Class<ARCHIVETYPE> type)
        throws IllegalArgumentException;

    /**
     * Formats the resultant artifact as an {@link Archive}; assumes a single artifact is returned from resolution.
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
    <ARCHIVETYPE extends Archive<ARCHIVETYPE>> ARCHIVETYPE asSingle(Class<ARCHIVETYPE> type)
        throws IllegalArgumentException, NonUniqueResultException, NoResolvedResultException;
}
