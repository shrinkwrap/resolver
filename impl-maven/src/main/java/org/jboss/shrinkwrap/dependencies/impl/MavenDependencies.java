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
package org.jboss.shrinkwrap.dependencies.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.dependencies.DependencyBuilder;
import org.jboss.shrinkwrap.dependencies.DependencyException;
import org.jboss.shrinkwrap.dependencies.DependencyFilter;
import org.jboss.shrinkwrap.dependencies.impl.filter.AcceptAllFilter;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * A default implementation of dependency builder based on Maven.
 * 
 * Apart from contract, it allows to load Maven settings from an
 * XML file, configure remote repositories from an POM file and retrieve
 * dependencies defined in a POM file, including ones in POM parents.
 * 
 * Maven can be configured externally, using following properties:
 * 
 * <ul>
 *    <li>{@see MavenRepositorySettings.ALT_USER_SETTINGS_XML_LOCATION} - a path to local settings.xml file</li>
 *    <li>{@see MavenRepositorySettings.ALT_GLOBAL_SETTINGS_XML_LOCATION} - a path to global settings.xml file</li>
 *    <li>{@see MavenRepositorySettings.ALT_LOCAL_REPOSITORY_LOCATION} - a path to local repository</li>
 * </ul>
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @see MavenRepositorySettings
 */
public class MavenDependencies implements DependencyBuilder<MavenDependencies>
{
   private static final Logger log = Logger.getLogger(MavenDependencies.class.getName());

   private static final Archive<?>[] ARCHIVE_CAST = new Archive<?>[0];
   private static final File[] FILE_CAST = new File[0];

   private static final DependencyFilter<MavenDependencies> ACCEPT_ALL = new AcceptAllFilter();

   private MavenRepositorySystem system;

   private RepositorySystemSession session;

   // these are package visible, so they can be wrapped and make visible for filters
   Stack<Dependency> dependencies;
   Map<ArtifactAsKey, Dependency> pomInternalDependencyManagement;

   /**
    * Constructs new instance of MavenDependencies
    */
   public MavenDependencies()
   {
      this.system = new MavenRepositorySystem(new MavenRepositorySettings());
      this.dependencies = new Stack<Dependency>();
      this.pomInternalDependencyManagement = new HashMap<ArtifactAsKey, Dependency>();
      this.session = system.getSession();
   }

   /**
    * Configures Maven from a settings.xml file
    * @param path A path to a settings.xml configuration file
    * @return A dependency builder with a configuration from given file
    */
   public MavenDependencies configureFrom(String path)
   {
      Validate.readable(path, "Path to the settings.xml must be defined and accessible");
      File settings = new File(path);
      system.loadSettings(settings, session);
      return this;
   }

   /**
    * Loads remote repositories for a POM file. If repositories are
    * defined in the parent of the POM file and there are accessible
    * via local file system, they are set as well.
    * 
    * These remote repositories are used to resolve the
    * artifacts during dependency resolution.
    * 
    * Additionally, it loads dependencies defined in the POM file model
    * in an internal cache, which can be later used to resolve an artifact
    * without explicitly specifying its version.
    * 
    * @param path A path to the POM file, must not be {@code null} or empty
    * @return A dependency builder with remote repositories set according
    *         to the content of POM file.
    * @throws Exception
    */
   public MavenDependencies loadPom(String path) throws DependencyException
   {
      Validate.readable(path, "Path to the pom.xml file must be defined and accessible");

      File pom = new File(path);
      Model model = system.loadPom(pom, session);

      ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

      // store all dependency information to be able to retrieve versions later
      for (org.apache.maven.model.Dependency dependency : model.getDependencies())
      {
         Dependency d = MavenConverter.convert(dependency, stereotypes);
         pomInternalDependencyManagement.put(new ArtifactAsKey(d.getArtifact()), d);
      }

      return this;
   }

   /**
    * Uses dependencies and remote repositories defined in a POM file to and
    * tries to resolve them
    * @param path A path to the POM file
    * @return An array of ShrinkWrap archives
    * @throws DependencyException If dependencies could not be resolved or the
    *         POM processing failed
    */
   public Archive<?>[] resolveFrom(String path) throws DependencyException
   {
      return resolveFrom(path, ACCEPT_ALL);
   }

   public Archive<?>[] resolveFrom(String path, DependencyFilter<MavenDependencies> filter) throws DependencyException
   {
      Validate.readable(path, "Path to the pom.xml file must be defined and accessible");
      File pom = new File(path);
      Model model = system.loadPom(pom, session);

      ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

      // wrap from Maven to Aether
      for (org.apache.maven.model.Dependency dependency : model.getDependencies())
      {
         dependencies.push(MavenConverter.convert(dependency, stereotypes));
      }
      return new MavenArtifactBuilder().resolve(filter);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang.String)
    */
   public MavenArtifactBuilder artifact(String coordinates) throws DependencyException
   {
      Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

      return new MavenArtifactBuilder(coordinates);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang.String)
    */
   public MavenArtifactsBuilder artifacts(String... coordinates) throws DependencyException
   {
      Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

      return new MavenArtifactsBuilder(coordinates);
   }

