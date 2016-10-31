package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.arquillian.cube.CubeController;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
@RunWith(Arquillian.class)
public class RunCubeTest {

    private static String GRADLE_SAMPLE_EMBEDDED_MAVEN = "gradle-sample-embedded-maven";

    @Test
    public void runCubeTest(@ArquillianResource CubeController cubeController) {

        // install Resolver bits into /tmp/shrinkwrap-resolver-impl-maven-embedded-integration-tests-cube-gradle directory
        EmbeddedMaven
            .forProject(System.getProperty("user.dir") + "/../../../../../pom.xml")
            .setGoals("install")
            .setUserSettingsFile(new File("src/test/resources/settings.xml"))
            .build();

        // create and run docker image
        cubeController.create(GRADLE_SAMPLE_EMBEDDED_MAVEN);
        cubeController.start(GRADLE_SAMPLE_EMBEDDED_MAVEN);

        // get gradle log
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        cubeController.copyLog(GRADLE_SAMPLE_EMBEDDED_MAVEN, true, true, true, true, -1, bos);
        String log = new String(bos.toByteArray());
        System.out.println("log: " + log);

        // stop docker image
        cubeController.stop(GRADLE_SAMPLE_EMBEDDED_MAVEN);
        cubeController.destroy(GRADLE_SAMPLE_EMBEDDED_MAVEN);

        // verify whether gradle build passed
        if (log.contains("FAILURE: Build failed with an exception.")) {
            Assert.fail("The gradle build failed. For more information is the log.");
        }
    }

    @AfterClass
    public static void cleanup() throws IOException {
        FileUtils
            .deleteDirectory(new File("/tmp/shrinkwrap-resolver-impl-maven-embedded-integration-tests-cube-gradle"));
    }
}
