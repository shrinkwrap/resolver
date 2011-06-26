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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.settings.Activation;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.sonatype.aether.RepositoryListener;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.transfer.TransferListener;

/**
 * An encapsulation of settings required to be handle Maven dependency
 * resolution.
 * 
 * It holds links to local and remote repositories.
 * 
 * Maven can be configured externally, using following properties:
 * 
 * <ul>
 * <li>{@see MavenRepositorySettings.ALT_USER_SETTINGS_XML_LOCATION} - a path to
 * local settings.xml file</li>
 * <li>{@see MavenRepositorySettings.ALT_GLOBAL_SETTINGS_XML_LOCATION} - a path
 * to global settings.xml file</li>
 * <li>{@see MavenRepositorySettings.ALT_LOCAL_REPOSITORY_LOCATION} - a path to
 * local repository</li>
 * </ul>
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenRepositorySettings
{
   /**
    * Sets an alternate location to Maven user settings.xml configuration
    */
   public static final String ALT_USER_SETTINGS_XML_LOCATION = "org.apache.maven.user-settings";

   /**
    * Sets an alternate location of Maven global settings.xml configuration
    */
   public static final String ALT_GLOBAL_SETTINGS_XML_LOCATION = "org.apache.maven.global-settings";

   /**
    * Sets an alternate location of Maven local repository
    */
   public static final String ALT_LOCAL_REPOSITORY_LOCATION = "maven.repo.local";

   private static final String DEFAULT_USER_SETTINGS_PATH = SecurityActions.getProperty("user.home").concat("/.m2/settings.xml");
   private static final String DEFAULT_REPOSITORY_PATH = SecurityActions.getProperty("user.home").concat("/.m2/repository");

   static final boolean DEFAULT_MAVEN_CENTRAL_USAGE = true;

   private boolean centralRepositoryUsage = MavenRepositorySettings.DEFAULT_MAVEN_CENTRAL_USAGE;

   private List<RemoteRepository> repositories;

   // settings object
   private Settings settings;

   /**
    * Creates a new Maven settings using default user settings, that is the one
    * located in ${user.home}/.m2/settings.xml.
    * 
    * Appends Maven Central repository to available remote repositories.
    * 
    * The file is used to track local Maven repository.
    */
   public MavenRepositorySettings()
   {
      SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

      String altUserSettings = SecurityActions.getProperty(ALT_USER_SETTINGS_XML_LOCATION);
      String altGlobalSettings = SecurityActions.getProperty(ALT_GLOBAL_SETTINGS_XML_LOCATION);

      request.setUserSettingsFile(new File(DEFAULT_USER_SETTINGS_PATH));

      // set alternate files
      if (altUserSettings != null && altUserSettings.length() > 0)
      {
         request.setUserSettingsFile(new File(altUserSettings));
      }

      if (altGlobalSettings != null && altGlobalSettings.length() > 0)
      {
         request.setUserSettingsFile(new File(altGlobalSettings));
      }

      buildSettings(request);
   }

   /**
    * Sets a list of remote repositories using a POM model. Maven Central
    * repository and repositories from Maven settings.xml file are always added
    * even if they are not explicitly listed in the the model.
    * 
    * @param model the POM model
    */
   public void setRemoteRepositories(Model model)
   {
      List<RemoteRepository> newRepositories = new ArrayList<RemoteRepository>();
      newRepositories.addAll(settingsRepositories(isUsingCentralRepository()));
      for (Repository repository : model.getRepositories())
      {
         newRepositories.add(MavenConverter.asRemoteRepository(repository));
      }

      this.repositories = newRepositories;
   }

   /**
    * Returns a list of available remote repositories
    * 
    * @return The list of remote repositories
    */
   public List<RemoteRepository> getRemoteRepositories()
   {
      return repositories;
   }

   /**
    * Returns a local repository determined from settings.xml or the default
    * repository located
    * 
    * @return The local repository
    */
   public LocalRepository getLocalRepository()
   {
      return new LocalRepository(settings.getLocalRepository());
   }

   /**
    * Returns a listener which captures repository based events, such as an
    * attempt to download from a repository and similar events.
    * 
    * @return The {@link RepositoryListener} implementation
    */
   public RepositoryListener getRepositoryListener()
   {
      return new LogRepositoryListener();
   }

   /**
    * Returns a listener which captures transfer based events, such as a
    * download progress and similar events.
    * 
    * @return The {@link TransferListener} implementation
    */
   public TransferListener getTransferListener()
   {
      return new LogTransferListerer();
   }

   /**
    * Replaces currents settings with ones retrieved from request.
    * 
    * The list of remote repositories is not affected.
    * 
    * @param request The request for new settings
    */
   public void buildSettings(SettingsBuildingRequest request)
   {

      SettingsBuildingResult result;
      try
      {
         SettingsBuilder builder = new DefaultSettingsBuilderFactory().newInstance();
         result = builder.build(request);
      }
      catch (SettingsBuildingException e)
      {
         e.printStackTrace();
         throw new RuntimeException("Unable to parse Maven configuration", e);
      }

      Settings settings = result.getEffectiveSettings();

      // set local repository path if no other was set
      if (settings.getLocalRepository() == null)
      {
         String altLocalRepository = SecurityActions.getProperty(ALT_LOCAL_REPOSITORY_LOCATION);

         settings.setLocalRepository(DEFAULT_REPOSITORY_PATH);

         if (altLocalRepository != null && altLocalRepository.length() > 0)
         {
            settings.setLocalRepository(altLocalRepository);
         }

      }
      this.settings = settings;
      this.repositories = settingsRepositories(isUsingCentralRepository());
   }

   // creates links to Repositories from settings.xml file
   @SuppressWarnings("unchecked")
   private List<RemoteRepository> settingsRepositories(final boolean addCentral)
   {
      List<String> actives = settings.getActiveProfiles();
      List<RemoteRepository> settingsRepos = new ArrayList<RemoteRepository>();
      if(addCentral)
      {
         settingsRepos.add(centralRepository());
      }
      for (Map.Entry<String, Profile> profile : (Set<Map.Entry<String, Profile>>) settings.getProfilesAsMap().entrySet())
      {
         Activation activation = profile.getValue().getActivation();
         if (actives.contains(profile.getKey()) || (activation != null && activation.isActiveByDefault()))
         {
            for (org.apache.maven.settings.Repository repo : profile.getValue().getRepositories())
            {
               settingsRepos.add(MavenConverter.asRemoteRepository(repo));
            }
         }
      }

      return settingsRepos;

   }

   public boolean isUsingCentralRepository()
   {
      return centralRepositoryUsage;
   }

   public void setCentralRepositoryUsage(final boolean centralRepositoryUsage)
   {
      this.centralRepositoryUsage = centralRepositoryUsage;
   }

   // creates a link to Maven Central Repository
   private RemoteRepository centralRepository()
   {
      return new RemoteRepository("central", "default", "http://repo1.maven.org/maven2");
   }
}
