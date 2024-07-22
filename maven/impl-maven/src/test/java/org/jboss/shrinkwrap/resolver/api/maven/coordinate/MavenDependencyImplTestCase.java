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

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenDependencyImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests asserting that the {@link MavenDependencyImpl} is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class MavenDependencyImplTestCase {

    @Test
    void equalsByValueNoExclusions() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional);
        Assertions.assertEquals(dependency1, dependency2);
    }

    @Test
    void equalsByValueExclusions() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        Assertions.assertEquals(dependency1, dependency2);
    }

    @Test
    void equalsByValueExclusionsUnordered() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion11 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion12 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependencyExclusion exclusion21 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion22 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion11, exclusion12);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion22, exclusion21);
        Assertions.assertEquals(dependency1, dependency2);
    }

    @Test
    void notEqualsByValueExclusions() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("wrong", "artifactId2");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion2);
        Assertions.assertEquals(dependency1, dependency2);
    }

    @Test
    void notEqualsByValueExclusionsMismatchThis() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion);
        Assertions.assertEquals(dependency1, dependency2);
    }

    @Test
    void notEqualsByValueExclusionsMismatchThat() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional);
        Assertions.assertEquals(dependency1, dependency2);
    }

    @Test
    void notEqualsByValueCoordinate() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(new MavenCoordinateImpl("g", "a", "v", null, "c"),
            scope, optional);
        Assertions.assertNotEquals(dependency1, dependency2);
    }

    @Test
    void notEqualsByValueScope() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, ScopeType.IMPORT, optional);
        Assertions.assertEquals(dependency1, dependency2);
    }

    @Test
    void notEqualsByValueOptional() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, false);
        Assertions.assertEquals(dependency1, dependency2);
    }

    @Test
    void equalHashCodes() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    void properties() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final ScopeType scope = ScopeType.IMPORT;
        final boolean optional = true;
        final MavenCoordinate coordinate = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependency dependency = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
        Assertions.assertEquals(packaging, dependency.getPackaging());
        Assertions.assertEquals(classifier, dependency.getClassifier());
        final Set<MavenDependencyExclusion> exclusions = dependency.getExclusions();
        Assertions.assertEquals(2, exclusions.size());
        final Iterator<MavenDependencyExclusion> it = exclusions.iterator();
        final MavenDependencyExclusion roundtrip1 = it.next();
        Assertions.assertTrue(exclusions.contains(roundtrip1));
        final MavenDependencyExclusion roundtrip2 = it.next();
        Assertions.assertTrue(exclusions.contains(roundtrip2));
        Assertions.assertEquals(groupId + ":" + artifactId + ":" + packaging + ":" + classifier + ":" + version
            + ":" + scope, dependency.toCanonicalForm());
    }

    @Test
    void prohibitAddingExclusions() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final MavenDependency dependency = new MavenDependencyImpl(coordinate, null, true);
        final MavenDependencyExclusion exclusion = new MavenDependencyExclusionImpl("g", "a");
        boolean gotExpectedException = false;
        try {
            dependency.getExclusions().add(exclusion);
        } catch (final UnsupportedOperationException uoe) {
            gotExpectedException = true;
        }
        Assertions.assertTrue(gotExpectedException);
    }

    @Test
    void defaultScope() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final boolean optional = true;
        final MavenDependency dependency = new MavenDependencyImpl(coordinate, null, optional);
        Assertions.assertEquals(ScopeType.COMPILE, dependency.getScope());
    }

    private MavenCoordinate createCoordinate() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final String version = "version";
        final PackagingType packaging = PackagingType.POM;
        final String classifier = "classifier";
        final MavenCoordinate coordinate = new MavenCoordinateImpl(groupId, artifactId, version, packaging, classifier);
        return coordinate;
    }
}
