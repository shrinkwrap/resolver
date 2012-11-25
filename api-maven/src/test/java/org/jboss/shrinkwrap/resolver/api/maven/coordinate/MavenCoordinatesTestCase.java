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

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases asserting that the {@link MavenCoordinates} factory class is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MavenCoordinatesTestCase {

    @Test(expected = IllegalArgumentException.class)
    public void prohibitsNull() {
        MavenCoordinates.createCoordinate(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void prohibitsEmptyString() {
        MavenCoordinates.createCoordinate("");

    }

    @Test(expected = CoordinateParseException.class)
    public void incorrectFormat() {
        MavenCoordinates.createCoordinate("not-in-correct-format");

    }

    @Test
    public void fullProperties() {
        final MavenCoordinate coordinate = MavenCoordinates
            .createCoordinate("groupId:artifactId:jar:classifier:version");
        Assert.assertEquals("groupId", coordinate.getGroupId());
        Assert.assertEquals("artifactId", coordinate.getArtifactId());
        Assert.assertEquals(PackagingType.JAR, coordinate.getPackaging());
        Assert.assertEquals("classifier", coordinate.getClassifier());
        Assert.assertEquals("version", coordinate.getVersion());
    }

    @Test
    public void unknownVersion() {
        final MavenCoordinate coordinate = MavenCoordinates.createCoordinate("groupId:artifactId");
        Assert.assertEquals("groupId", coordinate.getGroupId());
        Assert.assertEquals("artifactId", coordinate.getArtifactId());
        Assert.assertEquals(PackagingType.JAR, coordinate.getPackaging());
        Assert.assertEquals("", coordinate.getClassifier());
        Assert.assertNull(coordinate.getVersion());
    }

    @Test
    public void unknownPackagingTypeAndClassifier() {
        final MavenCoordinate coordinate = MavenCoordinates.createCoordinate("groupId:artifactId:version");
        Assert.assertEquals("groupId", coordinate.getGroupId());
        Assert.assertEquals("artifactId", coordinate.getArtifactId());
        Assert.assertEquals(PackagingType.JAR, coordinate.getPackaging());
        Assert.assertEquals("", coordinate.getClassifier());
        Assert.assertEquals("version", coordinate.getVersion());
    }

    @Test
    public void unknownClassifier() {
        final MavenCoordinate coordinate = MavenCoordinates.createCoordinate("groupId:artifactId:ear:version");
        Assert.assertEquals("groupId", coordinate.getGroupId());
        Assert.assertEquals("artifactId", coordinate.getArtifactId());
        Assert.assertEquals(PackagingType.EAR, coordinate.getPackaging());
        Assert.assertEquals("", coordinate.getClassifier());
        Assert.assertEquals("version", coordinate.getVersion());
    }

    @Test
    public void blankPackagingType() {
        final MavenCoordinate coordinate = MavenCoordinates.createCoordinate("groupId:artifactId::classifier:version");
        Assert.assertEquals("groupId", coordinate.getGroupId());
        Assert.assertEquals("artifactId", coordinate.getArtifactId());
        Assert.assertEquals(PackagingType.JAR, coordinate.getPackaging()); // Defaults
        Assert.assertEquals("classifier", coordinate.getClassifier());
        Assert.assertEquals("version", coordinate.getVersion());
    }

}
