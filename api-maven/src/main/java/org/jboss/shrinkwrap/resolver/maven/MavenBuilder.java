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
package org.jboss.shrinkwrap.resolver.maven;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.ResolutionException;

/**
 * A dependency builder encapsulates access to a repository which is used to
 * resolve dependencies.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public interface MavenBuilder {

	/**
	 * Creates an artifact builder. You can define additional parameters for the
	 * artifact later.
	 * 
	 * @param coordinates
	 *            Coordinates specified to a created artifact, specified in the
	 *            format
	 *            {@code <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]}
	 *            , must not be {@code null} or empty. If {@code version} is not
	 *            specified, is it determined if underlying repository system
	 *            supports so.
	 * 
	 * @return A new artifact builder
	 * @throws ResolutionException
	 *             If artifact coordinates are wrong or if version cannot be
	 *             determined.
	 */
	MavenArtifactBuilder artifact(String coordinates)
			throws ResolutionException;

	/**
	 * Creates an artifact builder. You can define additional parameters for the
	 * artifacts later. Additional parameters will be changed for all artifacts
	 * defined by this call.
	 * 
	 * @param coordinates
	 *            A list of coordinates specified to the created artifacts,
	 *            specified in the format
	 *            {@code <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]}
	 *            , must not be {@code null} or empty. If {@code version} is not
	 *            specified, is it determined if underlying repository system
	 *            supports so.
	 * @return A new artifact builder
	 * @throws ResolutionException
	 *             If artifact coordinates are wrong or if version cannot be
	 *             determined.
	 */
	MavenArtifactsBuilder artifacts(String... coordinates)
			throws ResolutionException;

	/**
	 * Configures Maven from a settings.xml file
	 * 
	 * @param path
	 *            A path to a settings.xml configuration file
	 * @return A dependency builder with a configuration from given file
	 */
	MavenBuilder configureFrom(String path);

	/**
	 * Loads remote repositories for a POM file. If repositories are defined in
	 * the parent of the POM file and there are accessible via local file
	 * system, they are set as well.
	 * 
	 * These remote repositories are used to resolve the artifacts during
	 * dependency resolution.
	 * 
	 * Additionally, it loads dependencies defined in the POM file model in an
	 * internal cache, which can be later used to resolve an artifact without
	 * explicitly specifying its version.
	 * 
	 * @param path
	 *            A path to the POM file, must not be {@code null} or empty
	 * @return A dependency builder with remote repositories set according to
	 *         the content of POM file.
	 * @throws Exception
	 */
	MavenBuilder loadPom(String path) throws ResolutionException;

	/**
	 * Uses dependencies and remote repositories defined in a POM file to and
	 * tries to resolve them
	 * 
	 * @param path
	 *            A path to the POM file
	 * @return An array of ShrinkWrap archives
	 * @throws DependencyException
	 *             If dependencies could not be resolved or the POM processing
	 *             failed
	 */
	Archive<?>[] resolveFrom(String path) throws ResolutionException;

	/**
	 * Uses dependencies and remote repositories defined in a POM file to and
	 * tries to resolve them
	 * 
	 * @param path
	 *            A path to the POM file
	 * @return An array of ShrinkWrap archives
	 * @throws DependencyException
	 *             If dependencies could not be resolved or the POM processing
	 *             failed
	 */
	Archive<?>[] resolveFrom(String path, MavenResolutionFilter filter)
			throws ResolutionException;

}
