/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.spi.loader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic {@link ServiceLoader} implementation which uses META-INF/services registration.
 *
 * In order to register a service, create a file META-INF/services/${service.interface.name}. The content of the file should
 * list fully qualified names of interface implementations, separated by new line character.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class SpiServiceLoader implements ServiceLoader {
    private static final Logger log = Logger.getLogger(SpiServiceLoader.class.getName());

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private static final String SERVICES = "META-INF/services";

    private ClassLoader classLoader;

    /**
     * Create an instance of SPI servicSe loader
     */
    public SpiServiceLoader() {
        // Use the CL which loaded this class as a default
        this.classLoader = SpiServiceLoader.class.getClassLoader();
    }

    /**
     * Creates an instance of SPI service loader. Uses specific {@link ClassLoader} to load service implementations.
     *
     * @param classLoader
     */
    public SpiServiceLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - ServiceLoader --------------------------------------------||
    // -------------------------------------------------------------------------------------||
    @Override
    public <T> Collection<T> all(Class<T> serviceClass) {

        if (serviceClass == null) {
            throw new IllegalArgumentException("ServiceClass must be provided");
        }

        return createInstances(serviceClass, load(serviceClass));
    }

    @Override
    public <T> T onlyOne(Class<T> serviceClass) {

        Collection<T> services = all(serviceClass);

        if (services.isEmpty()) {
            throw new IllegalStateException("There are no services for serviceClass " + serviceClass.getName());
        }
        else if (services.size() > 1) {
            throw new IllegalStateException("There are more than 1 services for serviceClass " + serviceClass.getName());
        }

        return services.iterator().next();
    }

    @Override
    public <T> T onlyOne(Class<T> serviceClass, Class<? extends T> defaultImplementationClass) {

        if (defaultImplementationClass == null) {
            throw new IllegalArgumentException("DefaultImplementationClass must be provided");
        }

        Collection<T> services = all(serviceClass);

        if (services.size() == 0) {
            return createInstance(defaultImplementationClass);
        } else if (services.size() == 1) {
            return services.iterator().next();
        } else if (services.size() == 2) {
            for (T service : services) {
                if (defaultImplementationClass.equals(service.getClass())) {
                    continue;
                }
                return service;
            }

        }
        throw new IllegalStateException("There is more then a one service for serviceClass " + serviceClass.getName());
    }

    // -------------------------------------------------------------------------------------||
    // Getters and setters -----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods - Service Loading -------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private <T> Set<Class<? extends T>> load(Class<T> serviceClass) {
        String serviceFile = SERVICES + "/" + serviceClass.getName();

        LinkedHashSet<Class<? extends T>> providers = new LinkedHashSet<>();

        try {
            Enumeration<URL> enumeration = classLoader.getResources(serviceFile);
            while (enumeration.hasMoreElements()) {
                final URL url = enumeration.nextElement();
                final InputStream is = url.openStream();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line = reader.readLine();
                    while (null != line) {
                        line = skipCommentAndTrim(line);

                        if (line.length() > 0) {
                            try {
                                Class<? extends T> provider = classLoader.loadClass(line).asSubclass(serviceClass);
                                providers.add(provider);
                            } catch (ClassCastException e) {
                                ClassLoader other = serviceClass.getClassLoader();
                                if (!classLoader.getClass().equals(serviceClass.getClassLoader())) {
                                    throw new IllegalStateException("Service " + line
                                        + " was loaded by different classloader (" + (other == null ? "bootstrap"
                                            : other.getClass().getName()) + ") then service interface "
                                        + serviceClass.getName() + " (" + classLoader.getClass().getName()
                                        + "), unable to cast classes");
                                }
                                throw new IllegalStateException("Service " + line + " does not implement expected type "
                                    + serviceClass.getName());
                            }
                        }
                        line = reader.readLine();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not load services for " + serviceClass.getName(), e);
        }
        return providers;
    }

    private String skipCommentAndTrim(String line) {
        final int comment = line.indexOf('#');
        if (comment > -1) {
            line = line.substring(0, comment);
        }

        line = line.trim();
        return line;
    }

    private <T> Set<T> createInstances(Class<T> serviceType, Set<Class<? extends T>> providers) {
        Set<T> providerImpls = new LinkedHashSet<>();
        for (Class<? extends T> serviceClass : providers) {
            // support enums as possible service providers
            if (serviceClass.isEnum()) {
                T[] enumInstances = serviceClass.getEnumConstants();
                for (T enumInstance : enumInstances) {
                    providerImpls.add(enumInstance);
                    if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, "Registered new service for type {0}: {1}#{2}", new Object[] {
                            serviceType.getName(), serviceClass.getName(), enumInstance.toString() });
                    }
                }
            } else {
                // add classes as service providers
                providerImpls.add(createInstance(serviceClass));
                if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE, "Registered new service for type {0}: {1}", new Object[] { serviceType.getName(),
                        serviceClass.getName() });
                }

            }
        }
        return providerImpls;
    }

    /**
     * Create a new instance of the found Service. <br/>
     *
     * Verifies that the found ServiceImpl implements Service.
     *
     * @param <T>
     * @param serviceType The Service interface
     * @param className The name of the implementation class
     * @param loader The ClassLoader to load the ServiceImpl from
     * @return A new instance of the ServiceImpl
     * @throws Exception If problems creating a new instance
     */
    private <T> T createInstance(final Class<T> implClass) {
        {
            // Get the constructor to use in making the new instance
            final Constructor<? extends T> ctor;
            try {
                ctor = SecurityActions.getConstructor(implClass, new Class<?>[] {});
            } catch (final NoSuchMethodException nsme) {
                throw new RuntimeException(implClass + " must contain a public no args contructor");
            }

            // Create a new instance using the backing model
            final T instance;
            try {
                instance = ctor.newInstance();
            }
            // Handle all construction errors equally
            catch (final Exception e) {
                throw new RuntimeException("Could not create new service instance", e);
            }

            // Return
            return implClass.cast(instance);

        }
    }
}
