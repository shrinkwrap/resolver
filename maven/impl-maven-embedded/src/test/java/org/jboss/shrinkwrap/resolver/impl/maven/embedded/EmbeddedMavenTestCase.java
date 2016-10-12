package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Test;

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class EmbeddedMavenTestCase {

    @Test
    public void runFluentAPITest() {

        BuiltProject build = EmbeddedMaven
            .forProject("pom.xml")
            .useMaven3Version("3.0.5")
            .setGoals("clean", "package")
            .setShowVersion(true)
            .build();

        verifyBuiltProject(build);
    }

    @Test
    public void runInvokerTest() {
        final InvocationRequest request = new DefaultInvocationRequest();
        Invoker invoker = new DefaultInvoker();

        request.setPomFile(new File("pom.xml"));
        request.setGoals(new ArrayList<>(Arrays.asList(new String[] { "clean", "package" })));

        Properties properties = new Properties();
        properties.put("skipTests", "true");
        request.setProperties(properties);

        BuiltProject build = EmbeddedMaven
            .withMavenInvokerSet(request, invoker)
            .useDefaultDistribution()
            .build();

        verifyBuiltProject(build);
    }

    private void verifyBuiltProject(BuiltProject builtProject) {
        Archive defaultBuiltArchive = builtProject.getDefaultBuiltArchive();
        String name = defaultBuiltArchive.getName();
        assertThat(name, startsWith("shrinkwrap-resolver-impl-maven-embedded-"));
        assertThat(name, endsWith(".jar"));

        assertTrue(builtProject.getModules().isEmpty());
        assertEquals(2, builtProject.getArchives().size());
        assertEquals(2, builtProject.getArchives(JavaArchive.class).size());
        assertEquals(0, builtProject.getArchives(WebArchive.class).size());
    }
}
