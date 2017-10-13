package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;
import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;

public class PomEquippedEmbeddedMavenRunningWithAlternateTestCase {

    @Test
    public void testWarSampleBuildUsingAlternatePomFile() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setAlternatePomFile(pathToWarSamplePom)
            .setGoals("clean", "package", "source:jar")
            .useDefaultDistribution()
            .build();

        File warSamplePom = new File(pathToWarSamplePom);
        File jarSamplePom = new File(pathToJarSamplePom);

        Assertions.assertThat(builtProject.getModel().getPomFile()).isEqualTo(warSamplePom.getAbsoluteFile());
        Assertions.assertThat(builtProject.getModel().getPomFile()).isNotEqualTo(jarSamplePom.getAbsoluteFile());
        verifyWarSampleWithSources(builtProject);
    }
}
