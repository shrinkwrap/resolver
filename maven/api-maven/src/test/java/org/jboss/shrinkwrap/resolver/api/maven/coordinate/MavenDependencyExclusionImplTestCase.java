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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests asserting that the {@link MavenDependencyExclusionImpl} is working as contracted
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class MavenDependencyExclusionImplTestCase {

    @Test
    void equalsByValue() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl(groupId, artifactId);
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl(groupId, artifactId);
        Assertions.assertEquals(exclusion1, exclusion2);
    }

    @Test
    void notEqualsByGroupIdValue() {
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId", "artifactId");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("groupId2", "artifactId");
        Assertions.assertNotEquals(exclusion1, exclusion2);
    }

    @Test
    void notEqualsByArtifactIdValue() {
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl("groupId", "artifactId");
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl("groupId", "artifactId2");
        Assertions.assertNotEquals(exclusion1, exclusion2);
    }

    @Test
    void equalHashCodes() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final MavenDependencyExclusion exclusion1 = new MavenDependencyExclusionImpl(groupId, artifactId);
        final MavenDependencyExclusion exclusion2 = new MavenDependencyExclusionImpl(groupId, artifactId);
        Assertions.assertEquals(exclusion1.hashCode(), exclusion2.hashCode());
    }

    @Test
    void properties() {
        final String groupId = "groupId";
        final String artifactId = "artifactId";
        final MavenDependencyExclusion exclusion = new MavenDependencyExclusionImpl(groupId, artifactId);
        Assertions.assertEquals(groupId, exclusion.getGroupId());
        Assertions.assertEquals(artifactId, exclusion.getArtifactId());
        Assertions.assertEquals(groupId + ":" + artifactId, exclusion.toCanonicalForm());
    }

}
