package org.jboss.shrinkwrap.resolver.plugin;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Propagates current Maven Execution properties to mimic they were specified on the command line by user himself.
 *
 * Following properties are propagated:
 *
 * <ul>
 * <li>pom-file</li>
 * <li>offline</li>
 * <li>user-settings</li>
 * <li>global-settings</li>
 * <li>active-profiles</li>
 * </ul>
 * length()
 *
 * @goal propagate-execution-context
 * @phase process-test-classes
 * @requiresProject
 * @executionStrategy always
 *
 */
public class PropagateExecutionContextMojo extends AbstractMojo {

    /**
     * The current build session instance.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * Name space where properties are stored. This means that all the properties are stored under
     * "namespace.value. + property.name"
     *
     * @parameter expression="${namespace}" default-value="maven.execution."
     */
    private String namespace;

    public void execute() throws MojoExecutionException {

        MavenExecutionRequest request = session.getRequest();

        Properties properties = request.getUserProperties();

        // set pom file
        File pom = session.getCurrentProject().getFile();
        if (pom != null) {
            updateUserProperty(properties, "pom-file", pom.getAbsolutePath());
        }

        // set offline flag
        updateUserProperty(properties, "offline", String.valueOf(session.isOffline()));

        // set settings.xml files
        File userSettings = request.getUserSettingsFile();
        if (userSettings != null) {
            updateUserProperty(properties, "user-settings", userSettings.getAbsolutePath());
        }
        File globalSettings = request.getGlobalSettingsFile();
        if (globalSettings != null) {
            updateUserProperty(properties, "global-settings", globalSettings.getAbsolutePath());
        }

        // set active profiles
        List<Profile> profiles = session.getCurrentProject().getActiveProfiles();
        StringBuilder sb = new StringBuilder();
        for (Profile p : profiles) {
            sb.append(p.getId()).append(",");
        }

        if (sb.length() > 0) {
            updateUserProperty(properties, "active-profiles", sb.substring(0, sb.length() - 1).toString());
        }

        request.setUserProperties(properties);
    }

    /**
     * Gets current value of name space
     *
     * @return
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of name space
     *
     * @param namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setSession(MavenSession session) {
        this.session = session;
    }

    private void updateUserProperty(Properties properties, String key, String value) {
        if (key != null && value != null) {
            properties.setProperty(getNamespace() + key, value);
            getLog().debug(
                    "Propagating [" + getNamespace() + key + "=" + value + "] from Maven Session to command line properties");
        }
    }
}
