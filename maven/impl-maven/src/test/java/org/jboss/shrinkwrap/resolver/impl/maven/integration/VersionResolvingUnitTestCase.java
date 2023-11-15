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

import java.io.File;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.TestFileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for version range request resolutions.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @see <a href="http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html">Version range maven doc</a>
 */
public class VersionResolvingUnitTestCase {

    @Before
    public void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/repository");
    }

    @After
    public void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    @Test
    public void shouldResolveConcreteVersion() {
        // given

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
    public void shouldResolveGreaterOrEqualWithoutLocalMetadata() throws Exception {
        final String fakeSettings = "target/settings/profiles/settings.xml";
        final String nonExistingRepository = "target/non-existing-repository";

        TestFileUtil.removeDirectory(new File(nonExistingRepository));

        System.clearProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);

        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, nonExistingRepository);

        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver()
                .fromFile(fakeSettings)
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
    public void shouldResolveLowerOrEqual() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
    public void shouldResolveLowerThan() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
    public void shouldResolveEqual() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
    public void shouldResolveGreaterOrEqual() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
    public void shouldResolveGreater() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
    public void shouldResolveRange() {
        // given

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(1.0.0,2.0.0)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        assertEquals(0, versions.size());
    }

    @Test
    public void shouldResolveIncludingRange() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
    public void shouldResolveMultipleSets() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:1.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
    public void shouldResolveDifferentThan() {
        // given
        final String lowestCoordinate = "org.jboss.shrinkwrap.test:test-deps-b:jar:2.0.0";
        final String highestCoordinate = lowestCoordinate;

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
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


    /**
     * Test for an usecase from SHRINKRES-219
     */
    @Test
    public void resolveVersionsWithWrongMetadataChecksum() {

        String repoPath = "file://" + System.getProperty("user.dir") + "/"
            + System.getProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);

        MavenVersionRangeResult resolveVersionRange = Maven
            .configureResolver().withRemoteRepo("test-repository", repoPath, "default")
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-wrong-metadata-checksum:[1.0.0,]");

        Assert.assertEquals(1, resolveVersionRange.getVersions().size());

    }
}
