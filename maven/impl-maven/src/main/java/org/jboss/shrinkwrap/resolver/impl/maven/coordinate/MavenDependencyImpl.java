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
package org.jboss.shrinkwrap.resolver.impl.maven.coordinate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencyExclusion;
import org.jboss.shrinkwrap.resolver.spi.MavenDependencySPI;

/**
 * Immutable, thread-safe implementation of a {@link MavenDependency}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public final class MavenDependencyImpl implements MavenDependencySPI {

    private final MavenCoordinate delegate;

    private final Set<MavenDependencyExclusion> exclusions;
    private final ScopeType scope;
    private final boolean optional;

    /**
     * SHRINKRES-123: Used to flag that the scope for this dependency has not been set in depMgmt
     */
    private final boolean undeclaredScope;

    /**
     * Creates a new instance with the specified properties. If no {@link ScopeType} is specified, default will be
     * {@link ScopeType#COMPILE}.  <code>undeclaredScope</code> will be set to <code>false</code>
     *
     * @param coordinate A coordinate
     * Delegate, required
     * @param scope A scope
     * @param optional Whether or not this {@link MavenDependency} has been marked as optional.
     * @param exclusions {@link MavenDependencyExclusion}s, if <code>null</code> will be ignored
     */
    public MavenDependencyImpl(final MavenCoordinate coordinate, final ScopeType scope, final boolean optional,
        final MavenDependencyExclusion... exclusions) {
        this(coordinate, scope, optional, false, exclusions);
    }

    /**
     * Creates a new instance with the specified properties. If no {@link ScopeType} is specified, default will be
     * {@link ScopeType#COMPILE}.
     *
     * @param coordinate A coordinate
     * Delegate, required
     * @param scope A scope
     * @param optional Whether or not this {@link MavenDependency} has been marked as optional.
     * @param undeclaredScope Whether there is no scope
     * @param exclusions {@link MavenDependencyExclusion}s, if <code>null</code> will be ignored
     */
    public MavenDependencyImpl(final MavenCoordinate coordinate, final ScopeType scope, final boolean optional,
            final boolean undeclaredScope, final MavenDependencyExclusion... exclusions) {

        // Precondition checks
        assert coordinate != null : "coodinate is required";

        // Set properties
        this.delegate = coordinate;
        this.scope = scope == null ? ScopeType.COMPILE : scope;
        this.optional = optional;
        this.undeclaredScope = undeclaredScope;
        final Set<MavenDependencyExclusion> exclusionsToSet = new HashSet<>(
                exclusions == null ? 0 : exclusions.length);
        if (exclusions != null) {
            for (final MavenDependencyExclusion exclusion : exclusions) {
                if (exclusion != null) {
                    exclusionsToSet.add(exclusion);
                }
            }
        }
        this.exclusions = Collections.unmodifiableSet(exclusionsToSet);

    }

    /**
     * {@inheritDoc}
     *
     * see: {@code org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenGABaseImpl#getGroupId()}
     */
    @Override
    public String getGroupId() {
        return delegate.getGroupId();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.Coordinate#toCanonicalForm()
     */
    @Override
    public String toCanonicalForm() {
        return delegate.toCanonicalForm() + ':' + this.getScope().toString();
    }

    /**
     * {@inheritDoc}
     *
     * see: {@code org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenGABaseImpl#getArtifactId()}
     */
    @Override
    public String getArtifactId() {
        return delegate.getArtifactId();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getPackaging()
     */
    @Override
    public PackagingType getPackaging() {
        return delegate.getPackaging();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getType()
     */
    @Override
    public PackagingType getType() {
        return delegate.getType();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getClassifier()
     */
    @Override
    public String getClassifier() {
        return delegate.getClassifier();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getVersion()
     */
    @Override
    public String getVersion() {
        return delegate.getVersion();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency#getExclusions()
     */
    @Override
    public Set<MavenDependencyExclusion> getExclusions() {
        return this.exclusions;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency#getScope()
     */
    @Override
    public ScopeType getScope() {
        return this.scope;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency#isOptional()
     */
    @Override
    public boolean isOptional() {
        return this.optional;
    }

    /**
     * {@inheritDoc}
     * @see org.jboss.shrinkwrap.resolver.spi.MavenDependencySPI#isUndeclaredScope()
     */
    @Override
    public boolean isUndeclaredScope() {
        return undeclaredScope;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MavenDependencyImpl other = (MavenDependencyImpl) obj;
        if (delegate == null) {
            if (other.delegate != null) {
                return false;
            }
        } else if (!delegate.equals(other.delegate)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MavenDependency.class.getSimpleName() + " [" + toCanonicalForm() + "]";
    }

}
