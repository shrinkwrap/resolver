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

/**
 * Immutable, thread-safe implementation of {@link MavenGABase}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class MavenGABaseImpl implements MavenGABase {

    protected static final char SEPARATOR_COORDINATE = ':';

    /** Required **/
    private final String groupId;
    /** Required **/
    private final String artifactId;

    /**
     * Creates a new instance with the specified properties. <code>groupId</code> and <code>artifactId</code> are both
     * required.
     *
     * @param groupId
     * @param artifactId
     */
    MavenGABaseImpl(final String groupId, final String artifactId) {

        // Precondition checks
        assert groupId != null && groupId.length() > 0 : "groupId is required";
        assert artifactId != null && artifactId.length() > 0 : "artifactId is required";

        // Set properties
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    /**
     * Valid form: <code>groupId:artifactId</code>
     *
     * @see org.jboss.shrinkwrap.resolver.api.Coordinate#toCanonicalForm()
     */
    @Override
    public String toCanonicalForm() {
        return new StringBuilder(groupId).append(SEPARATOR_COORDINATE).append(artifactId).toString();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenGABase#getGroupId()
     */
    @Override
    public final String getGroupId() {
        return this.groupId;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenGABase#getArtifactId()
     */
    @Override
    public final String getArtifactId() {
        return this.artifactId;
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
        result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        return result;
    }

    /**
     * Tests for equality by value considering the <code>groupId</code> and <code>artifactId</code> fields
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MavenGABaseImpl other = (MavenGABaseImpl) obj;
        if (artifactId == null) {
            if (other.artifactId != null) {
                return false;
            }
        } else if (!artifactId.equals(other.artifactId)) {
            return false;
        }
        if (groupId == null) {
            if (other.groupId != null) {
                return false;
            }
        } else if (!groupId.equals(other.groupId)) {
            return false;
        }
        return true;
    }

}
