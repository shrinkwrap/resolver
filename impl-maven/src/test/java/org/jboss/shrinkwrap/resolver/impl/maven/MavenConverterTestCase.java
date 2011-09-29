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

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.junit.Test;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Compares behavior of Maven and Shrinwrap Dependency Resolver behavior
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
public class MavenConverterTestCase {

    @Test
    public void testGAV() {
        final String coords = "g:a:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("", artifact.getClassifier());
        Assert.assertEquals("jar", artifact.getExtension());

        MavenDependencyImpl dependency = MavenConverter.asDependency(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("", dependency.getClassifier());
        Assert.assertEquals("jar", dependency.getType());

    }

    @Test
    public void testGATV() {
        final String coords = "g:a:zip:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("", artifact.getClassifier());
        Assert.assertEquals("zip", artifact.getExtension());

        MavenDependencyImpl dependency = MavenConverter.asDependency(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("", dependency.getClassifier());
        Assert.assertEquals("zip", dependency.getType());

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

        MavenDependencyImpl dependency = MavenConverter.asDependency(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("", dependency.getClassifier());
        Assert.assertEquals("jar", dependency.getType());
    }

    @Test
    public void testGATCV() {
        final String coords = "g:a:zip:sources:1";

        Artifact artifact = new DefaultArtifact(coords);
        Assert.assertEquals("g", artifact.getGroupId());
        Assert.assertEquals("a", artifact.getArtifactId());
        Assert.assertEquals("1", artifact.getVersion());
        Assert.assertEquals("sources", artifact.getClassifier());
        Assert.assertEquals("zip", artifact.getExtension());

        MavenDependencyImpl dependency = MavenConverter.asDependency(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("sources", dependency.getClassifier());
        Assert.assertEquals("zip", dependency.getType());
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

        MavenDependencyImpl dependency = MavenConverter.asDependency(coords);
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("sources", dependency.getClassifier());
        Assert.assertEquals("jar", dependency.getType());
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

    @Test(expected = ResolutionException.class)
    public void test2GAemptyTemptyCV() {
        MavenDependencyImpl dependency = MavenConverter.asDependency("g:a:::1");
        Assert.assertEquals("g", dependency.getGroupId());
        Assert.assertEquals("a", dependency.getArtifactId());
        Assert.assertEquals("1", dependency.getVersion());
        Assert.assertEquals("sources", dependency.getClassifier());
        Assert.assertEquals("jar", dependency.getType());

    }

}
