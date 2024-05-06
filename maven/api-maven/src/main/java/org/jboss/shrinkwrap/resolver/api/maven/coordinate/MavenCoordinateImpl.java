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

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

/**
 * Immutable, thread-safe implementation of {@link MavenCoordinate}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class MavenCoordinateImpl extends MavenGABaseImpl implements MavenCoordinate {

    private static final String EMPTY_STRING = "";
    private final String version;
    private final PackagingType packaging;
    private final String classifier;

    /**
     * Creates a new instance with the specified properties. <code>groupId</code> and <code>artifactId</code> are
     * required. If no {@link PackagingType} is specified, default will be to {@link PackagingType#JAR}. If no {@link ScopeType}
     * is specified, default will be {@link ScopeType#COMPILE}.
     *
     * @param groupId The group ID of the Maven coordinate.
     * @param artifactId The artifact ID of the Maven coordinate.
     * @param version The version of the Maven coordinate.
     * @param packaging The packaging type of the Maven coordinate. Defaults to {@link PackagingType#JAR} if not specified.
     * @param classifier The classifier of the Maven coordinate. If not specified, it defaults to the classifier derived from the packaging type.
     */
    MavenCoordinateImpl(final String groupId, final String artifactId, final String version,
            final PackagingType packaging, final String classifier) {

        // Precondition checks covered by superclass
        super(groupId, artifactId);

        // Set properties
        this.version = version;

        this.packaging = packaging == null ? PackagingType.JAR : packaging;
        // Adjust this for compatibility with Aether parser
        if (classifier == null) {
            this.classifier = packaging != null ? packaging.getClassifier() : null;
        } else {
            this.classifier = classifier;
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getPackaging()
     */
    @Override
    public final PackagingType getPackaging() {
        return packaging;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getType()
     */
    @Override
    public final PackagingType getType() {
        return this.packaging;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getClassifier()
     */
    @Override
    public final String getClassifier() {
        return this.classifier;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate#getVersion()
     */
    @Override
    public final String getVersion() {
        return this.version;
    }

    /**
     * Valid forms: <code>groupId:artifactId:packaging:classifier:version</code>
     * <code>groupId:artifactId:packaging:version</code> <code>groupId:artifactId:version</code> <code>groupId:artifactId</code>
     */
    @Override
    public final String toCanonicalForm() {

        final StringBuilder sb = new StringBuilder(super.toCanonicalForm());
        if (version == null || version.isEmpty()) {
            return sb.toString();
        }
        if (classifier != null && !classifier.isEmpty() && packaging != null) {
            sb.append(SEPARATOR_COORDINATE).append(packaging.getId()).append(SEPARATOR_COORDINATE)
                    .append(classifier).append(SEPARATOR_COORDINATE).append(version);
        }
        if ((classifier == null || classifier.isEmpty()) && packaging != null) {
            sb.append(SEPARATOR_COORDINATE).append(packaging.getId()).append(SEPARATOR_COORDINATE).append(version);
        }

        return sb.toString();
    }

    /**
     * Does not consider version
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
        result = prime * result + ((packaging == null) ? 0 : packaging.hashCode());
        return result;
    }

    /**
     * Value equality is determined by all fields except version
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MavenCoordinateImpl other = (MavenCoordinateImpl) obj;
        if (classifier == null) {
            if (other.classifier != null) {
                return false;
            }
        } else if (!classifier.equals(other.classifier)) {
            return false;
        }
        return packaging.equals(other.packaging);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MavenCoordinate.class.getSimpleName() + " [" + toCanonicalForm() + "]";
    }

}
