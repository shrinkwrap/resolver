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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfiguredDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionRetrieval;
import org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenCoordinateParser;
import org.jboss.shrinkwrap.resolver.impl.maven.exclusion.DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridgeImpl;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ConfiguredDependencyDeclarationBuilderImpl
        extends
        AbstractDependencyDeclarationBuilderBase<DependencyDeclaration, ConfiguredDependencyDeclarationBuilder, MavenResolutionFilter, DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge, ConfiguredResolveStage, MavenStrategyStage, MavenFormatStage, MavenResolutionStrategy>
        implements ConfiguredDependencyDeclarationBuilder, MavenWorkingSessionRetrieval {

    private static final Logger log = Logger.getLogger(ConfiguredDependencyDeclarationBuilderImpl.class.getName());

    public ConfiguredDependencyDeclarationBuilderImpl(MavenWorkingSession session) {
        super(session);
    }

    @Override
    public DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge addExclusion() {
        return new DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridgeImpl(exclusions, this);
    }

    @Override
    protected String inferDependencyVersion() throws CoordinateBuildException {

        // is not able to infer anything, it was not configured
        if (Validate.isNullOrEmpty(version) || MavenCoordinateParser.UNKNOWN_VERSION.equals(version)) {
            DependencyDeclaration candidate = new DependencyDeclarationImpl(groupId, artifactId, type, classifier, version,
                    scope, optional, exclusions);

            // version is ignore here, so we have to iterate to get the dependency we are looking for
            if (session.getVersionManagement().contains(candidate)) {

                // get the dependency from internal dependencyManagement
                DependencyDeclaration resolved = null;
                Iterator<DependencyDeclaration> it = session.getVersionManagement().iterator();
                while (it.hasNext()) {
                    resolved = it.next();
                    if (resolved.equals(candidate)) {
                        break;
                    }
                }
                // we have resolved a version from dependency management
                this.version = resolved.getVersion();
                log.log(Level.FINE, "Resolved version {} from the POM file for the artifact {}",
                        new Object[] { resolved.getVersion(), candidate.getAddress() });

            }
        }

        if (Validate.isNullOrEmpty(version)) {
            throw new CoordinateBuildException(
                    MessageFormat
                            .format("Unable to get version for dependency specified by {0}:{1}:{2}:{3}:?, it was not provided in <dependencyManagement> section.",
                                    groupId, artifactId, type, classifier));
        }

        return version;

    }

    @Override
    public MavenStrategyStage resolve() throws CoordinateBuildException {
        return resolveInternally();
    }

}
