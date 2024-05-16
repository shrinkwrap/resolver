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

import java.util.Iterator;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases asserting that the {@link MavenDependencies} factory class is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MavenDependenciesTestCase {

    @Test(expected = IllegalArgumentException.class)
    public void prohibitsNullCanonicalForm() {
        MavenDependencies.createDependency((String) null, ScopeType.IMPORT, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prohibitsNullCoordinate() {
        MavenDependencies.createDependency((MavenCoordinate) null, ScopeType.IMPORT, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prohibitsEmptyStringCanonicalForm() {
        MavenDependencies.createDependency("", ScopeType.IMPORT, true);
    }

    @Test(expected = CoordinateParseException.class)
    public void prohibitsIncorrectFormatCanonicalForm() {
        MavenDependencies.createDependency("not-in-correct-format", ScopeType.IMPORT, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prohibitsNullCanonicalFormForExclusion() {
        MavenDependencies.createExclusion(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prohibitsEmptyStringCanonicalFormForExclusion() {
        MavenDependencies.createExclusion(null);
    }

    @Test(expected = CoordinateParseException.class)
    public void prohibitsIncorrectFormatCanonicalFormForExclusion() {
        MavenDependencies.createExclusion("not-in-correct-format");
    }

    @Test
    public void createExclusion() {
        final MavenDependencyExclusion exclusion = MavenDependencies.createExclusion("groupId:artifactId");
        Assert.assertEquals("groupId", exclusion.getGroupId());
        Assert.assertEquals("artifactId", exclusion.getArtifactId());
    }

    @Test
    public void createExclusionFromGroupIdAndArtifactId() {
        final MavenDependencyExclusion exclusion = MavenDependencies.createExclusion("groupId", "artifactId");
        Assert.assertEquals("groupId", exclusion.getGroupId());
        Assert.assertEquals("artifactId", exclusion.getArtifactId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createExclusionNullGroupId() {
        MavenDependencies.createExclusion(null, "artifactId");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createExclusionNullArtifactId() {
        MavenDependencies.createExclusion("groupId", null);
    }

    @Test(expected = CoordinateParseException.class)
    public void createExclusionExtraProps() {
        MavenDependencies.createExclusion("groupId:artifactId:shouldNotBeHere");
    }

    @Test
    public void fullProperties() {
        final MavenDependency dependency = MavenDependencies.createDependency(
            "groupId:artifactId:ear:classifier:version", ScopeType.PROVIDED, true);
        Assert.assertEquals("groupId", dependency.getGroupId());
        Assert.assertEquals("artifactId", dependency.getArtifactId());
        Assert.assertEquals(PackagingType.EAR, dependency.getPackaging());
        Assert.assertEquals("classifier", dependency.getClassifier());
        Assert.assertEquals("version", dependency.getVersion());
        Assert.assertEquals(ScopeType.PROVIDED, dependency.getScope());
        Assert.assertTrue(dependency.isOptional());
    }

    @Test
    public void fullPropertiesWithExclusions() {
        final MavenDependencyExclusion exclusion1 = MavenDependencies.createExclusion("group1:artifact1");
        final MavenDependencyExclusion exclusion2 = MavenDependencies.createExclusion("group2:artifact2");
        final MavenDependency dependency = MavenDependencies.createDependency(
            "groupId:artifactId:ear:classifier:version", ScopeType.PROVIDED, true, exclusion1, exclusion2);
        Assert.assertEquals("groupId", dependency.getGroupId());
        Assert.assertEquals("artifactId", dependency.getArtifactId());
        Assert.assertEquals(PackagingType.EAR, dependency.getPackaging());
        Assert.assertEquals("classifier", dependency.getClassifier());
        Assert.assertEquals("version", dependency.getVersion());
        Assert.assertEquals(ScopeType.PROVIDED, dependency.getScope());
        Assert.assertTrue(dependency.isOptional());
        final Set<MavenDependencyExclusion> exclusions = dependency.getExclusions();
        Assert.assertEquals(2, exclusions.size());
        final Iterator<MavenDependencyExclusion> it = exclusions.iterator();
        final MavenDependencyExclusion roundtrip1 = it.next();
        Assert.assertEquals("group1", roundtrip1.getGroupId());
        Assert.assertEquals("artifact1", roundtrip1.getArtifactId());
        final MavenDependencyExclusion roundtrip2 = it.next();
        Assert.assertEquals("group2", roundtrip2.getGroupId());
        Assert.assertEquals("artifact2", roundtrip2.getArtifactId());
    }

    @Test
    public void nullExclusionsAdjustedToEmptySet() {
        final MavenDependency dependency = MavenDependencies.createDependency(
            "groupId:artifactId:ear:classifier:version", ScopeType.PROVIDED, true, (MavenDependencyExclusion) null);
        Assert.assertEquals(0, dependency.getExclusions().size());
    }

}
