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
import org.junit.Assert;
import org.junit.Test;

/**
 * Validates that internal parser has the same results as Aether one
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MavenCoordinateParserTestCase {
    @Test
    public void testGAV() {
        final String coords = "g:a:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("", artifact.getClassifier());
        Assert.assertEquals("jar", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("", dependency.getClassifier());
        Assert.assertEquals("jar", dependency.getPackaging().toString());

    }

    @Test
    public void testGATV() {
        final String coords = "g:a:pom:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("", artifact.getClassifier());
        Assert.assertEquals("pom", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("", dependency.getClassifier());
        Assert.assertEquals("pom", dependency.getPackaging().toString());

    }

    @Test
    public void testGAemptyTV() {
        final String coords = "g:a::1";

        Artifact artifact = new DefaultArtifact(coords);
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("", artifact.getClassifier());
        Assert.assertEquals("jar", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("", dependency.getClassifier());
        Assert.assertEquals("jar", dependency.getPackaging().toString());
    }

    @Test
    public void testGATCV() {
        final String coords = "g:a:pom:sources:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("sources", artifact.getClassifier());
        Assert.assertEquals("pom", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("sources", dependency.getClassifier());
        Assert.assertEquals("pom", dependency.getPackaging().toString());
    }

    @Test
    public void testGAemptyTCV() {
        final String coords = "g:a::sources:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("sources", artifact.getClassifier());
        Assert.assertEquals("jar", artifact.getExtension());

        MavenCoordinateParser dependency = MavenCoordinateParser.parse(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("sources", dependency.getClassifier());
        Assert.assertEquals("jar", dependency.getPackaging().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGAemptyTemptyCV() {
        Artifact artifact = new DefaultArtifact("g:a:::1");
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("sources", artifact.getClassifier());
        Assert.assertEquals("jar", artifact.getExtension());
    }

    @Test(expected = CoordinateParseException.class)
    public void test2GAemptyTemptyCV() {
        MavenCoordinateParser dependency = MavenCoordinateParser.parse("g:a:::1");
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("sources", dependency.getClassifier());
        Assert.assertEquals("jar", dependency.getPackaging());

    }
}
