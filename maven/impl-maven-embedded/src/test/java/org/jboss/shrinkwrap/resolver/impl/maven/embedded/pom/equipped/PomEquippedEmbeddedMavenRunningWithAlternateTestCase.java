package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirRule;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;

public class PomEquippedEmbeddedMavenRunningWithAlternateTestCase {

    @Rule
    public final TestWorkDirRule workDirRule = new TestWorkDirRule();

    @Test
    public void testWarSampleBuildUsingAlternatePomFile() {
        final File jarSamplePomFile = workDirRule.prepareProject(pathToJarSamplePom);
        final File warSamplePomFile = workDirRule.prepareProject(pathToWarSamplePom);

        BuiltProject builtProject = EmbeddedMaven
            .forProject(jarSamplePomFile.getPath())
            .setAlternatePomFile(warSamplePomFile.getPath())
            .setGoals("clean", "package", "source:jar")
            .useDefaultDistribution()
            .build();

        assertThat(builtProject.getModel().getPomFile()).isEqualTo(warSamplePomFile.getAbsoluteFile());
        assertThat(builtProject.getModel().getPomFile()).isNotEqualTo(jarSamplePomFile.getAbsoluteFile());
        verifyWarSampleWithSources(builtProject);
    }
}
