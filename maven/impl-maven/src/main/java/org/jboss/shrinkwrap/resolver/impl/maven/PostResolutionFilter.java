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
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.NonTransitiveStrategy;

/**
 * A utility to apply post filter on a list of resolved dependencies (post filtering).
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class PostResolutionFilter {
    private static final Logger log = Logger.getLogger(PostResolutionFilter.class.getName());

    /**
     * Run post-resolution filtering to weed out unwanted POMs.
     *
     * @param artifactResults The original list of resolved artifacts
     * @param depsForResolution Resolutions for the request. Used for specifying unwanted POMs
     * @param strategy Resolution strategy
     * @return List of modified artifactResults
     */
    static Collection<MavenResolvedArtifact> filter(final Collection<MavenResolvedArtifact> artifactResults, List<MavenDependency> depsForResolution, final MavenResolutionStrategy strategy) {

        final Collection<MavenResolvedArtifact> filteredArtifacts = new ArrayList<>();

        for (final MavenResolvedArtifact artifact : artifactResults) {
            final MavenDependency dependency = MavenDependencies.createDependency(artifact.getCoordinate(),
                    ScopeType.COMPILE, false);
            // Empty lists OK here because we know the RestrictPOM Filter doesn't consult them
            if (PackagingType.POM.equals(dependency.getPackaging())) {
                log.finer("Filtering out POM dependency resolution: " + dependency
                        + "; its transitive dependencies will be included");
                // Keeping POM if specified in the resolution (G:A:pom:V) if only the POM should be resolved
                if (strategy.getClass().equals(NonTransitiveStrategy.class) && checkForPomInDependencies(dependency, depsForResolution)) {
                    filteredArtifacts.add(artifact);
                }
            }
            else {
                filteredArtifacts.add(artifact);
            }
        }
        return Collections.unmodifiableCollection(filteredArtifacts);
    }

    private static boolean checkForPomInDependencies(final MavenDependency coordinate, final List<MavenDependency> dependenciesForResolution) {
        for (MavenDependency dependency : dependenciesForResolution) {
            if (dependency.equals(coordinate)) {
                return true;
            }
        }
        return false;
    }

}
