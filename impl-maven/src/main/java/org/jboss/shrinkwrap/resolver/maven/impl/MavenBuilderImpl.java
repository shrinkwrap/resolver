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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.jboss.shrinkwrap.resolver.ResolutionException;
import org.jboss.shrinkwrap.resolver.maven.MavenArtifactBuilder;
import org.jboss.shrinkwrap.resolver.maven.MavenArtifactsBuilder;
import org.jboss.shrinkwrap.resolver.maven.MavenBuilder;
import org.jboss.shrinkwrap.resolver.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.maven.impl.filter.AcceptAllFilter;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;

/**
 * A default implementation of dependency builder based on Maven.
 * 
 * Apart from contract, it allows to load Maven settings from an XML file,
 * configure remote repositories from an POM file and retrieve dependencies
 * defined in a POM file, including ones in POM parents.
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
 * @see MavenRepositorySettings
 */
public class MavenBuilderImpl implements MavenBuilder
{
   private static final Logger log = Logger.getLogger(MavenBuilderImpl.class.getName());

   private static final Archive<?>[] ARCHIVE_CAST = new Archive<?>[0];
   private static final File[] FILE_CAST = new File[0];

   private static final MavenResolutionFilter ACCEPT_ALL = new AcceptAllFilter();

   private MavenRepositorySystem system;

   private RepositorySystemSession session;

   // these are package visible, so they can be wrapped and make visible for
   // filters
   Stack<MavenDependency> dependencies;
   Map<ArtifactAsKey, MavenDependency> pomInternalDependencyManagement;

   /**
    * Constructs new instance of MavenDependencies
    */
   public MavenBuilderImpl()
   {
      this.system = new MavenRepositorySystem(new MavenRepositorySettings());
      this.dependencies = new Stack<MavenDependency>();
      this.pomInternalDependencyManagement = new HashMap<ArtifactAsKey, MavenDependency>();
      this.session = system.getSession();
   }

   /**
    * Configures Maven from a settings.xml file
    * 
    * @param path A path to a settings.xml configuration file
    * @return A dependency builder with a configuration from given file
    */
   public MavenBuilder configureFrom(String path)
   {
      Validate.readable(path, "Path to the settings.xml must be defined and accessible");
      File settings = new File(path);
      system.loadSettings(settings, session);
      return this;
   }

   /**
    * Loads remote repositories for a POM file. If repositories are defined in
    * the parent of the POM file and there are accessible via local file system,
    * they are set as well.
    * 
    * These remote repositories are used to resolve the artifacts during
    * dependency resolution.
    * 
    * Additionally, it loads dependencies defined in the POM file model in an
    * internal cache, which can be later used to resolve an artifact without
    * explicitly specifying its version.
    * 
    * @param path A path to the POM file, must not be {@code null} or empty
    * @return A dependency builder with remote repositories set according to the
    *         content of POM file.
    * @throws Exception
    */
   public MavenBuilder loadPom(String path) throws ResolutionException
   {
      Validate.readable(path, "Path to the pom.xml file must be defined and accessible");

      File pom = new File(path);
      Model model = system.loadPom(pom, session);

      ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

      // store all dependency information to be able to retrieve versions later
      for (org.apache.maven.model.Dependency dependency : model.getDependencies())
      {
         MavenDependency d = MavenConverter.convert(dependency, stereotypes);
         pomInternalDependencyManagement.put(new ArtifactAsKey(d.getCoordinates()), d);
      }

      return this;
   }

   /**
    * Uses dependencies and remote repositories defined in a POM file to and
    * tries to resolve them
    * 
    * @param path A path to the POM file
    * @return An array of ShrinkWrap archives
    * @throws ResolutionException If dependencies could not be resolved or the
    *            POM processing failed
    */
   public Archive<?>[] resolveFrom(String path) throws ResolutionException
   {
      return resolveFrom(path, ACCEPT_ALL);
   }

