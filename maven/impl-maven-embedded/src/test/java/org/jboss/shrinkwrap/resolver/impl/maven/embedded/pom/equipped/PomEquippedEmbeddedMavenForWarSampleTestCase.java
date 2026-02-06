package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyMavenVersion;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
class PomEquippedEmbeddedMavenForWarSampleTestCase {

    @RegisterExtension
    final TestWorkDirExtension workDirExtension = new TestWorkDirExtension();

    @Test
    void testWarSampleBuildWithMaven310() {

        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirExtension.prepareProject(pathToWarSamplePom))
            .setUserSettingsFile(new File("src/it/settings.xml"))
            .useMaven3Version("3.9.12")
            .setGoals("clean", "package", "source:jar")
            .setShowVersion(true)
            .build();

        verifyWarSampleWithSources(builtProject);
        verifyMavenVersion(builtProject, "3.9.12");
    }

    @Test
    void testIfWarSampleBuildFailsWithException() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            EmbeddedMaven
                    .forProject(workDirExtension.prepareProject(pathToWarSamplePom))
                    .setGoals("clean", "package")
                    .setProfiles("failing")
                    .build();
        });
    }

    @Test
    void testIfWarSampleBuildFailsWithoutException() {

        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirExtension.prepareProject(pathToWarSamplePom))
            .setGoals("clean", "package")
            .setProfiles("failing")
            .ignoreFailure()
            .build();

        Assertions.assertEquals(1, builtProject.getMavenBuildExitCode());
    }

}
