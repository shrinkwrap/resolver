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
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import static junit.framework.Assert.assertTrue;
import static org.jboss.shrinkwrap.resolver.impl.maven.MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION;
import static org.jboss.shrinkwrap.resolver.impl.maven.MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION;

import org.apache.maven.settings.Settings;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests maven settings acquisition.
 *
 * @author Davide D'Alto
 *
 */
public class MavenSettingsBuilderUnitTestCase
{
   @BeforeClass
   public static void beforeClass()
   {
      System.setProperty(ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings-user.xml");
      System.setProperty(ALT_GLOBAL_SETTINGS_XML_LOCATION, "target/settings/profiles/settings-global.xml");
   }

   @AfterClass
   public static void afterClass()
   {
      System.clearProperty(ALT_USER_SETTINGS_XML_LOCATION);
      System.clearProperty(ALT_GLOBAL_SETTINGS_XML_LOCATION);
   }

   @Test
   public void testFindUserProfile() throws ResolutionException
   {
      Settings mavenSettings = new MavenSettingsBuilder().buildDefaultSettings();
      assertTrue("Profile in user settings not found", mavenSettings.getProfilesAsMap().containsKey("user-profile"));
   }

   @Test
   public void testFindGlobalProfile() throws ResolutionException
   {
      Settings mavenSettings = new MavenSettingsBuilder().buildDefaultSettings();
      assertTrue("Profile in global settings not found", mavenSettings.getProfilesAsMap().containsKey("global-profile"));
   }
}
