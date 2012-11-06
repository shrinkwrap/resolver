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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
/**
 * Utility capable of creating {@link ResolverSystem} instances given a requested end-user view.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
final class ResolverSystemFactory {

    private static final String CLASS_NAME_SERVICELOADER = "org.jboss.shrinkwrap.resolver.spi.loader.ServiceLoader";
    private static final String CLASS_NAME_SPISERVICELOADER = "org.jboss.shrinkwrap.resolver.spi.loader.SpiServiceLoader";
    private static final String CLASS_NAME_SERVICEREGISTRY = "org.jboss.shrinkwrap.resolver.spi.loader.ServiceRegistry";
    private static final String METHOD_NAME_ONLY_ONE = "onlyOne";
    private static final String METHOD_NAME_REGISTER = "register";

    /**
     * Internal constructor; not to be called
     */
    private ResolverSystemFactory() {
        throw new UnsupportedOperationException("No instances permitted");
    }

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
     */
    static <RESOLVERSYSTEMTYPE extends ResolverSystem> RESOLVERSYSTEMTYPE createFromUserView(
        final Class<RESOLVERSYSTEMTYPE> userViewClass, final ClassLoader cl) {

        assert userViewClass != null : "user view class must be specified";
        assert cl != null : "ClassLoader must be specified";

        try {
            final Class<?> spiServiceLoaderClass = cl.loadClass(CLASS_NAME_SPISERVICELOADER);
            final Constructor<?> serviceLoaderCtor = spiServiceLoaderClass.getConstructor(ClassLoader.class);
            final Object spiServiceLoader = serviceLoaderCtor.newInstance(cl);
            final Method onlyOneMethod = spiServiceLoader.getClass().getMethod(METHOD_NAME_ONLY_ONE, Class.class,
                Class.class);
            final Object serviceLoader = onlyOneMethod.invoke(spiServiceLoader, spiServiceLoaderClass,
                spiServiceLoader.getClass());
            final Class<?> serviceRegistryClass = cl.loadClass(CLASS_NAME_SERVICEREGISTRY);
            final Class<?> serviceLoaderClass = cl.loadClass(CLASS_NAME_SERVICELOADER);
            final Constructor<?> serviceRegistryCtor = serviceRegistryClass.getConstructor(serviceLoaderClass);
            final Object serviceRegistry = serviceRegistryCtor.newInstance(serviceLoader);
            final Method registerMethod = serviceRegistry.getClass().getMethod(METHOD_NAME_REGISTER,
                serviceRegistry.getClass());
            registerMethod.invoke(null, serviceRegistry);
            final Method onlyOneMethodSingleArg = serviceRegistry.getClass().getMethod(METHOD_NAME_ONLY_ONE,
                Class.class);
            final Object userViewObject = onlyOneMethodSingleArg.invoke(serviceRegistry, userViewClass);
            return userViewClass.cast(userViewObject);
        } catch (final Exception e) {
            // Don't bother to catch all the reflection exceptions separately
            throw new RuntimeException("Could not create object from user view", e);
        }
    }

}
