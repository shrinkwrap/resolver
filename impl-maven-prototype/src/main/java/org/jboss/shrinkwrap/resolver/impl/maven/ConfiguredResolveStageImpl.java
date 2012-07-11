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
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.ResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfiguredDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.dependency.ConfiguredDependencyDeclarationBuilderImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsTask;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ConfiguredResolveStageImpl
        extends
        AbstractResolveStageBase<ConfiguredDependencyDeclarationBuilder, DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge, ConfiguredResolveStage>
        implements ConfiguredResolveStage {

    public ConfiguredResolveStageImpl(MavenWorkingSession session) {
        super(session);

        ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

        Validate.stateNotNull(session.getModel(),
                "Could not spawn ConfiguredResolveStage. An effective POM must be resolved first.");

        // store all dependency information to be able to retrieve versions later
        if (session.getModel().getDependencyManagement() != null) {
            Set<DependencyDeclaration> pomDependencyMngmt = MavenConverter.fromDependencies(session.getModel()
                    .getDependencyManagement().getDependencies(), stereotypes);
            session.getVersionManagement().addAll(pomDependencyMngmt);
        }

        // store all of the <dependencies> into version management
        Set<DependencyDeclaration> pomDefinedDependencies = MavenConverter.fromDependencies(session.getModel()
                .getDependencies(), stereotypes);

        session.getVersionManagement().addAll(pomDefinedDependencies);

    }

    @Override
    public MavenFormatStage importTestDependencies() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MavenFormatStage importTestDependencies(ResolutionStrategy strategy) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MavenFormatStage importDefinedDependencies() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MavenFormatStage importDefinedDependencies(ResolutionStrategy strategy) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfiguredDependencyDeclarationBuilder addDependency() {
        return new ConfiguredDependencyDeclarationBuilderImpl(session);
    }

    @Override
    public ConfiguredDependencyDeclarationBuilder addDependency(String coordinate) throws CoordinateParseException {
        return new ConfiguredDependencyDeclarationBuilderImpl(session).and(coordinate);
    }

    @Override
    public ConfiguredResolveStage configureSettings(File settingsXmlFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        this.session = new ConfigureSettingsTask(settingsXmlFile).execute(session);
        return new ConfiguredResolveStageImpl(session);
    }

    @Override
    public ConfiguredResolveStage configureSettings(String pathToSettingsXmlFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        this.session = new ConfigureSettingsTask(pathToSettingsXmlFile).execute(session);
        return new ConfiguredResolveStageImpl(session);
    }

    @Override
    public MavenStrategyStage resolve(String coordinate) throws IllegalArgumentException {
        return resolve(new ConfiguredDependencyDeclarationBuilderImpl(session), coordinate);
    }

    @Override
    public MavenStrategyStage resolve(String... coordinates) throws IllegalArgumentException {
        return resolve(new ConfiguredDependencyDeclarationBuilderImpl(session), coordinates);
    }

    @Override
    public MavenStrategyStage resolve(DependencyDeclaration coordinate) throws IllegalArgumentException {
        return resolve(new ConfiguredDependencyDeclarationBuilderImpl(session), coordinate);
    }

    @Override
    public MavenStrategyStage resolve(DependencyDeclaration... coordinates) throws IllegalArgumentException {
        return resolve(new ConfiguredDependencyDeclarationBuilderImpl(session), coordinates);
    }
}
