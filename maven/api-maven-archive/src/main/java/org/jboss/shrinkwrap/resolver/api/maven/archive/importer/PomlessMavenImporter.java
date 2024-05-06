/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.api.maven.archive.importer;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;

/**
 * Instance of {@link MavenImporter} that allows to load configuration from a POM file
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface PomlessMavenImporter {

    /**
     * Configures the Maven Importer from Project Object Model contained in the specified POM {@link File}.
     *
     * @param pomFile A POM {@link File} the maven Importer should be configured from.
     * @return The configured Maven Importer from the given Project Object Model.
     * @throws IllegalArgumentException If no file was specified, if the file does not exist or points to a directory
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenImporter loadPomFromFile(File pomFile) throws IllegalArgumentException, InvalidConfigurationFileException;

    /**
     * Configures the Maven Importer from Project Object Model contained in the specified POM {@link File}.
     *
     * @param pomFile A POM {@link File} the maven Importer should be configured from.
     * @param profiles Active/inactive profiles
     * @return The configured Maven Importer from the given Project Object Model.
     * @throws IllegalArgumentException If no file was specified, if the file does not exist or points to a directory
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenImporter loadPomFromFile(File pomFile, String... profiles) throws IllegalArgumentException,
            InvalidConfigurationFileException;

    /**
     * Configures the Maven Importer from Project Object Model contained in the POM file located at the specified path. The path
     * will be represented as a new {@link File} by means of {@link File#File(String)}
     *
     * @param pathToPomFile A path to a POM file the maven Importer should be configured from.
     * @return The configured Maven Importer from the given Project Object Model.
     * @throws IllegalArgumentException If no path was specified, or if the path points to a file which does not exist or is a
     *         directory
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenImporter loadPomFromFile(String pathToPomFile) throws IllegalArgumentException,
            InvalidConfigurationFileException;

    /**
     * Configures the Maven Importer from Project Object Model contained in the POM file located at the specified path. The path
     * will be represented as a new {@link File} by means of {@link File#File(String)}
     *
     * @param pathToPomFile A path to a POM file the maven Importer should be configured from.
     * @param profiles Active/inactive profiles
     * @return The configured Maven Importer from the given Project Object Model.
     * @throws IllegalArgumentException If no path was specified, or if the path points to a file which does not exist or is a
     *         directory
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenImporter loadPomFromFile(String pathToPomFile, String... profiles) throws IllegalArgumentException,
            InvalidConfigurationFileException;

    /**
     * Configures the Maven Importer from Project Object Model contained in the POM file located at the specified
     * {@link ClassLoader} resource path, loaded by the current {@link Thread#getContextClassLoader()}.
     *
     * @param pathToPomResource A {@link ClassLoader} resource path to a POM file the maven Importer should be configured from.
     * @return The configured Maven Importer from the given Project Object Model.
     * @throws IllegalArgumentException If no path was specified, or if the resource could not be found at the specified path
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenImporter loadPomFromClassLoaderResource(String pathToPomResource) throws IllegalArgumentException,
            InvalidConfigurationFileException;

    /**
     * Configures the Maven Importer from Project Object Model contained in the POM file located at the specified
     * {@link ClassLoader} resource path, loaded by the specified {@link ClassLoader}.
     *
     * @param pathToPomResource A {@link ClassLoader} resource path to a POM file the maven Importer should be configured from.
     * @param cl A {@link ClassLoader}
     * @return The configured Maven Importer from the given Project Object Model.
     * @throws IllegalArgumentException If no path was specified, no ClassLoader was specified, or if the resource could not be
     *         found at the specified path
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenImporter loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl)
            throws IllegalArgumentException, InvalidConfigurationFileException;

    /**
     * Configures the Maven Importer from Project Object Model contained in the POM file located at the specified
     * {@link ClassLoader} resource path, loaded by the specified {@link ClassLoader}.
     *
     * @param pathToPomResource A {@link ClassLoader} resource path to a POM file the maven Importer should be configured from.
     * @param cl A {@link ClassLoader}
     * @param profiles Active/inactive profiles
     * @return The configured Maven Importer from the given Project Object Model.
     * @throws IllegalArgumentException If no path was specified, no ClassLoader was specified, any specified profiles are
     *         invalid or null, or if the resource could not be found at the specified path
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenImporter loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl, String... profiles)
            throws IllegalArgumentException, InvalidConfigurationFileException;

    /**
     * <i>Optional operation</i>. Sets whether resolution should be done in "offline" (i.e. not connected to Internet) mode.
     * By default, resolution is done in online mode
     *
     * @param offline Whether resolution should be done in "offline". By default, resolution is done in online mode.
     *
     * @return Modified {@link PomlessMavenImporter} instance
     */
    PomlessMavenImporter offline(boolean offline);

    /**
     * <i>Optional operation</i>. Sets that resolution should be done in "offline" (i.e. not connected to Internet) mode. Alias to
     * {@link ConfiguredMavenImporter#offline(boolean)}, passing <code>true</code> as a parameter.
     *
     * @return Modified {@link PomlessMavenImporter} instance
     */
    PomlessMavenImporter offline();
}
