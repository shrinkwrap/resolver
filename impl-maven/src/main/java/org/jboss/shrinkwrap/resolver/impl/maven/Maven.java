/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.StrictFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;

/**
 * Shortcut API for Maven artifact builder which holds and construct dependencies and is able to resolve them into ShrinkWrap
 * archives.
 *
 * @author <a href="mailto:samaxes@gmail.com">Samuel Santos</a>
 */
public class Maven {
    private static final Logger log = Logger.getLogger(Maven.class.getName());

    private static final File[] FILE_CAST = new File[0];

    /**
     * Resolves dependency for dependency builder.
     *
     * @param coordinates Coordinates specified to a created artifact, specified in an implementation-specific format.
     * @return An archive of the resolved artifact.
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     */
    public static GenericArchive artifact(String coordinates) throws ResolutionException {
        return new MavenResolver().artifact(coordinates);
    }

    /**
     * Resolves dependencies for dependency builder.
     *
     * @param coordinates A list of coordinates specified to the created artifacts, specified in an implementation-specific
     *        format.
     * @return An array of archives which contains resolved artifacts.
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     */
    public static Collection<GenericArchive> artifacts(String... coordinates) throws ResolutionException {
        return new MavenResolver().artifacts(coordinates);
    }

    /**
     * Loads remote repositories for a POM file. If repositories are defined in the parent of the POM file and there are
     * accessible via local file system, they are set as well.
     *
     * These remote repositories are used to resolve the artifacts during dependency resolution.
     *
     * Additionally, it loads dependencies defined in the POM file model in an internal cache, which can be later used to
     * resolve an artifact without explicitly specifying its version.
     *
     * @param path A path to the POM file, must not be {@code null} or empty
     * @return A dependency builder with remote repositories set according to the content of POM file.
     */
    public static MavenResolver withPom(String path) {
        Validate.isReadable(path, "Path to the pom.xml file must be defined and accessible");

        return new MavenResolver(path);
    }

    public static class MavenResolver {
        private final MavenRepositorySystem system;

        private final MavenDependencyResolverSettings settings;

        private final RepositorySystemSession session;

        private Stack<MavenDependency> dependencies;

        private Map<ArtifactAsKey, MavenDependency> pomInternalDependencyManagement;

        MavenResolver() {
            this.system = new MavenRepositorySystem();
            this.settings = new MavenDependencyResolverSettings();
            this.session = system.getSession(settings);
            this.dependencies = new Stack<MavenDependency>();
            this.pomInternalDependencyManagement = new HashMap<ArtifactAsKey, MavenDependency>();
        }

        MavenResolver(String path) {
            this();

            File pom = new File(path);
            Model model = system.loadPom(pom, settings, session);

            ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

            // store all dependency information to be able to retrieve versions later
            for (org.apache.maven.model.Dependency dependency : model.getDependencies()) {
                MavenDependency d = MavenConverter.fromDependency(dependency, stereotypes);
                pomInternalDependencyManagement.put(new ArtifactAsKey(d.getCoordinates()), d);
            }
        }

        public GenericArchive artifact(String coordinates) throws ResolutionException {
            Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

            final Collection<GenericArchive> archives = fetchArchives(coordinates);

            return (archives == null || archives.isEmpty()) ? null : archives.iterator().next();
        }

        public Collection<GenericArchive> artifacts(String... coordinates) throws ResolutionException {
            Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

            return fetchArchives(coordinates);
        }

        // resolves dependencies as files
        private Collection<GenericArchive> fetchArchives(String... coordinates) throws ResolutionException {
            for (String coordinate : coordinates) {
                coordinate = MavenConverter.resolveArtifactVersion(pomInternalDependencyManagement, coordinate);
                MavenDependency dependency = new MavenDependencyImpl(coordinate);
                dependencies.push(dependency);
            }

            final MavenResolutionFilter filter = new StrictFilter(); // Omit all transitive dependencies
            final File[] files = resolveAsFiles(filter);
            final Collection<GenericArchive> archives = new ArrayList<GenericArchive>(files.length);

            for (final File file : files) {
                final GenericArchive archive = ShrinkWrap.create(ZipImporter.class, file.getName()).importFrom(convert(file))
                        .as(GenericArchive.class);
                archives.add(archive);
            }

            return archives;
        }

        // resolves dependencies as files
        private File[] resolveAsFiles(MavenResolutionFilter filter) throws ResolutionException {
            Validate.notEmpty(dependencies, "No dependencies were set for resolution");

            CollectRequest request = new CollectRequest(MavenConverter.asDependencies(dependencies), null,
                    settings.getRemoteRepositories());

            // configure filter
            filter.configure(Collections.unmodifiableList(dependencies));

            // wrap artifact files to archives
            Collection<ArtifactResult> artifacts;
            try {
                artifacts = system.resolveDependencies(session, request, filter);
            } catch (DependencyCollectionException e) {
                throw new ResolutionException("Unable to collect dependeny tree for a resolution", e);
            } catch (ArtifactResolutionException e) {
                throw new ResolutionException("Unable to resolve an artifact", e);
            }

            Collection<File> files = new ArrayList<File>(artifacts.size());
            for (ArtifactResult artifact : artifacts) {
                Artifact a = artifact.getArtifact();
                // skip all pom artifacts
                if ("pom".equals(a.getExtension())) {
                    log.info("Removed POM artifact " + a.toString() + " from archive, it's dependencies were fetched.");
                    continue;
                }

                files.add(a.getFile());
            }

            return files.toArray(FILE_CAST);
        }

        // Converts a file to a ZIP file
        private ZipFile convert(File file) throws ResolutionException {
            try {
                return new ZipFile(file);
            } catch (ZipException e) {
                throw new ResolutionException("Unable to treat dependency artifact \"" + file.getAbsolutePath()
                        + "\" as a ZIP file", e);
            } catch (IOException e) {
                throw new ResolutionException("Unable to access artifact file at \"" + file.getAbsolutePath() + "\".", e);
            }
        }
    }
}
