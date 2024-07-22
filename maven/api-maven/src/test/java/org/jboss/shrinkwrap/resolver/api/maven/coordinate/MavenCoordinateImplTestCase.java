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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests asserting that the {@link MavenCoordinateImpl} is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class MavenCoordinateImplTestCase {

    @Test
    void equalsByValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        Assertions.assertEquals(coordinate1, coordinate2);
    }

    @Test
    void notEqualsByGroupIdValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl("wrong", artifactId, version, packaging, classifier);
        Assertions.assertNotEquals(coordinate1, coordinate2);
    }

    @Test
    void notEqualsByArtifactIdValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, "wrong", version, packaging, classifier);
        Assertions.assertNotEquals(coordinate1, coordinate2);
    }

    @Test
    void equalsByValueWithDifferentVersions() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, null, packaging, classifier);
        Assertions.assertEquals(coordinate1, coordinate2, "Version should not be considered in value equality check");
    }

    @Test
    void notEqualsByPackagingValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, version, PackagingType.EAR,
            classifier);
        Assertions.assertNotEquals(coordinate1, coordinate2);
    }

    @Test
    void notEqualsByClassifierValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, "wrong");
        Assertions.assertNotEquals(coordinate1, coordinate2);
    }

    @Test
    void equalHashCodes() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        Assertions.assertEquals(coordinate1.hashCode(), coordinate2.hashCode());
    }

    @Test
    void properties() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        Assertions.assertEquals(groupId, coordinate.getGroupId());
        Assertions.assertEquals(artifactId, coordinate.getArtifactId());
        Assertions.assertEquals(version, coordinate.getVersion());
        Assertions.assertEquals(packaging, coordinate.getPackaging());
        Assertions.assertEquals(classifier, coordinate.getClassifier());
        Assertions.assertEquals(groupId + ":" + artifactId + ":" + packaging + ":" + classifier + ":" + version, coordinate.toCanonicalForm());
    }

    @Test
    void defaultPackagingType() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final String classifier = "classifier";
        final MavenCoordinate coordinate = new MavenCoordinateImpl(groupId, artifactId, version, null, classifier);
        Assertions.assertEquals(PackagingType.JAR, coordinate.getPackaging());
    }

    @Test
    void ejbPackaging() {

        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final PackagingType packaging = PackagingType.of("ejb");
        final String version = "version";
        final MavenCoordinate coordinate = new MavenCoordinateImpl(groupId, artifactId, version, packaging, null);
        Assertions.assertEquals(PackagingType.EJB, coordinate.getPackaging());
        Assertions.assertEquals("jar", coordinate.getPackaging().getExtension());
    }
}
