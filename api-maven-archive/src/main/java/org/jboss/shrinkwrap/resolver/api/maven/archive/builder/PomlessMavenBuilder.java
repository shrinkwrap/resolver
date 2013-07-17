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
package org.jboss.shrinkwrap.resolver.api.maven.archive.builder;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;

/**
 * Instance of {@link MavenBuilder} that allows to load configuration from a POM file
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface PomlessMavenBuilder {

    /**
     * Configures the Maven Builder from Project Object Model contained in the specified POM {@link File}.
     *
     * @param pomFile
     * @return
     * @throws IllegalArgumentException If no file was specified, if the file does not exist or points to a directory
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenBuilder loadPomFromFile(File pomFile) throws IllegalArgumentException, InvalidConfigurationFileException;

    /**
     * Configures the Maven Builder from Project Object Model contained in the specified POM {@link File}.
     *
     * @param pomFile
     * @param profiles Active/inactive profiles
     * @return
     * @throws IllegalArgumentException If no file was specified, if the file does not exist or points to a directory
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenBuilder loadPomFromFile(File pomFile, String... profiles) throws IllegalArgumentException,
            InvalidConfigurationFileException;

    /**
     * Configures the Maven Builder from Project Object Model contained in the POM file located at the specified path. The path
     * will be represented as a new {@link File} by means of {@link File#File(String)}
     *
     * @param pathToPomFile
     * @return
     * @throws IllegalArgumentException If no path was specified, or if the path points to a file which does not exist or is a
     *         directory
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenBuilder loadPomFromFile(String pathToPomFile) throws IllegalArgumentException,
            InvalidConfigurationFileException;

    /**
     * Configures the Maven Builder from Project Object Model contained in the POM file located at the specified path. The path
     * will be represented as a new {@link File} by means of {@link File#File(String)}
     *
     * @param pathToPomFile
     * @param profiles Active/inactive profiles
     * @return
     * @throws IllegalArgumentException If no path was specified, or if the path points to a file which does not exist or is a
     *         directory
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenBuilder loadPomFromFile(String pathToPomFile, String... profiles) throws IllegalArgumentException,
            InvalidConfigurationFileException;

    /**
     * Configures the Maven Builder from Project Object Model contained in the POM file located at the specified
     * {@link ClassLoader} resource path, loaded by the current {@link Thread#getContextClassLoader()}.
     *
     * @param pathToPomResource
     * @return
     * @throws IllegalArgumentException If no path was specified, or if the resource could not be found at the specified path
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenBuilder loadPomFromClassLoaderResource(String pathToPomResource) throws IllegalArgumentException,
            InvalidConfigurationFileException;

    /**
     * Configures the Maven Builder from Project Object Model contained in the POM file located at the specified
     * {@link ClassLoader} resource path, loaded by the specified {@link ClassLoader}.
     *
     * @param pathToPomResource
     * @param cl
     * @return
     * @throws IllegalArgumentException If no path was specified, no ClassLoader was specified, or if the resource could not be
     *         found at the specified path
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenBuilder loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl)
            throws IllegalArgumentException, InvalidConfigurationFileException;

    /**
     * Configures the Maven Builder from Project Object Model contained in the POM file located at the specified
     * {@link ClassLoader} resource path, loaded by the specified {@link ClassLoader}.
     *
     * @param pathToPomResource
     * @param cl
     * @param profiles Active/inactive profiles
     * @return
     * @throws IllegalArgumentException If no path was specified, no ClassLoader was specified, any specified profiles are
     *         invalid or null, or if the resource could not be found at the specified path
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    PomEquippedMavenBuilder loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl, String... profiles)
            throws IllegalArgumentException, InvalidConfigurationFileException;
}
