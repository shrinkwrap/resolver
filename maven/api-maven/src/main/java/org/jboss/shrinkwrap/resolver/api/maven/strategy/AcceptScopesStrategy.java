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
import java.util.HashSet;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;

/**
 * {@link MavenResolutionStrategy} implementation where only {@link MavenDependency}s in acceptable {@link ScopeType}s
 * are accepted.
 * <p>
 * Note, this implementation is not able to properly filter scopes defined in a pom.xml file.
 * See: <a href="https://issues.redhat.com/browse/SHRINKRES-112">SHRINKRES-112</a>
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public final class AcceptScopesStrategy implements MavenResolutionStrategy {

    private final MavenResolutionFilter[] resolutionFilters;

    /**
     * Creates a new instance allowing only the specified {@link ScopeType}s to pass through the
     * {@link AcceptScopesStrategy#getResolutionFilters()}
     *
     * @param scopes Allowed scopes, required
     * @throws IllegalArgumentException
     * If no scopes are specified
     */
    public AcceptScopesStrategy(final ScopeType... scopes) throws IllegalArgumentException {
        if (scopes == null || scopes.length == 0) {
            throw new IllegalArgumentException("at least one scope must be specified");
        }
        final Set<ScopeType> allowedScopes = new HashSet<>(scopes.length);
        allowedScopes.addAll(Arrays.asList(scopes));
        this.resolutionFilters = new MavenResolutionFilter[] { new ScopeFilter(
                allowedScopes.toArray(new ScopeType[] {})) };
    }

    /**
     * Returns a {@link MavenResolutionFilter} chain allowing only dependencies in the scopes specified during
     * construction of this instance to pass through
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy#getResolutionFilters()
     */
    @Override
    public MavenResolutionFilter[] getResolutionFilters() {
        return resolutionFilters;
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
