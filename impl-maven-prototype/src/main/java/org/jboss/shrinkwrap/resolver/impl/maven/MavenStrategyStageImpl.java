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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.jboss.shrinkwrap.resolver.api.NoResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.NonTransitiveStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.TransitiveStrategy;
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
        return using(new TransitiveStrategy());
    }

    @Override
    public MavenFormatStage withoutTransitivity() {
        return using(new NonTransitiveStrategy());
    }

    @Override
    public MavenWorkingSession getMavenWorkingSession() {
        return session;
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

    @Override
    public MavenFormatStage using(MavenResolutionStrategy strategy) throws IllegalArgumentException {
        // first, get dependencies specified for resolution in the session
        Validate.notEmpty(session.getDependencies(), "No dependencies were set for resolution");

        // create a copy
        List<DependencyDeclaration> declarations = new ArrayList<DependencyDeclaration>(session.getDependencies());
        List<DependencyDeclaration> depManagement = new ArrayList<DependencyDeclaration>(session.getVersionManagement());

        // prefiltering
        declarations = preFilter(configureFilterFromSession(session, strategy.preResolutionFilter()), declarations);

        CollectRequest request = new CollectRequest(MavenConverter.asDependencies(declarations),
            MavenConverter.asDependencies(depManagement), session.getRemoteRepositories());

        // wrap artifact files to archives
        Collection<ArtifactResult> artifacts = null;
        try {
            // resolution filtering
            artifacts = session.execute(request, configureFilterFromSession(session, strategy.resolutionFilter()));
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

        // post resolution filtering
        return new MavenFormatStageImpl(artifacts, configureFilterFromSession(session, strategy.postResolutionFilter()));
    }

    private MavenResolutionFilter configureFilterFromSession(MavenWorkingSession session, MavenResolutionFilter filter) {

        Validate.notNull(session, "MavenWorkingSession must not be null");
        // filter can be null
        if (filter == null) {
            return AcceptAllFilter.INSTANCE;
        }

        // prepare dependencies
        Stack<DependencyDeclaration> dependencies = session.getDependencies();
        List<DependencyDeclaration> dependenciesList;
        if (dependencies == null || dependencies.size() == 0) {
            dependenciesList = Collections.emptyList();
        } else {
            dependenciesList = new ArrayList<DependencyDeclaration>(dependencies);
        }

        // prepare dependency management
        Set<DependencyDeclaration> dependenciesMngmt = session.getVersionManagement();
        List<DependencyDeclaration> dependenciesMngmtList;
        if (dependenciesMngmt == null || dependenciesMngmt.size() == 0) {
            dependenciesMngmtList = Collections.emptyList();
        } else {
            dependenciesMngmtList = new ArrayList<DependencyDeclaration>(dependencies);
        }

        // configure filter
        filter.setDefinedDependencies(dependenciesList);
        filter.setDefinedDependencyManagement(dependenciesMngmtList);

        return filter;
    }
}
