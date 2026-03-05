/*
 * JBoss, Home of Professional Open Source
 * Copyright 2026, Red Hat Inc., and individual contributors
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

import org.apache.maven.settings.Settings;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidEnvironmentException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsFromPluginTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ConfigureSettingsFromPluginTask}
 *
 *  NOTE: This test case is in the main package because of the visibility of the method
 *  {@link ConfigurableMavenWorkingSessionImpl#getSettings()}.
 *
 * @author <a href="mailto:pberan@redhat.com">Petr Beran</a>
 */
class ConfigureSettingsFromPluginTaskTestCase {

    private static final String POM_FILE_KEY = "maven.execution.pom-file";
    private static final String OFFLINE_KEY = "maven.execution.offline";
    private static final String USER_SETTINGS_KEY = "maven.execution.user-settings";
    private static final String GLOBAL_SETTINGS_KEY = "maven.execution.global-settings";
    private static final String ACTIVE_PROFILES_KEY = "maven.execution.active-profiles";

    private static final String TEST_POM_PATH = "target/test-classes/poms/test-parent.xml";
    private static final String USER_SETTINGS_PATH = "target/test-classes/profiles/settings-user.xml";
    private static final String GLOBAL_SETTINGS_PATH = "target/test-classes/profiles/settings-global.xml";

    @BeforeEach
    void setUp() {
        clearProperties();
    }

    @AfterEach
    void tearDown() {
        clearProperties();
    }

    private void clearProperties() {
        System.clearProperty(POM_FILE_KEY);
        System.clearProperty(OFFLINE_KEY);
        System.clearProperty(USER_SETTINGS_KEY);
        System.clearProperty(GLOBAL_SETTINGS_KEY);
        System.clearProperty(ACTIVE_PROFILES_KEY);
    }

    private void setRequiredProperties() {
        System.setProperty(POM_FILE_KEY, TEST_POM_PATH);
        System.setProperty(OFFLINE_KEY, "false");
        System.setProperty(USER_SETTINGS_KEY, USER_SETTINGS_PATH);
        System.setProperty(GLOBAL_SETTINGS_KEY, GLOBAL_SETTINGS_PATH);
        System.setProperty(ACTIVE_PROFILES_KEY, "");
    }

    /**
     * Tests that global settings file is loaded and contains the expected profile
     */
    @Test
    void testGlobalSettingsAreLoaded() {
        setRequiredProperties();

        MavenWorkingSession session = executeConfigureSettingsTask();

        Settings settings = ((ConfigurableMavenWorkingSessionImpl) session).getSettings();
        Assertions.assertNotNull(settings, "Settings should not be null");
        Assertions.assertTrue(settings.getProfilesAsMap().containsKey("global-profile"),
                "Global settings should contain 'global-profile'");
        Assertions.assertNotNull(settings.getProfilesAsMap().get("global-profile").getProperties(),
                "Global profile should have properties");
        Assertions.assertEquals("global-value",
                settings.getProfilesAsMap().get("global-profile").getProperties().get("globalProperty"),
                "Global profile should have globalProperty=global-value");
    }

    /**
     * Tests that user settings file is loaded and contains the expected profile
     */
    @Test
    void testUserSettingsAreLoaded() {
        setRequiredProperties();

        MavenWorkingSession session = executeConfigureSettingsTask();

        Settings settings = ((ConfigurableMavenWorkingSessionImpl) session).getSettings();
        Assertions.assertNotNull(settings, "Settings should not be null");
        Assertions.assertTrue(settings.getProfilesAsMap().containsKey("user-profile"),
                "User settings should contain 'user-profile'");
        Assertions.assertNotNull(settings.getProfilesAsMap().get("user-profile").getProperties(),
                "User profile should have properties");
        Assertions.assertEquals("user-value",
                settings.getProfilesAsMap().get("user-profile").getProperties().get("userProperty"),
                "User profile should have userProperty=user-value");
    }

    /**
     * Tests that both global and user settings are loaded together.
     */
    @Test
    void testGlobalAndUserSettingsAreLoaded() {
        setRequiredProperties();

        MavenWorkingSession session = executeConfigureSettingsTask();

        Settings settings = ((ConfigurableMavenWorkingSessionImpl) session).getSettings();
        Assertions.assertNotNull(settings, "Settings should not be null");
        Assertions.assertTrue(settings.getProfilesAsMap().containsKey("global-profile"),
                "Settings should contain global profile");
        Assertions.assertTrue(settings.getProfilesAsMap().containsKey("user-profile"),
                "Settings should contain user profile");
        Assertions.assertEquals(2, settings.getProfilesAsMap().size(),
                "Settings should contain exactly 2 profiles (global-profile and user-profile)");
        Assertions.assertEquals("global-value",
                settings.getProfilesAsMap().get("global-profile").getProperties().get("globalProperty"),
                "Global profile should have property from global settings");
        Assertions.assertEquals("user-value",
                settings.getProfilesAsMap().get("user-profile").getProperties().get("userProperty"),
                "User profile should have property from user settings");
    }

