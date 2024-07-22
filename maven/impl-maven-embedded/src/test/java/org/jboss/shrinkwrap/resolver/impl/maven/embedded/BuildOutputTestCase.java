package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
class BuildOutputTestCase {

    @RegisterExtension
    final TestWorkDirExtension workDirExtension = new TestWorkDirExtension();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    void testJarSampleBuild() {
        EmbeddedMaven
            .forProject(workDirExtension.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "verify")
            .useLocalInstallation()
            .build();

        verifyStatuses();
        verifyBuildContent(true);
    }

    @Test
    void testJarSampleBuildInQuietMode() {
        EmbeddedMaven
            .forProject(workDirExtension.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "verify")
            .setQuiet()
            .useLocalInstallation()
            .build();

        verifyStatuses();
        verifyBuildContent(false);
    }

    private void verifyStatuses(){
        assertBuildStdoutContains("Embedded Maven build started: jar-sample" + File.separatorChar + "pom.xml", true);
        assertBuildStdoutContains("Embedded Maven build stopped: jar-sample" + File.separatorChar + "pom.xml", true);
    }

    private void verifyBuildContent(boolean shouldContain) {
        assertBuildStdoutContains("Building ShrinkWrap Resolver Embedded Maven Integration Tests", shouldContain);
        assertBuildStdoutContains("BUILD SUCCESS", shouldContain);
        assertBuildStdoutContains("INFO", shouldContain);
    }

    private void assertBuildStdoutContains(String expectedString, boolean shouldContain){
        StringBuilder assertWarn = new StringBuilder("The build output printed on stdout should ");
        if (!shouldContain){
            assertWarn.append("NOT ");
        }
        assertWarn.append("contain: ").append(expectedString);

        Assertions.assertEquals(shouldContain, outContent.toString().contains(expectedString), assertWarn.toString());
    }

    @AfterEach
    void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

}
