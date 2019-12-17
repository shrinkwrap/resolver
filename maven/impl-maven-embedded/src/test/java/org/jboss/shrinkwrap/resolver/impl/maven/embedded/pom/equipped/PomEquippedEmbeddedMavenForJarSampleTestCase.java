package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.apache.maven.shared.invoker.PrintStreamLogger;
import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.TestWorkDirRule;
import org.junit.Rule;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleContainsOnlyOneJar;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJarSampleSimpleBuild;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.verifyJasSampleContainsAlsoTestClasses;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomEquippedEmbeddedMavenForJarSampleTestCase {

    @Rule
    public final TestWorkDirRule workDirRule = new TestWorkDirRule();

    @Test
    public void testJarSampleBuild() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "verify")
            .useLocalInstallation()
            .build();

        verifyJarSampleSimpleBuild(builtProject);
        verifyJarSampleContainsOnlyOneJar(builtProject);
    }

    @Test
    public void testJarSampleBuildWithDebugLoggerLevelShouldDisplayCommand() {
        ByteArrayOutputStream logOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(logOutputStream);
        PrintStreamLogger printStreamLogger = new PrintStreamLogger(ps, InvokerLogger.INFO);

        EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "verify")
            .setLogger(printStreamLogger)
            .useDefaultDistribution()
            .setDebugLoggerLevel()
            .build();

        final String logOutput = logOutputStream.toString();
        Assertions.assertThat(logOutput).contains("[DEBUG] Using ${maven.home} of:");
        final String expectedExecPattern = System.getProperty("os.name").startsWith("Windows")
            ? "\\[DEBUG\\] Executing: cmd.exe /X /C .+bin\\\\mvn.+skipTests=true.+clean.+verify"
            : "\\[DEBUG\\] Executing: /bin/sh -c cd .+jar-sample.+bin/mvn.+skipTests=true.+clean.+verify";
        Assertions.assertThat(logOutput).containsPattern(expectedExecPattern);
    }

    @Test
    public void testJarSampleBuildWithTestClasses() {
        BuiltProject builtProject = EmbeddedMaven
            .forProject(workDirRule.prepareProject(pathToJarSamplePom))
            .setGoals("clean", "package")
            .setProfiles("test-classes")
            .build();

        verifyJarSampleSimpleBuild(builtProject);
        verifyJasSampleContainsAlsoTestClasses(builtProject);
    }

}
