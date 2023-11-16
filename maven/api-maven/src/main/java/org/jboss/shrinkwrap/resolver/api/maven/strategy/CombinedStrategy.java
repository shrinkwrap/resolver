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
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;

/**
 * {@link MavenResolutionStrategy} implementation where only {@link MavenDependency}s passing a series of other
 * {@link MavenResolutionStrategy}s are accepted
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class CombinedStrategy implements MavenResolutionStrategy {

    private static final MavenResolutionFilter[][] EMPTY_CHAIN_ARRAY = new MavenResolutionFilter[][] {};
    private static final MavenResolutionFilter[] EMPTY_FILTER_ARRAY = new MavenResolutionFilter[] {};

    private final MavenResolutionFilter[] resolutionFilters;

    public CombinedStrategy(final MavenResolutionStrategy... strategies) {
        if (strategies.length == 0) {
            throw new IllegalArgumentException("There must be at least one strategy for a combined strategy.");
        }
        final List<MavenResolutionFilter[]> resolutionFilterChains = new ArrayList<>();
        for (final MavenResolutionStrategy strategy : strategies) {
            resolutionFilterChains.add(strategy.getResolutionFilters());
        }
        resolutionFilters = this.combine(resolutionFilterChains.toArray(EMPTY_CHAIN_ARRAY));
    }

    @Override
    public MavenResolutionFilter[] getResolutionFilters() {
        return resolutionFilters;
    }

    private MavenResolutionFilter[] combine(final MavenResolutionFilter[]... inputFilterChains) {
        final List<MavenResolutionFilter> combinedFilters = new ArrayList<>();
        for (final MavenResolutionFilter[] filterChain : inputFilterChains) {
            combinedFilters.addAll(Arrays.asList(filterChain));
        }
        return combinedFilters.toArray(EMPTY_FILTER_ARRAY);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy#getTransitiveExclusionPolicy()
     */
    @Override
    public TransitiveExclusionPolicy getTransitiveExclusionPolicy() {
        return DefaultTransitiveExclusionPolicy.INSTANCE;
    }

}
