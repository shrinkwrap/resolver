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
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests asserting that the {@link MavenCoordinateImpl} is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MavenCoordinateImplTestCase {

    @Test
    public void equalsByValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        Assert.assertEquals(coordinate1, coordinate2);
    }

    @Test
    public void notEqualsByGroupIdValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl("wrong", artifactId, version, packaging, classifier);
        Assert.assertFalse(coordinate1.equals(coordinate2));
    }

    @Test
    public void notEqualsByArtifactIdValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, "wrong", version, packaging, classifier);
        Assert.assertFalse(coordinate1.equals(coordinate2));
    }

    @Test
    public void equalsByValueWithDifferentVersions() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, null, packaging, classifier);
        Assert.assertEquals("Version should not be considered in value equality check", coordinate1, coordinate2);
    }

    @Test
    public void notEqualsByPackagingValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, version, PackagingType.EAR,
            classifier);
        Assert.assertFalse(coordinate1.equals(coordinate2));
    }

    @Test
    public void notEqualsByClassifierValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, "wrong");
        Assert.assertFalse(coordinate1.equals(coordinate2));
    }

    @Test
    public void equalHashCodes() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate1 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenCoordinate coordinate2 = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        Assert.assertTrue(coordinate1.hashCode() == coordinate2.hashCode());
    }

    @Test
    public void properties() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        Assert.assertEquals(groupId, coordinate.getGroupId());
        Assert.assertEquals(artifactId, coordinate.getArtifactId());
        Assert.assertEquals(version, coordinate.getVersion());
        Assert.assertEquals(packaging, coordinate.getPackaging());
        Assert.assertEquals(classifier, coordinate.getClassifier());
        Assert.assertEquals(groupId + ":" + artifactId + ":" + packaging.toString() + ":" + classifier + ":" + version,
            coordinate.toCanonicalForm());
    }

    @Test
    public void defaultPackagingType() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final String classifier = "classifier";
        final MavenCoordinate coordinate = new MavenCoordinateImpl(groupId, artifactId, version, null, classifier);
        Assert.assertEquals(PackagingType.JAR, coordinate.getPackaging());
    }
}
