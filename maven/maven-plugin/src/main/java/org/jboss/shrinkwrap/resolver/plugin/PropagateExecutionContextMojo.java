/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.plugin;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Profile;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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
 *
 */
@Mojo(name = "propagate-execution-context", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES)
public class PropagateExecutionContextMojo extends AbstractResolverMojo {

    @Parameter(property = "namespace", defaultValue = "maven.execution.")
    private String namespace;

    @Override
    public void execute() {

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
            updateUserProperty(properties, "active-profiles", sb.substring(0, sb.length() - 1));
        }

        request.setUserProperties(properties);
    }

    /**
     * Gets current value of name space
     *
     * @return The current value of name space
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of name space
     *
     * @param namespace The value of name space
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
