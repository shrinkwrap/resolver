package org.jboss.shrinkwrap.resolver.impl.maven.embedded.invoker.equipped;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirRule;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped.ResolverErrorOutputHandler;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped.ResolverOutputHandler;
import org.junit.Rule;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.getPropertiesWithSkipTests;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToWarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyMavenVersion;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyWarSampleWithSources;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class InvokerEquippedEmbeddedMavenForWarSampleTestCase {

    @Rule
    public final TestWorkDirRule workDirRule = new TestWorkDirRule();

    @Test
    public void testWarSampleBuildWithMaven310() {
        InvocationRequest request = new DefaultInvocationRequest();
        Invoker invoker = new DefaultInvoker();

        request.setPomFile(workDirRule.prepareProject(pathToWarSamplePom));
        request.setGoals(Arrays.asList(new String[] { "clean", "package", "source:jar" }));
        request.setUserSettingsFile(new File("src/it/settings.xml"));

        Properties props = getPropertiesWithSkipTests();
        request.setProperties(props);

        StringBuffer logBuffer = new StringBuffer("");
        invoker.setOutputHandler(new ResolverOutputHandler(logBuffer));
        invoker.setErrorHandler(new ResolverErrorOutputHandler(logBuffer));

        request.setShowVersion(true);

        BuiltProject builtProject = EmbeddedMaven
            .withMavenInvokerSet(request, invoker)
            .useMaven3Version("3.9.6")
            .build();
        builtProject.setMavenLog(logBuffer.toString());

        verifyWarSampleWithSources(builtProject);
        verifyMavenVersion(builtProject, "3.9.6");
    }

    @Test(expected = IllegalStateException.class)
    public void testIfWarSampleBuildFailsWithException() {

        InvocationRequest request = new DefaultInvocationRequest();
        Invoker invoker = new DefaultInvoker();

        request.setPomFile(workDirRule.prepareProject(pathToWarSamplePom));
        request.setGoals(Arrays.asList(new String[] { "clean", "package" }));

        request.setProfiles(Arrays.asList(new String[] { "failing" }));
        request.setProperties(getPropertiesWithSkipTests());

        BuiltProject builtProject = EmbeddedMaven
            .withMavenInvokerSet(request, invoker)
            .build();
    }

    @Test
    public void testIfWarSampleBuildFailsWithoutException() {

        InvocationRequest request = new DefaultInvocationRequest();
        Invoker invoker = new DefaultInvoker();

        request.setPomFile(workDirRule.prepareProject(pathToWarSamplePom));
        request.setGoals(Arrays.asList(new String[] { "clean", "package" }));

        request.setProfiles(Arrays.asList(new String[] { "failing" }));
        request.setProperties(getPropertiesWithSkipTests());

        BuiltProject builtProject = EmbeddedMaven
            .withMavenInvokerSet(request, invoker)
            .ignoreFailure()
            .build();

        assertEquals(1, builtProject.getMavenBuildExitCode());
    }

}
