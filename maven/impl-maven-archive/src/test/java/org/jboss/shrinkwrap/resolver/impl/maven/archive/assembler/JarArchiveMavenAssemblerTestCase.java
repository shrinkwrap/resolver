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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.assembler;

import java.io.File;
import java.io.IOException;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.assembler.ArchiveMavenAssembler;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.util.TestFileUtil;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.jboss.shrinkwrap.resolver.impl.maven.archive.assembler.ArchiveContentMatchers.contains;
import static org.jboss.shrinkwrap.resolver.impl.maven.archive.assembler.ArchiveContentMatchers.size;
import static org.junit.Assert.assertThat;

/**
 * JAR import test case
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 *
 */
public class JarArchiveMavenAssemblerTestCase {


    @Test
    public void importJarWithCleanup() throws IOException {
        TestFileUtil.removeDirectory(new File("src/it/jar-sample/target"));
        TestFileUtil.removeDirectory(new File("src/it/jar-without-resources/target"));

        // When
        final Archive<?> archive = doImport("src/it/jar-sample/pom.xml");

        // Then
        System.out.println(archive.toString(true));
        assertThat(archive.getContent(), contains("/test/JarClass.class"));
        assertThat(archive.getContent(), contains("main.properties"));
        assertThat(archive.getContent(), not(contains("file.toExclude")));
        assertThat(archive.getContent(), size(4));
    }

    @Test
    public void importJarWithTestClasses() {

        // When
        JavaArchive archive = ShrinkWrap.create(ArchiveMavenAssembler.class).usingPom("src/it/jar-sample/pom.xml").withBuildOutput()
            .withTestBuildOutput().as(JavaArchive.class);

        // Then
        System.out.println(archive.toString(true));
        assertThat(archive.getContent(), contains("/test/JarClass.class"));
        assertThat(archive.getContent(), contains("test/JarTestCase.class"));
        assertThat(archive.getContent(), contains("main.properties"));
        assertThat(archive.getContent(), not(contains("file.toExclude")));
        assertThat(archive.getContent(), size(6));
    }

    @Test
    public void importJarWithIncludes() {
        // When
        final Archive<?> archive = doImport("src/it/jar-sample/pom-b.xml");

        // Then
        assertThat(archive.getContent(), not(contains("main.properties")));
        assertThat(archive.getContent(), contains("file.toExclude"));
        assertThat(archive.getContent(), size(1));
    }

    @Test
    public void importJarWithResourceIncludes() {
        // When
        final Archive<?> archive = doImport("src/it/jar-sample/pom-c.xml");

        System.out.println(archive.toString(true));
        // Then
        assertThat(archive.getContent(), contains("main.properties"));
        assertThat(archive.getContent(), contains("test/JarClass.class"));
        assertThat(archive.getContent(), size(4));
    }


    //SHRINKRES-141
    @Test
    public void importJarWithoutResources() {

        // When
        final Archive<?> archive = doImport("src/it/jar-without-resources/pom.xml");

        assertThat(archive.getContent(), size(3));
    }

    private Archive<?> doImport(String pomFile) {
        // When
        WebArchive archive = ShrinkWrap.create(ArchiveMavenAssembler.class).usingPom(pomFile).withBuildOutput()
                .as(WebArchive.class);

        // Then
        assertThat(archive.getContent(), not(contains(".svn")));
        assertThat(archive.getContent(), not(contains("WEB-INF/.svn")));

        return archive;
    }

}
