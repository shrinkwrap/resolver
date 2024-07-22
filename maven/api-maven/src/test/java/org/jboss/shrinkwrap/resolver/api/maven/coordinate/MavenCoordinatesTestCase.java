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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases asserting that the {@link MavenCoordinates} factory class is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class MavenCoordinatesTestCase {

    @Test
    void prohibitsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenCoordinates.createCoordinate(null);
        });
    }

    @Test
    void prohibitsEmptyString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenCoordinates.createCoordinate("");
        });
    }

    @Test
    void incorrectFormat() {
        Assertions.assertThrows(CoordinateParseException.class, () -> {
            MavenCoordinates.createCoordinate("not-in-correct-format");
        });
    }

    @Test
    void fullProperties() {
        final MavenCoordinate coordinate = MavenCoordinates
                .createCoordinate("groupId:artifactId:jar:classifier:version");
        Assertions.assertEquals("groupId", coordinate.getGroupId());
        Assertions.assertEquals("artifactId", coordinate.getArtifactId());
        Assertions.assertEquals(PackagingType.JAR, coordinate.getPackaging());
        Assertions.assertEquals("classifier", coordinate.getClassifier());
        Assertions.assertEquals("version", coordinate.getVersion());
    }

    @Test
    void unknownVersion() {
        final MavenCoordinate coordinate = MavenCoordinates.createCoordinate("groupId:artifactId");
        Assertions.assertEquals("groupId", coordinate.getGroupId());
        Assertions.assertEquals("artifactId", coordinate.getArtifactId());
        Assertions.assertEquals(PackagingType.JAR, coordinate.getPackaging());
        Assertions.assertEquals("", coordinate.getClassifier());
        Assertions.assertNull(coordinate.getVersion());
    }

    @Test
    void unknownPackagingTypeAndClassifier() {
        final MavenCoordinate coordinate = MavenCoordinates.createCoordinate("groupId:artifactId:version");
        Assertions.assertEquals("groupId", coordinate.getGroupId());
        Assertions.assertEquals("artifactId", coordinate.getArtifactId());
        Assertions.assertEquals(PackagingType.JAR, coordinate.getPackaging());
        Assertions.assertEquals("", coordinate.getClassifier());
        Assertions.assertEquals("version", coordinate.getVersion());
    }

    @Test
    void unknownClassifier() {
        final MavenCoordinate coordinate = MavenCoordinates.createCoordinate("groupId:artifactId:ear:version");
        Assertions.assertEquals("groupId", coordinate.getGroupId());
        Assertions.assertEquals("artifactId", coordinate.getArtifactId());
        Assertions.assertEquals(PackagingType.EAR, coordinate.getPackaging());
        Assertions.assertEquals("", coordinate.getClassifier());
        Assertions.assertEquals("version", coordinate.getVersion());
    }

    @Test
    void blankPackagingType() {
        final MavenCoordinate coordinate = MavenCoordinates.createCoordinate("groupId:artifactId::classifier:version");
        Assertions.assertEquals("groupId", coordinate.getGroupId());
        Assertions.assertEquals("artifactId", coordinate.getArtifactId());
        Assertions.assertEquals(PackagingType.JAR, coordinate.getPackaging()); // Defaults
        Assertions.assertEquals("classifier", coordinate.getClassifier());
        Assertions.assertEquals("version", coordinate.getVersion());
    }
}
