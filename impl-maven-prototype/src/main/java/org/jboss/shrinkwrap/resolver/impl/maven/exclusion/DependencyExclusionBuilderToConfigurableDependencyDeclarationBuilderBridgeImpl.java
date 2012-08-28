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

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfigurableDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl
    extends
    AbstractDependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase<ConfigurableDependencyDeclarationBuilder, ConfigurableResolveStage, MavenStrategyStage, MavenFormatStage, DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge>
    implements DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge {

    private final ConfigurableDependencyDeclarationBuilder parent;

    public DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl(
        ConfigurableDependencyDeclarationBuilder parent) {
        super();
        this.parent = parent;
    }

    @Override
    public ConfigurableDependencyDeclarationBuilder endExclusion() throws CoordinateBuildException {
        return parent.addExclusion(exclusionBuilder.build().getAddress());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.exclusion.AbstractDependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase#getActualClass()
     */
    @Override
    Class<DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge> getActualClass() {
        return DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge.class;
    }
}
