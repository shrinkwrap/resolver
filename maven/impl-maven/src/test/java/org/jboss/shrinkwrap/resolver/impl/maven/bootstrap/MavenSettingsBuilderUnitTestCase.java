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
package org.jboss.shrinkwrap.resolver.impl.maven.bootstrap;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests maven settings acquisition.
 *
 * @author Davide D'Alto
 *
 */
class MavenSettingsBuilderUnitTestCase {

    @BeforeEach
    void beforeMethod() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION,
                           "target/settings/profiles/settings-user.xml");
        System.setProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION,
                           "target/settings/profiles/settings-global.xml");
    }

    @AfterEach
    void afterMethod() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_SECURITY_SETTINGS_XML_LOCATION);

    }

    @Test
    void findUserProfile() {
        Settings mavenSettings = new MavenSettingsBuilder().buildDefaultSettings();
        Assertions.assertTrue(mavenSettings.getProfilesAsMap().containsKey("user-profile"), "Profile in user settings not found");
    }

    @Test
    void findGlobalProfile() {
        Settings mavenSettings = new MavenSettingsBuilder().buildDefaultSettings();
        Assertions.assertTrue(mavenSettings.getProfilesAsMap().containsKey("global-profile"), "Profile in global settings not found");
    }

    @Test
    void decryptEncryptedPassword() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION,
                "target/settings/profiles/settings-auth-encrypted.xml");
        System.setProperty(MavenSettingsBuilder.ALT_SECURITY_SETTINGS_XML_LOCATION,
                "target/settings/profiles/settings-security.xml");

        Settings mavenSettings = new MavenSettingsBuilder().buildDefaultSettings();

        Server server = mavenSettings.getServer("auth-repository");
        Assertions.assertNotNull(server, "Server auth-repository is not null");
        Assertions.assertEquals("shrinkwrap", server.getPassword(), "Password was decrypted to shrinkwrap");
    }

    @Test
    void missingSecuritySettingsNotNeeded() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION,
                           "target/settings/profiles/settings-auth.xml");
        System.setProperty(MavenSettingsBuilder.ALT_SECURITY_SETTINGS_XML_LOCATION,
                "target/settings/profiles/non-existing-settings-security.xml");

        Settings mavenSettings = new MavenSettingsBuilder().buildDefaultSettings();

        Server server = mavenSettings.getServer("auth-repository");
        Assertions.assertNotNull(server, "Server auth-repository is not null");
        Assertions.assertEquals("shrinkwrap", server.getPassword(), "Password was decrypted to shrinkwrap");
    }

    @Test
    void shouldRetrieveFirstNonNullString() {
        String value = MavenSettingsBuilder.getFirstNotNull(null, "shrinkwrap");
        Assertions.assertEquals("shrinkwrap", value);
    }

}
