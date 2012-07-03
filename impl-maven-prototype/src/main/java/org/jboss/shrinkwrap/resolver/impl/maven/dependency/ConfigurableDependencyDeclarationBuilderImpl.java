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
package org.jboss.shrinkwrap.resolver.impl.maven.dependency;

import java.text.MessageFormat;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfigurableDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionRetrieval;
import org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenCoordinateParser;
import org.jboss.shrinkwrap.resolver.impl.maven.exclusion.DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl;

public class ConfigurableDependencyDeclarationBuilderImpl
        extends
        AbstractDependencyDeclarationBuilderBase<DependencyDeclaration, ConfigurableDependencyDeclarationBuilder, DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge, ConfigurableResolveStage, MavenStrategyStage, MavenFormatStage>
        implements ConfigurableDependencyDeclarationBuilder, MavenWorkingSessionRetrieval {

    public ConfigurableDependencyDeclarationBuilderImpl(MavenWorkingSession session) {
        super(session);
    }

    @Override
    public DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge addExclusion() {
        return new DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl(this);
    }

    @Override
    protected String inferDependencyVersion() throws CoordinateBuildException {
        if (Validate.isNullOrEmpty(version)) {
            throw new CoordinateBuildException(MessageFormat.format(
                    "Unable to get version for dependency specified by {0}:{1}:{2}:{3}:?, it was either null or empty.",
                    groupId, artifactId, type, classifier));
        }
        // is not able to infer anything, it was not configured
        if (MavenCoordinateParser.UNKNOWN_VERSION.equals(version)) {
            throw new CoordinateBuildException(
                    MessageFormat
                            .format("Unable to get version for dependency specified by {0}:{1}:{2}:{3}:?, no <dependencyManagement> section was provided.",
                                    groupId, artifactId, type, classifier));
        }
        return version;
    }
}
