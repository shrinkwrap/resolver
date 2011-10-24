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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

/**
 * Utility capable of creating {@link ResolverEntryPoint} instances given a requested end-user view.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
final class ResolverEntryPointInstantiator {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Classpath location under which mapping configuration between ens-user view and implementation types is located
     */
    private static final String MAPPING_LOCATION = "META-INF/services/";

    /**
     * Key of the property denoting the implementation class for a given end-user view type
     */
    private static final String KEY_IMPL_CLASS_NAME = "implClass";

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Internal constructor; not to be called
     */
    private ResolverEntryPointInstantiator() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    // -------------------------------------------------------------------------------------||
    // Functional Methods -----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new {@link ResolverEntryPoint} instance of the specified user view type. Will consult a configuration file visible
     * to the {@link Thread} Context {@link ClassLoader} named "META-INF/services/$fullyQualfiedClassName" which should contain
     * a key=value format with the key {@link ResolverEntryPointInstantiator#KEY_IMPL_CLASS_NAME}. The implementation class name
     * must have a no-arg constructor.
     *
     * @param <T>
     * @param userViewClass
     * @return
     * @throws IllegalArgumentException If the user view class was not specified
     */
    static <T extends ResolverEntryPoint<T>> T createFromUserView(final Class<T> userViewClass) throws IllegalArgumentException {
        // Get the impl class for the specified user view
        final Class<T> implClass = getImplClassForUserView(userViewClass);

        // Get the constructor to use in making the new instance
        final Constructor<T> ctor;
        try {
            ctor = SecurityActions.getConstructor(implClass, new Class<?>[] {});
        } catch (final NoSuchMethodException nsme) {
            throw new RuntimeException(implClass + " must contain a public no args contructor");
        }

        // Create a new instance using the backing model
        final ResolverEntryPoint<T> dependencyType;
        try {
            dependencyType = ctor.newInstance();
        }
        // Handle all construction errors equally
        catch (final Exception e) {
            throw new RuntimeException("Could not create new descriptor instance", e);
        }

        // Return
        return userViewClass.cast(dependencyType);

    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Members ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains the {@link ResolverEntryPoint} class for the giving end user view, using a configuration file loaded from the TCCL of
     * name "META-INF/services.$fullyQualifiedClassName" having properties as described by
     * {@link ResolverEntryPointInstantiator#createFromUserView(Class)}.
     *
     * @param userViewClass
     * @return The construction information needed to create new instances conforming to the user view
     * @throws IllegalArgumentException If the user view was not specified
     */
    private static <T extends ResolverEntryPoint<T>> Class<T> getImplClassForUserView(final Class<?> userViewClass)
            throws IllegalArgumentException {
        // Precondition checks
        if (userViewClass == null) {
            throw new IllegalArgumentException("User view class must be specified");
        }

        // Get the configuration from which we'll create new instances
        final String className = userViewClass.getName();
        final String resourceName = MAPPING_LOCATION + className;
        final ClassLoader tccl = SecurityActions.getThreadContextClassLoader();
        final InputStream resourceStream = tccl.getResourceAsStream(resourceName);
        if (resourceStream == null) {
            throw new IllegalArgumentException("No resource " + resourceName + " was found configured for user view class "
                    + userViewClass.getName());
        }

        // Load
        final Properties props = new Properties();
        try {
            props.load(resourceStream);
        } catch (final IOException e) {
            throw new RuntimeException("I/O Problem in reading the properties for " + userViewClass.getName(), e);
        }
        final String implClassName = props.getProperty(KEY_IMPL_CLASS_NAME);
        if (implClassName == null || implClassName.length() == 0) {
            throw new IllegalStateException("Resource " + resourceName + " for " + userViewClass + " does not contain key "
                    + KEY_IMPL_CLASS_NAME);
        }

        // Load the Implementation class
        final Class<T> implClass;
        try {
            implClass = getImplClassForName(tccl, implClassName);
        } catch (final ClassNotFoundException e) {
            // Rethrow with some context
            throw new IllegalStateException(
                    "Could not load specified implementation class from " + tccl + ": " + implClassName, e);
        }

        // Return
        return implClass;
    }

    /**
     * Uses the specified {@link ClassLoader} to create and return a {@link Class} instance for the specified class name.
     * Externalized to suppress unchecked warnings.
     *
     * @param <T>
     * @param cl
     * @param implClassName
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T extends ResolverEntryPoint<T>> Class<T> getImplClassForName(final ClassLoader cl, final String implClassName)
            throws ClassNotFoundException {
        assert cl != null : "CL is required";
        assert implClassName != null && implClassName.length() > 0 : "Impl Class name is required";
        return (Class<T>) Class.forName(implClassName, false, cl);
    }

}
