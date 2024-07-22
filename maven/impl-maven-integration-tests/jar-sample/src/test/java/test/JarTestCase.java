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
package test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for MavenImporter with Jar
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
class JarTestCase {

    @Test
    void testJar() {
        JavaArchive archive = ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput()
            .as(JavaArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("test/JarClass.class"), "Archive does not contain jar class");
        Assertions.assertTrue(archive.contains("main.properties"), "Archive does not contain main.properties");
    }

    @Test
    void testJarWithTestClasses() {
        JavaArchive archive = ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput()
            .importTestBuildOutput().as(JavaArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("test/JarClass.class"), "Archive does not contain jar class");
        Assertions.assertTrue(archive.contains("main.properties"), "Archive does not contain main.properties");
        Assertions.assertTrue(archive.contains("test/JarTestCase.class"), "Archive does not contain jar test class");
        Assertions.assertTrue(archive.contains("test.properties"), "Archive does not contain test.properties");
    }

    @Test
    void testJavaArchiveAsMavenImporter() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class).addClass(Object.class).as(MavenImporter.class)
            .loadEffectivePom("pom.xml").importBuildOutput().importTestBuildOutput().as(JavaArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("java/lang/Object.class"), "Archive does not contain manually added class");
        Assertions.assertTrue(archive.contains("test/JarClass.class"), "Archive does not contain jar class");
        Assertions.assertTrue(archive.contains("main.properties"), "Archive does not contain main.properties");
        Assertions.assertTrue(archive.contains("test/JarTestCase.class"), "Archive does not contain jar test class");
        Assertions.assertTrue(archive.contains("test.properties"), "Archive does not contain test.properties");
    }

    @Test
    void testJarWithTestArtifacts() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput().importTestBuildOutput()
                    .importTestDependencies().as(JavaArchive.class);
        }, "UnsupportedOperationException should have been thrown for jar packaging");
    }
}
