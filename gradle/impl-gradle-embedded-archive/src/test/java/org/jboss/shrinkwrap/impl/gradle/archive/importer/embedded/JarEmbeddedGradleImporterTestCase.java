/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.shrinkwrap.impl.gradle.archive.importer.embedded;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

/**
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class JarEmbeddedGradleImporterTestCase {

    @Test
    public void shouldImportFromDefaultLocation() {
        final String dir = "src/it/jar-sample/";
        final JavaArchive javaArchive = ShrinkWrap.create(EmbeddedGradleImporter.class).forProjectDirectory(dir)
            .importBuildOutput().as(JavaArchive.class);

        AssertArchive.assertContains(javaArchive, "main.properties");
        AssertArchive.assertNotContains(javaArchive, "file.toExclude");
        assertThat(javaArchive.getContent().size()).isEqualTo(7);
    }

    @Test
    public void shouldImportFromSpecificPath() {
        final String dir = "src/it/jar-sample/";
        final JavaArchive javaArchive = ShrinkWrap
            .create(EmbeddedGradleImporter.class)
            .forProjectDirectory(dir)
            .importBuildOutput(
                "src" + File.separator + "it" + File.separator + "jar-sample" + File.separator + "build"
                    + File.separator + "libs" + File.separator + "jar-sample-1.0.0.jar").as(JavaArchive.class);

        AssertArchive.assertContains(javaArchive, "main.properties");
        AssertArchive.assertNotContains(javaArchive, "file.toExclude");
        assertThat(javaArchive.getContent().size()).isEqualTo(7);
    }
}
