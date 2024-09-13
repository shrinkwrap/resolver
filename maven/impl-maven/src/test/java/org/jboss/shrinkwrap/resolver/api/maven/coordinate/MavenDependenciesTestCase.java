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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases asserting that the {@link MavenDependencies} factory class is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class MavenDependenciesTestCase {

    @Test
    void prohibitsNullCanonicalForm() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenDependencies.createDependency((String) null, ScopeType.IMPORT, true);
        });
    }

    @Test
    void prohibitsNullCoordinate() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenDependencies.createDependency((MavenCoordinate) null, ScopeType.IMPORT, true);
        });
    }

    @Test
    void prohibitsEmptyStringCanonicalForm() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenDependencies.createDependency("", ScopeType.IMPORT, true);
        });
    }

    @Test
    void prohibitsIncorrectFormatCanonicalForm() {
        Assertions.assertThrows(CoordinateParseException.class, () -> {
            MavenDependencies.createDependency("not-in-correct-format", ScopeType.IMPORT, true);
        });
    }

    @Test
    void prohibitsNullCanonicalFormForExclusion() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenDependencies.createExclusion(null);
        });
    }

    @Test
    void prohibitsEmptyStringCanonicalFormForExclusion() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenDependencies.createExclusion("");
        });
    }

    @Test
    void prohibitsIncorrectFormatCanonicalFormForExclusion() {
        Assertions.assertThrows(CoordinateParseException.class, () -> {
            MavenDependencies.createExclusion("not-in-correct-format");
        });
    }

    @Test
    void createExclusion() {
        final MavenDependencyExclusion exclusion = MavenDependencies.createExclusion("groupId:artifactId");
        Assertions.assertEquals("groupId", exclusion.getGroupId());
        Assertions.assertEquals("artifactId", exclusion.getArtifactId());
    }

    @Test
    void createExclusionFromGroupIdAndArtifactId() {
        final MavenDependencyExclusion exclusion = MavenDependencies.createExclusion("groupId", "artifactId");
        Assertions.assertEquals("groupId", exclusion.getGroupId());
        Assertions.assertEquals("artifactId", exclusion.getArtifactId());
    }

    @Test
    void createExclusionNullGroupId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenDependencies.createExclusion(null, "artifactId");
        });
    }

    @Test
    void createExclusionNullArtifactId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MavenDependencies.createExclusion("groupId", null);
        });
    }

    @Test
    void createExclusionExtraProps() {
        Assertions.assertThrows(CoordinateParseException.class, () -> {
            MavenDependencies.createExclusion("groupId:artifactId:shouldNotBeHere");
        });
    }

    @Test
    void fullProperties() {
        final MavenDependency dependency = MavenDependencies.createDependency(
                "groupId:artifactId:ear:classifier:version", ScopeType.PROVIDED, true);
        Assertions.assertEquals("groupId", dependency.getGroupId());
        Assertions.assertEquals("artifactId", dependency.getArtifactId());
        Assertions.assertEquals(PackagingType.EAR, dependency.getPackaging());
        Assertions.assertEquals("classifier", dependency.getClassifier());
        Assertions.assertEquals("version", dependency.getVersion());
        Assertions.assertEquals(ScopeType.PROVIDED, dependency.getScope());
        Assertions.assertTrue(dependency.isOptional());
    }

    @Test
    void fullPropertiesWithExclusions() {
        final MavenDependencyExclusion exclusion1 = MavenDependencies.createExclusion("group1:artifact1");
        final MavenDependencyExclusion exclusion2 = MavenDependencies.createExclusion("group2:artifact2");
        final MavenDependency dependency = MavenDependencies.createDependency(
                "groupId:artifactId:ear:classifier:version", ScopeType.PROVIDED, true, exclusion1, exclusion2);
        Assertions.assertEquals("groupId", dependency.getGroupId());
        Assertions.assertEquals("artifactId", dependency.getArtifactId());
        Assertions.assertEquals(PackagingType.EAR, dependency.getPackaging());
        Assertions.assertEquals("classifier", dependency.getClassifier());
        Assertions.assertEquals("version", dependency.getVersion());
        Assertions.assertEquals(ScopeType.PROVIDED, dependency.getScope());
        Assertions.assertTrue(dependency.isOptional());
        final Set<MavenDependencyExclusion> exclusions = dependency.getExclusions();
        Assertions.assertEquals(2, exclusions.size());
        final Iterator<MavenDependencyExclusion> it = exclusions.iterator();
        final MavenDependencyExclusion roundtrip1 = it.next();
        Assertions.assertEquals("group1", roundtrip1.getGroupId());
        Assertions.assertEquals("artifact1", roundtrip1.getArtifactId());
        final MavenDependencyExclusion roundtrip2 = it.next();
        Assertions.assertEquals("group2", roundtrip2.getGroupId());
        Assertions.assertEquals("artifact2", roundtrip2.getArtifactId());
    }

    @Test
    void nullExclusionsAdjustedToEmptySet() {
        final MavenDependency dependency = MavenDependencies.createDependency(
                "groupId:artifactId:ear:classifier:version", ScopeType.PROVIDED, true, (MavenDependencyExclusion) null);
        Assertions.assertEquals(0, dependency.getExclusions().size());
    }

}
