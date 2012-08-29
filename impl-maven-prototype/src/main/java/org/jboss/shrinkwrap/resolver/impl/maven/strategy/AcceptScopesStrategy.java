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
package org.jboss.shrinkwrap.resolver.impl.maven.strategy;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.ScopeFilter;

/**
 * {@link MavenResolutionStrategy} implementation where only {@link DependencyDeclaration}s in acceptable
 * {@link ScopeType}s are accepted.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class AcceptScopesStrategy implements MavenResolutionStrategy {

    private static final Logger log = Logger.getLogger(AcceptScopesStrategy.class.getName());

    private final Set<ScopeType> allowedScopes = EnumSet.noneOf(ScopeType.class);

    public AcceptScopesStrategy(final ScopeType... scopes) {
        if (scopes.length == 0) {
            final ScopeType defaultScope = ScopeType.COMPILE;
            if (log.isLoggable(Level.FINER)) {
                log.finer("No scopes specified; defaulting to " + defaultScope);
            }
            allowedScopes.add(defaultScope);
        } else {
            allowedScopes.addAll(Arrays.asList(scopes));
        }
    }

    @Override
    public MavenResolutionFilter getPreResolutionFilter() {
        // We cannot prefilter based on scope, because we need transitive information available, so allow all scopes
        return new ScopeFilter(ScopeType.values());
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