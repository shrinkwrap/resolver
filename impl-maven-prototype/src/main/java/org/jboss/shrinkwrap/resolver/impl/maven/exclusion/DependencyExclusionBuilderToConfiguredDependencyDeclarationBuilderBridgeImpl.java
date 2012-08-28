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
package org.jboss.shrinkwrap.resolver.impl.maven.exclusion;

import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfiguredDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusion;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridgeImpl
    extends
    AbstractDependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase<DependencyDeclaration, ConfiguredDependencyDeclarationBuilder, MavenResolutionFilter, ConfiguredResolveStage, MavenStrategyStage, MavenFormatStage, DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge, MavenResolutionStrategy>
    implements DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge {

    private final ConfiguredDependencyDeclarationBuilder parent;

    public DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridgeImpl(
        Set<DependencyExclusion> exclusions, ConfiguredDependencyDeclarationBuilder parent) {
        super();
        this.parent = parent;
    }

    @Override
    public ConfiguredDependencyDeclarationBuilder endExclusion() throws CoordinateBuildException {
        return parent.addExclusion(exclusionBuilder.build().getAddress());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.exclusion.AbstractDependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase#getActualClass()
     */
    @Override
    Class<DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge> getActualClass() {
        return DependencyExclusionBuilderToConfiguredDependencyDeclarationBuilderBridge.class;
    }

}
