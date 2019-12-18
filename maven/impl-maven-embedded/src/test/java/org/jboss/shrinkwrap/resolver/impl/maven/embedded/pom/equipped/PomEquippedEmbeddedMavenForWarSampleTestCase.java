package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirRule;
import org.junit.Rule;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyMavenVersion;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomEquippedEmbeddedMavenForWarSampleTestCase {

    @Rule
    public final TestWorkDirRule workDirRule = new TestWorkDirRule();

    @Test
    public void testWarSampleBuildWithMaven310() {

        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToWarSamplePom))
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
            .forProject(workDirRule.prepareProject(pathToWarSamplePom))
            .setGoals("clean", "package")
            .setProfiles("failing")
            .build();
    }

    @Test
    public void testIfWarSampleBuildFailsWithoutException() {

        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToWarSamplePom))
            .setGoals("clean", "package")
            .setProfiles("failing")
            .ignoreFailure()
            .build();

        assertEquals(1, builtProject.getMavenBuildExitCode());
    }

}
