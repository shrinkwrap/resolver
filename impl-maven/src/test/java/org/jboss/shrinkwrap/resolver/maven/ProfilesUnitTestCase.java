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
package org.jboss.shrinkwrap.resolver.maven;

import java.io.File;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenRepositorySettings;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.StrictFilter;
import org.junit.Test;

/**
 * Excercise parsing of Maven profiles
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class ProfilesUnitTestCase
{
   /**
    * Tests a resolution of an artifact from JBoss repository specified in
    * settings.xml as active profile
    * 
    * @throws ResolutionException
    */
   @Test
   public void testSettingsProfiles() throws ResolutionException
   {
      File[] files = MavenResolver
            .configureFrom("target/settings/profiles/settings.xml")
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
            .resolveAsFiles(new StrictFilter());

      Assert.assertEquals("There is only one jar in the package", 1, files.length);
      Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", files[0].getName());
   }

   /**
    * Tests a resolution of an artifact from JBoss repository specified in
    * settings.xml within activeProfiles
    * 
    * @throws ResolutionException
    */
   @Test
   public void testSettingsProfiles2() throws ResolutionException
   {
      File[] files = MavenResolver
            .configureFrom("target/settings/profiles/settings2.xml")
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
            .resolveAsFiles(new StrictFilter());

      Assert.assertEquals("There is only one jar in the package", 1, files.length);
      Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", files[0].getName());
   }

   /**
    * Tests a resolution of an artifact from JBoss repository specified in
    * settings.xml within activeProfiles. The path to do file is defined via
    * system property.
    * 
    * @throws ResolutionException
    */
   @Test
   public void testSystemPropertiesSettingsProfiles() throws ResolutionException
   {
      System.setProperty(MavenRepositorySettings.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings3.xml");
      System.setProperty(MavenRepositorySettings.ALT_LOCAL_REPOSITORY_LOCATION, "target/prop-profiles");

      File[] files = MavenResolver
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
            .resolveAsFiles(new StrictFilter());

      Assert.assertEquals("There is only one jar in the package", 1, files.length);
      Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", files[0].getName());
   }

}
