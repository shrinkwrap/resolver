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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.importer;

import static org.jboss.shrinkwrap.resolver.impl.maven.archive.importer.ArchiveContentMatchers.contains;
import static org.jboss.shrinkwrap.resolver.impl.maven.archive.importer.ArchiveContentMatchers.hasManifestEntry;
import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.not;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.util.TestFileUtil;
import org.junit.Before;
import org.junit.Test;

public class ManifestTestCase {

    @Before
    public void cleanTarget() throws IOException {
        TestFileUtil.removeDirectory(new File("src/it/war-sample/target"));
        TestFileUtil.removeDirectory(new File("src/it/jar-sample/target"));
        TestFileUtil.removeDirectory(new File("src/it/jar-with-mf-sample/target"));
    }

    @Test
    public void manifestCreatedInJar() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class).loadPomFromFile("src/it/jar-sample/pom.xml")
                .importBuildOutput().as(JavaArchive.class);

        // Then
        assertThat(archive.getContent(), contains("META-INF/MANIFEST.MF"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Created-By", "ShrinkWrap Maven Resolver"));
    }

    @Test
    public void manifestCreatedInWar() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class).loadPomFromFile("src/it/war-sample/pom.xml")
                .importBuildOutput().as(WebArchive.class);

        // Then
        assertThat(archive.getContent(), contains("META-INF/MANIFEST.MF"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Created-By", "ShrinkWrap Maven Resolver"));
    }

    @Test
    public void suppliedManifestHasPrecedence() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class).loadPomFromFile("src/it/jar-with-mf-sample/pom.xml")
                .importBuildOutput().as(JavaArchive.class);

        // Then
        assertThat(archive.getContent(), contains("META-INF/MANIFEST.MF"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Created-By", "User"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(),
                hasManifestEntry(Attributes.Name.MANIFEST_VERSION.toString(), "1.0"));
    }

    @Test
    public void manifestWithDefaultImplementationEntries() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("src/it/jar-with-mf-sample/pom-b.xml").importBuildOutput().as(JavaArchive.class);

        // Then
        assertThat(archive.getContent(), contains("META-INF/MANIFEST.MF"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Created-By", "ShrinkWrap Maven Resolver"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Implementation-Title"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), not(hasManifestEntry("Implementation-Vendor")));
    }

    @Test
    public void manifestWithDefaultSpecificationEntries() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("src/it/jar-with-mf-sample/pom-c.xml").importBuildOutput().as(JavaArchive.class);

        // Then
        assertThat(archive.getContent(), contains("META-INF/MANIFEST.MF"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Created-By", "ShrinkWrap Maven Resolver"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Specification-Title"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Specification-Vendor", "Arquillian"));
    }

    @Test
    public void manifestWithManifestSection() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("src/it/jar-with-mf-sample/pom-d.xml").importBuildOutput().as(JavaArchive.class);

        // Then
        assertThat(archive.getContent(), contains("META-INF/MANIFEST.MF"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Created-By", "ShrinkWrap Maven Resolver"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Specification-Title"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Specification-Vendor", "Arquillian"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("MyFirstSection", "Foo", "bar"));
    }

    @Test
    public void manifestWithManifestSections() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("src/it/jar-with-mf-sample/pom-e.xml").importBuildOutput().as(JavaArchive.class);

        // Then
        assertThat(archive.getContent(), contains("META-INF/MANIFEST.MF"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Created-By", "ShrinkWrap Maven Resolver"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Specification-Title"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Specification-Vendor", "Arquillian"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("MyFirstSection", "Foo", "bar"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("MySecondSection", "Foo2", "bar2"));
    }

    @Test
    public void manifestWithCustomManifestEntries() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class).loadPomFromFile("src/it/war-sample/pom.xml")
                .importBuildOutput().as(JavaArchive.class);

        // Then
        assertThat(archive.getContent(), contains("META-INF/MANIFEST.MF"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Created-By", "ShrinkWrap Maven Resolver"));
        assertThat(archive.get("META-INF/MANIFEST.MF").getAsset(), hasManifestEntry("Dependencies"));
    }
}
