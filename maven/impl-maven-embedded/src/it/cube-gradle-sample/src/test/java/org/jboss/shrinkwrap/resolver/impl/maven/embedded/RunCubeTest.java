package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.ByteArrayOutputStream;

import org.arquillian.cube.CubeController;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
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

        cubeController.create(GRADLE_SAMPLE_EMBEDDED_MAVEN);
        cubeController.start(GRADLE_SAMPLE_EMBEDDED_MAVEN);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        cubeController.copyLog(GRADLE_SAMPLE_EMBEDDED_MAVEN, true, true, true, true, -1, bos);
        String log = new String(bos.toByteArray());
        System.out.println("log: " + log);

        cubeController.stop(GRADLE_SAMPLE_EMBEDDED_MAVEN);
        cubeController.destroy(GRADLE_SAMPLE_EMBEDDED_MAVEN);
    }
}
