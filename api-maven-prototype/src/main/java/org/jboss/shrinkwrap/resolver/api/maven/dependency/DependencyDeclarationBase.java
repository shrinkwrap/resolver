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
package org.jboss.shrinkwrap.resolver.api.maven.dependency;

import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinateBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusion;

/**
 * Contains base operations for building a <code><dependency /></code> declaration; immutable and Thread-safe.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface DependencyDeclarationBase extends MavenCoordinateBase {

    /**
     * Returns the exclusions defined for this {@link DependencyDeclarationBase} in an immutable, read-only view. If no
     * exclusions are defined, an empty {@link Set} will be returned.
     *
     * @return
     */
    Set<DependencyExclusion> getExclusions();

    /**
     * Returns the scope for this {@link DependencyDeclarationBase}. Never returns null; if no scope has been
     * explicitly-defined, the default {@link ScopeType#COMPILE} will be returned.
     *
     * @return
     */
    ScopeType getScope();

    /**
     * Returns whether or not this {@link DependencyDeclarationBase} has been marked as optional; defaults to
     * <code>false</code>
     *
     * @return
     */
    boolean isOptional();

}
