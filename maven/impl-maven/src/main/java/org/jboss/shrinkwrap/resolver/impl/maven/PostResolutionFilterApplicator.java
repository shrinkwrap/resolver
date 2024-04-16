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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;

/**
 * An utility to apply pre and post filter on either a list of resolved dependencies (post filtering) or on a list of resolution
 * candidates (pre filtering)
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class PostResolutionFilterApplicator {
    private static final Logger log = Logger.getLogger(PostResolutionFilterApplicator.class.getName());

    /**
     * Run post-resolution filtering to weed out POMs.
     *
     * @param artifactResults The original list of resolved artifacts
     * @return List of modified artifactResults
     */
    static Collection<MavenResolvedArtifact> postFilter(final Collection<MavenResolvedArtifact> artifactResults) {

        final MavenResolutionFilter postResolutionFilter = RestrictPomArtifactFilter.INSTANCE;
        final Collection<MavenResolvedArtifact> filteredArtifacts = new ArrayList<>();
        final List<MavenDependency> emptyList = Collections.emptyList();

        for (final MavenResolvedArtifact artifact : artifactResults) {
            final MavenDependency dependency = MavenDependencies.createDependency(artifact.getCoordinate(),
                    ScopeType.COMPILE, false);
            // Empty lists OK here because we know the RestrictPOM Filter doesn't consult them
            if (postResolutionFilter.accepts(dependency, emptyList, emptyList)) {
                filteredArtifacts.add(artifact);
            }
        }
        return Collections.unmodifiableCollection(filteredArtifacts);
    }

    /**
     * {@link MavenResolutionFilter} implementation which does not allow POMs to pass through
     *
     * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
     */
    private enum RestrictPomArtifactFilter implements MavenResolutionFilter {

        INSTANCE;

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter#accepts(org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency,
         * java.util.List, java.util.List)
         */
        @Override
        public boolean accepts(final MavenDependency coordinate, final List<MavenDependency> dependenciesForResolution, final List<MavenDependency> dependencyAncestors)
                throws IllegalArgumentException {
            if (PackagingType.POM.equals(coordinate.getPackaging())) {
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Filtering out POM dependency resolution: " + coordinate
                            + "; its transitive dependencies will be included");
                }

                return false;
            }
            return true;
        }

    }

}
