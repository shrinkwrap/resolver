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

/**
 * ServiceLoader allows to load services available on classpath implementing given service interface.
 *
 * All service are required to have a non-argument public constructor.
 *
 * All ServiceLoader are required to handle registration of services implemented as {@link Enum}s. See {@link SpiServiceLoader}
 * for default implementation.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface ServiceLoader {

    /**
     * Loads all registered services for given {@code serviceClass}
     *
     * @param serviceClass
     * @return
     * @throws IllegalArgumentException If either {@code classLoader} or {@code serviceClass} is {@code null}
     */
    <T> Collection<T> all(Class<T> serviceClass) throws IllegalArgumentException;

    /**
     * Loads a registered service for given {@code serviceClass}
     *
     * @param serviceClass
     * @return
     * @throws IllegalArgumentException If {@code serviceClass} is {@code null}
     * @throws IllegalStateException If more than a single service is registered
     */
    <T> T onlyOne(Class<T> serviceClass) throws IllegalArgumentException, IllegalStateException;

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
    <T> T onlyOne(Class<T> serviceClass, Class<? extends T> defaultImplementationClass)
            throws IllegalArgumentException, IllegalStateException;
}
