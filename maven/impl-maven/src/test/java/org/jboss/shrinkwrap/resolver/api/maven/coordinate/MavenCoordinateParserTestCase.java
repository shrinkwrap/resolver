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

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates.MavenCoordinateParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Validates that internal parser has the same results as Aether one
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MavenCoordinateParserTestCase {

    @Test
    void testGAV() {
        final String coords = "g:a:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assertions.assertEquals("g", artifact.getGroupId());
        Assertions.assertEquals("a", artifact.getArtifactId());
        Assertions.assertEquals("1", artifact.getVersion());
        Assertions.assertEquals("", artifact.getClassifier());
        Assertions.assertEquals("jar", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assertions.assertEquals("g", dependency.getGroupId());
        Assertions.assertEquals("a", dependency.getArtifactId());
        Assertions.assertEquals("1", dependency.getVersion());
        Assertions.assertEquals("", dependency.getClassifier());
        Assertions.assertEquals("jar", dependency.getPackaging().toString());

    }

    @Test
    void testGATV() {
        final String coords = "g:a:pom:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assertions.assertEquals("g", artifact.getGroupId());
        Assertions.assertEquals("a", artifact.getArtifactId());
        Assertions.assertEquals("1", artifact.getVersion());
        Assertions.assertEquals("", artifact.getClassifier());
        Assertions.assertEquals("pom", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assertions.assertEquals("g", dependency.getGroupId());
        Assertions.assertEquals("a", dependency.getArtifactId());
        Assertions.assertEquals("1", dependency.getVersion());
        Assertions.assertEquals("", dependency.getClassifier());
        Assertions.assertEquals("pom", dependency.getPackaging().toString());

    }

    @Test
    void testGAemptyTV() {
        final String coords = "g:a::1";

        Artifact artifact = new DefaultArtifact(coords);
        Assertions.assertEquals("g", artifact.getGroupId());
        Assertions.assertEquals("a", artifact.getArtifactId());
        Assertions.assertEquals("1", artifact.getVersion());
        Assertions.assertEquals("", artifact.getClassifier());
        Assertions.assertEquals("jar", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assertions.assertEquals("g", dependency.getGroupId());
        Assertions.assertEquals("a", dependency.getArtifactId());
        Assertions.assertEquals("1", dependency.getVersion());
        Assertions.assertEquals("", dependency.getClassifier());
        Assertions.assertEquals("jar", dependency.getPackaging().toString());
    }

    @Test
    void testGATCV() {
        final String coords = "g:a:pom:sources:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assertions.assertEquals("g", artifact.getGroupId());
        Assertions.assertEquals("a", artifact.getArtifactId());
        Assertions.assertEquals("1", artifact.getVersion());
        Assertions.assertEquals("sources", artifact.getClassifier());
        Assertions.assertEquals("pom", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assertions.assertEquals("g", dependency.getGroupId());
        Assertions.assertEquals("a", dependency.getArtifactId());
        Assertions.assertEquals("1", dependency.getVersion());
        Assertions.assertEquals("sources", dependency.getClassifier());
        Assertions.assertEquals("pom", dependency.getPackaging().toString());
    }

    @Test
    void testGAemptyTCV() {
        final String coords = "g:a::sources:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assertions.assertEquals("g", artifact.getGroupId());
        Assertions.assertEquals("a", artifact.getArtifactId());
        Assertions.assertEquals("1", artifact.getVersion());
        Assertions.assertEquals("sources", artifact.getClassifier());
        Assertions.assertEquals("jar", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assertions.assertEquals("g", dependency.getGroupId());
        Assertions.assertEquals("a", dependency.getArtifactId());
        Assertions.assertEquals("1", dependency.getVersion());
        Assertions.assertEquals("sources", dependency.getClassifier());
        Assertions.assertEquals("jar", dependency.getPackaging().toString());
    }

    @Test
    void testGAemptyTemptyCV() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DefaultArtifact("g:a:::1");
        });
    }

    @Test
    void test2GAemptyTemptyCV() {
        Assertions.assertThrows(CoordinateParseException.class, () -> {
            MavenCoordinateParser.parse("g:a:::1");
        });
    }
}
