package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuildStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public abstract class BuildStageImpl<NEXT_STEP> extends DistributionConfigurationStageImpl<NEXT_STEP>
    implements BuildStage {

    private static Logger log = Logger.getLogger(BuildStageImpl.class.getName());
    private static final String SAX_PARSER_FACTORY_KEY = "javax.xml.parsers.SAXParserFactory";

    private boolean ignoreFailure = false;

    @Override
    public BuiltProject build() {
        final String oldValue = removeSAXParserFactoryProperty();

        if (getSetMavenInstalation() != null) {
            getInvoker().setMavenHome(getSetMavenInstalation());
        }

        try {

            printStatus("started");

            InvocationResult result = getInvoker().execute(getInvocationRequest());

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

        return getBuiltProject();
    }

    private void printStatus(String status) {
        System.out.println("================================================");
        System.out.println("=======   Maven Embedded build " + status + "   =======");
        System.out.println("================================================");
    }

    public BuildStage ignoreFailure(boolean ignoreFailure) {
        this.ignoreFailure = ignoreFailure;
        return this;
    }

    public BuildStage ignoreFailure() {
        this.ignoreFailure = true;
        return this;
    }

    private BuiltProject getBuiltProject() {
        File pomFile = getInvocationRequest().getPomFile();
        if (pomFile == null){
            pomFile = new File(getInvocationRequest().getBaseDirectory() + File.separator + "pom.xml");
        }
        BuiltProjectImpl builtProject = new BuiltProjectImpl(
            pomFile,
            getInvocationRequest().getGlobalSettingsFile(),
            getInvocationRequest().getUserSettingsFile(),
            profilesInArray());

        return builtProject;
    }

    private String[] profilesInArray() {
        String[] profiles = new String[] {};
        List<String> profilesList = getInvocationRequest().getProfiles();
        if (profilesList != null) {
            profiles = new String[profilesList.size()];
            profiles = profilesList.toArray(profiles);
        }
        return profiles;
    }

    abstract InvocationRequest getInvocationRequest();

    abstract Invoker getInvoker();

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
