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
package org.jboss.shrinkwrap.resolver.maven.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Resolves an artifact even from remote repository during resolution of the model.
 * 
 * The repositories are added to the resolution chain as found during processing
 * of the POM file. Repository is added only if there is no other repository with
 * same id already defined.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class MavenModelResolver implements ModelResolver
{
   private List<RemoteRepository> repositories;
   private Set<String> repositoryIds;

   private RepositorySystem system;
   private RepositorySystemSession session;

   /**
    * Creates a new Maven repository resolver. This resolver uses service available to Maven
    * to create an artifact resolution chain
    * @param system the Maven based implementation of the {@link RepositorySystem}
    * @param session the current Maven execution session
    * @param remotes Currently available remote repositories
    */
   public MavenModelResolver(RepositorySystem system, RepositorySystemSession session, List<RemoteRepository> remotes)
   {
      this.system = system;
      this.session = session;
      this.repositories = new ArrayList<RemoteRepository>(remotes.size());
      this.repositoryIds = new HashSet<String>(remotes.size());
      for (RemoteRepository remote : remotes)
      {
         repositoryIds.add(remote.getId());
         repositories.add(remote);
      }
   }

   // a cloning constructor
   private MavenModelResolver(MavenModelResolver clone)
   {
      this.system = clone.system;
      this.session = clone.session;
      this.repositories = new ArrayList<RemoteRepository>(clone.repositories);
      this.repositoryIds = new HashSet<String>(clone.repositoryIds);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.apache.maven.model.resolution.ModelResolver#addRepository(org.apache.maven.model.Repository)
    */
   public void addRepository(Repository repository) throws InvalidRepositoryException
   {
      if (repositoryIds.contains(repository.getId()))
      {
         return;
      }

      repositoryIds.add(repository.getId());
      repositories.add(MavenConverter.asRemoteRepository(repository));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.apache.maven.model.resolution.ModelResolver#newCopy()
    */
   public ModelResolver newCopy()
   {
      return new MavenModelResolver(this);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.apache.maven.model.resolution.ModelResolver#resolveModel(java.lang.String, java.lang.String, java.lang.String)
    */
   public ModelSource resolveModel(String groupId, String artifactId, String version) throws UnresolvableModelException
   {
      Artifact pomArtifact = new DefaultArtifact(groupId, artifactId, "", "pom", version);
      try
      {
         ArtifactRequest request = new ArtifactRequest(pomArtifact, repositories, null);
         pomArtifact = system.resolveArtifact(session, request).getArtifact();

      }
      catch (ArtifactResolutionException e)
      {
         throw new UnresolvableModelException("Failed to resolve POM for " + groupId + ":" + artifactId + ":"
                      + version + " due to " + e.getMessage(), groupId, artifactId, version, e);
      }

      File pomFile = pomArtifact.getFile();

      return new FileModelSource(pomFile);

   }
}
