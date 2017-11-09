package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleContainsOnlyOneJar;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleSimpleBuild;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJasSampleContainsAlsoTestClasses;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomEquippedEmbeddedMavenForJarSampleTestCase {

    @Test
    public void testJarSampleBuild() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("clean", "verify")
            .useLocalInstallation()
            .build();

        verifyJarSampleSimpleBuild(builtProject);
        verifyJarSampleContainsOnlyOneJar(builtProject);
    }

    @Test
    public void testJarSampleBuildWithTestClasses() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("clean", "package")
            .setProfiles("test-classes")
            .build();

        verifyJarSampleSimpleBuild(builtProject);
        verifyJasSampleContainsAlsoTestClasses(builtProject);
    }

}
