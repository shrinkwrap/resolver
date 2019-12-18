package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class BuildOutputTestCase {

    @Rule
    public final TestWorkDirRule workDirRule = new TestWorkDirRule();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testJarSampleBuild() {
        EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "verify")
            .useLocalInstallation()
            .build();

        verifyStatuses();
        verifyBuildContent(true);
    }

    @Test
    public void testJarSampleBuildInQuietMode() {
        EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
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
        StringBuffer assertWarn = new StringBuffer("The build output printed on stdout should ");
        if (!shouldContain){
            assertWarn.append("NOT ");
        }
        assertWarn.append("contain: ").append(expectedString);

        assertEquals(assertWarn.toString(), shouldContain,outContent.toString().contains(expectedString));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

}
