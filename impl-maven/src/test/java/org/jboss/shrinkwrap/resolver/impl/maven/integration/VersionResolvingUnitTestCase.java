/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for version range request resolutions.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @see <a href="http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html">Version range maven doc</a>
 */
public class VersionResolvingUnitTestCase {

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    @Test
    public void shouldResolveConcreteVersion() {
        // given

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:1.0.0");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(1, versions.size());
        assertEquals(lowest.getVersion(), highest.getVersion());
        assertEquals(lowest.getVersion(), "1.0.0");
    }

    @Test
    public void shouldResolveLowerOrEqual() throws Exception {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(,2.0.0]");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(2, versions.size());
        assertEquals(lowest, versions.get(0));
        assertEquals(highest, versions.get(1));
    }

    @Test
    public void shouldResolveLowerThan() throws Exception {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(,2.0.0)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(1, versions.size());
        assertEquals(lowest, versions.get(0));
    }

    @Test
    public void shouldResolveEqual() throws Exception {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:[1.0.0]");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(1, versions.size());
        assertEquals(lowest, versions.get(0));
    }

    @Test
    public void shouldResolveGreaterOrEqual() throws Exception {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:[1.0.0,)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(2, versions.size());
        assertEquals(lowest, versions.get(0));
        assertEquals(highest, versions.get(1));
    }

    @Test
    public void shouldResolveGreater() throws Exception {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(1.0.0,)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(1, versions.size());
        assertEquals(lowest, versions.get(0));
        assertEquals(highest, versions.get(0));
    }

    @Test
    public void shouldNotResolveAnyVersion() {
        // given

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(3.0,)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertNull(lowest);
        assertNull(highest);
        assertEquals(0, versions.size());
    }

    @Test
    public void shouldResolveRange() throws Exception {
        // given

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(1.0.0,2.0.0)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(0, versions.size());
    }

    @Test
    public void shouldResolveIncludingRange() throws Exception {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:[1.0.0,2.0.0]");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(2, versions.size());
        assertEquals(lowest, versions.get(0));
        assertEquals(highest, versions.get(1));
    }

    @Test
    public void shouldResolveMultipleSets() throws Exception {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(,1.0.0],[2.1.0,)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(1, versions.size());
        assertEquals(lowest, versions.get(0));
    }

    @Test
    public void shouldResolveDifferentThan() throws Exception {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(,1.0.0),(1.0.0,)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(1, versions.size());
        assertEquals(lowest, versions.get(0));
    }

    @Test
    public void shouldResolveAllVersions() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.resolver().offline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(,)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        assertEquals(highestCoordinate, highest.toCanonicalForm());
        assertEquals(2, versions.size());
        assertEquals(lowest, versions.get(0));
        assertEquals(highest, versions.get(1));
    }
}
