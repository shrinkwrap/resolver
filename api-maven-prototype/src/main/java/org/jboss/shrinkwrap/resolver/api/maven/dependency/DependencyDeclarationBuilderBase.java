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
package org.jboss.shrinkwrap.resolver.api.maven.dependency;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuilder;
import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilterBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategyBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MutableMavenCoordinateBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderBase;

/**
 * Base operations for a builder of <code><dependency /></code> declarations
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface DependencyDeclarationBuilderBase<COORDINATETYPE extends DependencyDeclarationBase, COORDINATEBUILDERTYPE extends DependencyDeclarationBuilderBase<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, RESOLUTIONFILTERTYPE extends MavenResolutionFilterBase<COORDINATETYPE, RESOLUTIONFILTERTYPE>, EXCLUSIONBUILDERTYPE extends DependencyExclusionBuilderBase<EXCLUSIONBUILDERTYPE>, RESOLVESTAGETYPE extends MavenResolveStageBase<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<COORDINATETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>, FORMATSTAGETYPE extends MavenFormatStage, RESOLUTIONSTRATEGYTYPE extends MavenResolutionStrategyBase<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>>
    extends
    MutableMavenCoordinateBase<COORDINATEBUILDERTYPE>,
    CoordinateBuilder<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE> {

    /**
     * Sets the scope, returning this builder. <code>null</code> value permitted; will default to
     * {@link ScopeType#COMPILE}
     *
     * @param scope
     * @return
     */
    COORDINATEBUILDERTYPE scope(ScopeType scope);

    /**
     * Sets whether or not this dependency is <code><optional /></code>; defaults to "false".
     *
     * @param optional
     * @return
     */
    COORDINATEBUILDERTYPE optional(boolean optional);

    /**
     * Creates and returns a builder for a new <code><exclusion /></code> element
     *
     * @return
     */
    EXCLUSIONBUILDERTYPE addExclusion();

    /**
     * Creates a new <code><exclusion /></code> element from the specified coordinate canonical form
     * <code>groupId:artifactId</code>, returning <code>this</code> {@link DependencyDeclarationBuilderBase}
     *
     * @return
     * @throws IllegalArgumentException
     *             If no canonical coordinate form was specified
     * @throws CoordinateParseException
     *             If the supplied canonical form could not be parsed
     */
    COORDINATEBUILDERTYPE addExclusion(String coordinates) throws IllegalArgumentException, CoordinateParseException;

    /**
     * Creates new <code><exclusion /></code> elements from the specified coordinate canonical forms
     * <code>groupId:artifactId</code>, returning <code>this</code> {@link DependencyDeclarationBuilderBase}
     *
     * @return
     * @throws IllegalArgumentException
     *             If no canonical coordinate forms were specified
     * @throws CoordinateParseException
     *             If the supplied canonical form(s) could not be parsed
     */
    COORDINATEBUILDERTYPE addExclusions(String... coordinates) throws IllegalArgumentException,
        CoordinateParseException;

}
