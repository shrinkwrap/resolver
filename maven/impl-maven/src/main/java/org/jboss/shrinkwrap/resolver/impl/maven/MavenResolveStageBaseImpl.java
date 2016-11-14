/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven;

import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Base implementation of Maven Resolve Stages
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
abstract class MavenResolveStageBaseImpl<RESOLVESTAGETYPE extends MavenResolveStageBase<RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
        extends ResolveStageBaseImpl<RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    protected MavenResolveStageBaseImpl(final MavenWorkingSession session) {
        super(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see ResolveStageBaseImpl#resolveVersionRange(String)
     */
    @Override
    public MavenVersionRangeResult resolveVersionRange(final String coordinate) throws IllegalArgumentException {
        Validate.isNullOrEmpty(coordinate);

        final MavenCoordinate mavenCoordinate = MavenCoordinates.createCoordinate(coordinate);
        return this.getMavenWorkingSession().resolveVersionRange(mavenCoordinate);
    }
}