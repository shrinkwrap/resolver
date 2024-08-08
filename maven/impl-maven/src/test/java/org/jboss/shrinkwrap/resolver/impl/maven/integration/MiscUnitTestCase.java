/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionContainer;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests misc functionality
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MiscUnitTestCase {

    @BeforeAll
    static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterAll
    static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     */
    @Test
    void testFilesResolution() {
        File[] files = Resolvers.use(MavenResolverSystem.class)
                .resolve("org.jboss.shrinkwrap.test:test-deps-a:1.0.0", "org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withTransitivity().as(File.class);

        new ValidationUtil("test-deps-a-1.0.0.jar", "test-deps-c-1.0.0.jar", "test-deps-b-1.0.0.jar").validate(files);
    }

    @Test
    void testMavenSystemResolverAsSessionContainer() {
        MavenResolverSystem resolver = Maven.resolver();
        resolver.resolve("org.jboss.shrinkwrap.test:test-deps-a:1.0.0");

        List<MavenDependency> dependencies = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession()
                .getDependenciesForResolution();
        Assertions.assertEquals(1, dependencies.size(), "There is one dependency to be resolved");
        Assertions.assertEquals("test-deps-a", dependencies.iterator().next()
                .getArtifactId(), "Dependency artifactId to be resolved matches");
    }

    @Test
    void testParsedPomFile() {
        PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("target/poms/test-resources.xml");

        ParsedPomFile pom = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession().getParsedPomFile();

        Assertions.assertTrue(pom.getResources() != null
                && !pom.getResources().isEmpty(), "Project resources are not null nor empty");

        Assertions.assertTrue(pom.getTestResources() != null
                && !pom.getTestResources().isEmpty(), "Project test resources are not null nor empty");
    }

    @Test
    void testTestOutputDirectory() {
        PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("target/poms/test-resources.xml");

        ParsedPomFile pom = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession().getParsedPomFile();

        File testOutputDir = pom.getTestOutputDirectory();

        Assertions.assertNotNull(testOutputDir, "Test output directory is defined");

        Assertions.assertTrue(testOutputDir.getAbsolutePath().endsWith("myoutputdir"), "Test output directory was defined to myoutputdir");
    }

    // SHRINKRES-217
    @Test
    void fileUtilCreatesUniqueFile() {

        // define a specific classloader with file that contains settings.xml file
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            File file  = new File("target/additional-test.jar");
            cl = new URLClassLoader( new URL[]{file.toURI().toURL()});
        }
        catch(Exception e) {
            System.err.println("Unable to redefine classloader to let test run from IDE, could not find target/additional-test.jar");
            // pass through, if we are running from Maven, this will still work
        }

        File one = FileUtil.INSTANCE.fileFromClassLoaderResource("profiles/settings3-from-classpath.xml", cl);
        File two = FileUtil.INSTANCE.fileFromClassLoaderResource("profiles/settings3-from-classpath.xml", cl);

        Assertions.assertNotEquals(one, two, "Two different settings.xml files were created");
        Assertions.assertNotEquals(one.getPath(), two.getPath(), "Two different settings.xml files were created in the same directory");
        Assertions.assertNotEquals(one.getName(), two.getName(), "Two different settings.xml files were created");
    }
}
