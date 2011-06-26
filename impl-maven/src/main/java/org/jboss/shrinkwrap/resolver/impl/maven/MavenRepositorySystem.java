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
import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;

/**
 * Abstraction of the repository system for purposes of dependency resolution
 * used by Maven
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenRepositorySystem
{
   private final MavenRepositorySettings settings;

   private final RepositorySystem system;

   public MavenRepositorySystem(MavenRepositorySettings settings)
   {
      this.settings = settings;
      this.system = getRepositorySystem();
   }

   /**
    * Spawns a working session from the repository system. Working session is
    * shared between all Maven based commands
    * 
    * @param system A repository system
    * @param settings A configuration of current session, such as local or
    *           remote repositories and listeners
    * @return The working session for dependency resolution
    */
   public RepositorySystemSession getSession()
   {
      MavenRepositorySystemSession session = new MavenRepositorySystemSession();
      session.setLocalRepositoryManager(system.newLocalRepositoryManager(settings.getLocalRepository()));
      session.setTransferListener(settings.getTransferListener());
      session.setRepositoryListener(settings.getRepositoryListener());

      return session;
   }

   /**
    * Loads a POM file and updates settings both in current system and the
    * session. Namely remote repositories are updated using the settings found
    * in the POM file.
    * 
    * @param pom The POM file which contains either settings or a reference to a
    *           parent POM
    * @param session The session to be used to fetch possible parents
    * @return The model generated from the POM file
    * @throws ResolutionException If dependency resolution, such as retrieving
    *            an artifact parent fails
    */
   public Model loadPom(File pom, RepositorySystemSession session) throws ResolutionException
   {
      ModelBuildingRequest request = new DefaultModelBuildingRequest();
      request.setPomFile(pom);
      request.setModelResolver(new MavenModelResolver(system, session, getRemoteRepositories()));

      ModelBuilder builder = new DefaultModelBuilderFactory().newInstance();
      ModelBuildingResult result;
      try
      {
         result = builder.build(request);
      }
      // wrap exception message
      catch (ModelBuildingException e)
      {
         StringBuilder sb = new StringBuilder("Found ").append(e.getProblems().size()).append(" problems while building POM model from ").append(pom.getAbsolutePath());

         int counter = 1;
         for (ModelProblem problem : e.getProblems())
         {
            sb.append(counter++).append("/ ").append(problem).append("\n");
         }

         throw new ResolutionException(sb.toString(), e);
      }

      Model model = result.getEffectiveModel();
      settings.setRemoteRepositories(model);
      return model;
   }

   /**
    * Loads Maven settings and updates session settings
    * 
    * @param file The file which contains Maven settings
    * @param session The session to be updated apart from settings
    */
   public void loadSettings(File file, RepositorySystemSession session)
   {
      if (!(session instanceof MavenRepositorySystemSession))
      {
         throw new IllegalArgumentException("Cannot set local repository path for a Maven repository, expecting instance of " + MavenRepositorySystemSession.class.getName() + ", but got " + session.getClass().getName());
      }

      SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
      request.setUserSettingsFile(file);
      settings.buildSettings(request);
      ((MavenRepositorySystemSession) session).setLocalRepositoryManager(system.newLocalRepositoryManager(settings.getLocalRepository()));
   }

   /**
    * 
    * @return
    */
   public List<RemoteRepository> getRemoteRepositories()
   {
      return settings.getRemoteRepositories();
   }

   /**
    * Resolves artifact dependencies.
    * 
    * The {@see ArtifactResult} contains a reference to a file in Maven local
    * repository.
    * 
    * @param session The current Maven session
    * @param request The request to be computed
    * @param filter The filter of dependency results
    * @return A collection of artifacts which have built dependency tree from
    *         {@link request}
    * @throws DependencyCollectionException If a dependency could not be
    *            computed or collected
    * @throws ArtifactResolutionException If an artifact could not be fetched
    */
   public Collection<ArtifactResult> resolveDependencies(RepositorySystemSession session, CollectRequest request, MavenResolutionFilter filter) throws DependencyCollectionException, ArtifactResolutionException
   {
      return system.resolveDependencies(session, request, new MavenResolutionFilterWrap(filter));
   }

   /**
    * Resolves an artifact
    * 
    * @param session The current Maven session
    * @param request The request to be computed
    * @return The artifact
    * @throws ArtifactResolutionException If the artifact could not be fetched
    */
   public ArtifactResult resolveArtifact(RepositorySystemSession session, ArtifactRequest request) throws ArtifactResolutionException
   {
      return system.resolveArtifact(session, request);
   }

   public void setCentralRepositoryUsage(final boolean useCentralRepo)
   {
      if(settings != null)
      {
         settings.setCentralRepositoryUsage(useCentralRepo);
      }
   }

   /**
    * Finds a current implementation of repository system. A
    * {@link RepositorySystem} is an entry point to dependency resolution
    * 
    * @return A repository system
    */
   private RepositorySystem getRepositorySystem()
   {
      try
      {
         return new DefaultPlexusContainer().lookup(RepositorySystem.class);
      }
      catch (ComponentLookupException e)
      {
         throw new RuntimeException("Unable to lookup component RepositorySystem, cannot establish Aether dependency resolver.", e);
      }
      catch (PlexusContainerException e)
      {
         throw new RuntimeException("Unable to load RepositorySystem component by Plexus, cannot establish Aether dependency resolver.", e);
      }
   }

}

class MavenResolutionFilterWrap implements org.sonatype.aether.graph.DependencyFilter
{
   private MavenResolutionFilter delegate;

   public MavenResolutionFilterWrap(MavenResolutionFilter filter)
   {
      delegate = filter;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.sonatype.aether.graph.DependencyFilter#accept(org.sonatype.aether.
    * graph.DependencyNode, java.util.List)
    */
   public boolean accept(DependencyNode node, List<DependencyNode> parents)
   {
      Dependency dependency = node.getDependency();
      if (dependency == null)
      {
         return false;
      }

      return delegate.accept(MavenConverter.fromDependency(dependency));
   }

}
