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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidEnvironmentException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfigurableDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge;
import org.jboss.shrinkwrap.resolver.impl.maven.dependency.ConfigurableDependencyDeclarationBuilderImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureFromPluginTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureFromPomTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsTask;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ConfigurableResolveStageImpl
        extends
        AbstractResolveStageBase<ConfigurableDependencyDeclarationBuilder, DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge, ConfigurableResolveStage>
        implements ConfigurableResolveStage {

    public ConfigurableResolveStageImpl(MavenWorkingSession session) {
        super(session);
    }

    @Override
    public ConfigurableDependencyDeclarationBuilder addDependency() {
        return new ConfigurableDependencyDeclarationBuilderImpl(session);
    }

    @Override
    public ConfigurableDependencyDeclarationBuilder addDependency(String coordinate) throws CoordinateParseException {
        return new ConfigurableDependencyDeclarationBuilderImpl(session).and(coordinate);
    }

    @Override
    public ConfigurableResolveStage configureSettings(File settingsXmlFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        this.session = new ConfigureSettingsTask(settingsXmlFile).execute(session);
        return new ConfigurableResolveStageImpl(session);
    }

    @Override
    public ConfigurableResolveStage configureSettings(String pathToSettingsXmlFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        this.session = new ConfigureSettingsTask(pathToSettingsXmlFile).execute(session);
        return new ConfigurableResolveStageImpl(session);
    }

    @Override
    public ConfigurableResolveStage configureFromPom(File pomFile, String... profiles) throws IllegalArgumentException {
        this.session = new ConfigureFromPomTask(pomFile, profiles).execute(session);
        return new ConfigurableResolveStageImpl(session);
    }

    @Override
    public ConfigurableResolveStage configureFromPom(String pathToPomFile, String... profiles) throws IllegalArgumentException {
        this.session = new ConfigureFromPomTask(pathToPomFile, profiles).execute(session);
        return new ConfigurableResolveStageImpl(session);
    }

    @Override
    public ConfigurableResolveStage configureFromPlugin() throws InvalidEnvironmentException {
        this.session = new ConfigureFromPluginTask().execute(session);
        return new ConfigurableResolveStageImpl(session);
    }

    @Override
    public MavenStrategyStage resolve(String coordinate) throws IllegalArgumentException {
        return resolve(new ConfigurableDependencyDeclarationBuilderImpl(session), coordinate);
    }

    @Override
    public MavenStrategyStage resolve(String... coordinates) throws IllegalArgumentException {
        return resolve(new ConfigurableDependencyDeclarationBuilderImpl(session), coordinates);
    }

    @Override
    public MavenStrategyStage resolve(DependencyDeclaration coordinate) throws IllegalArgumentException {
        return resolve(new ConfigurableDependencyDeclarationBuilderImpl(session), coordinate);
    }

    @Override
    public MavenStrategyStage resolve(DependencyDeclaration... coordinates) throws IllegalArgumentException {
        return resolve(new ConfigurableDependencyDeclarationBuilderImpl(session), coordinates);
    }

}
