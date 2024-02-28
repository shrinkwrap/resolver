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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jboss.shrinkwrap.resolver.impl.maven.archive.importer.ArchiveContentMatchers.contains;
import static org.jboss.shrinkwrap.resolver.impl.maven.archive.importer.ArchiveContentMatchers.size;

import java.io.File;
import java.io.IOException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.util.TestFileUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * WAR import test case
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class WarMavenImporterTestCase {

    @Before
    public void cleanTarget() throws IOException {
        TestFileUtil.removeDirectory(new File("src/it/war-sample/target"));
    }

    @Test
    public void importWar() {
        // When
        final WebArchive archive = doImport("src/it/war-sample/pom.xml");

        archive.as(ZipExporter.class).exportTo(new File("target/foo.war"), true);

        // Then
        assertThat(archive.getContent(), contains("WEB-INF/web.xml"));
        assertThat(archive.getContent(), contains("WEB-INF/classes/test/nested/NestedWarClass.class"));
        assertThat(archive.getContent(), contains("WEB-INF/classes/test/WarClass.class"));
        assertThat(archive.getContent(), contains("WEB-INF/classes/main.properties"));
        assertThat(archive.getContent(), contains("WEB-INF/classes/nesteddir/nested.properties"));
        assertThat(archive.getContent(), not(contains("file.toExclude")));
        assertThat(archive.getContent(), not(contains("file.packagingToExclude")));
        assertThat(archive.getContent(), not(contains("file.warSourceToExclude")));
        assertThat(archive.getContent(), size(7));
    }

    // SHRINKRES-176
    @Test
    public void importWarWithName() {

        // Given
        final String name = "myownname.war";

        // When
        final WebArchive archive = ShrinkWrap.create(MavenImporter.class, name)
            .loadPomFromFile("src/it/war-sample/pom.xml")
            .importBuildOutput()
            .as(WebArchive.class);



        // Then
        assertThat(archive.getName(), is(name));
        assertThat(archive.getContent(), contains("WEB-INF/web.xml"));
        assertThat(archive.getContent(), contains("WEB-INF/classes/test/nested/NestedWarClass.class"));
        assertThat(archive.getContent(), contains("WEB-INF/classes/test/WarClass.class"));
        assertThat(archive.getContent(), contains("WEB-INF/classes/main.properties"));
        assertThat(archive.getContent(), contains("WEB-INF/classes/nesteddir/nested.properties"));
        assertThat(archive.getContent(), not(contains("file.toExclude")));
        assertThat(archive.getContent(), not(contains("file.packagingToExclude")));
        assertThat(archive.getContent(), not(contains("file.warSourceToExclude")));
        assertThat(archive.getContent(), size(7));
    }

    @Test
    public void importWarWithIncludes() {
        // When
        final WebArchive archive = doImport("src/it/war-sample/pom-b.xml");

        // Then
        assertThat(archive.getContent(), contains("WEB-INF/web.xml"));
        assertThat(archive.getContent(), contains("file.toExclude"));
        assertThat(archive.getContent(), contains("file.packagingToExclude"));
        assertThat(archive.getContent(), contains("file.warSourceToExclude"));
        assertThat(archive.getContent(), size(5));
    }

    private WebArchive doImport(String pomFile) {

        // When
        WebArchive archive = ShrinkWrap.create(MavenImporter.class).loadPomFromFile(pomFile).importBuildOutput()
            .as(WebArchive.class);

        // Then
        assertThat(archive.getContent(), not(contains(".svn")));
        assertThat(archive.getContent(), not(contains("WEB-INF/.svn")));

        assertThat(archive.getContent(), contains("WEB-INF/lib/commons-codec-1.15.jar"));

        return archive;
    }
}