   public Archive<?>[] resolveFrom(String path, MavenResolutionFilter filter) throws ResolutionException
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
      return new MavenArtifactBuilderImpl().resolve(filter);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
    * .String)
    */
   public MavenArtifactBuilderImpl artifact(String coordinates) throws ResolutionException
   {
      Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

      return new MavenArtifactBuilderImpl(coordinates);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
    * .String)
    */
   public MavenArtifactsBuilder artifacts(String... coordinates) throws ResolutionException
   {
      Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

      return new MavenArtifactsBuilderImpl(coordinates);
   }

   public class MavenArtifactBuilderImpl implements MavenArtifactBuilder
   {

      public MavenArtifactBuilderImpl(String coordinates) throws ResolutionException
      {
         coordinates = MavenConverter.resolveArtifactVersion(pomInternalDependencyManagement, coordinates);
         MavenDependency dependency = new MavenDependencyImpl(coordinates);
         dependencies.push(dependency);
      }

      // used for resolution from pom.xml only or for inheritance
      private MavenArtifactBuilderImpl()
      {
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #exclusion(org.sonatype.aether.graph.Exclusion)
       */
      public MavenArtifactBuilder exclusion(String coordinates)
      {
         MavenDependency dependency = dependencies.peek();
         dependency.addExclusions(coordinates);

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #exclusions(org.sonatype.aether.graph.Exclusion[])
       */
      public MavenArtifactBuilder exclusions(String... coordinates)
      {
         MavenDependency dependency = dependencies.peek();
         dependency.addExclusions(coordinates);
         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #exclusions(java.util.Collection)
       */
      public MavenArtifactBuilder exclusions(Collection<String> coordinates)
      {
         MavenDependency dependency = dependencies.peek();
         dependency.addExclusions(coordinates.toArray(new String[0]));
         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #optional(boolean)
       */
      public MavenArtifactBuilder optional(boolean optional)
      {
         MavenDependency dependency = dependencies.peek();
         dependency.setOptional(optional);

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #scope(java.lang.String)
       */
      public MavenArtifactBuilder scope(String scope)
      {
         MavenDependency dependency = dependencies.peek();
         dependency.setScope(scope);

         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #resolve()
       */
      public Archive<?>[] resolve() throws ResolutionException
      {
         return resolve(ACCEPT_ALL);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #resolve(org.sonatype.aether.graph.DependencyFilter)
       */
      public Archive<?>[] resolve(MavenResolutionFilter filter) throws ResolutionException
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
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
       * .String)
       */
      public MavenArtifactBuilder artifact(String coordinates)
      {
         Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");
         return new MavenArtifactBuilderImpl(coordinates);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifacts(java.
       * lang.String[])
       */
      public MavenArtifactsBuilder artifacts(String... coordinates) throws ResolutionException
      {
         Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");
         return new MavenArtifactsBuilderImpl(coordinates);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #resolveAsFiles()
       */
      public File[] resolveAsFiles() throws ResolutionException
      {
         return resolveAsFiles(ACCEPT_ALL);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
       * #resolveAsFiles()
       */
      public File[] resolveAsFiles(MavenResolutionFilter filter) throws ResolutionException
      {
         Validate.notEmpty(dependencies, "No dependencies were set for resolution");

         CollectRequest request = new CollectRequest(MavenConverter.asDependencies(dependencies), null, system.getRemoteRepositories());

         // configure filter
         filter.configure(Collections.unmodifiableList(dependencies));

         // wrap artifact files to archives
         Collection<ArtifactResult> artifacts;
         try
         {
            artifacts = system.resolveDependencies(session, request, filter);
         }
         catch (DependencyCollectionException e)
         {
            throw new ResolutionException("Unable to collect dependeny tree for a resolution", e);
         }
         catch (ArtifactResolutionException e)
         {
            throw new ResolutionException("Unable to resolve an artifact", e);
         }

         Collection<File> files = new ArrayList<File>(artifacts.size());
         for (ArtifactResult artifact : artifacts)
         {
            Artifact a = artifact.getArtifact();
            // skip all pom artifacts
            if ("pom".equals(a.getExtension()))
            {
               log.info("Removed POM artifact " + a.toString() + " from archive, it's dependencies were fetched.");
               continue;
            }

            files.add(a.getFile());
         }

         return files.toArray(FILE_CAST);
      }

      // converts a file to a ZIP file
      private ZipFile convert(File file) throws ResolutionException
      {
         try
         {
            return new ZipFile(file);
         }
         catch (ZipException e)
         {
            throw new ResolutionException("Unable to treat dependecy artifact \"" + file.getAbsolutePath() + "\" as a ZIP file", e);
         }
         catch (IOException e)
         {
            throw new ResolutionException("Unable to access artifact file at \"" + file.getAbsolutePath() + "\".");
         }
      }
   }

   public class MavenArtifactsBuilderImpl extends MavenArtifactBuilderImpl implements MavenArtifactsBuilder
   {
      private int size;

      public MavenArtifactsBuilderImpl(String... coordinates)
      {
         this.size = coordinates.length;

         for (String coords : coordinates)
         {
            coords = MavenConverter.resolveArtifactVersion(pomInternalDependencyManagement, coords);
            MavenDependency dependency = new MavenDependencyImpl(coords);
            dependencies.push(dependency);
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.
       * MavenArtifactBuilder#optional(boolean)
       */
      @Override
      public MavenArtifactsBuilder optional(boolean optional)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = dependencies.pop();
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
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.
       * MavenArtifactBuilder#scope(java.lang.String)
       */
      @Override
      public MavenArtifactBuilder scope(String scope)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = dependencies.pop();
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
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.
       * MavenArtifactBuilder#exclusions(org.sonatype.aether.graph.Exclusion[])
       */
      @Override
      public MavenArtifactBuilder exclusions(String... coordinates)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = dependencies.pop();
            workplace.add(dependency.addExclusions(coordinates));
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
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.
       * MavenArtifactBuilder#exclusions(java.util.Collection)
       */
      @Override
      public MavenArtifactBuilder exclusions(Collection<String> coordinates)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = dependencies.pop();
            workplace.add(dependency.addExclusions(coordinates.toArray(new String[0])));
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
       * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.
       * MavenArtifactBuilder#exclusion(org.sonatype.aether.graph.Exclusion)
       */
      @Override
      public MavenArtifactBuilder exclusion(String exclusion)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = dependencies.pop();
            workplace.add(dependency.addExclusions(exclusion));
         }

         for (; i > 0; i--)
         {
            dependencies.push(workplace.get(i - 1));
         }

         return this;
      }

   }

}
