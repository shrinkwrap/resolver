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
package org.jboss.shrinkwrap.resolver.impl.maven.bootstrap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.apache.maven.settings.building.SettingsProblem;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.decrypt.MavenSettingsDecrypter;

/**
 * Builds Maven settings from arbitrary settings.xml file
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class MavenSettingsBuilder {
    private static Logger log = Logger.getLogger(MavenSettingsBuilder.class.getName());

    /**
     * Sets an alternate location to Maven user settings.xml configuration
     */
    public static final String ALT_USER_SETTINGS_XML_LOCATION = "org.apache.maven.user-settings";

    /**
     * Sets an alternate location of Maven global settings.xml configuration
     */
    public static final String ALT_GLOBAL_SETTINGS_XML_LOCATION = "org.apache.maven.global-settings";

    /**
     * Sets an alternate location of Maven settings-security.xml configuration
     */
    public static final String ALT_SECURITY_SETTINGS_XML_LOCATION = "settings.security";

    /**
     * Sets an alternate location of Maven settings-security.xml configuration, old key, see SHRINKRES-197
     */
    public static final String ALT_SECURITY_SETTINGS_XML_LOCATION_DEPRECATED = "org.apache.maven.security-settings";

    /**
     * Sets Maven resolution either online or offline
     */
    public static final String ALT_MAVEN_OFFLINE = "org.apache.maven.offline";

    /**
     * Sets an alternate location of Maven local repository
     */
    public static final String ALT_LOCAL_REPOSITORY_LOCATION = "maven.repo.local";

    // path to global settings.xml
    private static final String DEFAULT_GLOBAL_SETTINGS_PATH;
    // path to the user settings.xml
    private static final String DEFAULT_USER_SETTINGS_PATH;
    // path to the default local repository
    private static final String DEFAULT_REPOSITORY_PATH;
    // path to security settings
    private static final String DEFAULT_SETTINGS_SECURITY_PATH;

    static {
        // it might happen that "user.home" is not defined
        String userHome = SecurityActions.getProperty("user.home");
        // it might happen that "M2_HOME" is not defined
        String m2HomeEnv = SecurityActions.getEnvProperty("M2_HOME");
        String mHomeEnv = SecurityActions.getEnvProperty("MAVEN_HOME");
        String m2HomeProp = SecurityActions.getProperty("maven.home");
        String m2Home = getFirstNotNull(m2HomeProp, m2HomeEnv, mHomeEnv);

        // note that pointing settings.xml to a non existining file does not matter here
        DEFAULT_GLOBAL_SETTINGS_PATH = m2Home == null ? "conf/settings.xml" : m2Home.concat("/conf/settings.xml".replace(
                '/', File.separatorChar));
        DEFAULT_USER_SETTINGS_PATH = userHome == null ? "settings.xml" : userHome.concat("/.m2/settings.xml".replace('/',
                File.separatorChar));
        DEFAULT_REPOSITORY_PATH = userHome == null ? "repository" : userHome.concat("/.m2/repository".replace('/',
                File.separatorChar));
        DEFAULT_SETTINGS_SECURITY_PATH = userHome == null ? ".settings-security.xml" : userHome
                .concat("/.m2/settings-security.xml").replace('/', File.separatorChar);

    }

    static String getFirstNotNull(String... values) {
        for(String value : values) {
            if(value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Loads default Maven settings from standard location or from a location specified by a property
     *
     * @return The built default maven {@link Settings}
     */
    public Settings buildDefaultSettings() {
        return buildSettings(getDefaultSettingsBuildingRequest());
    }

    /**
     * Builds Maven settings from request.
     *
     * @param request
     * The request for new settings
     * @return The built default maven {@link Settings}
     */
    public Settings buildSettings(SettingsBuildingRequest request) {
        SettingsBuildingResult result;
        try {
            SettingsBuilder builder = new DefaultSettingsBuilderFactory().newInstance();

            if (request.getGlobalSettingsFile() != null) {
                log.log(Level.FINE, "Using {0} to get global Maven settings.xml", request.getGlobalSettingsFile()
                        .getAbsolutePath());
            }
            final File userSettingsFile = request.getUserSettingsFile();
            if (userSettingsFile != null) {
                log.log(Level.FINE, "Using {0} to get user Maven settings.xml", userSettingsFile.getAbsolutePath());

                // Maven will not check the format passed in (any XML will do), so let's ensure we have a
                // settings.xml by checking just the top-level element
                final XMLStreamReader reader;
                try {
                    reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(userSettingsFile));
                    // get the first element name
                    while (reader.hasNext()) {
                        if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                            break;
                        }
                    }
                    final String topLevel = reader.getLocalName();

                    if (!"settings".equals(topLevel)) {
                        throw new InvalidConfigurationFileException("Invalid format settings.xml found: "
                                + userSettingsFile);
                    }
                } catch (final FileNotFoundException e) {
                    // Ignore at this level
                } catch (final XMLStreamException xmlse) {
                    throw new RuntimeException("Could not check file format of specified settings.xml: "
                            + userSettingsFile, xmlse);
                }

            }

            result = builder.build(request);
        }
        // wrap exception message
        catch (SettingsBuildingException e) {
            StringBuilder sb = new StringBuilder("Found ").append(e.getProblems().size())
                    .append(" problems while building settings.xml model from both global Maven configuration file")
                    .append(request.getGlobalSettingsFile()).append(" and/or user configuration file: ")
                    .append(request.getUserSettingsFile()).append("\n");

            int counter = 1;
            for (SettingsProblem problem : e.getProblems()) {
                sb.append(counter++).append("/ ").append(problem).append("\n");
            }

            throw new InvalidConfigurationFileException(sb.toString());
        }

        // get settings object and update it according to property values
        Settings settings = result.getEffectiveSettings();
        settings = enrichWithLocalRepository(settings);
        settings = enrichWithOfflineMode(settings);
        settings = decryptPasswords(settings);
        return settings;
    }

    private SettingsBuildingRequest getDefaultSettingsBuildingRequest() {
        SettingsBuildingRequest request = new DefaultSettingsBuildingRequest().setSystemProperties(SecurityActions
                .getProperties());

        String altUserSettings = SecurityActions.getProperty(ALT_USER_SETTINGS_XML_LOCATION);
        String altGlobalSettings = SecurityActions.getProperty(ALT_GLOBAL_SETTINGS_XML_LOCATION);

        request.setGlobalSettingsFile(new File(DEFAULT_GLOBAL_SETTINGS_PATH));
        request.setUserSettingsFile(new File(DEFAULT_USER_SETTINGS_PATH));
        // set alternate files
        if (altUserSettings != null && altUserSettings.length() > 0) {
            request.setUserSettingsFile(new File(altUserSettings));
        }

        if (altGlobalSettings != null && altGlobalSettings.length() > 0) {
            request.setGlobalSettingsFile(new File(altGlobalSettings));
        }

        return request;
    }

    private Settings decryptPasswords(Settings settings) {

        File securitySettings = new File(DEFAULT_SETTINGS_SECURITY_PATH);
        String altSecuritySettings = SecurityActions.getProperty(ALT_SECURITY_SETTINGS_XML_LOCATION);
        String altSecuritySettingsDeprecated = SecurityActions.getProperty(ALT_SECURITY_SETTINGS_XML_LOCATION_DEPRECATED);

        // set alternate file
        if (altSecuritySettingsDeprecated != null && altSecuritySettingsDeprecated.length() > 0) {
            log.log(Level.WARNING,
                    "Maven settings-security.xml location ({0}) set via deprecated property \"{1}\", please use \"{2}\" instead",
                    new Object[] { altSecuritySettingsDeprecated, ALT_SECURITY_SETTINGS_XML_LOCATION_DEPRECATED,
                            ALT_SECURITY_SETTINGS_XML_LOCATION });
            securitySettings = new File(altSecuritySettingsDeprecated);
        }
        // set alternate file
        if (altSecuritySettings != null && altSecuritySettings.length() > 0) {
            securitySettings = new File(altSecuritySettings);
        }

        SettingsDecrypter decrypter = new MavenSettingsDecrypter(securitySettings);
        SettingsDecryptionRequest request = new DefaultSettingsDecryptionRequest(settings);
        SettingsDecryptionResult result = decrypter.decrypt(request);

        if (result.getProblems().size() > 0) {
            StringBuilder sb = new StringBuilder("Found ").append(result.getProblems().size())
                    .append(" problems while trying to decrypt settings configuration.");

            int counter = 1;
            for (SettingsProblem problem : result.getProblems()) {
                sb.append(counter++).append("/ ").append(problem).append("\n");
            }

            throw new InvalidConfigurationFileException(sb.toString());
        }

        settings.setProxies(result.getProxies());
        settings.setServers(result.getServers());

        return settings;
    }

    // adds local repository
    private Settings enrichWithLocalRepository(Settings settings) {

        // set default value if not set at all
        if (settings.getLocalRepository() == null || settings.getLocalRepository().length() == 0) {
            settings.setLocalRepository(DEFAULT_REPOSITORY_PATH);
        }

        // override any value with system property based location
        String altLocalRepository = SecurityActions.getProperty(ALT_LOCAL_REPOSITORY_LOCATION);
        if (altLocalRepository != null && altLocalRepository.length() > 0) {
            settings.setLocalRepository(altLocalRepository);
        }
        return settings;
    }

    // adds offline mode from system property
    private Settings enrichWithOfflineMode(Settings settings) {

        String goOffline = SecurityActions.getProperty(ALT_MAVEN_OFFLINE);
        if (goOffline != null && goOffline.length() > 0) {
            settings.setOffline(Boolean.valueOf(goOffline));
        }

        return settings;
    }
}
