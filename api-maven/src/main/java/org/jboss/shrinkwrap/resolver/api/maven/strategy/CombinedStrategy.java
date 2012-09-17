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
package org.jboss.shrinkwrap.resolver.api.maven.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.CombinedFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;

/**
 * {@link MavenResolutionStrategy} implementation where only {@link MavenDependency}s passing a series of other
 * {@link MavenResolutionStrategy}s are accepted
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
@Deprecated
// SHRINKRES-52
public class CombinedStrategy implements MavenResolutionStrategy {

    private final Set<MavenResolutionStrategy> strategies;

    private final MavenResolutionFilter[] EMPTY_ARRAY = new MavenResolutionFilter[] {};

    public CombinedStrategy(MavenResolutionStrategy... strategies) {
        if (strategies.length == 0) {
            throw new IllegalArgumentException("There must be at least one strategy for a combined strategy.");
        }
        this.strategies = new HashSet<MavenResolutionStrategy>(Arrays.asList(strategies));
    }

    @Override
    public MavenResolutionFilter getPreResolutionFilter() {
        final List<MavenResolutionFilter> filters = new ArrayList<MavenResolutionFilter>(strategies.size());
        for (MavenResolutionStrategy s : strategies) {
            filters.add(s.getPreResolutionFilter());
        }

        return new CombinedFilter(filters.toArray(EMPTY_ARRAY));
    }

    @Override
    public MavenResolutionFilter getResolutionFilter() {
        final List<MavenResolutionFilter> filters = new ArrayList<MavenResolutionFilter>(strategies.size());
        for (MavenResolutionStrategy s : strategies) {
            filters.add(s.getResolutionFilter());
        }

        return new CombinedFilter(filters.toArray(EMPTY_ARRAY));
    }

}
