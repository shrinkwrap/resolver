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

import org.jboss.shrinkwrap.resolver.api.loadable.ServiceLoader;
import org.jboss.shrinkwrap.resolver.api.loadable.ServiceRegistry;
import org.jboss.shrinkwrap.resolver.api.loadable.SpiServiceLoader;

/**
 * Utility capable of creating {@link ResolverSystem} instances given a requested end-user view.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
final class ResolverSystemFactory {
    // -------------------------------------------------------------------------------------||
    // Class Members -----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    // -------------------------------------------------------------------------------------||
    // Constructor -------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Internal constructor; not to be called
     */
    private ResolverSystemFactory() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    // -------------------------------------------------------------------------------------||
    // Functional Methods ------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new {@link ResolverSystem} instance of the specified user view type using the {@link Thread} Context
     * {@link ClassLoader}. Will consult a configuration file visible to the {@link Thread} Context {@link ClassLoader} named
     * "META-INF/services/$fullyQualfiedClassName" which should contain a key=value format with the key
     * {@link ResolverSystemFactory#KEY_IMPL_CLASS_NAME}. The implementation class name must have a no-arg constructor.
     *
     * @param userViewClass
     * @return
     * @throws IllegalArgumentException
     * If the user view class was not specified
     */
    static <RESOLVERSYSTEMTYPE extends ResolverSystem> RESOLVERSYSTEMTYPE createFromUserView(
            final Class<RESOLVERSYSTEMTYPE> userViewClass) throws IllegalArgumentException {
        return createFromUserView(userViewClass, SecurityActions.getThreadContextClassLoader());
    }

    /**
     * Creates a new {@link ResolverSystem} instance of the specified user view type using the specified {@link ClassLoader}.
     * Will consult a configuration file visible to the specified {@link ClassLoader} named
     * "META-INF/services/$fullyQualfiedClassName" which should contain a key=value format with the key
     * {@link ResolverSystemFactory#KEY_IMPL_CLASS_NAME}. The implementation class name must have a no-arg constructor.
     *
     *
     * @param userViewClass
     * @param cl
     * @return
     * @throws IllegalArgumentException
     * If either argument was not specified
     */
    static <RESOLVERSYSTEMTYPE extends ResolverSystem> RESOLVERSYSTEMTYPE createFromUserView(
            final Class<RESOLVERSYSTEMTYPE> userViewClass, final ClassLoader cl) throws IllegalArgumentException {

        // create service loader using default SPI Service Loader
        // use SPI Service loader to check if other Service Loader was registered and if so, use it instead of default one
        ServiceLoader loader = new SpiServiceLoader(cl).onlyOne(ServiceLoader.class, SpiServiceLoader.class);
        if (loader instanceof SpiServiceLoader) {
            ((SpiServiceLoader) loader).setClassLoader(cl);
        }

        // create and register service registry
        ServiceRegistry registry = new ServiceRegistry(loader);
        ServiceRegistry.register(registry);

        // load implementation and cache results in registry
        return userViewClass.cast(registry.onlyOne(userViewClass));
    }

}
