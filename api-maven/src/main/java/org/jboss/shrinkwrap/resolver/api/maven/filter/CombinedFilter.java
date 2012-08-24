/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
import java.util.Collection;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;

/**
 * A combinator for multiple filters.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class CombinedFilter implements MavenResolutionFilter {
    private List<MavenResolutionFilter> filters;

    /**
     * Combines multiple filters in a such way that all must pass.
     *
     * Implementation note: The varargs arguments cannot have a type bound, because this leads to an unchecked cast
     * while invoked
     *
     * @param filters
     *            The filters to be combined
     * @throws DependencyException
     *             If any of the filter cannot be used to filter MavenDependencies
     * @see MavenBuilderImpl
     */
    public CombinedFilter(MavenResolutionFilter... filters) {
        this.filters = new ArrayList<MavenResolutionFilter>(filters.length);
        this.filters.addAll(Arrays.asList(filters));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter#configure(java.util.Collection)
     */
    public MavenResolutionFilter configure(Collection<MavenDependency> dependencies) {
        for (MavenResolutionFilter f : filters) {
            f.configure(dependencies);
        }
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter#accept(org.jboss.shrinkwrap.resolver.api.maven.
     * MavenDependency )
     */
    public boolean accept(MavenDependency element) {
        for (MavenResolutionFilter f : filters) {
            if (f.accept(element) == false) {
                return false;
            }
        }

        return true;
    }

}
