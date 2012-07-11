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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.NoResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategyBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.MavenNonTransitiveStrategyImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.MavenTransitiveStrategyImpl;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MavenStrategyStageImpl implements MavenStrategyStage, MavenWorkingSessionRetrieval {

    private MavenWorkingSession session;

    public MavenStrategyStageImpl(MavenWorkingSession session) {
        this.session = session;
    }

    @Override
    public MavenFormatStage withTransitivity() {
        return using(new MavenTransitiveStrategyImpl());
    }

    @Override
    public MavenFormatStage withoutTransitivity() {
        return using(new MavenNonTransitiveStrategyImpl());
    }

    @Override
    public MavenWorkingSession getMavenWorkingSession() {
        return session;
    }

    @Override
    // FIXME seems that generics are not able to handle this
    public MavenFormatStage using(MavenResolutionStrategyBase<DependencyDeclaration> strategy) throws IllegalArgumentException {

        // first, get dependencies specified for resolution in the session
        Validate.notEmpty(session.getDependencies(), "No dependencies were set for resolution");
        // create a copy
        List<DependencyDeclaration> declarations = new ArrayList<DependencyDeclaration>(session.getDependencies());
        // FIXME generics
        declarations = preFilter((MavenResolutionFilter) strategy.preResolutionFilter(), declarations);

        List<DependencyDeclaration> depManagement = new ArrayList<DependencyDeclaration>(session.getVersionManagement());

        CollectRequest request = new CollectRequest(MavenConverter.asDependencies(declarations),
                MavenConverter.asDependencies(depManagement), session.getRemoteRepositories());

        // wrap artifact files to archives
        Collection<ArtifactResult> artifacts = null;
        try {
            artifacts = session.execute(request, (MavenResolutionFilter) strategy.resolutionFilter());
        } catch (DependencyResolutionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof ArtifactResolutionException) {
                    throw new NoResolutionException("Unable to get artifact from the repository");
                } else if (cause instanceof DependencyCollectionException) {
                    throw new NoResolutionException("Unable to collect dependency tree for given dependencies");
                }
                throw new NoResolutionException("Unable to collect/resolve dependency tree for a resulution");
            }
        }

        return new MavenFormatStageImpl(artifacts);
    }

    private List<DependencyDeclaration> preFilter(MavenResolutionFilter filter, List<DependencyDeclaration> heap) {

        if (filter == null) {
            return heap;
        }

        List<DependencyDeclaration> filtered = new ArrayList<DependencyDeclaration>();
        for (DependencyDeclaration candidate : heap) {
            if (filter.accepts(candidate)) {
                filtered.add(candidate);
            }
        }

        return filtered;
    }
}