    /**
     * Tests that the exception is thrown when global settings property is missing
     */
    @Test
    void testMissingGlobalSettingsPropertyThrowsException() {
        System.setProperty(POM_FILE_KEY, TEST_POM_PATH);
        System.setProperty(OFFLINE_KEY, "false");
        System.setProperty(USER_SETTINGS_KEY, USER_SETTINGS_PATH);
        // Intentionally not setting GLOBAL_SETTINGS_KEY
        System.setProperty(ACTIVE_PROFILES_KEY, "");

        MavenResolverSystem resolver = Resolvers.use(MavenResolverSystem.class);
        MavenWorkingSession session = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession();

        Assertions.assertThrows(InvalidEnvironmentException.class, () ->
                ConfigureSettingsFromPluginTask.INSTANCE.execute(session),
                "Should throw InvalidEnvironmentException when global settings property is missing");
    }

    /**
     * Tests that the exception is thrown when user settings property is missing
     */
    @Test
    void testMissingUserSettingsPropertyThrowsException() {
        System.setProperty(POM_FILE_KEY, TEST_POM_PATH);
        System.setProperty(OFFLINE_KEY, "false");
        // Intentionally not setting USER_SETTINGS_KEY
        System.setProperty(GLOBAL_SETTINGS_KEY, GLOBAL_SETTINGS_PATH);
        System.setProperty(ACTIVE_PROFILES_KEY, "");

        MavenResolverSystem resolver = Resolvers.use(MavenResolverSystem.class);
        MavenWorkingSession session = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession();

        Assertions.assertThrows(InvalidEnvironmentException.class, () ->
                ConfigureSettingsFromPluginTask.INSTANCE.execute(session),
                "Should throw InvalidEnvironmentException when user settings property is missing");
    }

    /**
     * Tests proper handling of non-existent global settings file
     */
    @Test
    void testNonExistentGlobalSettingsFile() {
        System.setProperty(POM_FILE_KEY, TEST_POM_PATH);
        System.setProperty(OFFLINE_KEY, "false");
        System.setProperty(USER_SETTINGS_KEY, USER_SETTINGS_PATH);
        System.setProperty(GLOBAL_SETTINGS_KEY, "target/test-classes/profiles/non-existent-settings.xml");
        System.setProperty(ACTIVE_PROFILES_KEY, "");

        MavenWorkingSession session = executeConfigureSettingsTask();

        Settings settings = ((ConfigurableMavenWorkingSessionImpl) session).getSettings();
        Assertions.assertNotNull(settings, "Settings should not be null");
        Assertions.assertTrue(settings.getProfilesAsMap().containsKey("user-profile"),
                "User settings should still be loaded");
        Assertions.assertFalse(settings.getProfilesAsMap().containsKey("global-profile"),
                "Global profile should not be present since the file doesn't exist");
        Assertions.assertEquals("user-value",
                settings.getProfilesAsMap().get("user-profile").getProperties().get("userProperty"),
                "User profile should have property from user settings");
    }

    /**
     * Tests proper handling of non-existent user settings file
     */
    @Test
    void testNonExistentUserSettingsFile() {
        System.setProperty(POM_FILE_KEY, TEST_POM_PATH);
        System.setProperty(OFFLINE_KEY, "false");
        System.setProperty(USER_SETTINGS_KEY, "target/test-classes/profiles/non-existent-settings.xml");
        System.setProperty(GLOBAL_SETTINGS_KEY, GLOBAL_SETTINGS_PATH);
        System.setProperty(ACTIVE_PROFILES_KEY, "");

        MavenWorkingSession session = executeConfigureSettingsTask();

        Settings settings = ((ConfigurableMavenWorkingSessionImpl) session).getSettings();
        Assertions.assertNotNull(settings, "Settings should not be null");
        Assertions.assertTrue(settings.getProfilesAsMap().containsKey("global-profile"),
                "Global settings should still be loaded");
        Assertions.assertFalse(settings.getProfilesAsMap().containsKey("user-profile"),
                "User profile should not be present since the file doesn't exist");
        Assertions.assertEquals("global-value",
                settings.getProfilesAsMap().get("global-profile").getProperties().get("globalProperty"),
                "Global profile should have property from global settings");
    }

    /**
     * Smoke test that the POM file is handled correctly
     */
    @Test
    void testPomFileIsLoaded() {
        setRequiredProperties();

        MavenWorkingSession session = executeConfigureSettingsTask();

        Assertions.assertNotNull(session.getParsedPomFile(), "Parsed POM file should be loaded");
        Assertions.assertNotNull(session.getParsedPomFile().getGroupId(), "POM should have a group ID");
    }

    private MavenWorkingSession executeConfigureSettingsTask() {
        MavenResolverSystem resolver = Resolvers.use(MavenResolverSystem.class);
        MavenWorkingSession session = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession();
        return ConfigureSettingsFromPluginTask.INSTANCE.execute(session);
    }
}
