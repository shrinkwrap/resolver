package org.jboss.shrinkwrap.impl.gradle.archive.importer.embedded;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
class MultiModuleEmbeddedGradleImporterTestCase {

    @Test
    void should() {
        final String dir = "src/it/multi-module-sample/module-two";
        final WebArchive webArchive = ShrinkWrap.create(EmbeddedGradleImporter.class).forProjectDirectory(dir)
            .importBuildOutput().as(WebArchive.class);

        AssertArchive.assertContains(webArchive, "/WEB-INF/lib/module-one.jar");
    }
}
