package org.jboss.shrinkwrap.resolver.impl.maven.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.CombinedFilter;

public class CombinedStrategy implements MavenResolutionStrategy {

    private Set<MavenResolutionStrategy> strategies;

    public CombinedStrategy(MavenResolutionStrategy... strategies) {
        if (strategies.length == 0) {
            throw new IllegalArgumentException("There must be at least one strategy for a combined strategy.");
        }
        this.strategies = new HashSet<MavenResolutionStrategy>(Arrays.asList(strategies));
    }

    @Override
    public MavenResolutionFilter preResolutionFilter() {
        List<MavenResolutionFilter> filters = new ArrayList<MavenResolutionFilter>(strategies.size());
        for (MavenResolutionStrategy s : strategies) {
            filters.add(s.preResolutionFilter());
        }

        return new CombinedFilter(filters.toArray(new MavenResolutionFilter[0]));
    }

    @Override
    public MavenResolutionFilter resolutionFilter() {
        List<MavenResolutionFilter> filters = new ArrayList<MavenResolutionFilter>(strategies.size());
        for (MavenResolutionStrategy s : strategies) {
            filters.add(s.resolutionFilter());
        }

        return new CombinedFilter(filters.toArray(new MavenResolutionFilter[0]));
    }

    @Override
    public MavenResolutionFilter postResolutionFilter() {
        List<MavenResolutionFilter> filters = new ArrayList<MavenResolutionFilter>(strategies.size());
        for (MavenResolutionStrategy s : strategies) {
            filters.add(s.postResolutionFilter());
        }

        return new CombinedFilter(filters.toArray(new MavenResolutionFilter[0]));
    }

}
