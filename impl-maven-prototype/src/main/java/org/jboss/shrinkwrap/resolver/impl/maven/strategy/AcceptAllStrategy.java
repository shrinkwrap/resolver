package org.jboss.shrinkwrap.resolver.impl.maven.strategy;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.AcceptAllFilter;

enum AcceptAllStrategy implements MavenResolutionStrategy {
    INSTANCE;

    @Override
    public MavenResolutionFilter preResolutionFilter() {
        return AcceptAllFilter.INSTANCE;
    }

    @Override
    public MavenResolutionFilter resolutionFilter() {
        return AcceptAllFilter.INSTANCE;
    }

    @Override
    public MavenResolutionFilter postResolutionFilter() {
        return AcceptAllFilter.INSTANCE;
    }
}