/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.importer;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.EmbeddedMavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.PomEquippedEmbeddedMavenImporter;
import org.junit.Before;
import org.junit.Test;

public class EmbeddedMavenImporterTestCase {

    @Before
    public void before() throws IOException {
        FileUtils.deleteDirectory(new File("src/it/jar-sample/target"));
        FileUtils.deleteDirectory(new File("src/it/war-sample/target"));
    }

    @Test
    public void importJarTypeProject() {
//        When
        final EmbeddedMavenImporter importer = ShrinkWrap.create(EmbeddedMavenImporter.class, "jar-with-excludes.jar");
        final PomEquippedEmbeddedMavenImporter pomEquippedMavenImporter = importer.loadPomFromFile("src/it/jar-sample/pom.xml");
        final JavaArchive archive = pomEquippedMavenImporter.importBuildOutput().as(JavaArchive.class);

//        Then
        AssertArchive.assertContains(archive, "main.properties");
        AssertArchive.assertContains(archive, "META-INF/MANIFEST.MF");
        AssertArchive.assertContains(archive, "test/nested/NestedWarClass.class");
        AssertArchive.assertContains(archive, "test/JarClass.class");
        AssertArchive.assertContains(archive, "META-INF/maven/org.jboss.shrinkwrap.resolver.test/shrinkwrap-resolver-impl-maven-test-jar-sample/pom.xml");
        AssertArchive.assertContains(archive, "META-INF/maven/org.jboss.shrinkwrap.resolver.test/shrinkwrap-resolver-impl-maven-test-jar-sample/pom.properties");
        AssertArchive.assertNotContains(archive, "file.toExclude");
        Assert.assertEquals(12, archive.getContent().size());
    }

    @Test
    public void importWarTypeProjectWithWarSourceExcludes() {
//        When
        final EmbeddedMavenImporter importer = ShrinkWrap.create(EmbeddedMavenImporter.class, "war-with-war-source-excludes.war");
        final PomEquippedEmbeddedMavenImporter pomEquippedMavenImporter = importer.loadPomFromFile("src/it/war-sample/pom.xml");
        final WebArchive archive = pomEquippedMavenImporter.importBuildOutput().as(WebArchive.class);

//        Then
        AssertArchive.assertContains(archive, "WEB-INF/lib/commons-codec-1.7.jar");
        AssertArchive.assertContains(archive, "WEB-INF/web.xml");
        AssertArchive.assertContains(archive, "WEB-INF/classes/test/nested/NestedWarClass.class");
        AssertArchive.assertContains(archive, "WEB-INF/classes/test/WarClass.class");
        AssertArchive.assertContains(archive, "WEB-INF/classes/main.properties");
        AssertArchive.assertContains(archive, "META-INF/maven/org.jboss.shrinkwrap.resolver.test/shrinkwrap-resolver-impl-maven-test-war-sample/pom.xml");
        AssertArchive.assertContains(archive, "META-INF/maven/org.jboss.shrinkwrap.resolver.test/shrinkwrap-resolver-impl-maven-test-war-sample/pom.properties");
        AssertArchive.assertNotContains(archive, "file.toExclude");
        AssertArchive.assertNotContains(archive, "file.packagingToExclude");
        AssertArchive.assertNotContains(archive, "file.warSourceToExclude");
        Assert.assertEquals(17, archive.getContent().size());
    }

    @Test
    public void importWarTypeProjectWithExcludes() {
//        When
        final EmbeddedMavenImporter importer = ShrinkWrap.create(EmbeddedMavenImporter.class, "war-with-excludes.war");
        final PomEquippedEmbeddedMavenImporter pomEquippedMavenImporter = importer.loadPomFromFile("src/it/war-sample/pom-c.xml");
        final WebArchive archive = pomEquippedMavenImporter.importBuildOutput().as(WebArchive.class);

//        Then
        AssertArchive.assertContains(archive, "WEB-INF/lib/commons-codec-1.7.jar");
        AssertArchive.assertContains(archive, "WEB-INF/web.xml");
        AssertArchive.assertContains(archive, "WEB-INF/classes/test/nested/NestedWarClass.class");
        AssertArchive.assertContains(archive, "WEB-INF/classes/test/WarClass.class");
        AssertArchive.assertContains(archive, "WEB-INF/classes/main.properties");
        AssertArchive.assertContains(archive, "META-INF/maven/org.jboss.shrinkwrap.resolver.test/shrinkwrap-resolver-impl-maven-test-war-sample/pom.xml");
        AssertArchive.assertContains(archive, "META-INF/maven/org.jboss.shrinkwrap.resolver.test/shrinkwrap-resolver-impl-maven-test-war-sample/pom.properties");
        AssertArchive.assertNotContains(archive, "file.toExclude");
        AssertArchive.assertNotContains(archive, "file.packagingToExclude");
        AssertArchive.assertNotContains(archive, "file.warSourceToExclude");
        Assert.assertEquals(17, archive.getContent().size());
    }

    @Test
    public void importWarTypeProjectWithIncludes() {
//        When
        final EmbeddedMavenImporter importer = ShrinkWrap.create(EmbeddedMavenImporter.class, "war-with-includes.war");
        final PomEquippedEmbeddedMavenImporter pomEquippedMavenImporter = importer.loadPomFromFile("src/it/war-sample/pom-b.xml");
        final WebArchive archive = pomEquippedMavenImporter.importBuildOutput().as(WebArchive.class);

//        Then
        AssertArchive.assertNotContains(archive, "WEB-INF/lib/commons-codec-1.7.jar");
        AssertArchive.assertContains(archive, "WEB-INF/web.xml");
        AssertArchive.assertNotContains(archive, "WEB-INF/classes/test/nested/NestedWarClass.class");
        AssertArchive.assertNotContains(archive, "WEB-INF/classes/test/WarClass.class");
        AssertArchive.assertNotContains(archive, "WEB-INF/classes/main.properties");
        AssertArchive.assertContains(archive, "META-INF/maven/org.jboss.shrinkwrap.resolver.test/shrinkwrap-resolver-impl-maven-test-war-sample/pom.xml");
        AssertArchive.assertContains(archive, "META-INF/maven/org.jboss.shrinkwrap.resolver.test/shrinkwrap-resolver-impl-maven-test-war-sample/pom.properties");
        AssertArchive.assertContains(archive, "file.packagingToExclude");
        AssertArchive.assertNotContains(archive, "file.toExclude");
        AssertArchive.assertNotContains(archive, "file.warSourceToExclude");
        Assert.assertEquals(10, archive.getContent().size());
    }
}
