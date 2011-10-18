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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven.MavenShortcutAPI;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenShortcutDependencyResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ResourceUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;

/**
 * Shortcut API implementation for Maven artifact builder which holds and construct dependencies and is able to resolve them
 * into ShrinkWrap archives.
 *
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class MavenImpl implements MavenShortcutDependencyResolverInternal, MavenShortcutAPI {

    private final MavenRepositorySystem system;

    private final MavenDependencyResolverSettings settings;

    private RepositorySystemSession session;

    private Stack<MavenDependency> dependencies;

    private Set<MavenDependency> versionManagement;

    /**
     * Constructs new instance of MavenDependencies
     */
    public MavenImpl() {
        this.system = new MavenRepositorySystem();
        this.settings = new MavenDependencyResolverSettings();
        this.dependencies = new Stack<MavenDependency>();
        this.versionManagement = new HashSet<MavenDependency>();
        // get session to spare time
        this.session = system.getSession(settings);
    }

    public MavenImpl(MavenRepositorySystem system, RepositorySystemSession session, MavenDependencyResolverSettings settings,
            Stack<MavenDependency> dependencies, Set<MavenDependency> dependencyManagement) {
        this.system = system;
        this.session = session;
        this.settings = settings;
        this.dependencies = dependencies;
        this.versionManagement = dependencyManagement;
    }

    /**
     * Resolves dependency for dependency builder.
     *
     * @param coordinates Coordinates specified to a created artifact, specified in an implementation-specific format.
     * @return An archive of the resolved artifact.
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    @Override
    public GenericArchive dependency(String coordinates) throws ResolutionException {
        return artifact(coordinates).resolveArtifactAs(GenericArchive.class);
    }

    /**
     * Resolves dependencies for dependency builder.
     *
     * @param coordinates A list of coordinates specified to the created artifacts, specified in an implementation-specific
     *        format.
     * @return An array of archives which contains resolved artifacts.
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    @Override
    public Collection<GenericArchive> dependencies(String... coordinates) throws ResolutionException {
        return artifacts(coordinates).resolveArtifactsAs(GenericArchive.class);
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
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     */
    @Override
    public MavenShortcutAPI withPom(final String path) throws ResolutionException {
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the pom.xml ('" + path + "')file must be defined and accessible");

        File pom = new File(resolvedPath);
        Model model = system.loadPom(pom, settings, session);

        ArtifactTypeRegistry stereotypes = system.getArtifactTypeRegistry(session);

        // store all dependency information to be able to retrieve versions later
        Stack<MavenDependency> pomDefinedDependencies = MavenConverter.fromDependencies(model.getDependencies(), stereotypes);
        versionManagement.addAll(pomDefinedDependencies);

        return this;
    }

    /**
     * Resolves dependency for dependency builder.
     *
     * @param coordinates Coordinates specified to a created artifact, specified in an implementation-specific format.
     * @return A dependency builder with remote repositories set according to the content of POM file.
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     */
    @Override
    public MavenShortcutDependencyResolver artifact(String coordinates) throws ResolutionException {
        Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

        return new MavenResolver(this, coordinates);
    }

    /**
     * Resolves dependencies for dependency builder.
     *
     * @param coordinates A list of coordinates specified to the created artifacts, specified in an implementation-specific
     *        format.
     * @return An array of archives which contains resolved artifacts.
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     */
    @Override
    public MavenShortcutDependencyResolver artifacts(String... coordinates) throws ResolutionException {
        Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

        return new MavenResolver(this, coordinates);
    }

    /**
     * Resolves dependency for dependency builder.
     *
     * @param archiveView End-user view of the archive requested (ie. {@link GenericArchive} or {@link JavaArchive})
     * @return An archive of the resolved artifact.
     * @throws ResolutionException If artifact could not be resolved
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    @Override
    public <ARCHIVEVIEW extends Assignable> ARCHIVEVIEW resolveArtifactAs(Class<ARCHIVEVIEW> archiveView)
            throws ResolutionException {
        Collection<ARCHIVEVIEW> archiveViews = new MavenBuilderImpl(system, session, settings, dependencies, versionManagement)
                .resolveAs(archiveView);

        return (archiveViews == null || archiveViews.isEmpty()) ? null : archiveViews.iterator().next();
    }

    /**
     * Resolves dependencies for dependency builder.
     *
     * @param archiveView End-user view of the archive requested (ie. {@link GenericArchive} or {@link JavaArchive})
     * @return An array of archives which contains resolved artifacts.
     * @throws ResolutionException If artifacts could not be resolved
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    @Override
    public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveArtifactsAs(Class<ARCHIVEVIEW> archiveView)
            throws ResolutionException {

        return new MavenBuilderImpl(system, session, settings, dependencies, versionManagement).resolveAs(archiveView);
    }

    /**
     * Gets all the dependencies marked by Resolver to be resolved
     *
     * @return the stack which represents content of MavenDependencyResolver
     */
    @Override
    public Stack<MavenDependency> getDependencies() {
        return dependencies;
    }

    /**
     * Gets all the dependencies retrieved from metadata parsing.
     *
     * @return the set which represents content of {@link MavenDependencyResolver} version metadata
     */
    @Override
    public Set<MavenDependency> getVersionManagement() {
        return versionManagement;
    }

    private class MavenResolver implements MavenShortcutDependencyResolverInternal {

        private final MavenShortcutDependencyResolverInternal delegate;

        MavenResolver(final MavenShortcutDependencyResolverInternal delegate, String coordinates) throws ResolutionException {
            assert delegate != null : "Delegate must be specified";
            this.delegate = delegate;
            MavenDependency dependency = MavenConverter.asDepedencyWithVersionManagement(delegate.getVersionManagement(),
                    coordinates);
            delegate.getDependencies().push(dependency);
        }

        MavenResolver(final MavenShortcutDependencyResolverInternal delegate, final String... coordinates) {
            assert delegate != null : "Delegate must be specified";
            this.delegate = delegate;

            for (String coords : coordinates) {
                MavenDependency dependency = MavenConverter.asDepedencyWithVersionManagement(delegate.getVersionManagement(),
                        coords);
                delegate.getDependencies().push(dependency);
            }
        }

        @Override
        public MavenShortcutAPI withPom(String path) {
            return delegate.withPom(path);
        }

        @Override
        public MavenShortcutDependencyResolver artifact(String coordinates) throws ResolutionException {
            return delegate.artifact(coordinates);
        }

        @Override
        public MavenShortcutDependencyResolver artifacts(String... coordinates) throws ResolutionException {
            return delegate.artifacts(coordinates);
        }

        @Override
        public <ARCHIVEVIEW extends Assignable> ARCHIVEVIEW resolveArtifactAs(Class<ARCHIVEVIEW> archiveView)
                throws ResolutionException {
            return delegate.resolveArtifactAs(archiveView);
        }

        @Override
        public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveArtifactsAs(Class<ARCHIVEVIEW> archiveView)
                throws ResolutionException {
            return delegate.resolveArtifactsAs(archiveView);
        }

        @Override
        public Stack<MavenDependency> getDependencies() {
            return delegate.getDependencies();
        }

        @Override
        public Set<MavenDependency> getVersionManagement() {
            return delegate.getVersionManagement();
        }
    }
}
