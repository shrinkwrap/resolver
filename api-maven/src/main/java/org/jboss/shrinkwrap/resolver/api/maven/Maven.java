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

package org.jboss.shrinkwrap.resolver.api.maven;

import java.io.File;
import java.util.Collection;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

/**
 * Shortcut API for Maven artifact builder which holds and construct dependencies and is able to resolve them into
 * ShrinkWrap archives.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class Maven {

    /**
     * Resolves dependency for dependency builder.
     *
     * @param coordinates
     *            Coordinates specified to a created artifact, specified in an implementation-specific format.
     * @return An archive of the resolved artifact.
     * @throws ResolutionException
     *             If artifact coordinates are wrong or if version cannot be determined.
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    public static GenericArchive dependency(String coordinates) throws ResolutionException {
        return DependencyResolvers.use(MavenDependencyShortcut.class).dependency(coordinates);
    }

    /**
     * Resolves dependencies for dependency builder.
     *
     * @param coordinates
     *            A list of coordinates specified to the created artifacts, specified in an implementation-specific
     *            format.
     * @return An array of archives which contains resolved artifacts.
     * @throws ResolutionException
     *             If artifact coordinates are wrong or if version cannot be determined.
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    public static Collection<GenericArchive> dependencies(String... coordinates) throws ResolutionException {
        return DependencyResolvers.use(MavenDependencyShortcut.class).dependencies(coordinates);
    }

    /**
     * Resolves dependency for dependency builder.
     *
     * @param coordinates
     *            Coordinates specified to a created artifact, specified in an implementation-specific format.
     * @return A File which contain resolved artifact.
     * @throws ResolutionException
     *             If artifact could not be resolved
     */
    public static File resolveAsFile(String coordinates) throws ResolutionException {
        return DependencyResolvers.use(MavenDependencyShortcut.class).resolveAsFile(coordinates);
    }

    /**
     * Resolves dependencies for dependency builder.
     *
     * @param coordinates
     *            A list of coordinates specified to the created artifacts, specified in an implementation-specific
     *            format.
     * @return An array of Files which contains resolved artifacts
     * @throws ResolutionException
     *             If artifact could not be resolved
     */
    public static File[] resolveAsFiles(String... coordinates) throws ResolutionException {
        return DependencyResolvers.use(MavenDependencyShortcut.class).resolveAsFiles(coordinates);
    }

    /**
     * Loads remote repositories for a POM file. If repositories are defined in the parent of the POM file and there are
     * accessible via local file system, they are set as well.
     *
     * These remote repositories are used to resolve the artifacts during dependency resolution.
     *
     * Additionally, it loads dependencies defined in the POM file model in an internal cache, which can be later used
     * to resolve an artifact without explicitly specifying its version.
     *
     * @param path
     *            A path to the POM file, must not be {@code null} or empty
     * @param profiles
     *            Allows user to specify which profiles will be activated. Note, profiles from settings.xml file are
     *            activated by default. If you want to disable a profile, use {@code !$ profile.name} or
     *            {@code -$ profile.name} syntax
     * @return A dependency builder with remote repositories set according to the content of POM file.
     */
    public static EffectivePomMavenDependencyShortcut withPom(String path, String... profiles) {
        return DependencyResolvers.use(MavenDependencyShortcut.class).withPom(path, profiles);
    }
}
