package org.jboss.shrinkwrap.resolver.impl.maven.strategy;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.ScopeFilter;

public class AcceptScopesStrategy implements MavenResolutionStrategy {

    private final Set<ScopeType> allowedScopes = EnumSet.noneOf(ScopeType.class);

    public AcceptScopesStrategy(ScopeType... scopes) {
        if (scopes.length == 0) {
            allowedScopes.add(ScopeType.COMPILE);
        } else {
            allowedScopes.addAll(Arrays.asList(scopes));
        }
    }

    @Override
    public MavenResolutionFilter getPreResolutionFilter() {
        return new ScopeFilter(allowedScopes.toArray(new ScopeType[0]));
    }

    @Override
    public MavenResolutionFilter getResolutionFilter() {
        return new ScopeFilter(allowedScopes.toArray(new ScopeType[0]));
    }

    @Override
    public MavenResolutionFilter getPostResolutionFilter() {
        return new ScopeFilter(allowedScopes.toArray(new ScopeType[0]));
    }
}