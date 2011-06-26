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
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
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
public class MavenBuilderImpl implements MavenDependencyResolverInternal
{

   private static final Logger log = Logger.getLogger(MavenArtifactBuilderImpl.class.getName());

   private static final File[] FILE_CAST = new File[0];

   private final MavenRepositorySystem system;

   private final RepositorySystemSession session;

   // these are package visible, so they can be wrapped and make visible for
   // filters
   Stack<MavenDependency> dependencies;

   Map<ArtifactAsKey, MavenDependency> pomInternalDependencyManagement;

   @Override
   public Stack<MavenDependency> getDependencies()
   {
      return dependencies;
   }

   @Override
   public Map<ArtifactAsKey, MavenDependency> getPomInternalDependencyManagement()
   {
      return pomInternalDependencyManagement;
   }

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
   public MavenDependencyResolver configureFrom(String path)
   {
      Validate.isReadable(path, "Path to the settings.xml must be defined and accessible");
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
   @Override
   public MavenDependencyResolver loadMetadataFromPom(final String path) throws ResolutionException
   {
      Validate.isReadable(path, "Path to the pom.xml file must be defined and accessible");

      File pom = new File(path);
      Model model = system.loadPom(pom, session);

      ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

      // store all dependency information to be able to retrieve versions later
      for (org.apache.maven.model.Dependency dependency : model.getDependencies())
      {
         MavenDependency d = MavenConverter.fromDependency(dependency, stereotypes);
         pomInternalDependencyManagement.put(new ArtifactAsKey(d.getCoordinates()), d);
      }

      return this;
   }

   /**
    * @deprecated please use {@link #loadMetadataFromPom(String)} instead
    */
   @Override
   public MavenDependencyResolver loadReposFromPom(final String path) throws ResolutionException
   {
      return loadMetadataFromPom(path);
   }

   @Override
   public MavenDependencyResolver includeDependenciesFromPom(final String path) throws ResolutionException
   {
      Validate.isReadable(path, "Path to the pom.xml file must be defined and accessible");
      File pom = new File(path);
      Model model = system.loadPom(pom, session);

      ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

      for (org.apache.maven.model.Dependency dependency : model.getDependencies())
      {
         dependencies.push(MavenConverter.fromDependency(dependency, stereotypes));
      }
      return this;
   }

   /**
    * @deprecated please use {@link #includeDependenciesFromPom(String)} instead
    */
   @Override
   public MavenDependencyResolver loadDependenciesFromPom(final String path) throws ResolutionException
   {
      return includeDependenciesFromPom(path);
   }

   /**
    * @deprecated please use {@link #includeDependenciesFromPom(String)} instead
    */
   @Override
   public MavenDependencyResolver loadDependenciesFromPom(final String path, final MavenResolutionFilter filter)
         throws ResolutionException
   {
      return includeDependenciesFromPom(path);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
    * .String)
    */
   public MavenDependencyResolver artifact(String coordinates) throws ResolutionException
   {
      Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

      return new MavenArtifactBuilderImpl(this, coordinates);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
    * .String)
    */
   public MavenDependencyResolver artifacts(String... coordinates) throws ResolutionException
   {
      Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

      return new MavenArtifactsBuilderImpl(this, coordinates);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
    * #exclusion(org.sonatype.aether.graph.Exclusion)
    */
   public MavenDependencyResolver exclusion(String coordinates)
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
   public MavenDependencyResolver exclusions(String... coordinates)
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
   public MavenDependencyResolver exclusions(Collection<String> coordinates)
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
   public MavenDependencyResolver optional(boolean optional)
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
   public MavenDependencyResolver scope(String scope)
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
    * #resolveAsFiles()
    */
   public File[] resolveAsFiles() throws ResolutionException
   {
      return resolveAsFiles(AcceptAllFilter.INSTANCE);
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

      CollectRequest request = new CollectRequest(MavenConverter.asDependencies(dependencies), null,
            system.getRemoteRepositories());

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

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
    * #resolve()
    */
   public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(final Class<ARCHIVEVIEW> archiveView)
         throws ResolutionException
   {
      return resolveAs(archiveView, AcceptAllFilter.INSTANCE);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
    * #resolve(org.sonatype.aether.graph.DependencyFilter)
    */
   public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(final Class<ARCHIVEVIEW> archiveView,
         MavenResolutionFilter filter) throws ResolutionException
   {
      // Precondition checks
      if (archiveView == null)
      {
         throw new IllegalArgumentException("Archive view must be specified");
      }
      if (filter == null)
      {
         throw new IllegalArgumentException("Filter must be specified");
      }

      final File[] files = resolveAsFiles(filter);
      final Collection<ARCHIVEVIEW> archives = new ArrayList<ARCHIVEVIEW>(files.length);
      for (final File file : files)
      {
         final ARCHIVEVIEW archive = ShrinkWrap.create(ZipImporter.class, file.getName()).importFrom(convert(file))
               .as(archiveView);
         archives.add(archive);
      }

      return archives;
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
         throw new ResolutionException("Unable to treat dependency artifact \"" + file.getAbsolutePath()
               + "\" as a ZIP file", e);
      }
      catch (IOException e)
      {
         throw new ResolutionException("Unable to access artifact file at \"" + file.getAbsolutePath() + "\".", e);
      }
   }

   class MavenArtifactBuilderImpl implements MavenDependencyResolverInternal
   {
      private final MavenDependencyResolverInternal delegate;

      MavenArtifactBuilderImpl(final MavenDependencyResolverInternal delegate, String coordinates)
            throws ResolutionException
      {
         assert delegate != null : "Delegate must be specified";
         this.delegate = delegate;
         coordinates = MavenConverter.resolveArtifactVersion(pomInternalDependencyManagement, coordinates);
         MavenDependency dependency = new MavenDependencyImpl(coordinates);
         delegate.getDependencies().push(dependency);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
       * .String)
       */
      @Override
      public MavenDependencyResolver artifact(String coordinates)
      {
         Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");
         return new MavenArtifactsBuilderImpl(this, coordinates);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifacts(java.
       * lang.String[])
       */
      @Override
      public MavenDependencyResolver artifacts(String... coordinates) throws ResolutionException
      {
         Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");
         return new MavenArtifactsBuilderImpl(this, coordinates);
      }

      @Override
      public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView)
            throws ResolutionException
      {
         return delegate.resolveAs(archiveView);
      }

      @Override
      public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView,
            MavenResolutionFilter filter) throws ResolutionException
      {
         return delegate.resolveAs(archiveView, filter);
      }

      @Override
      public File[] resolveAsFiles() throws ResolutionException
      {
         return delegate.resolveAsFiles();
      }

      @Override
      public MavenDependencyResolver configureFrom(String path)
      {
         return delegate.configureFrom(path);
      }

      @Override
      public File[] resolveAsFiles(MavenResolutionFilter filter) throws ResolutionException
      {
         return delegate.resolveAsFiles(filter);
      }

      @Override
      public MavenDependencyResolver loadMetadataFromPom(String path) throws ResolutionException
      {
         return delegate.loadMetadataFromPom(path);
      }

      /**
       * @deprecated please use {@link #loadMetadataFromPom(String)} instead
       */
      @Override
      public MavenDependencyResolver loadReposFromPom(String path) throws ResolutionException
      {
         return delegate.loadReposFromPom(path);
      }

      @Override
      public MavenDependencyResolver scope(String scope)
      {
         return delegate.scope(scope);
      }

      @Override
      public MavenDependencyResolver optional(boolean optional)
      {
         return delegate.optional(optional);
      }

      @Override
      public MavenDependencyResolver exclusion(String exclusion)
      {
         return delegate.exclusion(exclusion);
      }

      @Override
      public MavenDependencyResolver exclusions(String... exclusions)
      {
         return delegate.exclusions(exclusions);
      }

      @Override
      public MavenDependencyResolver exclusions(Collection<String> exclusions)
      {
         return delegate.exclusions(exclusions);
      }

      @Override
      public Stack<MavenDependency> getDependencies()
      {
         return delegate.getDependencies();
      }

      @Override
      public Map<ArtifactAsKey, MavenDependency> getPomInternalDependencyManagement()
      {
         return delegate.getPomInternalDependencyManagement();
      }

      @Override
      public MavenDependencyResolver includeDependenciesFromPom(String path) throws ResolutionException
      {
         return delegate.includeDependenciesFromPom(path);
      }

      /**
       * @deprecated please use {@link #includeDependenciesFromPom(String)} instead
       */
      @Override
      public MavenDependencyResolver loadDependenciesFromPom(String path) throws ResolutionException
      {
         return delegate.loadDependenciesFromPom(path);
      }

      /**
       * @deprecated please use {@link #includeDependenciesFromPom(String)} instead
       */
      @Override
      public MavenDependencyResolver loadDependenciesFromPom(String path, MavenResolutionFilter filter)
            throws ResolutionException
      {
         return delegate.loadDependenciesFromPom(path, filter);
      }
   }

   static class MavenArtifactsBuilderImpl implements MavenDependencyResolverInternal
   {
      private final MavenDependencyResolverInternal delegate;

      private int size;

      MavenArtifactsBuilderImpl(final MavenDependencyResolverInternal delegate, final String... coordinates)
      {
         assert delegate != null : "Delegate must be specified";
         this.delegate = delegate;

         this.size = coordinates.length;

         for (String coords : coordinates)
         {
            coords = MavenConverter.resolveArtifactVersion(delegate.getPomInternalDependencyManagement(), coords);
            MavenDependency dependency = new MavenDependencyImpl(coords);
            delegate.getDependencies().push(dependency);
         }
      }

      /**
       * {@inheritDoc}
       * @see org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver#optional(boolean)
       */
      @Override
      public MavenDependencyResolver optional(boolean optional)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = delegate.getDependencies().pop();
            workplace.add(dependency.setOptional(optional));
         }

         for (; i > 0; i--)
         {
            delegate.getDependencies().push(workplace.get(i - 1));
         }

         return this;
      }

      /**
       * {@inheritDoc}
       * @see org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver#scope(java.lang.String)
       */
      @Override
      public MavenDependencyResolver scope(String scope)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = delegate.getDependencies().pop();
            workplace.add(dependency.setScope(scope));
         }

