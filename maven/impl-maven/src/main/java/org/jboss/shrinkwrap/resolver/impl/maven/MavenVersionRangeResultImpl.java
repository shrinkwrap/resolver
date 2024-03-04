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

package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;

/**
 * Basic implementation of {@link org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult}
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class MavenVersionRangeResultImpl implements MavenVersionRangeResult {

    private final Artifact artifact;
    private final VersionRangeResult versionRangeResult;

    private List<MavenCoordinate> versions;

    public MavenVersionRangeResultImpl(final Artifact artifact, final VersionRangeResult versionRangeResult) {
        this.artifact = artifact;
        this.versionRangeResult = versionRangeResult;
    }

    @Override
    public MavenCoordinate getLowestVersion() {
        return getCoordinate(versionRangeResult.getLowestVersion());
    }

    @Override
    public MavenCoordinate getHighestVersion() {
        return getCoordinate(versionRangeResult.getHighestVersion());
    }

    @Override
    public List<MavenCoordinate> getVersions() {
        if (versions == null) {
            final List<Version> versions = versionRangeResult.getVersions();
            final List<MavenCoordinate> coordinates = new ArrayList<>(versions.size());

            for (final Version version : versions) {
                coordinates.add(getCoordinate(version));
            }

            this.versions = Collections.unmodifiableList(coordinates);
        }

        return versions;
    }

    private MavenCoordinate getCoordinate(final Version version) {
        if(version == null) {
            return null;
        }

        return MavenCoordinates.createCoordinate(artifact.getGroupId(), artifact.getArtifactId(), version.toString(), PackagingType.of(artifact.getExtension()), artifact.getClassifier());
    }

    @Override
    public String toString() {
        final StringBuilder versionsBuilder = new StringBuilder("{");
        boolean first = true;
        for (final MavenCoordinate version : getVersions()) {
            if (first) {
                first = false;
            } else {
                versionsBuilder.append(',');
            }
            versionsBuilder.append(version.getVersion());
        }
        versionsBuilder.append("}");

        return "MavenResolvedVersionsImpl[" +
                "artifact=" + artifact +
                ", versions=" + versionsBuilder +
                ']';
    }
}
