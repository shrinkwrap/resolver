package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.util.List;
import java.util.Properties;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class Utils {

    // Jar sample constants
    public static final String pathToJarSamplePom = "src/it/jar-sample/pom.xml";
    public static final String jarSampleArchiveBaseName =
        "shrinkwrap-resolver-impl-maven-embedded-integration-tests-jar-3.0.0-SNAPSHOT";

    // War sample constants
    public static final String pathToWarSamplePom = "src/it/war-sample/pom.xml";

    // multi module sample constants
    public static final String pathToMultiModulePom = "src/it/multi-module-sample/pom.xml";
    public static final String baseArchiveNameModuleOne =
        "shrinkwrap-resolver-impl-maven-embedded-integration-tests-module-one-3.0.0-SNAPSHOT";
    public static final String multiModuleactivateModulesParamKey = "activate-modules";
    public static final String multiModuleactivateModulesParamValue = "activated";
    public static final String archiveNameModuleTwoParamKey = "module.two.archive.name";
    public static final String archiveNameModuleTwoParamValue = "cool-ear-name";

    public static void verifyThatArchiveContains(Archive<?> archive, String path) {
        assertTrue("The archive should contain " + path, archive.contains(path));

    }

    public static void verifyMavenVersion(BuiltProject builtProject, String mavenVersion) {
        assertTrue(builtProject.getMavenLog().contains("Apache Maven " + mavenVersion));
    }

    public static Properties getPropertiesWithSkipTests() {
        Properties properties = new Properties();
        properties.put("skipTests", "true");
        return properties;
    }

    //
    // Jar sample methods
    //

    public static void verifyJarSampleSimpleBuild(BuiltProject builtProject) {
        // verify the exit code
        assertEquals("The exit code should be 0", 0, builtProject.getMavenBuildExitCode());

        // verify archive name
        Archive<?> defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        assertEquals(jarSampleArchiveBaseName + ".jar", defaultBuiltArchive.getName());

        // contains both the compiled classes and resources
        verifyThatArchiveContains(defaultBuiltArchive, "/test/nested/NestedJarClass.class");
        verifyThatArchiveContains(defaultBuiltArchive, "/main.properties");

        // there are no modules
        assertTrue("set of modules should be empty", builtProject.getModules().isEmpty());
    }

    public static void verifyJarSampleContainsOnlyOneJar(BuiltProject builtProject) {

        // contains only one jar
        assertEquals("should contain only jar", 1, builtProject.getArchives().size());

        List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
        assertEquals(1, javaArchives.size());
    }

    public static void verifyJasSampleContainsAlsoTestClasses(BuiltProject builtProject) {

        // contains also test classes
        assertEquals("should contain two jar archives", 2, builtProject.getArchives().size());

        List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
        assertEquals(2, javaArchives.size());

        if (!javaArchives.get(0).equals(builtProject.getDefaultBuiltArchive())) {
            verifyTestsJar(javaArchives.get(0));
        } else {
            assertEquals("one of the retrieved jar archives should be same as the one returned as default",
                         javaArchives.get(0), builtProject.getDefaultBuiltArchive());
            verifyTestsJar(javaArchives.get(1));
        }
    }

    private static void verifyTestsJar(JavaArchive javaArchive) {
        assertEquals(jarSampleArchiveBaseName + "-tests.jar", javaArchive.getName());
        verifyThatArchiveContains(javaArchive, "/test/JarTestCase.class");
        verifyThatArchiveContains(javaArchive, "/test.properties");
    }

    //
    // War sample methods
    //

    public static void verifyWarSampleWithSources(BuiltProject builtProject) {
        // verify the exit code
        assertEquals("The exit code should be 0", 0, builtProject.getMavenBuildExitCode());

        // verify archive name
        Archive<?> defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        assertEquals("cool-war-sample.war", defaultBuiltArchive.getName());

        // contains both the compiled classes and resources
        verifyThatArchiveContains(defaultBuiltArchive, "/WEB-INF/classes/test/WarClass.class");
        verifyThatArchiveContains(defaultBuiltArchive, "/WEB-INF/classes/main.properties");
        verifyThatArchiveContains(defaultBuiltArchive, "/WEB-INF/web.xml");

        // there are no modules
        assertTrue("set of modules should be empty", builtProject.getModules().isEmpty());

        // contains both war and jar containing sources
        assertEquals("should contain both war and jar containing sources", 2, builtProject.getArchives().size());

        List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
        assertEquals(1, javaArchives.size());
        assertEquals("cool-war-sample-sources.jar", javaArchives.get(0).getName());

        assertEquals(1, builtProject.getArchives(WebArchive.class).size());
    }

    //
    // multi module sample methods
    //

    public static void verifyMultiModuleSample(BuiltProject builtProject, boolean activatedModules) {
        // verify the exit code
        assertEquals("The exit code should be 0", 0, builtProject.getMavenBuildExitCode());

        // verify that there should be no archive and no target directory
        assertNull(builtProject.getDefaultBuiltArchive());

        if (activatedModules) {
            // there are two modules
            assertEquals("there should be two modules", 2, builtProject.getModules().size());

            verifyModuleOne(builtProject.getModule("module-one"));
            verifyModuleTwo(builtProject.getModule("module-two"));
        } else {
            // no module activated
            assertEquals("there should no module activated", 0, builtProject.getModules().size());
        }
    }

    public static void verifyMultiModuleSampleWasCleaned(BuiltProject builtProject) {
        for (BuiltProject module : builtProject.getModules()) {
            assertNull("project should be cleaned, so no target directory and no archives should be present",
                       module.getArchives());
        }
    }

    public static void verifyModuleOne(BuiltProject builtProject) {

        // verify archive name
        Archive<?> defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        assertEquals(baseArchiveNameModuleOne + ".jar", defaultBuiltArchive.getName());

        // contains the compiled class
        verifyThatArchiveContains(defaultBuiltArchive, "/test/JarClass.class");

        // there are no modules
        assertTrue("set of modules should be empty", builtProject.getModules().isEmpty());

        // contains only jar
        assertEquals("should contain only jar", 1, builtProject.getArchives().size());

        List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
        assertEquals(1, javaArchives.size());
        assertEquals("found Java archive should be same as the default one", defaultBuiltArchive, javaArchives.get(0));

        assertEquals(0, builtProject.getArchives(EnterpriseArchive.class).size());
    }

    public static void verifyModuleTwo(BuiltProject builtProject) {

        // verify archive name
        Archive<?> defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        assertEquals(archiveNameModuleTwoParamValue + ".ear", defaultBuiltArchive.getName());

        verifyThatArchiveContains(defaultBuiltArchive, "/org.jboss.shrinkwrap.resolver-" + baseArchiveNameModuleOne + ".jar");
        verifyThatArchiveContains(defaultBuiltArchive, "/commons-codec-commons-codec-1.16.1.jar");

        // there are no modules
        assertTrue("set of modules should be empty", builtProject.getModules().isEmpty());

        // contains only ear
        assertEquals("should contain only ear", 1, builtProject.getArchives().size());

        List<EnterpriseArchive> earArchives = builtProject.getArchives(EnterpriseArchive.class);
        assertEquals(1, earArchives.size());
        assertEquals("found Ear archive should be same as the default one", defaultBuiltArchive, earArchives.get(0));

        assertEquals(0, builtProject.getArchives(JavaArchive.class).size());
    }
}
