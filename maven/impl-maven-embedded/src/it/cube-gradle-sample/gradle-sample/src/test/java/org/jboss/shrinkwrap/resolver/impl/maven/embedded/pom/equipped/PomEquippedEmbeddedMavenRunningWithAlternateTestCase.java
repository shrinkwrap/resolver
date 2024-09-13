package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;

class PomEquippedEmbeddedMavenRunningWithAlternateTestCase {

    @Test
    void testWarSampleBuildUsingAlternatePomFile() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setAlternatePomFile(pathToWarSamplePom)
            .setGoals("clean", "package", "source:jar")
            .useDefaultDistribution()
            .build();

        File warSamplePom = new File(pathToWarSamplePom);
        File jarSamplePom = new File(pathToJarSamplePom);

        Assertions.assertEquals(warSamplePom.getAbsoluteFile(), builtProject.getModel().getPomFile());
        Assertions.assertNotEquals(jarSamplePom.getAbsoluteFile(), builtProject.getModel().getPomFile());
        verifyWarSampleWithSources(builtProject);
    }
}
