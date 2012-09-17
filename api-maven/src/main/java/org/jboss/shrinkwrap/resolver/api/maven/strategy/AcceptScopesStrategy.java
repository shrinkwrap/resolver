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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;

/**
 * {@link MavenResolutionStrategy} implementation where only {@link MavenDependency}s in acceptable {@link ScopeType}s
 * are accepted.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public final class AcceptScopesStrategy implements MavenResolutionStrategy {

    private final Set<ScopeType> allowedScopes;

    /**
     * Creates a new instance allowing only the specified {@link ScopeType}s to pass through the
     * {@link AcceptScopesStrategy#getResolutionFilter()}
     *
     * @param scopes
     *
     *            llowed scopes, required
     * @throws IllegalArgumentException
     *             If no scopes are specified
     */
    public AcceptScopesStrategy(final ScopeType... scopes) throws IllegalArgumentException {
        if (scopes == null || scopes.length == 0) {
            throw new IllegalArgumentException("at least one scope must be specified");
        }
        final Set<ScopeType> allowedScopes = new HashSet<ScopeType>(scopes.length);
        allowedScopes.addAll(Arrays.asList(scopes));
        this.allowedScopes = Collections.unmodifiableSet(allowedScopes);
    }

    /**
     * Returns a {@link MavenResolutionFilter} chain allowing all {@link MavenDependency}s to pass-through.
     *
     * @see org.jboss.shrinkwrap.r .api.maven.strategy.MavenResolutionStrategy#getPreResolutionFilter()
     */
    @Override
    public MavenResolutionFilter getPreResolutionFilter() {
        return AcceptAllFilter.INSTANCE;
    }

    /**
     * Returns a {@link MavenResolutionFilter} chain allowing only dependencies in the scopes specified during
     * construction of this instance to pass through
     *
     * @see org.jboss.shrinkwrap.re api.maven.strategy.MavenResolutionStrategy#getResolutionFilter()
     */
    @Override
    public MavenResolutionFilter getResolutionFilter() {
        return new ScopeFilter(allowedScopes.toArray(new ScopeType[0]));
    }
}