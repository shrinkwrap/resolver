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

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

/**
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class WarEmbeddedGradleImporterTestCase {

    @Test
    public void should() {
        final String dir = "src/it/war-sample/";
        final WebArchive webArchive = ShrinkWrap.create(EmbeddedGradleImporter.class).forProjectDirectory(dir)
            .importBuildOutput().as(WebArchive.class);

        AssertArchive.assertContains(webArchive, "WEB-INF/lib/commons-codec-1.7.jar");
        AssertArchive.assertContains(webArchive, "WEB-INF/classes/test/nested/NestedWarClass.class");
        AssertArchive.assertContains(webArchive, "WEB-INF/classes/test/WarClass.class");
        AssertArchive.assertContains(webArchive, "WEB-INF/classes/main.properties");
        AssertArchive.assertContains(webArchive, "WEB-INF/web.xml");
        AssertArchive.assertNotContains(webArchive, "file.toExclude");
        assertThat(webArchive.getContent().size()).isEqualTo(12);
    }
}
