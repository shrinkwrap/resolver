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
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ClassifiersTestCase {

    /**
     * Tests that resolver resolves artifact with classifier, not the original one
     * https://issues.jboss.org/browse/SHRINKRES-102
     *
     * @throws Exception
     */
    @Test
    public void testClassifier() throws Exception {

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

        Assert.assertTrue("Tests-jar artifact was resolved", containsFieldClass == true);
    }

    /**
     * Tests that user can use test-jar as packaging type, e.g. type
     *
     * @throws Exception
     */
    @Test
    public void testClassifierAndTestJarType() throws Exception {

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

        Assert.assertTrue("Tests-jar artifact was resolved", containsFieldClass == true);
    }

    // SHRINKRES-162
    @Test
    public void testClassifierAndTestJarTypeMetadat() throws Exception {

        MavenResolvedArtifact artifact = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .loadPomFromFile("target/poms/test-tests-classifier.xml")
                .resolve("org.jboss.shrinkwrap.test:test-dependency-with-test-jar:test-jar:tests:1.0.0")
                .withoutTransitivity()
                .asSingleResolvedArtifact();

        new ValidationUtil("test-dependency-with-test-jar").validate(artifact.asFile());

        Assert.assertEquals("jar", artifact.getExtension());
        Assert.assertEquals("tests", artifact.getCoordinate().getClassifier());
        Assert.assertEquals(PackagingType.TEST_JAR, artifact.getCoordinate().getPackaging());
    }

    /**
     * Tests that user can use test-jar as packaging type, e.g. type
     *
     * @throws Exception
     */
    @Test
    public void testClassifierAndTestJarTypeVersionFromPom() throws Exception {

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

        Assert.assertTrue("Tests-jar artifact was resolved", containsFieldClass == true);
    }
}
