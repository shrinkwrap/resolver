package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;

class PomEquippedEmbeddedMavenRunningWithAlternateTestCase {

    @RegisterExtension
    final TestWorkDirExtension workDirExtension = new TestWorkDirExtension();

    @Test
    void testWarSampleBuildUsingAlternatePomFile() {
        final File jarSamplePomFile = workDirExtension.prepareProject(pathToJarSamplePom);
        final File warSamplePomFile = workDirExtension.prepareProject(pathToWarSamplePom);

        BuiltProject builtProject = EmbeddedMaven
            .forProject(jarSamplePomFile.getPath())
            .setAlternatePomFile(warSamplePomFile.getPath())
            .setGoals("clean", "package", "source:jar")
            .useDefaultDistribution()
            .build();

        Assertions.assertEquals(builtProject.getModel().getPomFile(), warSamplePomFile.getAbsoluteFile());
        Assertions.assertNotEquals(builtProject.getModel().getPomFile(), jarSamplePomFile.getAbsoluteFile());
        verifyWarSampleWithSources(builtProject);
    }
}
