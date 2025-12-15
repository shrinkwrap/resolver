/*
 * JBoss, Home of Professional Open Source
 * Copyright 2026, Red Hat Inc., and individual contributors
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

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression test for SHRINKRES-355.
 * Verifies that the importer picks the correct artifact (.war) even when a .jar is present.
 */
public class MultipleArtifactsEmbeddedGradleImporterTestCase {

    @Test
    void shouldImportWarWhenJarIsAlsoPresent() {
        final String dir = "src/it/multiple-artifacts-sample/";

        final WebArchive webArchive = ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forProjectDirectory(dir)
                .importBuildOutput()
                .as(WebArchive.class);

        assertThat(webArchive.contains("WEB-INF/classes/testArq/Service.class"))
                .as("Main sources should be inside WEB-INF/classes")
                .isTrue();
    }
}
