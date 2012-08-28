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

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.ResolveStage;
import org.jboss.shrinkwrap.resolver.api.ResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclarationBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclarationBuilderBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderBase;

/**
 * Defines base operations for Maven-specific {@link ResolverSystem}s; provides an extensibility point. For concrete
 * end-user use, look to {@link MavenResolverSystem}.
 *
 * @param <COORDINATETYPE>
 * @param <COORDINATEBUILDERTYPE>
 * @param <RESOLVESTAGETYPE>
 * @param <STRATEGYSTAGETYPE>
 * @param <FORMATSTAGETYPE>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a
 */
public interface MavenResolveStageBase<COORDINATETYPE extends DependencyDeclarationBase, COORDINATEBUILDERTYPE extends DependencyDeclarationBuilderBase<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, RESOLUTIONFILTERTYPE extends MavenResolutionFilterBase<COORDINATETYPE>, EXCLUSIONBUILDERTYPE extends DependencyExclusionBuilderBase<EXCLUSIONBUILDERTYPE>, RESOLVESTAGETYPE extends MavenResolveStageBase<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<COORDINATETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>, FORMATSTAGETYPE extends MavenFormatStage, RESOLUTIONSTRATEGYTYPE extends MavenResolutionStrategyBase<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>>
    extends
    ResolveStage<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLUTIONFILTERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE> {

    /**
     * Configures the Maven Resolver System Settings from metadata contained in the specified settings.xml {@link File}.
     *
     * @param settingsXmlFile
     * @return
     * @throws IllegalArgumentException
     *             If no file was specified, if the file does not exist or points to a directory
     * @throws InvalidConfigurationFileException
     *             If specified file does not represent a valid settings.xml file
     */
    RESOLVESTAGETYPE configureSettings(File settingsXmlFile) throws IllegalArgumentException,
        InvalidConfigurationFileException;

    /**
     * Configures the Maven Resolver System Settings from metadata contained in the settings.xml file located at the
     * specified path. The path will be represented as a new {@link File} by means of {@link File#File(String)}
     *
     * @param pathToSettingsXmlFile
     * @return
     * @throws IllegalArgumentException
     *             If no path was specified, or if the path points to a file which does not exist or is a directory
     * @throws InvalidConfigurationFileException
     *             If specified path does not point to a valid settings.xml file
     */
    RESOLVESTAGETYPE configureSettings(String pathToSettingsXmlFile) throws IllegalArgumentException,
        InvalidConfigurationFileException;

}
