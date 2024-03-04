package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped.ResolverErrorOutputHandler;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped.ResolverOutputHandler;

public class BuildTrigger {

    private final String SAX_PARSER_FACTORY_KEY = "javax.xml.parsers.SAXParserFactory";
    private final Logger log = Logger.getLogger(BuildStageImpl.class.getName());
    private final boolean ignoreFailure;
    private final File mavenInstallation;
    private final InvocationRequest invocationRequest;
    private final Invoker invoker;
    private final StringBuffer logBuffer;
    private final boolean quiet;

    public BuildTrigger(File mavenInstallation, InvocationRequest invocationRequest, Invoker invoker,
        StringBuffer logBuffer, boolean quiet, boolean ignoreFailure) {
        this.mavenInstallation = mavenInstallation;
        this.invocationRequest = invocationRequest;
        this.invoker = invoker;
        this.logBuffer = logBuffer;
        this.quiet = quiet;
        this.ignoreFailure = ignoreFailure;
    }

    private void setOutputHandlers(String expectedRegex, CountDownLatch countDownLatch) {
        if (logBuffer != null) {
            ResolverErrorOutputHandler errorOutputHandler =
                new ResolverErrorOutputHandler(logBuffer, expectedRegex, countDownLatch);
            ResolverOutputHandler outputHandler = new ResolverOutputHandler(logBuffer, expectedRegex, countDownLatch);

            invoker.setOutputHandler(outputHandler);
            invocationRequest.setOutputHandler(outputHandler);

            invoker.setErrorHandler(errorOutputHandler);
            invocationRequest.setErrorHandler(errorOutputHandler);

            errorOutputHandler.setQuiet(quiet);
            outputHandler.setQuiet(quiet);
        }
    }

    public BuiltProject build(String expectedRegex, CountDownLatch countDownLatch) {
        final String oldValue = removeSAXParserFactoryProperty();

        if (mavenInstallation != null) {
            invoker.setMavenHome(mavenInstallation);
        }

        setOutputHandlers(expectedRegex, countDownLatch);

        InvocationResult result = null;
        try {

            printStatus("started");

            result = invoker.execute(invocationRequest);

            if (result.getExitCode() != 0) {
                if (ignoreFailure) {
                    log.severe("Maven build failed - the exit code is: " + result.getExitCode());
                } else {
                    throw new IllegalStateException("Maven build failed - the exit code is: " + result.getExitCode()
                        + "\n To ignore this failure use method ignoreFailure()",
                        result.getExecutionException());
                }
            }
        } catch (MavenInvocationException e) {
            throw new IllegalStateException("Execution of a Maven build has failed", e);
        } finally {
            printStatus("stopped");
        }
        setSAXParserFactoryProperty(oldValue);

        return getBuiltProject(result);
    }

    private void printStatus(String status) {
        File pomFile = invocationRequest.getPomFile();
        String projectPom = "";

        if (pomFile == null) {
            File baseDirectory = invocationRequest.getBaseDirectory();
            if (baseDirectory != null) {
                projectPom = baseDirectory.getName() + File.separator;
            }
            String pomFileName = invocationRequest.getPomFileName();
            if (pomFileName != null) {
                projectPom = projectPom + pomFileName;
            }
        } else {
            projectPom = pomFile.getParentFile().getName() + File.separator + pomFile.getName();
        }
        StringBuffer borders = new StringBuffer("==========================================");
        for (int i = 0; i < projectPom.length(); i++) {
            borders.append("=");
        }
        System.out.println(borders.toString());
        System.out.println("===   Embedded Maven build " + status + ": " + projectPom + "   ===");
        System.out.println(borders.toString());
    }

    private BuiltProject getBuiltProject(InvocationResult result) {
        File pomFile = invocationRequest.getPomFile();
        if (pomFile == null) {
            pomFile = new File(invocationRequest.getBaseDirectory() + File.separator + "pom.xml");
        }
        BuiltProjectImpl builtProject = new BuiltProjectImpl(
            pomFile,
            invocationRequest.getGlobalSettingsFile(),
            invocationRequest.getUserSettingsFile(),
            invocationRequest.getProperties(),
            profilesInArray());

        if (logBuffer != null) {
            builtProject.setMavenLog(logBuffer.toString());
        }
        if (result != null) {
            builtProject.setMavenBuildExitCode(result.getExitCode());
        }

        return builtProject;
    }

    private String[] profilesInArray() {
        String[] profiles = new String[] {};
        List<String> profilesList = invocationRequest.getProfiles();
        if (profilesList != null) {
            profiles = new String[profilesList.size()];
            profiles = profilesList.toArray(profiles);
        }
        return profiles;
    }

    private String removeSAXParserFactoryProperty() {
        // solution for https://issues.jboss.org/browse/SHRINKRES-212
        final Object value = System.getProperties().remove(SAX_PARSER_FACTORY_KEY);
        return value != null ? (String) value : null;
    }

    private void setSAXParserFactoryProperty(String oldValue) {
        if (oldValue != null) {
            System.setProperty(SAX_PARSER_FACTORY_KEY, oldValue);
        }
    }
}
