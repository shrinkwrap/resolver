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

import java.util.Collection;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyShortcut;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyShortcut;
import org.jboss.shrinkwrap.resolver.api.maven.filter.StrictFilter;

/**
 * Shortcut API implementation for Maven artifact builder which holds and construct dependencies and is able to resolve them
 * into ShrinkWrap archives.
 *
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class MavenDependencyShortcutImpl implements MavenDependencyShortcut {

    private MavenDependencyResolver delegate;

    public MavenDependencyShortcutImpl() {
        delegate = new MavenDependencyResolverImpl();
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

        Collection<GenericArchive> result = delegate.artifact(coordinates).resolveAs(GenericArchive.class, new StrictFilter());

        if (result != null && result.size() != 1) {
            throw new ResolutionException("Only one artifact should have been resolved. Resolved " + result.size()
                    + " artifacts.");
        }
        return result.iterator().next();
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
        return delegate.artifacts(coordinates).resolveAs(GenericArchive.class, new StrictFilter());
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
    public EffectivePomMavenDependencyShortcut withPom(final String path, String... profiles) throws ResolutionException {
        this.delegate = delegate.loadEffectivePom(path, profiles).up();
        return this;
    }

}
