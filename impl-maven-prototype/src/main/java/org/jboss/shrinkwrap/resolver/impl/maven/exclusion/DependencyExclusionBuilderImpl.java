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
package org.jboss.shrinkwrap.resolver.impl.maven.exclusion;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusion;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Implementation of {@link DependencyExclusionBuilder}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class DependencyExclusionBuilderImpl implements DependencyExclusionBuilder {

    private String groupId;
    private String artifactId;

    public static DependencyExclusionBuilderImpl fromExclusionCoordinateAddress(String coordinates)
        throws IllegalArgumentException, CoordinateParseException {
        Validate.notNullOrEmpty(coordinates, "Exclusion coordinates must not be null nor empty.");
        String[] result = coordinates.split(":");
        if (result.length != 2) {
            throw new CoordinateParseException("Exclusion coordinate address " + coordinates
                + " has to follow groupId:artifactId pattern.");
        }
        DependencyExclusionBuilderImpl builder = new DependencyExclusionBuilderImpl();
        builder.groupId(result[0]);
        builder.artifactId(result[1]);
        return builder;
    }

    public DependencyExclusion build() throws CoordinateBuildException {

        if (Validate.isNullOrEmpty(groupId)) {
            throw new CoordinateBuildException("GroupId for an exclusion must not be null nor empty.");
        }
        if (Validate.isNullOrEmpty(artifactId)) {
            throw new CoordinateBuildException("ArtifactId for an exclusion must not be null nor empty.");
        }

        DependencyExclusionImpl exclusion = new DependencyExclusionImpl(groupId, artifactId);
        return exclusion;
    }

    @Override
    public DependencyExclusionBuilder groupId(String groupId) throws IllegalArgumentException {
        Validate.notNullOrEmpty(groupId, "GroupId must not be null nor empty.");
        this.groupId = groupId;
        return this;
    }

    @Override
    public DependencyExclusionBuilder artifactId(String artifactId) throws IllegalArgumentException {
        Validate.notNullOrEmpty(artifactId, "GroupId must not be null nor empty.");
        this.artifactId = artifactId;
        return this;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getAddress() {
        return groupId + ":" + artifactId;
    }

}
