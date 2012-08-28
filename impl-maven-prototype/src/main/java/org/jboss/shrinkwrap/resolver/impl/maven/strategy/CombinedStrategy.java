package org.jboss.shrinkwrap.resolver.impl.maven.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.CombinedFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.MavenResolutionFilterInternalView;

public class CombinedStrategy implements MavenResolutionStrategy {

    private final Set<MavenResolutionStrategy> strategies;

    public CombinedStrategy(MavenResolutionStrategy... strategies) {
        if (strategies.length == 0) {
            throw new IllegalArgumentException("There must be at least one strategy for a combined strategy.");
        }
        this.strategies = new HashSet<MavenResolutionStrategy>(Arrays.asList(strategies));
    }

    @Override
    public MavenResolutionFilter getPreResolutionFilter() {
        List<MavenResolutionFilter> filters = new ArrayList<MavenResolutionFilter>(strategies.size());
        for (MavenResolutionStrategy s : strategies) {
            filters.add(s.getPreResolutionFilter());
        }

        return new CombinedFilter(filters.toArray(new MavenResolutionFilterInternalView[0]));
    }

    @Override
    public MavenResolutionFilter getResolutionFilter() {
        List<MavenResolutionFilter> filters = new ArrayList<MavenResolutionFilter>(strategies.size());
        for (MavenResolutionStrategy s : strategies) {
            filters.add(s.getResolutionFilter());
        }

        return new CombinedFilter(filters.toArray(new MavenResolutionFilterInternalView[0]));
    }

    @Override
    public MavenResolutionFilter getPostResolutionFilter() {
        List<MavenResolutionFilter> filters = new ArrayList<MavenResolutionFilter>(strategies.size());
        for (MavenResolutionStrategy s : strategies) {
            filters.add(s.getPostResolutionFilter());
        }

        return new CombinedFilter(filters.toArray(new MavenResolutionFilterInternalView[0]));
    }

}
