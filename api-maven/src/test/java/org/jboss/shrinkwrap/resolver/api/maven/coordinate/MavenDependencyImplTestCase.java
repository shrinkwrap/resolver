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
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests asserting that the {@link MavenDependencyImpl} is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MavenDependencyImplTestCase {

    @Test
    public void equalsByValueNoExclusions() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional);
        Assert.assertEquals(dependency1, dependency2);
    }

    @Test
    public void equalsByValueExclusions() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        Assert.assertEquals(dependency1, dependency2);
    }

    @Test
    public void equalsByValueExclusionsUnordered() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion11 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion12 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependencyExclusion exclusion21 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion22 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion11, exclusion12);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion22, exclusion21);
        Assert.assertEquals(dependency1, dependency2);
    }

    @Test
    public void notEqualsByValueExclusions() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("wrong", "artifactId2");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion2);
        Assert.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void notEqualsByValueExclusionsMismatchThis() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion);
        Assert.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void notEqualsByValueExclusionsMismatchThat() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional);
        Assert.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void notEqualsByValueCoordinate() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(new MavenCoordinateImpl("g", "a", "v", null, "c"),
            scope, optional);
        Assert.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void notEqualsByValueScope() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, ScopeType.IMPORT, optional);
        Assert.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void notEqualsByValueOptional() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, false);
        Assert.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void equalHashCodes() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final ScopeType scope = ScopeType.RUNTIME;
        final boolean optional = true;
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId1", "artifactId1");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("groupId2", "artifactId2");
        final MavenDependency dependency1 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        final MavenDependency dependency2 = new MavenDependencyImpl(coordinate, scope, optional, exclusion1, exclusion2);
        Assert.assertTrue(dependency1.hashCode() == dependency2.hashCode());
    }

    @Test
    public void properties() {
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
        Assert.assertEquals(groupId, dependency.getGroupId());
        Assert.assertEquals(artifactId, dependency.getArtifactId());
        Assert.assertEquals(version, dependency.getVersion());
        Assert.assertEquals(packaging, dependency.getPackaging());
        Assert.assertEquals(classifier, dependency.getClassifier());
        final Set<MavenDependencyExclusion> exclusions = dependency.getExclusions();
        Assert.assertEquals(2, exclusions.size());
        final Iterator<MavenDependencyExclusion> it = exclusions.iterator();
        final MavenDependencyExclusion roundtrip1 = it.next();
        Assert.assertEquals(exclusion1, roundtrip1);
        final MavenDependencyExclusion roundtrip2 = it.next();
        Assert.assertEquals(exclusion2, roundtrip2);
        Assert.assertEquals(groupId + ":" + artifactId + ":" + packaging.toString() + ":" + classifier + ":" + version,
            dependency.toCanonicalForm());
    }

    @Test
    public void prohibitAddingExclusions() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final MavenDependency dependency = new MavenDependencyImpl(coordinate, null, true);
        final MavenDependencyExclusion exclusion = new MavenDependencyExclusionImpl("g", "a");
        boolean gotExpectedException = false;
        try {
            dependency.getExclusions().add(exclusion);
        } catch (final UnsupportedOperationException uoe) {
            gotExpectedException = true;
        }
        Assert.assertTrue(gotExpectedException);
    }

    @Test
    public void defaultScope() {
        final MavenCoordinate coordinate = this.createCoordinate();
        final boolean optional = true;
        final MavenDependency dependency = new MavenDependencyImpl(coordinate, null, optional);
        Assert.assertEquals(ScopeType.COMPILE, dependency.getScope());
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
