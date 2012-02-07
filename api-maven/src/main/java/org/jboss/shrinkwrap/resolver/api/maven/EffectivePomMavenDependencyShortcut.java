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
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

/**
 * An artifact builder is object which holds and construct dependencies and it is able to resolve them into an array of
 * ShrinkWrap archives.
 *
 * Artifact builder allows chaining of artifacts, that is specifying a new artifact. In this case, currently constructed
 * artifact is stored as a dependency and user is allowed to specify parameters for another artifact.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public interface EffectivePomMavenDependencyShortcut {

    /**
     * Resolves dependency for dependency builder.
     *
     * @param archiveView End-user view of the archive requested (ie. {@link GenericArchive} or {@link JavaArchive})
     * @return An archive of the resolved artifact.
     * @throws ResolutionException If artifact could not be resolved
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    GenericArchive dependency(String coordinates) throws ResolutionException;

    /**
     * Resolves dependencies for dependency builder.
     *
     * @param archiveView End-user view of the archive requested (ie. {@link GenericArchive} or {@link JavaArchive})
     * @return An array of archives which contains resolved artifacts.
     * @throws ResolutionException If artifacts could not be resolved
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    Collection<GenericArchive> dependencies(String... coordinates) throws ResolutionException;

    /**
     * Resolves dependency for dependency builder.
     *
     * @param coordinates Coordinates specified to a created artifact, specified in an implementation-specific format.
     * @return A File which contain resolved artifact.
     * @throws ResolutionException If artifact could not be resolved
     */
    File resolveAsFile(String coordinates) throws ResolutionException;

    /**
     * Resolves dependencies for dependency builder.
     *
     * @param coordinates A list of coordinates specified to the created artifacts, specified in an implementation-specific
     *        format.
     * @return An array of Files which contains resolved artifacts
     * @throws ResolutionException If artifact could not be resolved
     */
    File[] resolveAsFiles(String... coordinates) throws ResolutionException;
}
