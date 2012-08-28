/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.filter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;

/**
 * A filter which limits scope of the artifacts. Only the artifacts within specified scopes are included in resolution.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ScopeFilter implements MavenResolutionFilterInternalView {
    private final Set<ScopeType> allowedScopes = EnumSet.noneOf(ScopeType.class);

    /**
     * Creates a filter which accepts all artifacts that have scope {{@link ScopeType#COMPILE}
     */
    public ScopeFilter() {
        this(ScopeType.COMPILE);
    }

    /**
     * Creates a filter which accepts all artifacts that their scope is one of the specified.
     *
     * @param scopes
     *            The enumeration of allowed scopes
     */
    public ScopeFilter(ScopeType... scopes) {

        if (scopes.length == 0) {
            allowedScopes.add(ScopeType.COMPILE);
        } else {
            allowedScopes.addAll(Arrays.asList(scopes));
        }

    }

    @Override
    public MavenResolutionFilterInternalView setDefinedDependencies(List<DependencyDeclaration> dependencies) {
        return this;
    }

    @Override
    public MavenResolutionFilterInternalView setDefinedDependencyManagement(
        List<DependencyDeclaration> dependencyManagement) {
        return this;
    }

    @Override
    public boolean accepts(DependencyDeclaration coordinate) throws IllegalArgumentException {
        if (coordinate == null) {
            return false;
        }

        if (allowedScopes.contains(coordinate.getScope())) {
            return true;
        }

        return false;
    }
}
