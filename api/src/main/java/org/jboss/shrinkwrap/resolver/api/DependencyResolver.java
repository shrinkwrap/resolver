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
package org.jboss.shrinkwrap.resolver.api;

import java.io.File;
import java.util.Collection;

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface DependencyResolver<F extends DependencyResolutionFilter<F, E>, E extends ResolutionElement<E>> {

    /**
     * Resolves dependencies for dependency builder
     *
     * @param archiveView End-user view of the archive requested (ie. {@link GenericArchive} or {@link JavaArchive})
     * @return An array of archives which contains resolved artifacts.
     * @throws ResolutionException If artifacts could not be resolved
     * @throws {@link IllegalArgumentException} If target archive view is not supplied
     */
    <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView)
            throws ResolutionException;

    /**
     * Resolves dependencies for dependency builder. Uses a filter to limit dependency tree
     *
     * @param archiveView End-user view of the archive requested (ie. {@link GenericArchive} or {@link JavaArchive})
     * @param filter The filter to limit the dependencies during resolution
     * @return An array of archive which contains resolved artifacts
     * @throws ResolutionException If artifact could not be resolved
     * @throws {@link IllegalArgumentException} If either argument is not supplied
     */
    <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView, F filter)
            throws ResolutionException;

    /**
     * Resolves dependencies for dependency builder
     *
     * @return An array of Files which contains resolved artifacts.
     * @throws ResolutionException If artifacts could not be resolved
     */
    File[] resolveAsFiles() throws ResolutionException;

    /**
     * Resolves dependencies for dependency builder. Uses a filter to limit dependency tree
     *
     * @param filter The filter to limit the dependencies during resolution
     * @return An array of Files which contains resolved artifacts
     * @throws IllegalArgumentException If filter is not supplied
     * @throws ResolutionException If artifacts could not be resolved
     */
    File[] resolveAsFiles(F filter) throws ResolutionException;
}
