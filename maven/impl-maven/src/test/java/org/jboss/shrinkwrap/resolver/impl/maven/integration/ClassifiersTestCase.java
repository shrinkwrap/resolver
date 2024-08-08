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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ClassifiersTestCase {

    /**
     * Tests that the resolver resolves the artifact with a classifier, not the original one.
     * For more information, see: <a href="https://issues.redhat.com/browse/SHRINKRES-102">SHRINKRES-102</a>
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void testClassifier() throws Exception {

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .loadPomFromFile("target/poms/test-tests-classifier.xml")
                .resolve("org.jboss.shrinkwrap.test:test-dependency-with-test-jar:jar:tests:1.0.0").withoutTransitivity()
                .asSingleFile();

        new ValidationUtil("test-dependency-with-test-jar").validate(file);

        // check content of resolved jar, it should contain Field class
        boolean containsFieldClass = false;
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.equals("java/lang/reflect/Field.class")) {
                containsFieldClass = true;
                break;
            }
        }
        jarFile.close();

        Assertions.assertTrue(containsFieldClass, "Tests-jar artifact was resolved");
    }

    /**
     * Tests that user can use test-jar as packaging type, e.g. type
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void testClassifierAndTestJarType() throws Exception {

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .loadPomFromFile("target/poms/test-tests-classifier.xml")
                .resolve("org.jboss.shrinkwrap.test:test-dependency-with-test-jar:test-jar:tests:1.0.0")
                .withoutTransitivity()
                .asSingleFile();

        new ValidationUtil("test-dependency-with-test-jar").validate(file);

        // check content of resolved jar, it should contain Field class
        boolean containsFieldClass = false;
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.equals("java/lang/reflect/Field.class")) {
                containsFieldClass = true;
                break;
            }
        }
        jarFile.close();

        Assertions.assertTrue(containsFieldClass, "Tests-jar artifact was resolved");
    }

    // SHRINKRES-162
    @Test
    void testClassifierAndTestJarTypeMetadata() {

        MavenResolvedArtifact artifact = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .loadPomFromFile("target/poms/test-tests-classifier.xml")
                .resolve("org.jboss.shrinkwrap.test:test-dependency-with-test-jar:test-jar:tests:1.0.0")
                .withoutTransitivity()
                .asSingleResolvedArtifact();

        new ValidationUtil("test-dependency-with-test-jar").validate(artifact.asFile());

        Assertions.assertEquals("jar", artifact.getExtension());
        Assertions.assertEquals("tests", artifact.getCoordinate().getClassifier());
        Assertions.assertEquals(PackagingType.TEST_JAR, artifact.getCoordinate().getPackaging());
    }

    /**
     * Tests that user can use test-jar as packaging type, e.g. type
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void testClassifierAndTestJarTypeVersionFromPom() throws Exception {

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .loadPomFromFile("target/poms/test-tests-classifier.xml")
                .resolve("org.jboss.shrinkwrap.test:test-dependency-with-test-jar:test-jar:tests:?")
                .withoutTransitivity()
                .asSingleFile();

        new ValidationUtil("test-dependency-with-test-jar").validate(file);

        // check content of resolved jar, it should contain Field class
        boolean containsFieldClass = false;
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.equals("java/lang/reflect/Field.class")) {
                containsFieldClass = true;
                break;
            }
        }
        jarFile.close();

        Assertions.assertTrue(containsFieldClass, "Tests-jar artifact was resolved");
    }
}
