package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyMavenVersion;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomEquippedEmbeddedMavenForWarSampleTestCase {

    @Test
    public void testWarSampleBuildWithMaven310() {

        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToWarSamplePom)
            .useMaven3Version("3.1.0")
            .setGoals("clean", "package", "source:jar")
            .setShowVersion(true)
            .build();

        verifyWarSampleWithSources(builtProject);
        verifyMavenVersion(builtProject, "3.1.0");
    }

    @Test(expected = IllegalStateException.class)
    public void testIfWarSampleBuildFailsWithException() {

        EmbeddedMaven
            .forProject(pathToWarSamplePom)
            .setGoals("clean", "package")
            .setProfiles("failing")
            .useMaven3Version("3.3.9")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testIfWarSampleBuildFailsWithExceptionBecauseOfMissingMaven() {

        EmbeddedMaven
            .forProject(pathToWarSamplePom)
            .setGoals("clean", "package")
            .build();
    }

    @Test
    public void testIfWarSampleBuildFailsWithoutException() {

        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToWarSamplePom)
            .setGoals("clean", "package")
            .setProfiles("failing")
            .useMaven3Version("3.3.9")
            .ignoreFailure()
            .build();

        assertEquals(1, builtProject.getMavenBuildExitCode());
    }

}
