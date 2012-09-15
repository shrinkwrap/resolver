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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

/**
 * A combinator for multiple filters.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class CombinedFilter implements MavenResolutionFilter {
    private final List<MavenResolutionFilter> filters;

    /**
     * Combines multiple filters in a such way that all must pass.
     */
    public CombinedFilter(final MavenResolutionFilter... filters) {
        this.filters = new ArrayList<MavenResolutionFilter>(filters.length);
        this.filters.addAll(Arrays.asList(filters));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter#accepts(org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency,
     *      java.util.List)
     */
    @Override
    public boolean accepts(final MavenDependency dependency, final List<MavenDependency> dependenciesForResolution) {
        for (final MavenResolutionFilter f : filters) {
            if (!f.accepts(dependency, dependenciesForResolution)) {
                return false;
            }
        }
        return true;
    }

}
