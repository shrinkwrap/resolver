/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat Middleware LLC, and individual contributors
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

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenChecksumPolicy;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepositories;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.TestFileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for <a href="https://issues.redhat.com/browse/SHRINKRES-226">SHRINKRES-226</a> - Loading of the settings.xml configuration has been postponed. This test case aims at
 * (no-)loading of the invalid settings.xml, not at the result of resolving etc...
 *
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
class InvalidSettingsTestCase {

    private static final String INVALID_SETTINGS = "target/settings/profiles/settings-invalid.xml";
    private static final String CENTRAL_SETTINGS = "target/settings/profiles/settings-central.xml";
    private static final String FROM_CLASSLOADER = "profiles/settings3-from-classpath.xml";
    private static final String JUNIT_CANONICAL = "org.junit.jupiter:junit-jupiter:5.11.3";
    private static final File TEST_BOM = new File("target/poms/test-bom.xml");

    @BeforeAll
    static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION, INVALID_SETTINGS);
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, INVALID_SETTINGS);
    }

    private final MavenDependency dependency = MavenDependencies.createDependency(
        "org.jboss.shrinkwrap.test:test-dependency-test:1.0.0", ScopeType.TEST, false);

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @BeforeEach
    @AfterEach
    // For debugging, you might want to temporarily remove the @After lifecycle call just to sanity-check for yourself
    // the repo
    void cleanup() throws Exception {
        TestFileUtil.removeDirectory(new File("target/local-only-repository"));
    }

    /**
     * Testing methods, that doesn't need to have loaded configuration for its calling
     * During this test the settings-invalid.xml shouldn't be loaded so no exception should be thrown
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test
    void shouldNotLoadInvalidSettingsCR() {
        Maven.configureResolver().withRemoteRepo(MavenRemoteRepositories.createRemoteRepository("jboss",
            "https://repository.jboss.org/nexus/content/repositories/releases/", "default")
            .setChecksumPolicy(MavenChecksumPolicy.CHECKSUM_POLICY_IGNORE));

        Maven.configureResolver().workOffline();
        Maven.configureResolver().workOffline(true);
        Maven.configureResolver().useLegacyLocalRepo(true);
        Maven.configureResolver().withClassPathResolution(false);
        Maven.configureResolver().withMavenCentralRepo(false);
        Maven.configureResolver().addDependency(dependency);
    }

    /**
     * Testing methods, that doesn't need to have loaded configuration for its calling
     * During this test the settings-invalid.xml shouldn't be loaded so no exception should be thrown
     * <br/>
     * Uses method Maven.resolver()
     */
    @Test
    void shouldNotLoadInvalidSettingsR() {
        Maven.resolver().addDependency(dependency);
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>resolve("org.junit.jupiter:junit-jupiter:5.11.3").withTransitivity()</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test
    void shouldLoadInvalidSettingsDueResolvingCR() {
        Assertions.assertThrows(InvalidConfigurationFileException.class, () -> {
            Maven.configureResolver().resolve(JUNIT_CANONICAL).withTransitivity();
        });
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>resolve("org.junit.jupiter:junit-jupiter:5.11.3").withTransitivity()</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.resolver()
     */
    @Test
    void shouldLoadInvalidSettingsDueResolvingR() {
        Assertions.assertThrows(InvalidConfigurationFileException.class, () -> {
            Maven.resolver().resolve(JUNIT_CANONICAL).withTransitivity();
        });
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>resolveVersionRange("junit:junit")</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test
    void shouldLoadInvalidSettingsDueResolvingVersionsCR() {
        Assertions.assertThrows(InvalidConfigurationFileException.class, () -> {
            Maven.configureResolver().resolveVersionRange(JUNIT_CANONICAL);
        });
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>resolveVersionRange("junit:junit")</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.resolver()
     */
    @Test
    void shouldLoadInvalidSettingsDueResolvingVersionsR() {
        Assertions.assertThrows(InvalidConfigurationFileException.class, () -> {
            Maven.resolver().resolveVersionRange(JUNIT_CANONICAL);
        });
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>loadPomFromFile(new File("target/poms/test-bom.xml"))</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test
    void shouldLoadInvalidSettingsDueLoadingPomCR() {
        Assertions.assertThrows(InvalidConfigurationFileException.class, () -> {
            Maven.configureResolver().loadPomFromFile(TEST_BOM);
        });
    }

    /**
     * In this test, the settings-invalid.xml should be loaded during the
     * <code>loadPomFromFile(new File("target/poms/test-bom.xml")</code> phase,
     * for this reason, the InvalidConfigurationFileException should be thrown.
     * <br/>
     * Uses method Maven.resolver()
     */
    @Test
    void shouldLoadInvalidSettingsDueLoadingPomR() {
        Assertions.assertThrows(InvalidConfigurationFileException.class, () -> {
            Maven.resolver().loadPomFromFile(TEST_BOM);
        });
    }

    /**
     * During this test the settings-invalid.xml should NOT be loaded, but the settings-central.xml should.
     * No exception should be thrown.
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test
    void shouldLoadCentralSettingsFromFileCR() {
        // from file
        MavenResolverSystem centralFromFile = Maven.configureResolver().fromFile(CENTRAL_SETTINGS);
        shouldResolveAndLoadPom(centralFromFile);

        //from classloader resource
        MavenResolverSystem fromClassloaderRes =
            Maven.configureResolver().fromClassloaderResource(FROM_CLASSLOADER);
        shouldResolveAndLoadPom(fromClassloaderRes);
    }

    private void shouldResolveAndLoadPom(MavenResolverSystem mavenResolverSystem) {
        mavenResolverSystem.resolve(JUNIT_CANONICAL).withTransitivity();
        mavenResolverSystem.loadPomFromFile(TEST_BOM);
    }

}
