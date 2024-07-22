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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for version range request resolutions.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @see <a href="http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html">Version range maven doc</a>
 */
class VersionResolvingUnitTestCase {

    @BeforeEach
    void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/repository");
    }

    @AfterEach
    void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    @Test
    void shouldResolveConcreteVersion() {
        // given

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:1.0.0");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        Assertions.assertEquals(1, versions.size());
        Assertions.assertEquals(lowest.getVersion(), highest.getVersion());
        Assertions.assertEquals(lowest.getVersion(), "1.0.0");
    }

    @Test
    void shouldResolveGreaterOrEqualWithoutLocalMetadata() throws Exception {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(2, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
        Assertions.assertEquals(highest, versions.get(1));
    }

    @Test
    void shouldResolveLowerOrEqual() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(2, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
        Assertions.assertEquals(highest, versions.get(1));
    }

    @Test
    void shouldResolveLowerThan() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(1, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
    }

    @Test
    void shouldResolveEqual() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(1, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
    }

    @Test
    void shouldResolveGreaterOrEqual() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(2, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
        Assertions.assertEquals(highest, versions.get(1));
    }

    @Test
    void shouldResolveGreater() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(1, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
        Assertions.assertEquals(highest, versions.get(0));
    }

    @Test
    void shouldNotResolveAnyVersion() {
        // given

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(3.0,)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        Assertions.assertNull(lowest);
        Assertions.assertNull(highest);
        Assertions.assertEquals(0, versions.size());
    }

    @Test
    void shouldResolveRange() {
        // given

        // when
        final MavenVersionRangeResult versionRangeResult = Maven.configureResolver().workOffline()
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-deps-b:(1.0.0,2.0.0)");
        final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
        final MavenCoordinate highest = versionRangeResult.getHighestVersion();
        final List<MavenCoordinate> versions = versionRangeResult.getVersions();

        // then
        Assertions.assertEquals(0, versions.size());
    }

    @Test
    void shouldResolveIncludingRange() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(2, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
        Assertions.assertEquals(highest, versions.get(1));
    }

    @Test
    void shouldResolveMultipleSets() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(1, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
    }

    @Test
    void shouldResolveDifferentThan() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(1, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
    }

    @Test
    void shouldResolveAllVersions() {
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
        Assertions.assertEquals(lowestCoordinate, lowest.toCanonicalForm());
        Assertions.assertEquals(highestCoordinate, highest.toCanonicalForm());
        Assertions.assertEquals(2, versions.size());
        Assertions.assertEquals(lowest, versions.get(0));
        Assertions.assertEquals(highest, versions.get(1));
    }


    /**
     * Test for a use-case from <a href="https://issues.redhat.com/browse/SHRINKRES-219">SHRINKRES-219</a>
     */
    @Test
    void resolveVersionsWithWrongMetadataChecksum() {

        String repoPath = "file://" + System.getProperty("user.dir") + "/"
            + System.getProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);

        MavenVersionRangeResult resolveVersionRange = Maven
            .configureResolver().withRemoteRepo("test-repository", repoPath, "default")
            .resolveVersionRange("org.jboss.shrinkwrap.test:test-wrong-metadata-checksum:[1.0.0,]");

        Assertions.assertEquals(1, resolveVersionRange.getVersions().size());

    }
}
