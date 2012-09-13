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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

/**
 * Immutable, thread-safe implementation of a {@link MavenDependency}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
final class MavenDependencyImpl implements MavenDependency {

    private final MavenCoordinate delegate;

    private final Set<MavenDependencyExclusion> exclusions;
    private final ScopeType scope;
    private final boolean optional;

    /**
     * Creates a new instance with the specified properties. If no {@link ScopeType} is specified, default will be
     * {@link ScopeType#COMPILE}.
     *
     * @param coordinate
     *            Delegate, required
     * @param scope
     * @param optional
     * @param exclusions
     *            {@link MavenDependencyExclusion}s, if <code>null</code> will be ignored
     */
    MavenDependencyImpl(final MavenCoordinate coordinate, final ScopeType scope, final boolean optional,
        final MavenDependencyExclusion... exclusions) {

        // Precondition checks
        assert coordinate != null : "coodinate is required";

        // Set properties
        this.delegate = coordinate;
        this.scope = scope == null ? ScopeType.COMPILE : scope;
        this.optional = optional;
        final Set<MavenDependencyExclusion> exclusionsToSet = new HashSet<MavenDependencyExclusion>(
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
     * @return
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenGABase#getGroupId()
     */
    @Override
    public String getGroupId() {
        return delegate.getGroupId();
    }

    /**
     * @return
     * @see org.jboss.shrinkwrap.resolver.api.Coordinate#toCanonicalForm()
     */
    @Override
    public String toCanonicalForm() {
        return delegate.toCanonicalForm();
    }

    /**
     * @return
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenGABase#getArtifactId()
     */
    @Override
    public String getArtifactId() {
        return delegate.getArtifactId();
    }

    /**
     * @return
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getPackaging()
     */
    @Override
    public PackagingType getPackaging() {
        return delegate.getPackaging();
    }

    /**
     * @return
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getType()
     */
    @Override
    public PackagingType getType() {
        return delegate.getType();
    }

    /**
     * @return
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getClassifier()
     */
    @Override
    public String getClassifier() {
        return delegate.getClassifier();
    }

    /**
     * @return
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
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
        result = prime * result + ((exclusions == null) ? 0 : exclusions.hashCode());
        result = prime * result + (optional ? 1231 : 1237);
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
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
        if (exclusions == null) {
            if (other.exclusions != null) {
                return false;
            }
        } else {
            if (other.exclusions == null) {
                return false;
            }
            final Set<MavenDependencyExclusion> theirExclusions = other.exclusions;
            final Iterator<MavenDependencyExclusion> theirIt = other.exclusions.iterator();
            final Set<MavenDependencyExclusion> ourExclusions = exclusions;
            if (ourExclusions.size() != theirExclusions.size()) {
                return false;
            }
            for (final MavenDependencyExclusion exclusion : ourExclusions) {
                if (!exclusion.equals(theirIt.next())) {
                    return false;
                }
            }
        }
        if (optional != other.optional) {
            return false;
        }
        if (scope != other.scope) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MavenDependency.class.getSimpleName() + " [" + toCanonicalForm() + "]";
    }

}
