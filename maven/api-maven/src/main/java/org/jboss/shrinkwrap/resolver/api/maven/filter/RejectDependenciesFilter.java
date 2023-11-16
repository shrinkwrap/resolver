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
package org.jboss.shrinkwrap.resolver.api.maven.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

/**
 * A {@link MavenResolutionFilter} which will selectively ban specified dependencies
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class RejectDependenciesFilter implements MavenResolutionFilter {

    private final Set<MavenDependency> bannedDependencies;

    private final boolean rejectTransitives;

    public RejectDependenciesFilter(final String... coordinates) {
        this(true, coordinates);
    }

    public RejectDependenciesFilter(final boolean rejectTransitives, final String... coordinates)
            throws IllegalArgumentException,
            CoordinateParseException {
        if (coordinates == null || coordinates.length == 0) {
            throw new IllegalArgumentException("There must be at least one coordinate specified to be rejected.");
        }

        final Set<MavenDependency> bannedDependencies = new HashSet<>(coordinates.length);
        for (final String coords : coordinates) {
            final MavenCoordinate coordinate = MavenCoordinates.createCoordinate(coords);
            final MavenDependency dependency = MavenDependencies.createDependency(coordinate, ScopeType.COMPILE, false);
            bannedDependencies.add(dependency);
        }
        this.bannedDependencies = Collections.unmodifiableSet(bannedDependencies);
        this.rejectTransitives = rejectTransitives;

    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter#accepts(org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency,
     * java.util.List, java.util.List)
     */
    @Override
    public boolean accepts(final MavenDependency dependency, final List<MavenDependency> dependenciesForResolution,
            final List<MavenDependency> dependencyAncestors) {
        if (bannedDependencies.contains(dependency)) {
            return false;
        }

        if (rejectTransitives) {
            if (dependencyAncestors != null && dependencyAncestors.size() != 0) {
                return dependencyAncestors.get(0).equals(dependency);
            }
        }

        return true;
    }

}
