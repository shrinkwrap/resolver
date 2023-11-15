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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.util.TestFileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JAR import test case with settings.xml configuration
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ConfiguredMavenImporterTestCase {

    private static final String LOCAL_REPOSITORY = "target/local-only-repository";
    private static final String SETTINGS_FILE = "src/test/resources/settings.xml";

    @BeforeClass
    public static void clearLocalRepositoryReference() {
        System.clearProperty("maven.repo.local"); // May conflict with release settings
    }

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    @After
    // For debugging you might want to temporarily remove the @After lifecycle call just to sanity-check for yourself
    // the repo
    public void cleanLocalRepository() throws Exception {
        TestFileUtil.removeDirectory(new File(LOCAL_REPOSITORY));
    }

    @Before
    public void cleanTarget() throws IOException {
        TestFileUtil.removeDirectory(new File("src/it/jar-sample/target"));
    }

    @Test
    public void importJar() {
        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class).configureFromFile(SETTINGS_FILE)
                .loadPomFromFile("src/it/jar-sample/pom.xml").importBuildOutput().as(WebArchive.class);

        // Then
        assertThat(archive.getContent(), contains("main.properties"));
        assertThat(archive.getContent(), not(contains("file.toExclude")));
        assertThat(archive.getContent(), size(4));

        File commonsCodec = new File(LOCAL_REPOSITORY + "/commons-codec/commons-codec/1.7/commons-codec-1.7.jar");
        assertThat(commonsCodec.exists(), is(true));
    }

    // SHRINKRES-176
    @Test
    public void importJarWithName() {

        // given
        final String name = "myownname.jar";

        // When
        final Archive<?> archive = ShrinkWrap.create(MavenImporter.class, name).configureFromFile(SETTINGS_FILE)
                .loadPomFromFile("src/it/jar-sample/pom.xml").importBuildOutput().as(WebArchive.class);

        // Then
        assertThat(archive.getName(), is(name));
        assertThat(archive.getContent(), contains("main.properties"));
        assertThat(archive.getContent(), not(contains("file.toExclude")));
        assertThat(archive.getContent(), size(4));

        File commonsCodec = new File(LOCAL_REPOSITORY + "/commons-codec/commons-codec/1.7/commons-codec-1.7.jar");
        assertThat(commonsCodec.exists(), is(true));
    }


    @Test
    public void importJarOffline() {
        // if running offline, this would not work
        Assert.assertThrows(NoResolvedResultException.class, () -> ShrinkWrap.create(MavenImporter.class).configureFromFile(SETTINGS_FILE).offline()
                .loadPomFromFile("src/it/jar-sample/pom.xml").importBuildOutput().as(WebArchive.class));
    }

}
