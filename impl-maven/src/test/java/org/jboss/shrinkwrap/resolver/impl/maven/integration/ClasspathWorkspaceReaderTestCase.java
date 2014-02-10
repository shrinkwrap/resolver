/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.TestFileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests that resolution of archives from a ClassPath-based repository works as expected
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class ClasspathWorkspaceReaderTestCase {
    private static final String FAKE_SETTINGS = "target/settings/profile/settings.xml";

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION, FAKE_SETTINGS);
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, FAKE_SETTINGS);
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/non-existing-repository");
    }

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    public void cleanup() throws Exception {
        TestFileUtil.removeDirectory(new File("target/non-existing-repository"));
    }

    @Test(expected = NoResolvedResultException.class)
    public void shouldFailWhileNotReadingReactor() {

        final PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");
        // Ensure we can disable ClassPath resolution
        resolver.resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven")
                .withClassPathResolution(false).withoutTransitivity().asSingle(File.class);
        Assert.fail("Reactor is not activated, resolution of another module should fail.");
    }

    @Test
    public void shouldBeAbleToLoadArtifactDirectlyFromClassPath() {

        // Ensure we can use ClassPath resolution to get the results of the "current" build
        final PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");
        File[] files = resolver.resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven")
                .withTransitivity().as(File.class);
        new ValidationUtil("shrinkwrap-resolver-api", "shrinkwrap-resolver-api-maven")
                .validate(files);
    }

    // SHRINKRES-102
    @Test
    public void shouldBeAbleToLoadTestsArtifactDirectlyFromClassPath() throws Exception {

        // Ensure we can use ClassPath resolution to get the tests package of the "current" build
        final PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");
        File file = resolver.resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:tests:?")
                .withoutTransitivity().asSingle(File.class);

        new ValidationUtil("shrinkwrap-resolver-api-maven").validate(file);

        // check content of resolved jar, it should contain given class
        boolean containsTestClass = false;
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.equals("org/jboss/shrinkwrap/resolver/api/maven/ScopeTypeTestCase.class")) {
                containsTestClass = true;
                break;
            }
        }

        Assert.assertTrue("Classpath resolver was able to get tests package", containsTestClass == true);

    }

    // Note that following test is tricky, as it won't fail if run via
    // mvn clean verify
    // that's because verify is after package and thus reactor contain already packaged jar instead of bunch of class files
    // SHRINKRES-94
    @Test
    public void packagedArtifactShouldNotContainBackslashes() throws Exception {

        // Ensure we can use ClassPath resolution to get the results of the "current" build
        final PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");
        File file = resolver.resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven")
                .withoutTransitivity().asSingleFile();

        JarFile jarFile = new JarFile(file);

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            Assert.assertFalse("There are not backslashes in created JAR", entryName.contains("\\"));
        }
    }

    // the idea of the test is that shrinkwrap-resolver-api is fetched
    // before SHRINKRES-130, user might get shrinkwrap-resolver-api-maven, dependening on classpath order
    // SHRINKRES-130
    @Test
    public void resolvesRightArtifactIdFromClasspath() throws Exception {

        // Get current version
        // Get version of current project
        String resolvedVersion = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven")
                .withoutTransitivity().asSingleResolvedArtifact().getResolvedVersion();

        // Get resolver-api from classpath
        File file = Maven.resolver().resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:" + resolvedVersion)
                .withoutTransitivity().asSingleFile();

        // check content of resolved jar, it should contain given class
        boolean containsTestClass = false;
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.equals("org/jboss/shrinkwrap/resolver/api/Coordinate.class")) {
                containsTestClass = true;
                break;
            }
        }

        Assert.assertTrue("Classpath resolver was able to get api package", containsTestClass == true);

    }

    @Test
    public void resolvesCorrectVersion() throws Exception {

        final String expectedVersion = "2.0.0-alpha-5";

        // Resolve an this project's current artifact (a different version) classpath (such that the ClassPathWorkspaceReader
        // picks it up)
        final MavenResolvedArtifact artifact = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:" + expectedVersion)
                .withoutTransitivity().asSingleResolvedArtifact();

        final String resolvedFilename = artifact.asFile().getName();

        Assert.assertTrue("Incorrect version was resolved", resolvedFilename.contains(expectedVersion));
    }

}
