/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;

/**
 * Tests resolution of the artifacts without enabling any remote repository
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class OfflineRepositoryTestCase
{

   /**
    * Goes offline from settings.xml
    *
    * @throws Exception
    */
   @Test
   public void searchJunitOnOffineSettingsTest() throws Exception
   {
      try
      {
         DependencyResolvers.use(MavenDependencyResolver.class)
               .configureFrom("target/settings/profiles/settings-offline.xml")
               .artifact("junit:junit:3.8.2").resolveAsFiles();
      }
      catch (ResolutionException e)
      {
         Assert.assertTrue("Unable to resolve an artifact", e.getMessage().startsWith("Unable to resolve an artifact"));
      }
   }

   /**
    * Goes offline if specified by user
    *
    * @throws Exception
    */
   @Test
   public void searchJunitOnOffineProgrammaticTest() throws Exception
   {

      try
      {
         DependencyResolvers.use(MavenDependencyResolver.class)
               .configureFrom("target/settings/profiles/settings.xml")
               .goOffline()
               .artifact("junit:junit:3.8.2").resolveAsFiles();
         Assert.fail("Artifact junit:junit:3.8.2 is not present in local repository");
      }
      catch (ResolutionException e)
      {
         Assert.assertTrue("Unable to resolve an artifact", e.getMessage().startsWith("Unable to resolve an artifact"));
      }
   }

   /**
    * Goes offline if specified by system property
    *
    * @throws Exception
    */
   @Test
   public void searchJunitOnOffinePropertyTest() throws Exception
   {
      System.setProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE, "true");

      try
      {
         DependencyResolvers.use(MavenDependencyResolver.class)
               .configureFrom("target/settings/profiles/settings.xml")
               .artifact("junit:junit:3.8.2").resolveAsFiles();
         Assert.fail("Artifact junit:junit:3.8.2 is not present in local repository");
      }
      catch (ResolutionException e)
      {
         Assert.assertTrue("Unable to resolve an artifact", e.getMessage().startsWith("Unable to resolve an artifact"));
      }

      System.clearProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE);
   }

}
