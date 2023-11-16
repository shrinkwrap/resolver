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
package org.jboss.shrinkwrap.resolver.impl.maven.bootstrap;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;

/**
 * SecurityActions
 *
 * A set of privileged actions that are not to leak out of this package
 *
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 * @version $Revision: $
 */
final class SecurityActions {

    // -------------------------------------------------------------------------------||
    // Constructor -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * No instantiation
     */
    private SecurityActions() {
        throw new UnsupportedOperationException("No instantiation");
    }

    // -------------------------------------------------------------------------------||
    // Utility Methods ---------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    static String getProperty(final String key) {
        try {
            String value = AccessController.doPrivileged((PrivilegedExceptionAction<String>) () -> System.getProperty(key));
            return value;
        }
        // Unwrap
        catch (final PrivilegedActionException pae) {
            final Throwable t = pae.getCause();
            // Rethrow
            if (t instanceof SecurityException) {
                throw (SecurityException) t;
            }
            if (t instanceof NullPointerException) {
                throw (NullPointerException) t;
            } else if (t instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) t;
            } else {
                // No other checked Exception thrown by System.getProperty
                try {
                    throw (RuntimeException) t;
                }
                // Just in case we've really messed up
                catch (final ClassCastException cce) {
                    throw new RuntimeException("Obtained unchecked Exception; this code should never be reached", t);
                }
            }
        }
    }

    static String getEnvProperty(final String key) {
        try {
            String value = AccessController.doPrivileged((PrivilegedExceptionAction<String>) () -> System.getenv(key));
            return value;
        }
        // Unwrap
        catch (final PrivilegedActionException pae) {
            final Throwable t = pae.getCause();
            // Rethrow
            if (t instanceof SecurityException) {
                throw (SecurityException) t;
            }
            if (t instanceof NullPointerException) {
                throw (NullPointerException) t;
            } else if (t instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) t;
            } else {
                // No other checked Exception thrown by System.getProperty
                try {
                    throw (RuntimeException) t;
                }
                // Just in case we've really messed up
                catch (final ClassCastException cce) {
                    throw new RuntimeException("Obtained unchecked Exception; this code should never be reached", t);
                }
            }
        }
    }

    static Properties getProperties() {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Properties>) System::getProperties);
        }
        // Unwrap
        catch (final PrivilegedActionException pae) {
            final Throwable t = pae.getCause();
            // Rethrow
            if (t instanceof SecurityException) {
                throw (SecurityException) t;
            }
            if (t instanceof NullPointerException) {
                throw (NullPointerException) t;
            } else if (t instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) t;
            } else {
                // No other checked Exception thrown by System.getProperty
                try {
                    throw (RuntimeException) t;
                }
                // Just in case we've really messed up
                catch (final ClassCastException cce) {
                    throw new RuntimeException("Obtained unchecked Exception; this code should never be reached", t);
                }
            }
        }
    }

    /**
     * Create a new instance by finding a constructor that matches the argumentTypes signature
     * using the arguments for instantiation.
     *
     * @param className Full classname of class to create
     * @param argumentTypes The constructor argument types
     * @param arguments The constructor arguments
     * @return a new instance
     * @throws IllegalArgumentException if className, argumentTypes, or arguments are null
     * @throws RuntimeException if any exceptions during creation
     * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
     * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
     */
    static <T> T newInstance(final Class<T> implClass, final Class<?>[] argumentTypes, final Object[] arguments) {
        if (implClass == null) {
            throw new IllegalArgumentException("ImplClass must be specified");
        }
        if (argumentTypes == null) {
            throw new IllegalArgumentException("ArgumentTypes must be specified. Use empty array if no arguments");
        }
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments must be specified. Use empty array if no arguments");
        }
        final T obj;

        try {
            Constructor<T> constructor = getConstructor(implClass, argumentTypes);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            obj = constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new RuntimeException("Could not create new instance of " + implClass, e);
        }

        return obj;
    }

    /**
     * Obtains the Constructor specified from the given Class and argument types
     *
     * @param clazz
     * @param argumentTypes
     * @return
     * @throws NoSuchMethodException
     */
    static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... argumentTypes)
            throws NoSuchMethodException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Constructor<T>>)
                    () -> clazz.getDeclaredConstructor(argumentTypes));
        }
        // Unwrap
        catch (final PrivilegedActionException pae) {
            final Throwable t = pae.getCause();
            // Rethrow
            if (t instanceof NoSuchMethodException) {
                throw (NoSuchMethodException) t;
            }
            else {
                // No other checked Exception thrown by Class.getConstructor
                try {
                    throw (RuntimeException) t;
                }
                // Just in case we've really messed up
                catch (final ClassCastException cce) {
                    throw new RuntimeException("Obtained unchecked Exception; this code should never be reached", t);
                }
            }
        }
    }
}
