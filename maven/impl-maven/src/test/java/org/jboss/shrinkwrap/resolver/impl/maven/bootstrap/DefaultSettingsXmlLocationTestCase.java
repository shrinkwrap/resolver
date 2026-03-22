/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtime;
import eu.maveniverse.maven.mima.context.Runtimes;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Verifies that default paths to maven settings.xml files are set by default.
 * See <a href="https://issues.redhat.com/browse/SHRINKRES-127">SHRINKRES-127</a> for more details.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class DefaultSettingsXmlLocationTestCase {

    @BeforeAll
    static void beforeClass() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION);
    }

    @Test
    void loadDefaultUserSettingsXmlLocation() {

        // user.home might not be set, so ignore test if that happens
        Assertions.assertNotNull(System.getProperty("user.home"));

        SettingsBuildingRequest request = createBuildingRequest();
        Assertions.assertNotNull(request, "BuildingRequest failed to setup settings.xml");
        Assertions.assertNotNull(request.getUserSettingsFile());

        Assertions.assertEquals(removeDoubledSeparator(request.getUserSettingsFile().getPath()),
                removeDoubledSeparator(System.getProperty("user.home") + "/.m2/settings.xml".replace('/', File.separatorChar)));
    }

    @Test
    void loadDefaultGlobalSettingsXmlLocation() {

        // M2_HOME is optional, so ignore test if that happens
        Assumptions.assumeTrue(System.getenv("M2_HOME") != null);
        SettingsBuildingRequest request = createBuildingRequest();
        Assertions.assertNotNull(request, "BuildingRequest failed to setup settings.xml");
        Assertions.assertNotNull(request.getGlobalSettingsFile());

        Assertions.assertEquals(removeDoubledSeparator(request.getGlobalSettingsFile().getPath()),
                removeDoubledSeparator(System.getenv("M2_HOME") + "/conf/settings.xml".replaceAll("//", "/").replace('/', File.separatorChar)));
    }

    private String removeDoubledSeparator(String path){
        return path.replace(File.separator + File.separator, File.separator);
    }

    // this is calling internal private method that handles logic of settings.xml setup
    private SettingsBuildingRequest createBuildingRequest() {
        try {
            MavenSettingsBuilder builder = new MavenSettingsBuilder(getSettingsDecrypter());
            Class<? extends MavenSettingsBuilder> clazz = builder.getClass();
            Method m = clazz.getDeclaredMethod("getDefaultSettingsBuildingRequest");
            m.setAccessible(true);
            return (SettingsBuildingRequest) m.invoke(builder);
        } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
            Assertions.fail("Unable to call getDefaultSettingsBuildingRequest via reflection, reason: " + e.getMessage());
        }

        return null;
    }

    static SettingsDecrypter getSettingsDecrypter() {
        Runtime runtime = Runtimes.INSTANCE.getRuntime();
        Context context = runtime.create(ContextOverrides.create().build());
        return context.lookup().lookup(SettingsDecrypter.class).get();
    }

}
