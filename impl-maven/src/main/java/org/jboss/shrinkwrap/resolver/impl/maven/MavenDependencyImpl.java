/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;

/**
 * Representation of MavenDependency.
 *
 * Uniqueness of a dependency is based on groupId, artifactId, type and classifier. Version is not important. This follows Maven
 * representation and allows us to do versionManagement metadata magic.
 *
 * All the setters are safe, that is they handle invalid values by setting reasonable defaults. If version is not specified, it
 * is set to a question mark character ('?').
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
class MavenDependencyImpl implements MavenDependency {

    private String groupId;
    private String artifactId;
    private String version;
    private String type;
    private String classifier;

    private String scope;
    private boolean optional;

    private List<String> exclusions;

    /**
     * Constructs a MavenDependency using default values
     */
    public MavenDependencyImpl() {
        this.scope = "";
        this.optional = false;
        this.exclusions = new ArrayList<String>();
        this.type = "jar";
        this.classifier = "";
        this.version = "?";
    }

    @Override
    public MavenDependency coordinates(String coordinates) {
        MavenDependency dependency = MavenConverter.asDependency(coordinates);
        dependency.exclusions(this.exclusions());
        dependency.optional(this.optional());
        dependency.scope(this.scope());
        return dependency;
    }

    @Override
    public String scope() {
        return scope;
    }

    @Override
    public MavenDependency scope(String scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public boolean optional() {
        return optional;
    }

    @Override
    public MavenDependency optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    @Override
    public String[] exclusions() {
        return exclusions.toArray(new String[0]);
    }

    @Override
    public String coordinates() {
        StringBuilder sb = new StringBuilder();
        sb.append(groupId);
        sb.append(":").append(artifactId);
        sb.append(":").append(type);
        if (classifier != null && classifier.length() != 0) {
            sb.append(":").append(classifier);
        }
        sb.append(":").append(version);

        return sb.toString();
    }

    @Override
    public MavenDependency exclusions(String... exclusions) {
        if (exclusions.length == 0) {
            return this;
        }

        this.exclusions.addAll(Arrays.asList(exclusions));
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenDependency#hasSameArtifactAs(org.jboss.shrinkwrap.resolver.api.maven.
     * MavenDependency)
     */
    @Override
    public boolean hasSameArtifactAs(MavenDependency other) {
        return equals(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenDependency#hasSameArtifactAs(java.lang.String)
     */
    @Override
    public boolean hasSameArtifactAs(String other) {
        return equals(MavenConverter.asDependency(other));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
        result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MavenDependencyImpl other = (MavenDependencyImpl) obj;
        if (artifactId == null) {
            if (other.artifactId != null)
                return false;
        } else if (!artifactId.equals(other.artifactId))
            return false;
        if (classifier == null) {
            if (other.classifier != null)
                return false;
        } else if (!classifier.equals(other.classifier))
            return false;
        if (groupId == null) {
            if (other.groupId != null)
                return false;
        } else if (!groupId.equals(other.groupId))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        version = (version == null || version.length() == 0) ? "?" : version;
        this.version = version;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        type = ((type == null || type.length() == 0) ? "jar" : type);
        this.type = type;
    }

    /**
     * @return the classifier
     */
    public String getClassifier() {
        return classifier;
    }

    /**
     * @param classifier the classifier to set
     */
    public void setClassifier(String classifier) {
        classifier = ((classifier == null || classifier.length() == 0) ? "" : classifier);
        this.classifier = classifier;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return coordinates();
    }
}
