package org.jboss.shrinkwrap.resolver.impl.maven.embedded.invoker.equipped;

import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirRule;
import org.junit.Rule;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.getPropertiesWithSkipTests;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleContainsOnlyOneJar;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleSimpleBuild;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJasSampleContainsAlsoTestClasses;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class InvokerEquippedEmbeddedMavenForJarSampleTestCase {

    @Rule
    public final TestWorkDirRule workDirRule = new TestWorkDirRule();

    @Test
    public void testJarSampleBuild() {

        final InvocationRequest request = new DefaultInvocationRequest();
        Invoker invoker = new DefaultInvoker();

        request.setPomFile(workDirRule.prepareProject(pathToJarSamplePom));
        request.setGoals(Arrays.asList("clean", "verify"));

        request.setProperties(getPropertiesWithSkipTests());

        BuiltProject builtProject = EmbeddedMaven
            .withMavenInvokerSet(request, invoker)
            .useLocalInstallation()
            .build();

        verifyJarSampleSimpleBuild(builtProject);
        verifyJarSampleContainsOnlyOneJar(builtProject);
    }

    @Test
    public void testJarSampleBuildWithTestClasses() {
        final InvocationRequest request = new DefaultInvocationRequest();
        Invoker invoker = new DefaultInvoker();

        request.setPomFile(workDirRule.prepareProject(pathToJarSamplePom));
        request.setGoals(Arrays.asList("clean", "package"));

        request.setProperties(getPropertiesWithSkipTests());
        request.setProfiles(Collections.singletonList("test-classes"));

        BuiltProject builtProject = EmbeddedMaven
            .withMavenInvokerSet(request, invoker)
            .useLocalInstallation()
            .build();


        verifyJarSampleSimpleBuild(builtProject);
        verifyJasSampleContainsAlsoTestClasses(builtProject);
    }



}
