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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.settings.Activation;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;

/**
 * Representation of Maven and resolver settings
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenDependencyResolverSettings
{
   private static final Logger log = Logger.getLogger(MavenDependencyResolverSettings.class.getName());

   // creates a link to Maven Central Repository
   private static final RemoteRepository MAVEN_CENTRAL = new RemoteRepository("central", "default",
         "http://repo1.maven.org/maven2");

   // Maven native settings object
   private Settings settings;

   // enable usage of central
   private boolean useMavenCentral = true;

   private Collection<RemoteRepository> modelRemoteRepositories = Collections.emptyList();

   /**
    * Creates settings with default values
    */
   public MavenDependencyResolverSettings()
   {
      this.settings = new MavenSettingsBuilder().buildDefaultSettings();
   }

   /**
    * Gets remote repositories. Collects ones activated via pom.xml, settings.xml and
    * then mirrors are applied
    * 
    * @return the effective list of repositories
    */
   @SuppressWarnings("unchecked")
   public List<RemoteRepository> getRemoteRepositories()
   {
      // disable repositories if working offline
      if (isOffline())
      {
         return Collections.emptyList();
      }

      List<String> actives = settings.getActiveProfiles();
      Set<RemoteRepository> enhancedRepos = new LinkedHashSet<RemoteRepository>();

      for (Map.Entry<String, Profile> profile : (Set<Map.Entry<String, Profile>>) settings.getProfilesAsMap().entrySet())
      {
         Activation activation = profile.getValue().getActivation();
         if (actives.contains(profile.getKey()) || (activation != null && activation.isActiveByDefault()))
         {
            for (org.apache.maven.settings.Repository repo : profile.getValue().getRepositories())
            {
               enhancedRepos.add(MavenConverter.asRemoteRepository(repo));
            }
         }
      }

      // add repositories from model
      enhancedRepos.addAll(modelRemoteRepositories);

      // add maven central if selected
      if (useMavenCentral)
      {
         enhancedRepos.add(MAVEN_CENTRAL);
      }

      if (settings.getMirrors().size() == 0)
      {
         return new ArrayList<RemoteRepository>(enhancedRepos);
      }

      // use mirrors if any to do the mirroring stuff
      DefaultMirrorSelector dms = new DefaultMirrorSelector();
      // fill in mirrors
      for (Mirror mirror : settings.getMirrors())
      {
         // Repository manager flag is set to false
         // Maven does not support specifying it in the settings.xml
         dms.add(mirror.getId(), mirror.getUrl(), mirror.getLayout(), false, mirror.getMirrorOf(), mirror.getMirrorOfLayouts());
      }

      Set<RemoteRepository> mirroredRepos = new LinkedHashSet<RemoteRepository>();
      for (RemoteRepository repository : enhancedRepos)
      {
         RemoteRepository mirror = dms.getMirror(repository);
         if (mirror != null)
         {
            mirroredRepos.add(mirror);
         }
         else
         {
            mirroredRepos.add(repository);
         }
      }

      return new ArrayList<RemoteRepository>(mirroredRepos);
   }

   /**
    * Sets a list of remote repositories using a POM model
    * 
    * @param model the POM model
    */
   public void setModelRemoteRepositories(Model model)
   {
      List<RemoteRepository> repositories = new ArrayList<RemoteRepository>();
      for (Repository repository : model.getRepositories())
      {
         repositories.add(MavenConverter.asRemoteRepository(repository));
      }

      this.modelRemoteRepositories = repositories;
   }

   // getters and setters

   /**
    * @return the settings
    */
   public Settings getSettings()
   {
      return settings;
   }

   /**
    * @param settings the settings to set
    */
   public void setSettings(Settings settings)
   {
      this.settings = settings;
   }

   /**
    * @return the useMavenCentral
    */
   public boolean isUseMavenCentral()
   {
      return useMavenCentral;
   }

   /**
    * @param useMavenCentral the useMavenCentral to set
    */
   public void setUseMavenCentral(boolean useMavenCentral)
   {
      this.useMavenCentral = useMavenCentral;
   }

   /**
    * @return the offline
    */
   public boolean isOffline()
   {
      return settings.isOffline();
   }

   /**
    * @param offline the offline to set
    */
   public void setOffline(boolean offline)
   {
      String goOffline = SecurityActions.getProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE);
      if (goOffline != null)
      {
         this.settings.setOffline(Boolean.valueOf(goOffline));
         if (log.isLoggable(Level.FINER))
         {
            log.finer("Offline settings is set via a system property. The new offline flag value is: "
                  + settings.isOffline());
         }

      }
      else
      {
         this.settings.setOffline(offline);
      }
   }

}
