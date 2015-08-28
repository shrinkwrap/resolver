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
package org.jboss.shrinkwrap.resolver.api.maven.coordinate;

import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

/**
 * Metadata describing a {@code <dependency />}declaration; immutable and Thread-safe.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface MavenDependency extends MavenCoordinate {

    /**
     * Returns the exclusions defined for this {@link MavenDependency} in an immutable, read-only view. If no
     * exclusions are defined, an empty {@link Set} will be returned.
     *
     * @return The exclusions defined for this {@link MavenDependency} in an immutable, read-only view.
     * An empty {@link Set} if no exclusions are defined.
     */
    Set<MavenDependencyExclusion> getExclusions();

    /**
     * Returns the scope for this {@link MavenDependency}. Never returns null; if no scope has been
     * explicitly-defined, the default {@link ScopeType#COMPILE} will be returned.
     *
     * @return The scope for this {@link MavenDependency}. The default {@link ScopeType#COMPILE} if no scope has been
     * explicitly-defined. Never returns null.
     */
    ScopeType getScope();

    /**
     * Returns whether or not this {@link MavenDependency} has been marked as optional; defaults to
     * <code>false</code>
     *
     * @return Whether or not this {@link MavenDependency} has been marked as optional. Defaults to <code>false</code>
     */
    boolean isOptional();

}
