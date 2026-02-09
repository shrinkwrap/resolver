package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.util.List;
import java.util.Properties;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.junit.jupiter.api.Assertions;

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
        Assertions.assertTrue(archive.contains(path), "The archive should contain " + path);

    }

    public static void verifyMavenVersion(BuiltProject builtProject, String mavenVersion) {
        Assertions.assertTrue(builtProject.getMavenLog().contains("Apache Maven " + mavenVersion));
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
        Assertions.assertEquals(0, builtProject.getMavenBuildExitCode(), "The exit code should be 0");

        // verify archive name
        Archive<?> defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        Assertions.assertEquals(jarSampleArchiveBaseName + ".jar", defaultBuiltArchive.getName());

        // contains both the compiled classes and resources
        verifyThatArchiveContains(defaultBuiltArchive, "/test/nested/NestedJarClass.class");
        verifyThatArchiveContains(defaultBuiltArchive, "/main.properties");

        // there are no modules
        Assertions.assertTrue(builtProject.getModules().isEmpty(), "set of modules should be empty");
    }

    public static void verifyJarSampleContainsOnlyOneJar(BuiltProject builtProject) {

        // contains only one jar
        Assertions.assertEquals(1, builtProject.getArchives().size(),"should contain only jar");

        List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
        Assertions.assertEquals(1, javaArchives.size());
    }

    public static void verifyJasSampleContainsAlsoTestClasses(BuiltProject builtProject) {

        // contains also test classes
        Assertions.assertEquals(2, builtProject.getArchives().size(), "should contain two jar archives");

        List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
        Assertions.assertEquals(2, javaArchives.size());

        if (!javaArchives.get(0).equals(builtProject.getDefaultBuiltArchive())) {
            verifyTestsJar(javaArchives.get(0));
        } else {
            Assertions.assertEquals(javaArchives.get(0), builtProject.getDefaultBuiltArchive(),
                    "one of the retrieved jar archives should be same as the one returned as default");
            verifyTestsJar(javaArchives.get(1));
        }
    }

    private static void verifyTestsJar(JavaArchive javaArchive) {
        Assertions.assertEquals(jarSampleArchiveBaseName + "-tests.jar", javaArchive.getName());
        verifyThatArchiveContains(javaArchive, "/test/JarTestCase.class");
        verifyThatArchiveContains(javaArchive, "/test.properties");
    }

    //
    // War sample methods
    //

    public static void verifyWarSampleWithSources(BuiltProject builtProject) {
        // verify the exit code
        Assertions.assertEquals(0, builtProject.getMavenBuildExitCode(), "The exit code should be 0");

        // verify archive name
        Archive<?> defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        Assertions.assertEquals("cool-war-sample.war", defaultBuiltArchive.getName());

        // contains both the compiled classes and resources
        verifyThatArchiveContains(defaultBuiltArchive, "/WEB-INF/classes/test/WarClass.class");
        verifyThatArchiveContains(defaultBuiltArchive, "/WEB-INF/classes/main.properties");
        verifyThatArchiveContains(defaultBuiltArchive, "/WEB-INF/web.xml");

        // there are no modules
        Assertions.assertTrue(builtProject.getModules().isEmpty(), "set of modules should be empty");

        // contains both war and jar containing sources
        Assertions.assertEquals(2, builtProject.getArchives().size(), "should contain both war and jar containing sources");

        List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
        Assertions.assertEquals(1, javaArchives.size());
        Assertions.assertEquals("cool-war-sample-sources.jar", javaArchives.get(0).getName());

        Assertions.assertEquals(1, builtProject.getArchives(WebArchive.class).size());
    }

    //
    // multi module sample methods
    //

    public static void verifyMultiModuleSample(BuiltProject builtProject, boolean activatedModules) {
        // verify the exit code
        Assertions.assertEquals(0, builtProject.getMavenBuildExitCode(), "The exit code should be 0");

        // verify that there should be no archive and no target directory
        Assertions.assertNull(builtProject.getDefaultBuiltArchive());

        if (activatedModules) {
            // there are two modules
            Assertions.assertEquals(2, builtProject.getModules().size(), "there should be two modules");

            verifyModuleOne(builtProject.getModule("module-one"));
            verifyModuleTwo(builtProject.getModule("module-two"));
        } else {
            // no module activated
            Assertions.assertEquals(0, builtProject.getModules().size(), "there should no module activated");
        }
    }

    public static void verifyMultiModuleSampleWasCleaned(BuiltProject builtProject) {
        for (BuiltProject module : builtProject.getModules()) {
            Assertions.assertNull(module.getArchives(),
                    "project should be cleaned, so no target directory and no archives should be present");

        }
    }

    public static void verifyModuleOne(BuiltProject builtProject) {

        // verify archive name
        Archive<?> defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        Assertions.assertEquals(baseArchiveNameModuleOne + ".jar", defaultBuiltArchive.getName());

        // contains the compiled class
        verifyThatArchiveContains(defaultBuiltArchive, "/test/JarClass.class");

        // there are no modules
        Assertions.assertTrue(builtProject.getModules().isEmpty(), "set of modules should be empty");

        // Check if it contains only one jar
        Assertions.assertEquals(1, builtProject.getArchives().size(), "should contain only jar");

        List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
        Assertions.assertEquals(1, javaArchives.size());
        Assertions.assertEquals(defaultBuiltArchive, javaArchives.get(0), "found Java archive should be same as the default one");

        Assertions.assertEquals(0, builtProject.getArchives(EnterpriseArchive.class).size());
    }

    public static void verifyModuleTwo(BuiltProject builtProject) {

        // verify archive name
        Archive<?> defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        Assertions.assertEquals(archiveNameModuleTwoParamValue + ".ear", defaultBuiltArchive.getName());

        verifyThatArchiveContains(defaultBuiltArchive, "/org.jboss.shrinkwrap.resolver-" + baseArchiveNameModuleOne + ".jar");
        verifyThatArchiveContains(defaultBuiltArchive, "/commons-codec-commons-codec-1.21.0.jar");

        // there are no modules
        Assertions.assertTrue(builtProject.getModules().isEmpty(), "set of modules should be empty");

        // contains only ear
        Assertions.assertEquals(1, builtProject.getArchives().size(), "should contain only ear");

        List<EnterpriseArchive> earArchives = builtProject.getArchives(EnterpriseArchive.class);
        Assertions.assertEquals(1, earArchives.size());
        Assertions.assertEquals(defaultBuiltArchive, earArchives.get(0), "found Ear archive should be same as the default one");

        Assertions.assertEquals(0, builtProject.getArchives(JavaArchive.class).size());
    }
}
