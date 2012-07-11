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

import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclarationBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclarationBuilderBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <COORDINATETYPE>
 * @param <COORDINATEBUILDERTYPE>
 * @param <RESOLVESTAGETYPE>
 * @param <STRATEGYSTAGETYPE>
 * @param <FORMATSTAGETYPE>
 * @param <EXCLUSIONBUILDERTYPE>
 */
abstract class AbstractDependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase<COORDINATETYPE extends DependencyDeclarationBase, COORDINATEBUILDERTYPE extends DependencyDeclarationBuilderBase<COORDINATETYPE, COORDINATEBUILDERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, RESOLVESTAGETYPE extends MavenResolveStageBase<COORDINATETYPE, COORDINATEBUILDERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<COORDINATETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage, EXCLUSIONBUILDERTYPE extends DependencyExclusionBuilderBase<EXCLUSIONBUILDERTYPE>>
        implements
        DependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase<COORDINATETYPE, COORDINATEBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, EXCLUSIONBUILDERTYPE> {

    protected DependencyExclusionBuilderImpl exclusionBuilder;

    protected AbstractDependencyExclusionBuilderToDependencyDeclarationBuilderBridgeBase() {
        this.exclusionBuilder = new DependencyExclusionBuilderImpl();
    }

    @SuppressWarnings("unchecked")
    @Override
    public EXCLUSIONBUILDERTYPE groupId(String groupId) throws IllegalArgumentException {
        exclusionBuilder.groupId(groupId);
        return (EXCLUSIONBUILDERTYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EXCLUSIONBUILDERTYPE artifactId(String artifactId) throws IllegalArgumentException {
        exclusionBuilder.artifactId(artifactId);
        return (EXCLUSIONBUILDERTYPE) this;
    }

    @Override
    public String getGroupId() {
        return exclusionBuilder.getGroupId();
    }

    @Override
    public String getArtifactId() {
        return exclusionBuilder.getArtifactId();
    }

    @Override
    public String getAddress() {
        return exclusionBuilder.getAddress();
    }

}
