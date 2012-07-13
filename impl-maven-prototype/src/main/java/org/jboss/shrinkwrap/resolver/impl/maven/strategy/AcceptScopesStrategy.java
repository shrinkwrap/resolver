package org.jboss.shrinkwrap.resolver.impl.maven.strategy;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.ScopeFilter;

public class AcceptScopesStrategy implements MavenResolutionStrategy {

    private Set<ScopeType> allowedScopes = EnumSet.noneOf(ScopeType.class);

    public AcceptScopesStrategy(ScopeType... scopes) {
        if (scopes.length == 0) {
            allowedScopes.add(ScopeType.COMPILE);
        } else {
            allowedScopes.addAll(Arrays.asList(scopes));
        }
    }

    @Override
    public MavenResolutionFilter preResolutionFilter() {
        return new ScopeFilter(allowedScopes.toArray(new ScopeType[0]));
    }

    @Override
    public MavenResolutionFilter resolutionFilter() {
        return new ScopeFilter(allowedScopes.toArray(new ScopeType[0]));
    }

    @Override
    public MavenResolutionFilter postResolutionFilter() {
        return new ScopeFilter(allowedScopes.toArray(new ScopeType[0]));
    }
}