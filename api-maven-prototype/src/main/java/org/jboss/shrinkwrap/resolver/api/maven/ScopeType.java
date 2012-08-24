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
package org.jboss.shrinkwrap.resolver.api.maven;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;

/**
 * Valid scope types for Maven {@link DependencyDeclaration} metadata
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public enum ScopeType {
    COMPILE("compile"), PROVIDED("provided"), RUNTIME("runtime"), TEST("test"), SYSTEM("system"), IMPORT("import");
    private final String value;

    ScopeType(final String value) {
        this.value = value;
    }

    private static final Logger log = Logger.getLogger(ScopeType.class.getName());

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Maps a string to ScopeType
     *
     * @param scopeName
     *            String name of the scope type
     * @return Corresponding ScopeType object
     * @throws IllegalArgumentException
     *             Thrown if scopeName is {@code null}, empty or does not represent a valid scope type
     */
    public static ScopeType fromScopeType(String scopeName) throws IllegalArgumentException {

        if (scopeName == null || scopeName.length() == 0) {
            log.log(Level.FINEST, "Empty scope was replaced with default {0}", COMPILE.value);
            return COMPILE;
        }

        ScopeType scope = SCOPE_NAME_CACHE.get(scopeName);
        if (scope == null) {
            throw new IllegalArgumentException("Scope type " + scopeName + " is not supported.");
        }
        return scope;
    }

    private static final Map<String, ScopeType> SCOPE_NAME_CACHE = new HashMap<String, ScopeType>() {
        private static final long serialVersionUID = 1L;
        {
            for (ScopeType scope : ScopeType.values()) {
                this.put(scope.value, scope);
            }
        }
    };
}
