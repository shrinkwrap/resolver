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
package org.jboss.shrinkwrap.resolver.api;

import java.io.File;

/**
 * Factory to create configured {@link ResolverSystem} instances
 *
 * @param <RESOLVERSYSTEMTYPE>
 *            Configured view type of the {@link ResolverSystem}
 * @param <CONFIGURABLERESOLVERSYSTEMTYPE>
 *            Configurable view type of the {@link ResolverSystem}
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ConfiguredResolverSystemFactory<RESOLVERSYSTEMTYPE extends ResolverSystem, CONFIGURABLERESOLVERSYSTEMTYPE extends ConfigurableResolverSystem<RESOLVERSYSTEMTYPE>> {

    /**
     * The type of {@link ConfigurableResolverSystem} to create, configure, and return
     */
    private final Class<CONFIGURABLERESOLVERSYSTEMTYPE> clazz;
    /**
     * Used to create the {@link ConfigurableResolverSystem} instance
     */
    private final ClassLoader clForCreation;

    /**
     * Creates a new instance to configure a new {@link ConfigurableResolverSystem} of the specified type, using the
     * current {@link Thread#getContextClassLoader()} for creation of the {@link ConfigurableResolverSystem}
     *
     * @param type
     * @throws IllegalArgumentException
     *             If the type is not specified
     */
    public ConfiguredResolverSystemFactory(final Class<CONFIGURABLERESOLVERSYSTEMTYPE> type)
        throws IllegalArgumentException {
        this(type, SecurityActions.getThreadContextClassLoader());
    }

    /**
     * Creates a new instance to configure a new {@link ConfigurableResolverSystem} of the specified type, using the
     * {@link Thread#getContextClassLoader()} of the specified {@link ClassLoader} for creation of the
     * {@link ConfigurableResolverSystem}
     *
     * @param type
     * @param clForCreation
     * @throws IllegalArgumentException
     *             If either argument is not specified
     */
    public ConfiguredResolverSystemFactory(final Class<CONFIGURABLERESOLVERSYSTEMTYPE> type,
        final ClassLoader clForCreation) throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("Resolver system class must be specified");
        }
        if (clForCreation == null) {
            throw new IllegalArgumentException("ClassLoader is required");
        }
        this.clazz = type;
        this.clForCreation = clForCreation;
    }

    /**
     * Configures this {@link ResolverSystem} from the specified file
     *
     * @param file
     * @throws IllegalArgumentException
     *             If the file is not specified, is a directory, or does not exist
     * @throws InvalidConfigurationFileException
     *             If the file is not in correct format
     */
    public RESOLVERSYSTEMTYPE fromFile(final File file) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        if (file == null) {
            throw new IllegalArgumentException("file must be specified");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("file specified does not exist: " + file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException("file specified is a directory: " + file.getAbsolutePath());
        }
        final CONFIGURABLERESOLVERSYSTEMTYPE resolverSystem = this.create();
        return resolverSystem.configureFromFile(file);
    }

    /**
     * Configures this {@link ResolverSystem} from the file at the specified path
     *
     * @param pathToFile
     * @throws IllegalArgumentException
     *             If the file is not specified, is a directory, or does not exist
     * @throws InvalidConfigurationFileException
     *             If the file is not in correct format
     */
    public RESOLVERSYSTEMTYPE fromFile(final String pathToFile) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        if (pathToFile == null || pathToFile.length() == 0) {
            throw new IllegalArgumentException("path to file must be specified");
        }
        final File file = new File(pathToFile);
        return this.fromFile(file);
    }

    /**
     * Configures this {@link ResolverSystem} from the result of {@link ClassLoader#getResource(String)} using the
     * current {@link Thread#getContextClassLoader()}
     *
     * @param path
     * @throws IllegalArgumentException
     *             If the path is not specified or can not be found
     * @throws InvalidConfigurationFileException
     *             If the file is not in correct format
     */
    public RESOLVERSYSTEMTYPE fromClassloaderResource(final String path) throws IllegalArgumentException,
        InvalidConfigurationFileException {
        return this.fromClassloaderResource(path, SecurityActions.getThreadContextClassLoader());
    }

    /**
     * Configures this {@link ResolverSystem} from the result of {@link ClassLoader#getResource(String)} using the
     * specified {@link ClassLoader}
     *
     * @param path
     * @throws IllegalArgumentException
     *             If the either argument is not specified or if the path can not be found
     * @throws InvalidConfigurationFileException
     *             If the file is not in correct format
     */
    public RESOLVERSYSTEMTYPE fromClassloaderResource(final String path, final ClassLoader loader)
        throws IllegalArgumentException, InvalidConfigurationFileException {
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException("path to resource must be specified");
        }
        if (loader == null) {
            throw new IllegalArgumentException("ClassLoader must be specified");
        }
        final CONFIGURABLERESOLVERSYSTEMTYPE resolverSystem = this.create();
        return resolverSystem.configureFromClassloaderResource(path, loader);
    }

    protected CONFIGURABLERESOLVERSYSTEMTYPE create() {
        return Resolvers.use(clazz, clForCreation);
    }

}
