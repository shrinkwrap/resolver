package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirRule;
import org.junit.Rule;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.archiveNameModuleTwoParamKey;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.archiveNameModuleTwoParamValue;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.multiModuleactivateModulesParamKey;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.multiModuleactivateModulesParamValue;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToMultiModulePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyMavenVersion;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyMultiModuleSample;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyMultiModuleSampleWasCleaned;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomEquippedEmbeddedMavenForMultiModuleSampleTestCase {

    @Rule
    public final TestWorkDirRule workDirRule = new TestWorkDirRule();

    /**
     * Original test name testMultiModuleSampleBuildWithMaven305
     * But Maven 3.0.x should be avoided, using 3.1.0 instead.
     */
    @Test
    public void testMultiModuleSampleBuildWithMaven310() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToMultiModulePom))
            .useMaven3Version("3.9.9")
            .setUserSettingsFile(new File("src/it/settings.xml"))
            .setGoals("install")
            .addProperty(multiModuleactivateModulesParamKey, multiModuleactivateModulesParamValue)
            .addProperty(archiveNameModuleTwoParamKey, archiveNameModuleTwoParamValue)
            .setShowVersion(true)
            .build();

        verifyMavenVersion(builtProject, "3.9.9");
        verifyMultiModuleSample(builtProject, true);
    }

    @Test
    public void testMultiModuleSampleCleanBuild() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToMultiModulePom))
            .setGoals("clean")
            .addProperty(multiModuleactivateModulesParamKey, multiModuleactivateModulesParamValue)
            .addProperty(archiveNameModuleTwoParamKey, archiveNameModuleTwoParamValue)
            .build();

        verifyMultiModuleSampleWasCleaned(builtProject);
    }

    @Test
    public void testMultiModuleSampleBuildWithoutModulesActivated() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToMultiModulePom))
            .setGoals("clean", "package")
            .addProperty(archiveNameModuleTwoParamKey, archiveNameModuleTwoParamValue)
            .build();

        verifyMultiModuleSample(builtProject, false);
    }
}
