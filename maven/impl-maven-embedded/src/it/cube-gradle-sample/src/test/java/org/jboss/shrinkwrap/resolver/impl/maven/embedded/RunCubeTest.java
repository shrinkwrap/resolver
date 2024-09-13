package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.arquillian.cube.CubeController;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
@ExtendWith(ArquillianExtension.class)
class RunCubeTest {

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
        Assertions.assertFalse(log.contains("FAILURE: Build failed with an exception."),
                "The gradle build failed. For more information see the log.");
    }

    @AfterAll
    static void cleanup() throws IOException {
        FileUtils
            .deleteDirectory(new File("/tmp/shrinkwrap-resolver-impl-maven-embedded-integration-tests-cube-gradle"));
    }
}
