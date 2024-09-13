package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.jupiter.api.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleContainsOnlyOneJar;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleSimpleBuild;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJasSampleContainsAlsoTestClasses;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
class PomEquippedEmbeddedMavenForJarSampleTestCase {

    @Test
    void testJarSampleBuild() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("clean", "verify")
            .useMaven3Version("3.3.9")
            .build();

        verifyJarSampleSimpleBuild(builtProject);
        verifyJarSampleContainsOnlyOneJar(builtProject);
    }

    @Test
    void testJarSampleBuildWithTestClasses() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .setGoals("clean", "package")
            .setProfiles("test-classes")
            .useMaven3Version("3.3.9")
            .build();

        verifyJarSampleSimpleBuild(builtProject);
        verifyJasSampleContainsAlsoTestClasses(builtProject);
    }

}
