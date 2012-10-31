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
package org.jboss.shrinkwrap.resolver.api.loadable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple singleton (application scoped} registry that fulfills {@link ServiceLoader} contract. This registry uses underlying
 * {@link ServiceLoader} implementation to load the service while storing cached instances in a local map.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ServiceRegistry implements ServiceLoader {

    private ServiceLoader serviceLoader;

    private Map<Class<?>, Collection<?>> cachedServiceInstances;

    private static ServiceRegistry instance;

    /**
     * Creates a service registry with a {@link ServiceLoader} to load service instances until they are cached
     *
     * @param serviceLoader
     */
    public ServiceRegistry(ServiceLoader serviceLoader) {
        this.serviceLoader = serviceLoader;
        this.cachedServiceInstances = Collections.synchronizedMap(new HashMap<Class<?>, Collection<?>>());
    }

    /**
     * Returns {@link ServiceRegistry} instance. Instance must be registered first.
     *
     * @return
     * @throws IllegalStateException If no service registry was registered yet
     */
    public static synchronized ServiceRegistry getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("Unable to get instance of Service Registry, it was not initialized.");
        }
        return instance;
    }

    /**
     * Registers an instance of {@link ServiceRegistry}.
     *
     * @param registry
     */
    public static synchronized void register(ServiceRegistry registry) {
        instance = registry;
    }

    /**
     * Loads all registered services for given {@code serviceClass}
     *
     * @param serviceClass
     * @return
     * @throws IllegalArgumentException If {@code serviceClass} is {@code null}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Collection<T> all(Class<T> serviceClass) throws IllegalArgumentException {

        if (serviceClass == null) {
            throw new IllegalArgumentException("ServiceClass must be provided");
        }

        synchronized (cachedServiceInstances) {
            if (cachedServiceInstances.containsKey(serviceClass)) {
                return (Collection<T>) cachedServiceInstances.get(serviceClass);
            }
            Collection<Object> services = (Collection<Object>) serviceLoader.all(serviceClass);
            cachedServiceInstances.put(serviceClass, services);
            return (Collection<T>) services;
        }
    }

    /**
     * Loads a registered service for given {@code serviceClass}
     *
     * @param serviceClass
     * @return
     * @throws IllegalArgumentException If either {@code classLoader} or {@code serviceClass} is {@code null}
     * @throws IllegalStateException If more than a single service is registered
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T onlyOne(Class<T> serviceClass) throws IllegalArgumentException, IllegalStateException {

        // double check here, we don't eventually want to pass null as a key to the map
        if (serviceClass == null) {
            throw new IllegalArgumentException("ServiceClass must be provided");
        }

        synchronized (cachedServiceInstances) {
            if (cachedServiceInstances.containsKey(serviceClass)) {
                return (T) cachedServiceInstances.get(serviceClass).iterator().next();
            }
            T service = serviceLoader.onlyOne(serviceClass);
            cachedServiceInstances.put(serviceClass, Collections.singleton(service));
            return service;
        }
    }

    /**
     * Loads a registered service for given {@code serviceClass}. Reverts to the {@code defaultImplementationClass} if no
     * other service is registered. If {@code defaultImplemenationClass} is registered as well, it simply ignores it during
     * resolution.
     *
     * @param serviceClass
     * @param defaultImplementationClass
     * @return
     * @throws IllegalArgumentException If either {@code serviceClass} or {@code defaultImplementationClass} is {@code null}
     * @throws IllegalStateException If more than a single service is registered
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T onlyOne(Class<T> serviceClass, Class<? extends T> defaultImplementationClass)
            throws IllegalArgumentException, IllegalStateException {

        // double check here, we don't eventually want to pass null as a key to the map
        if (serviceClass == null) {
            throw new IllegalArgumentException("ServiceClass must be provided");
        }

        synchronized (cachedServiceInstances) {
            if (cachedServiceInstances.containsKey(serviceClass)) {
                return (T) cachedServiceInstances.get(serviceClass).iterator().next();
            }
            T service = serviceLoader.onlyOne(serviceClass, defaultImplementationClass);
            cachedServiceInstances.put(serviceClass, Collections.singleton(service));
            return service;
        }
    }

}
