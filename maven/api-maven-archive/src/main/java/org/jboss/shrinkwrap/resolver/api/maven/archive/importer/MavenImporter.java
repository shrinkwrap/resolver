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

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.ConfigurableResolverSystem;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;

/**
 * MavenImporter is an abstraction of Maven Package phase for ShrinkWrap. It allows to package an archive based on pom.xml file.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface MavenImporter extends PomlessMavenImporter, Assignable {

    /**
     * <i>Optional operation</i>. Configures this {@link MavenImporter} from the specified settings.xml file
     *
     * @param file A settings.xml {@link File} this {@link MavenImporter} should be configured from.
     * @return This configured {@link MavenImporter}
     * @throws IllegalArgumentException If the file is not specified, is a directory, or does not exist
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     * @throws UnsupportedOperationException If this {@link ConfigurableResolverSystem} does not support configuration by
     *         {@link File}
     */
    ConfiguredMavenImporter configureFromFile(File file) throws IllegalArgumentException, UnsupportedOperationException,
            InvalidConfigurationFileException;

    /**
     * <i>Optional operation</i>. Configures this {@link MavenImporter} from the specified settings.xml file at the specified
     * path
     *
     * @param pathToFile A path to a settings.xml file this {@link MavenImporter} should be configured from.
     * @return This configured {@link MavenImporter}
     * @throws IllegalArgumentException If the file is not specified, is a directory, or does not exist
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     * @throws UnsupportedOperationException If this {@link ConfigurableResolverSystem} does not support configuration by
     *         {@link File}
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    ConfiguredMavenImporter configureFromFile(String pathToFile) throws IllegalArgumentException,
            UnsupportedOperationException, InvalidConfigurationFileException;

    /**
     * <i>Optional operation</i>. Configures this {@link MavenImporter} from the result of
     * {@link ClassLoader#getResource(String)} call using the current {@link Thread#getContextClassLoader()}
     *
     * @param path A {@link ClassLoader} path to a settings.xml file this {@link MavenImporter} should be configured from.
     * @return This configured {@link MavenImporter}
     * @throws IllegalArgumentException If the either argument is not specified or if the path can not be found
     * @throws UnsupportedOperationException If this {@link ConfigurableResolverSystem} does not support configuration by
     *         {@link ClassLoader} resource
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    ConfiguredMavenImporter configureFromClassloaderResource(String path) throws IllegalArgumentException,
            UnsupportedOperationException, InvalidConfigurationFileException;

    /**
     * <i>Optional operation</i>. Configures this {@link ConfigurableResolverSystem} from the result of
     * {@link ClassLoader#getResource(String)} using the specified {@link ClassLoader}
     *
     * @param path A {@link ClassLoader} path to a settings.xml file this {@link MavenImporter} should be configured from.
     * @param cl A {@link ClassLoader}
     * @return This configured {@link MavenImporter}
     * @throws IllegalArgumentException If the either argument is not specified or if the path can not be found
     * @throws UnsupportedOperationException If this {@link ConfigurableResolverSystem} does not support configuration by
     *         {@link ClassLoader} resource
     * @throws InvalidConfigurationFileException If the configuration file contents are not in appropriate format
     */
    ConfiguredMavenImporter configureFromClassloaderResource(String path, ClassLoader cl) throws IllegalArgumentException,
            UnsupportedOperationException, InvalidConfigurationFileException;

}
