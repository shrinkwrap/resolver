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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Verifies that default paths to maven settings.xml files are set by default.
 * See https://issues.jboss.org/browse/SHRINKRES-127 for more details.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class DefaultSettingsXmlLocationTestCase {

    @BeforeClass
    public static void beforeClass() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION);
    }

    @Test
    public void loadDefaultUserSettingsXmlLocation() {

        // user.home might not be set, so ignore test if that happens
        Assume.assumeThat(System.getProperty("user.home"), is(not(nullValue())));

        SettingsBuildingRequest request = createBuildingRequest();
        Assert.assertThat(request.getUserSettingsFile(), is(not(nullValue())));

        Assert.assertThat(request.getUserSettingsFile().getPath(),
                is(System.getProperty("user.home") + "/.m2/settings.xml".replaceAll("/", File.separator)));
    }

    @Test
    public void loadDefaultGlobalSettingsXmlLocation() {

        // M2_HOME is optional, so ignore test if that happens
        Assume.assumeThat(System.getenv("M2_HOME"), is(not(nullValue())));

        SettingsBuildingRequest request = createBuildingRequest();
        Assert.assertThat(request.getGlobalSettingsFile(), is(not(nullValue())));

        Assert.assertThat(request.getGlobalSettingsFile().getPath(),
                is(System.getenv("M2_HOME") + "/conf/settings.xml".replaceAll("/", File.separator)));
    }

    // this is calling internal private method that handles logic of settings.xml setup
    private SettingsBuildingRequest createBuildingRequest() {
        try {
            MavenSettingsBuilder builder = new MavenSettingsBuilder();
            Class<? extends MavenSettingsBuilder> clazz = builder.getClass();
            Method m = clazz.getDeclaredMethod("getDefaultSettingsBuildingRequest");
            m.setAccessible(true);
            return (SettingsBuildingRequest) m.invoke(builder);
        } catch (SecurityException e) {
            e.printStackTrace();
            Assert.fail("Unable to call getDefaultSettingsBuildingRequest via reflection, reason: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Assert.fail("Unable to call getDefaultSettingsBuildingRequest via reflection, reason: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.fail("Unable to call getDefaultSettingsBuildingRequest via reflection, reason: " + e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Assert.fail("Unable to call getDefaultSettingsBuildingRequest via reflection, reason: " + e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail("Unable to call getDefaultSettingsBuildingRequest via reflection, reason: " + e.getMessage());
        }

        return null;
    }

}
