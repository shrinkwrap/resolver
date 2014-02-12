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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * Represents a wrapper on top of Java Reflection API. It makes indirect execution much easier
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class Invokable {

    private final Class<?> classType;

    /**
     * Creates a new instance of Invokable. Class itself is loaded by provided classloader
     *
     * @param cl classloader
     * @param className fully qualified class name
     */
    Invokable(ClassLoader cl, String className) {
        this.classType = loadClass(cl, className);
    }

    /**
     * Creates a new instance of Invokable. Class itself is reloaded by provider classloader
     *
     * @param cl classloader
     * @param classType class object. Class with the same fully qualified name will be loader from provided classloader
     */
    Invokable(ClassLoader cl, Class<?> classType) {
        this.classType = reloadClass(cl, classType);
    }

    /**
     * Loads a class from classloader
     *
     * @param cl classloader to be used
     * @param classTypeName fully qualified class name
     * @return
     * @throws InvocationException if class was not found in classloader
     */
    static Class<?> loadClass(ClassLoader cl, String classTypeName) throws InvocationException {
        try {
            return cl.loadClass(classTypeName);
        } catch (ClassNotFoundException e) {
            throw new InvocationException(e, "Unable to load class {0} with class loader {1}", classTypeName, cl);
        }
    }

    /**
     * Reloads a class from classloader using same fully qualified class name
     *
     * @param cl classloader to be used
     * @param classType class object
     * @return
     * @throws InvocationException if class was not found in classloader
     */
    static Class<?> reloadClass(ClassLoader cl, Class<?> classType) throws InvocationException {
        try {
            return cl.loadClass(classType.getName());
        } catch (ClassNotFoundException e) {
            throw new InvocationException(e, "Unable to reload class {0} with class loader {1}, previously loaded with {2}",
                    classType.getName(), cl, classType.getClassLoader());
        }
    }

    /**
     * Invokes method on class registered within {@link Invokable}. It looks also for superclasses
     *
     * @param name name of the method
     * @param parameterTypes parameter types of the method
     * @param instance instance on which method is called, {@code null} for static methods
     * @param parameters parameters for method invocation
     * @return
     * @throws InvocationException if method was not found or could not be invoked
     */
    Object invokeMethod(String name, Class<?>[] parameterTypes, Object instance, Object[] parameters)
            throws InvocationException {
        try {
            return findMethod(name, parameterTypes).invoke(instance, parameters);
        } catch (IllegalAccessException e) {
            throw new InvocationException(e, "Unable to invoke {0}({1}) on object {2} with parameters {3}", name,
                    parameterTypes, instance == null ? "" : instance.getClass().getName(), parameters);
        } catch (IllegalArgumentException e) {
            throw new InvocationException(e, "Unable to invoke {0}({1}) on object {2} with parameters {3}", name,
                    parameterTypes, instance == null ? "" : instance.getClass().getName(), parameters);
        } catch (InvocationTargetException e) {
            throw new InvocationException(e, "Unable to invoke {0}({1}) on object {2} with parameters {3}", name,
                    parameterTypes, instance == null ? "" : instance.getClass().getName(), parameters);
        } catch (SecurityException e) {
            throw new InvocationException(e, "Unable to invoke {0}({1}) on object {2} with parameters {3}", name,
                    parameterTypes, instance == null ? "" : instance.getClass().getName(), parameters);
        } catch (InvocationException e) {
            throw new InvocationException(e, "Unable to invoke {0}({1}) on object {2} with parameters {3}", name,
                    parameterTypes, instance == null ? "" : instance.getClass().getName(), parameters);
        }
    }

    /**
     * Creates a new instance of the class registered within {@link Invokable}.
     *
     * @param parameterTypes parameter types of constructor
     * @param parameters parameter values
     * @return new instance
     * @throws InvocationException If constructor was not found or could not be invoked
     */
    Object invokeConstructor(Class<?>[] parameterTypes, Object[] parameters) throws InvocationException {

        try {
            Constructor<?> con = classType.getConstructor(parameterTypes);
            return con.newInstance(parameters);
        } catch (NoSuchMethodException e) {
            throw new InvocationException(e, "Unable to invoke constructor {0}({1}) with parameters {3}",
                    classType.getSimpleName(),
                    parameterTypes, parameters);
        } catch (SecurityException e) {
            throw new InvocationException(e, "Unable to invoke constructor {0}({1}) with parameters {3}",
                    classType.getSimpleName(),
                    parameterTypes, parameters);
        } catch (InstantiationException e) {
            throw new InvocationException(e, "Unable to invoke constructor {0}({1}) with parameters {3}",
                    classType.getSimpleName(),
                    parameterTypes, parameters);
        } catch (IllegalAccessException e) {
            throw new InvocationException(e, "Unable to invoke constructor {0}({1}) with parameters {3}",
                    classType.getSimpleName(),
                    parameterTypes, parameters);
        } catch (IllegalArgumentException e) {
            throw new InvocationException(e, "Unable to invoke constructor {0}({1}) with parameters {3}",
                    classType.getSimpleName(),
                    parameterTypes, parameters);
        } catch (InvocationTargetException e) {
            throw new InvocationException(e, "Unable to invoke constructor {0}({1}) with parameters {3}",
                    classType.getSimpleName(),
                    parameterTypes, parameters);
        }
    }

    private Method findMethod(String name, Class<?>[] parameterTypes) throws SecurityException, InvocationException {

        Method m = null;
        try {
            m = classType.getDeclaredMethod(name, parameterTypes);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            // ignore
        }

        // look for parent classes
        Class<?> iterator = classType.getSuperclass();
        while (iterator != null) {
            try {
                m = iterator.getDeclaredMethod(name, parameterTypes);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
                // ignore, try parent
                iterator = iterator.getSuperclass();
            }
        }

        /*
         * for (Class<?> iface : classType.getInterfaces()) {
         * try {
         * m = iface.getDeclaredMethod(name, parameterTypes);
         * m.setAccessible(true);
         * return m;
         * } catch (NoSuchMethodException e) {
         * // ignore, try next
         * }
         * }
         */

        throw new InvocationException(
                "Unable to find method {0}({1}) in class {2} nor its superclasses or implemented interfaces", name,
                parameterTypes, classType.getName());

    }

    /**
     * Represents an invocation exception that can happen during reflection calls
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    static class InvocationException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        InvocationException(Throwable e) {
            super(e);
        }

        InvocationException(Throwable e, String pattern, Object... args) {
            super(MessageFormat.format(pattern, args), e);
        }

        InvocationException(String pattern, Object... args) {
            super(MessageFormat.format(pattern, args));
        }
    }
}