   public class MavenArtifactBuilder implements DependencyBuilder.ArtifactBuilder<MavenDependencies>
   {

      private Artifact artifact;

      protected List<Exclusion> exclusions = new ArrayList<Exclusion>();

      protected String scope;

      protected boolean optional = false;

      public MavenArtifactBuilder(String coordinates) throws DependencyException
      {
         try
         {
            coordinates = MavenConverter.resolveArtifactVersion(pomInternalDependencyManagement, coordinates);
            this.artifact = new DefaultArtifact(coordinates);

            Dependency dependency = new Dependency(artifact, scope, optional, exclusions);
            dependencies.push(dependency);
         }
         catch (IllegalArgumentException e)
         {
            throw new DependencyException("Unable to create artifact from coordinates " + coordinates + ", " +
                  "they are either invalid or version information was not specified in loaded POM file (maybe the POM file wasn't load at all)", e);
         }
      }

      // used for resolution from pom.xml only or for inheritance
      private MavenArtifactBuilder()
      {
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#exclusion(org.sonatype.aether.graph.Exclusion)
       */
      public MavenArtifactBuilder exclusion(String coordinates)
      {
         Dependency dependency = dependencies.pop();
         this.exclusions.add(MavenConverter.convertExclusion(coordinates));
         dependencies.push(dependency.setExclusions(this.exclusions));

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#exclusions(org.sonatype.aether.graph.Exclusion[])
       */
      public MavenArtifactBuilder exclusions(String... coordinates)
      {
         Dependency dependency = dependencies.pop();
         this.exclusions.addAll(MavenConverter.convertExclusions(Arrays.asList(coordinates)));
         dependencies.push(dependency.setExclusions(this.exclusions));

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#exclusions(java.util.Collection)
       */
      public MavenArtifactBuilder exclusions(Collection<String> coordinates)
      {
         Dependency dependency = dependencies.pop();
         this.exclusions.addAll(MavenConverter.convertExclusions(coordinates));
         dependencies.push(dependency.setExclusions(this.exclusions));

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#optional(boolean)
       */
      public MavenArtifactBuilder optional(boolean optional)
      {
         Dependency dependency = dependencies.pop();
         this.optional = optional;
         dependencies.push(dependency.setOptional(optional));

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#scope(java.lang.String)
       */
      public MavenArtifactBuilder scope(String scope)
      {
         Dependency dependency = dependencies.pop();
         this.scope = scope;
         dependencies.push(dependency.setScope(scope));

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#resolve()
       */
      public Archive<?>[] resolve() throws DependencyException
      {
         return resolve(ACCEPT_ALL);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#resolve(org.sonatype.aether.graph.DependencyFilter)
       */
      public Archive<?>[] resolve(DependencyFilter<MavenDependencies> filter) throws DependencyException
      {
         File[] files = resolveAsFiles(filter);
         Collection<Archive<?>> archives = new ArrayList<Archive<?>>(files.length);
         for (File file : files)
         {
            Archive<?> archive = ShrinkWrap.create(JavaArchive.class, file.getName()).as(ZipImporter.class).importFrom(convert(file)).as(JavaArchive.class);
            archives.add(archive);
         }

         return archives.toArray(ARCHIVE_CAST);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang.String)
       */
      public MavenArtifactBuilder artifact(String coordinates)
      {
         Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");
         return new MavenArtifactBuilder(coordinates);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifacts(java.lang.String[])
       */
      public MavenArtifactsBuilder artifacts(String... coordinates) throws DependencyException
      {
         Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");
         return new MavenArtifactsBuilder(coordinates);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#resolveAsFiles()
       */
      public File[] resolveAsFiles() throws DependencyException
      {
         return resolveAsFiles(ACCEPT_ALL);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#resolveAsFiles()
       */
      public File[] resolveAsFiles(DependencyFilter<MavenDependencies> filter) throws DependencyException
      {
         Validate.notEmpty(dependencies, "No dependencies were set for resolution");

         // configure filter to have access to properties set in the parent class
         filter.configure(MavenDependencies.this);

         CollectRequest request = new CollectRequest(dependencies, null, system.getRemoteRepositories());

         // wrap artifact files to archives
         Collection<ArtifactResult> artifacts;
         try
         {
            artifacts = system.resolveDependencies(session, request, filter);
         }
         catch (DependencyCollectionException e)
         {
            throw new DependencyException("Unable to collect dependeny tree for a resolution", e);
         }
         catch (ArtifactResolutionException e)
         {
            throw new DependencyException("Unable to resolve an artifact", e);
         }

         Collection<File> files = new ArrayList<File>(artifacts.size());
         for (ArtifactResult artifact : artifacts)
         {
            Artifact a = artifact.getArtifact();
            // skip all non-jar artifacts
            if (!"jar".equals(a.getExtension()))
            {
               log.info("Removed non-JAR artifact " + a.toString() + " from archive, it's dependencies were fetched");
               continue;
            }

            files.add(a.getFile());
         }

         return files.toArray(FILE_CAST);
      }

      // converts a file to a ZIP file
      private ZipFile convert(File file) throws DependencyException
      {
         try
         {
            return new ZipFile(file);
         }
         catch (ZipException e)
         {
            throw new DependencyException("Unable to treat dependecy artifact \"" + file.getAbsolutePath() + "\" as a ZIP file", e);
         }
         catch (IOException e)
         {
            throw new DependencyException("Unable to access artifact file at \"" + file.getAbsolutePath() + "\".");
         }
      }
   }

   public class MavenArtifactsBuilder extends MavenArtifactBuilder implements DependencyBuilder.ArtifactsBuilder<MavenDependencies>
   {

      private List<Artifact> artifacts = new ArrayList<Artifact>();

      public MavenArtifactsBuilder(String... coordinates)
      {
         for (String coords : coordinates)
         {
            try
            {
               coords = MavenConverter.resolveArtifactVersion(pomInternalDependencyManagement, coords);
               Artifact artifact = new DefaultArtifact(coords);
               artifacts.add(artifact);

               Dependency dependency = new Dependency(artifact, scope, optional, exclusions);
               dependencies.push(dependency);

            }
            catch (IllegalArgumentException e)
            {
               throw new DependencyException("Unable to create artifact from coordinates " + coords + ", " +
                     "they are either invalid or version information was not specified in loaded POM file (maybe the POM file wasn't load at all)", e);
            }
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.MavenArtifactBuilder#optional(boolean)
       */
      @Override
      public MavenArtifactsBuilder optional(boolean optional)
      {
         this.optional = optional;
         List<Dependency> workplace = new ArrayList<Dependency>();

         int i;
         for (i = 0; i < artifacts.size(); i++)
         {
            Dependency dependency = dependencies.pop();
            workplace.add(dependency.setOptional(optional));
         }

         for (; i > 0; i--)
         {
            dependencies.push(workplace.get(i - 1));
         }

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.MavenArtifactBuilder#scope(java.lang.String)
       */
      @Override
      public MavenArtifactBuilder scope(String scope)
      {
         this.scope = scope;
         List<Dependency> workplace = new ArrayList<Dependency>();

         int i;
         for (i = 0; i < artifacts.size(); i++)
         {
            Dependency dependency = dependencies.pop();
            workplace.add(dependency.setScope(scope));
         }

         for (; i > 0; i--)
         {
            dependencies.push(workplace.get(i - 1));
         }

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.MavenArtifactBuilder#exclusions(org.sonatype.aether.graph.Exclusion[])
       */
      @Override
      public MavenArtifactBuilder exclusions(String... coordinates)
      {
         this.exclusions.addAll(MavenConverter.convertExclusions(Arrays.asList(coordinates)));
         List<Dependency> workplace = new ArrayList<Dependency>();

         int i;
         for (i = 0; i < artifacts.size(); i++)
         {
            Dependency dependency = dependencies.pop();
            workplace.add(dependency.setExclusions(this.exclusions));
         }

         for (; i > 0; i--)
         {
            dependencies.push(workplace.get(i - 1));
         }

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.MavenArtifactBuilder#exclusions(java.util.Collection)
       */
      @Override
      public MavenArtifactBuilder exclusions(Collection<String> coordinates)
      {
         this.exclusions.addAll(MavenConverter.convertExclusions(coordinates));
         List<Dependency> workplace = new ArrayList<Dependency>();

         int i;
         for (i = 0; i < artifacts.size(); i++)
         {
            Dependency dependency = dependencies.pop();
            workplace.add(dependency.setExclusions(this.exclusions));
         }

         for (; i > 0; i--)
         {
            dependencies.push(workplace.get(i - 1));
         }

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.MavenArtifactBuilder#exclusion(org.sonatype.aether.graph.Exclusion)
       */
      @Override
      public MavenArtifactBuilder exclusion(String exclusion)
      {
         this.exclusions.add(MavenConverter.convertExclusion(exclusion));
         List<Dependency> workplace = new ArrayList<Dependency>();

         int i;
         for (i = 0; i < artifacts.size(); i++)
         {
            Dependency dependency = dependencies.pop();
            workplace.add(dependency.setExclusions(this.exclusions));
         }

         for (; i > 0; i--)
         {
            dependencies.push(workplace.get(i - 1));
         }

         return this;
      }

   }

}
