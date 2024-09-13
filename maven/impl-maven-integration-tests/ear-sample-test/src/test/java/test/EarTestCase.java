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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.DependenciesFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EarTestCase {

    @Test
    void testEar() {
        EnterpriseArchive archive = ShrinkWrap.create(MavenImporter.class, "test.ear")
            .loadEffectivePom("../ear-sample/pom.xml").importBuildOutput().as(EnterpriseArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("test.xml"), "Archive does not contain test.xml");
        Assertions.assertTrue(archive.contains("META-INF/application.xml"), "Archive does not contain application.xml");

        Assertions.assertEquals(1, getLibraries(archive).size(), "Archive does not contain one library");
    }

    @Test
    void testEarWithTestClasses() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            ShrinkWrap.create(MavenImporter.class, "testWithTestClasses.ear").loadEffectivePom("../ear-sample/pom.xml")
                .importBuildOutput().importTestBuildOutput().as(EnterpriseArchive.class);
        });
    }

    @Test
    void testEnterpriseArchiveAsMavenImporter() {
        EnterpriseArchive archive = ShrinkWrap
            .create(EnterpriseArchive.class, "testEnterpriseArchiveAsMavenImporter.ear").as(MavenImporter.class)
            .loadEffectivePom("../ear-sample/pom.xml").importBuildOutput().as(EnterpriseArchive.class);

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("test.xml"), "Archive does not contain test.xml");
        Assertions.assertTrue(archive.contains("META-INF/application.xml"), "Archive does not contain application.xml");

        Assertions.assertEquals(1, getLibraries(archive).size(), "Archive does not contain one library");
    }

    @Test
    void testEarWithTestArtifacts() {
        EnterpriseArchive archive = ShrinkWrap.create(MavenImporter.class, "testWithTestArtifacts.ear")
            .loadEffectivePom("../ear-sample/pom.xml").importBuildOutput()
            .importTestDependencies(new DependenciesFilter("junit:junit")).as(EnterpriseArchive.class);
        System.out.println(archive.toString(true));

        Assertions.assertNotNull(archive, "Archive is null");
        Assertions.assertTrue(archive.contains("test.xml"), "Archive does not contain test.xml");
        Assertions.assertTrue(archive.contains("META-INF/application.xml"), "Archive does not contain application.xml");

        Assertions.assertTrue(getLibraries(archive).size() > 1, "Archive does not contain more than one library");
    }
    }

    private Set<ArchivePath> getLibraries(Archive<?> archive) {
        return archive.getContent(new Filter<ArchivePath>() {

            public boolean include(ArchivePath arg0) {
                String path = arg0.get();
                return path.endsWith(".jar");
            }
        }).keySet();
    }

}
