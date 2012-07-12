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
package org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilterBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategyBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclarationBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclarationBuilderBase;

/**
 * Adapts {@link DependencyExclusionBuilderBase} types to build, returning the {@link DependencyDeclarationBuilderBase} which
 * spawned the <code><dependency><exclusion /></dependency></code> call.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface DependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase<COORDINATETYPE extends DependencyDeclarationBase, COORDINATEBUILDERTYPE extends DependencyDeclarationBuilderBase<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, RESOLUTIONFILTERTYPE extends MavenResolutionFilterBase<COORDINATETYPE, RESOLUTIONFILTERTYPE>, RESOLVESTAGETYPE extends MavenResolveStageBase<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<COORDINATETYPE, RESOLUTIONFILTERTYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, FORMATSTAGETYPE extends MavenFormatStage, EXCLUSIONBUILDERTYPE extends DependencyExclusionBuilderBase<EXCLUSIONBUILDERTYPE>, RESOLUTIONSTRATEGYTYPE extends MavenResolutionStrategyBase<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>>
        extends DependencyExclusionBuilderBase<EXCLUSIONBUILDERTYPE> {

    /**
     * Builds the <code><dependency><exclusion /></dependency></code> element from properties defined in this builder, and
     * returns the parent {@link DependencyDeclarationBuilderBase}
     *
     * @return
     * @throws CoordinateBuildException If the properties specified in the builder are invalid for creating a new
     *         {@link DependencyExclusionBase} instance
     */
    COORDINATEBUILDERTYPE endExclusion() throws CoordinateBuildException;

}