         for (; i > 0; i--)
         {
            delegate.getDependencies().push(workplace.get(i - 1));
         }

         return this;
      }

      /**
       * {@inheritDoc}
       * @see org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver#exclusions(java.lang.String[])
       */
      @Override
      public MavenDependencyResolver exclusions(String... coordinates)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = delegate.getDependencies().pop();
            workplace.add(dependency.addExclusions(coordinates));
         }

         for (; i > 0; i--)
         {
            delegate.getDependencies().push(workplace.get(i - 1));
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
      public MavenDependencyResolver exclusions(Collection<String> coordinates)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = delegate.getDependencies().pop();
            workplace.add(dependency.addExclusions(coordinates.toArray(new String[0])));
         }

         for (; i > 0; i--)
         {
            delegate.getDependencies().push(workplace.get(i - 1));
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
      public MavenDependencyResolver exclusion(String exclusion)
      {
         List<MavenDependency> workplace = new ArrayList<MavenDependency>();

         int i;
         for (i = 0; i < size; i++)
         {
            MavenDependency dependency = delegate.getDependencies().pop();
            workplace.add(dependency.addExclusions(exclusion));
         }

         for (; i > 0; i--)
         {
            delegate.getDependencies().push(workplace.get(i - 1));
         }

         return this;
      }

      @Override
      public int hashCode()
      {
         return delegate.hashCode();
      }

      @Override
      public boolean equals(Object obj)
      {
         return delegate.equals(obj);
      }

      @Override
      public MavenDependencyResolver configureFrom(String path)
      {
         return delegate.configureFrom(path);
      }

      @Override
      public MavenDependencyResolver loadMetadataFromPom(String path) throws ResolutionException
      {
         return delegate.loadMetadataFromPom(path);
      }

      /**
       * @deprecated please use {@link #loadMetadataFromPom(String)} instead
       */
      @Override
      public MavenDependencyResolver loadReposFromPom(String path) throws ResolutionException
      {
         return delegate.loadReposFromPom(path);
      }

      @Override
      public MavenDependencyResolver artifact(String coordinates) throws ResolutionException
      {
         return delegate.artifact(coordinates);
      }

      @Override
      public MavenDependencyResolver artifacts(String... coordinates) throws ResolutionException
      {
         return delegate.artifacts(coordinates);
      }

      @Override
      public File[] resolveAsFiles() throws ResolutionException
      {
         return delegate.resolveAsFiles();
      }

      @Override
      public File[] resolveAsFiles(MavenResolutionFilter filter) throws ResolutionException
      {
         return delegate.resolveAsFiles(filter);
      }

      @Override
      public String toString()
      {
         return delegate.toString();
      }

      @Override
      public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView)
            throws ResolutionException
      {
         return delegate.resolveAs(archiveView);
      }

      @Override
      public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView,
            MavenResolutionFilter filter) throws ResolutionException
      {
         return delegate.resolveAs(archiveView, filter);
      }

      @Override
      public Stack<MavenDependency> getDependencies()
      {
         return delegate.getDependencies();
      }

      @Override
      public Map<ArtifactAsKey, MavenDependency> getPomInternalDependencyManagement()
      {
         return delegate.getPomInternalDependencyManagement();
      }

      @Override
      public MavenDependencyResolver includeDependenciesFromPom(String path) throws ResolutionException
      {
         return delegate.includeDependenciesFromPom(path);
      }

      /**
       * @deprecated please use {@link #includeDependenciesFromPom(String)} instead
       */
      @Override
      public MavenDependencyResolver loadDependenciesFromPom(String path) throws ResolutionException
      {
         return delegate.loadDependenciesFromPom(path);
      }

      /**
       * @deprecated please use {@link #includeDependenciesFromPom(String)} instead
       */
      @Override
      public MavenDependencyResolver loadDependenciesFromPom(String path, MavenResolutionFilter filter)
            throws ResolutionException
      {
         return delegate.loadDependenciesFromPom(path, filter);
      }
   }
}
