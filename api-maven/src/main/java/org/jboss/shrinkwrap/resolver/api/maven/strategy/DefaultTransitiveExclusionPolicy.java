/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

/**
 * Applies the default behavior exhibited by Maven with regards to handling transitive dependencies during resolution.
 *
 * @see <a href="http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Transitive_Dependencies">Transitive_Dependencies</a>
 * @see <a href="http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope">Dependency_Scope</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public enum DefaultTransitiveExclusionPolicy implements TransitiveExclusionPolicy {
    INSTANCE;

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveExclusionPolicy#allowOptional()
     */
    @Override
    public boolean allowOptional() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveExclusionPolicy#getFilteredScopes()
     */
    @Override
    public ScopeType[] getFilteredScopes() {
        return new ScopeType[] { ScopeType.PROVIDED, ScopeType.TEST };
    }

}
