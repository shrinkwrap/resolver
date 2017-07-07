package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.EmbeddedMavenBuildException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyMavenVersion;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomEquippedEmbeddedMavenForWarSampleTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


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

    @Test
    public void testIfWarSampleBuildFailsWithException() {

        expectedException.expect(new TypeSafeMatcher<EmbeddedMavenBuildException>() {
            @Override
            protected boolean matchesSafely(EmbeddedMavenBuildException buildError) {
                final BuiltProject builtProject = buildError.getBuiltProject();
                return !builtProject.getMavenLog().isEmpty() && builtProject.getMavenBuildExitCode() != 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("expects project output to be attached, and exit code != 0.");
            }
        });

        EmbeddedMaven
            .forProject(pathToWarSamplePom)
            .setGoals("clean", "package")
            .setProfiles("failing")
            .build();
    }

    @Test
    public void testIfWarSampleBuildFailsWithoutException() {

        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToWarSamplePom)
            .setGoals("clean", "package")
            .setProfiles("failing")
            .ignoreFailure()
            .build();

        assertEquals(1, builtProject.getMavenBuildExitCode());
    }

}
