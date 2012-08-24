/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.task;

import org.jboss.shrinkwrap.resolver.api.maven.InvalidEnvironmentException;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ConfigureFromPluginTask implements MavenWorkingSessionTask {

    private static final String POM_FILE_KEY = "maven.execution.pom-file";
    private static final String OFFLINE_KEY = "maven.execution.offline";
    private static final String USER_SETTINGS_KEY = "maven.execution.user-settings";
    private static final String GLOBAL_SETTINGS_KEY = "maven.execution.global-settings";
    private static final String ACTIVE_PROFILES_KEY = "maven.execution.active-profiles";

    private static final String CONSTRUCTION_EXCEPTION = "Configuration from environment requires that user has following properties set, however they were not detected in runtime environment:\n"
        + "\t"
        + POM_FILE_KEY
        + "\n"
        + "\t"
        + OFFLINE_KEY
        + "\n"
        + "\t"
        + USER_SETTINGS_KEY
        + "\n"
        + "\t"
        + GLOBAL_SETTINGS_KEY
        + "\n"
        + "\t"
        + ACTIVE_PROFILES_KEY
        + "\n"
        + "\n"
        + "You should enable ShrinkWrap Maven Resolver to get them set for you automatically if executing from Maven via adding following to your <build> section:\n\n"
        + "<plugin>\n"
        + "\t<groupId>org.jboss.shrinkwrap.resolver</groupId>\n"
        + "\t<artifactId>shrinkwrap-resolver-maven-plugin</artifactId>\n"
        + "\t<executions>\n"
        + "\t\t<execution>\n"
        + "\t\t\t<goals>\n"
        + "\t\t\t\t<goal>propagate-execution-context</goal>\n"
        + "\t\t\t</goals>\n"
        + "\t\t</execution>\n" + "\t</executions>\n" + "</plugin>\n";

    @Override
    public MavenWorkingSession execute(MavenWorkingSession session) {

        String pomFile = SecurityActions.getProperty(POM_FILE_KEY);
        if (Validate.isNullOrEmpty(pomFile)) {
            throw new InvalidEnvironmentException(CONSTRUCTION_EXCEPTION);
        }
        Validate.isReadable(pomFile, "POM file " + pomFile + " does not represent a readable file");

        String userSettings = SecurityActions.getProperty(USER_SETTINGS_KEY);
        if (Validate.isNullOrEmpty(userSettings)) {
            throw new InvalidEnvironmentException(CONSTRUCTION_EXCEPTION);
        }

        boolean hasSettingsXml = true;
        try {
            Validate.isReadable(userSettings, "Settings.xml file " + userSettings
                + " does not represent a readable file");
        } catch (final IllegalArgumentException iae) {
            hasSettingsXml = false;
        }

        if (hasSettingsXml) {
            session = new ConfigureSettingsTask(userSettings).execute(session);
        }

        String activeProfiles = SecurityActions.getProperty(ACTIVE_PROFILES_KEY);
        String[] profiles = new String[0];
        if (Validate.isNullOrEmpty(activeProfiles)) {
            profiles = activeProfiles.split(",");
        }

        return new ConfigureFromPomTask(pomFile, profiles).execute(session);
    }

}
