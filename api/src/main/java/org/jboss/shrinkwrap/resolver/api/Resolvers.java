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

/**
 * Client entry point to resolve artifacts from a set of coordinates in a repository-based system. To create a new
 * instance, pass the desired view (subtype of {@link ResolverSystem}) into either {@link Resolvers#use(Class)} or
 * {@link Resolvers#use(Class, ClassLoader)}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class Resolvers {

    /**
     * Returns a factory to create configured {@link ResolverSystem} instances of the specified type. New instances will
     * be created using the current {@link Thread#getContextClassLoader()}.
     *
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     *             If the type is not specified
     */
    public static <RESOLVERSYSTEMTYPE extends ResolverSystem, CONFIGURABLERESOLVERSYSTEMTYPE extends ConfigurableResolverSystem<RESOLVERSYSTEMTYPE>> ConfiguredResolverSystemFactory<RESOLVERSYSTEMTYPE, CONFIGURABLERESOLVERSYSTEMTYPE> configure(
        final Class<CONFIGURABLERESOLVERSYSTEMTYPE> clazz) throws IllegalArgumentException {
        return new ConfiguredResolverSystemFactory<RESOLVERSYSTEMTYPE, CONFIGURABLERESOLVERSYSTEMTYPE>(clazz);
    }

    /**
     * Returns a factory to create configured {@link ResolverSystem} instances of the specified type. New instances will
     * be created using the specified {@link ClassLoader}.
     *
     * @param clazz
     * @param cl
     * @return
     * @throws IllegalArgumentException
     *             If the type or {@link ClassLoader} is not specified
     */
    public static <RESOLVERSYSTEMTYPE extends ResolverSystem, CONFIGURABLERESOLVERSYSTEMTYPE extends ConfigurableResolverSystem<RESOLVERSYSTEMTYPE>> ConfiguredResolverSystemFactory<RESOLVERSYSTEMTYPE, CONFIGURABLERESOLVERSYSTEMTYPE> configure(
        final Class<CONFIGURABLERESOLVERSYSTEMTYPE> clazz, final ClassLoader cl) throws IllegalArgumentException {
        return new ConfiguredResolverSystemFactory<RESOLVERSYSTEMTYPE, CONFIGURABLERESOLVERSYSTEMTYPE>(clazz, cl);
    }

    /**
     * Creates and returns a new instance of the specified view type.
     *
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <RESOLVERSYSTEMTYPE extends ResolverSystem> RESOLVERSYSTEMTYPE use(
        final Class<RESOLVERSYSTEMTYPE> clazz) throws IllegalArgumentException {
        return ResolverSystemFactory.createFromUserView(clazz);
    }

    /**
     * Creates and returns a new instance of the specified view type.
     *
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     *             If either argument is not supplied
     */
    public static <RESOLVERSYSTEMTYPE extends ResolverSystem> RESOLVERSYSTEMTYPE use(
        final Class<RESOLVERSYSTEMTYPE> clazz, final ClassLoader cl) throws IllegalArgumentException {
        return ResolverSystemFactory.createFromUserView(clazz, cl);
    }
}
