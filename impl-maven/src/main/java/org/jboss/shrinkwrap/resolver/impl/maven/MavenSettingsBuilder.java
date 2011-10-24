/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;

/**
 * Builds Maven settings from arbitrary settings.xml file
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MavenSettingsBuilder {
    /**
     * Sets an alternate location to Maven user settings.xml configuration
     */
    public static final String ALT_USER_SETTINGS_XML_LOCATION = "org.apache.maven.user-settings";

    /**
     * Sets an alternate location of Maven global settings.xml configuration
     */
    public static final String ALT_GLOBAL_SETTINGS_XML_LOCATION = "org.apache.maven.global-settings";

    /**
     * Sets Maven resolution either online or offline
     */
    public static final String ALT_MAVEN_OFFLINE = "org.apache.maven.offline";

    /**
     * Sets an alternate location of Maven local repository
     */
    public static final String ALT_LOCAL_REPOSITORY_LOCATION = "maven.repo.local";

    // path to the user settings.xml
    private static final String DEFAULT_USER_SETTINGS_PATH = SecurityActions.getProperty("user.home").concat(
            "/.m2/settings.xml");

    // path to the default local repository
    private static final String DEFAULT_REPOSITORY_PATH = SecurityActions.getProperty("user.home").concat("/.m2/repository");

    /**
     * Loads default Maven settings from standard location or from a location specified by a property
     *
     * @return
     */
    public Settings buildDefaultSettings() {
        SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

        String altUserSettings = SecurityActions.getProperty(ALT_USER_SETTINGS_XML_LOCATION);
        String altGlobalSettings = SecurityActions.getProperty(ALT_GLOBAL_SETTINGS_XML_LOCATION);

        request.setUserSettingsFile(new File(DEFAULT_USER_SETTINGS_PATH));
        // set alternate files
        if (altUserSettings != null && altUserSettings.length() > 0) {
            request.setUserSettingsFile(new File(altUserSettings));
        }

        if (altGlobalSettings != null && altGlobalSettings.length() > 0) {
            request.setGlobalSettingsFile(new File(altGlobalSettings));
        }

        return buildSettings(request);
    }

    /**
     * Builds Maven settings from request.
     *
     * @param request The request for new settings
     */
    public Settings buildSettings(SettingsBuildingRequest request) {
        SettingsBuildingResult result;
        try {
            SettingsBuilder builder = new DefaultSettingsBuilderFactory().newInstance();
            result = builder.build(request);
        } catch (SettingsBuildingException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to parse Maven configuration", e);
        }

        Settings settings = result.getEffectiveSettings();

        // enrich with local repository
        if (settings.getLocalRepository() == null) {
            settings = enrichWithLocalRepository(settings);
        }

        return enrichWithOfflineMode(settings);
    }

    // adds local repository
    private Settings enrichWithLocalRepository(Settings settings) {
        String altLocalRepository = SecurityActions.getProperty(ALT_LOCAL_REPOSITORY_LOCATION);
        settings.setLocalRepository(DEFAULT_REPOSITORY_PATH);

        if (altLocalRepository != null && altLocalRepository.length() > 0) {
            settings.setLocalRepository(altLocalRepository);
        }
        return settings;
    }

    // adds offline mode from system property
    private Settings enrichWithOfflineMode(Settings settings) {

        String goOffline = SecurityActions.getProperty(ALT_MAVEN_OFFLINE);
        if (goOffline != null) {
            settings.setOffline(Boolean.valueOf(goOffline));
        }

        return settings;
    }

}
