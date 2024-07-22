/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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

import java.util.Set;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.DependenciesFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test cases for MavenImporter with war packaging
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
class WarTestCase {

    @Test
    void testWar() {
        WebArchive archive = ShrinkWrap.create(MavenImporter.class, "test.war").loadEffectivePom("pom.xml")
            .importBuildOutput().as(WebArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarClass.class"), "Archive does not contain war class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/main.properties"), "Archive does not contain main.properties");
        Assertions.assertTrue(archive.contains("WEB-INF/web.xml"), "Archive does not contain web.xml");
    }

    @Test
    @Disabled("https://issues.redhat.com/browse/SHRINKWRAP-378")
    void testWarManifest() {
        WebArchive archive = ShrinkWrap.create(MavenImporter.class, "test.war").loadEffectivePom("pom.xml")
            .importBuildOutput().as(WebArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("META-INF/MANIFEST.MF"), "Archive does not contain manifest");
    }

    @Test
    void testWarWithTestClasses() {
        WebArchive archive = ShrinkWrap.create(MavenImporter.class, "testWithTestClasses.war")
            .loadEffectivePom("pom.xml").importBuildOutput().importTestBuildOutput().as(WebArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarClass.class"), "Archive does not contain war class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/main.properties"), "Archive does not contain main.properties");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarTestCase.class"), "Archive does not contain war test class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test.properties"), "Archive does not contain test.properties");
    }

    @Test
    void testWebArchiveAsMavenImporter() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "testWebArchiveAsMavenImporter.war")
            .addClass(Object.class).as(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput()
            .importTestBuildOutput().as(WebArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/java/lang/Object.class"), "Archive does not contain manually added class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarClass.class"), "Archive does not contain war class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/main.properties"), "Archive does not contain main.properties");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarTestCase.class"), "Archive does not contain war test class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test.properties"), "Archive does not contain test.properties");
    }

    @Test
    void testWarWithTestArtifacts() {
        WebArchive archive = ShrinkWrap.create(MavenImporter.class, "testWithTestArtifacts.war")
            .loadEffectivePom("pom.xml").importBuildOutput().importTestBuildOutput()
            .importTestDependencies(new DependenciesFilter("junit:junit")).as(WebArchive.class);
        System.out.println(archive.toString(true));

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarClass.class"), "Archive does not contain war class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/main.properties"), "Archive does not contain main.properties");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarTestCase.class"), "Archive does not contain war test class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test.properties"), "Archive does not contain test.properties");

        boolean foundJunit = false;
        Set<ArchivePath> libs = archive.getContent(new Filter<ArchivePath>() {

            public boolean include(ArchivePath arg0) {
                return arg0.get().startsWith("/WEB-INF/lib");
            }
        }).keySet();
        for (final ArchivePath lib : libs) {
            if (lib.get().startsWith("/WEB-INF/lib/junit")) {
                foundJunit = true;
            }
        }

        Assertions.assertTrue(foundJunit, "Should have been able to import test dependency upon junit");
    }

    @Test
    void testWarWithFilteredTestArtifacts() {
        WebArchive archive = ShrinkWrap.create(MavenImporter.class, "testWithFilteredTestArtifacts.war")
            .loadEffectivePom("pom.xml").importBuildOutput().importTestBuildOutput()
            .importAnyDependencies(new DependenciesFilter("junit:junit")).as(WebArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarClass.class"), "Archive does not contain war class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/main.properties"), "Archive does not contain main.properties");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test/WarTestCase.class"), "Archive does not contain war test class");
        Assertions.assertTrue(archive.contains("WEB-INF/classes/test.properties"), "Archive does not contain test.properties");

        Set<ArchivePath> libs = archive.getContent(new Filter<ArchivePath>() {

            public boolean include(ArchivePath arg0) {
                String path = arg0.get();
                return path.startsWith("/WEB-INF/lib") && path.endsWith(".jar");
            }
        }).keySet();

        Assertions.assertEquals(1, libs.size(), "There should be one filtered lib");
    }

}
