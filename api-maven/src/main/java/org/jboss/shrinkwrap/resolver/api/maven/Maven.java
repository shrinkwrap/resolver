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
package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.resolver.api.Resolvers;

/**
 * Shorthand convenience API where the call {@link Maven#resolver()} is analogous to a more longhand, formal call to
 * {@link Resolvers#use(Class)}, passing {@link MavenResolverSystem} as the argument. Also supports configuration via
 * {@link Maven#configureResolver()}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class Maven {

    /**
     * Creates and returns a new {@link MavenResolverSystem} instance
     *
     * @return
     */
    public static MavenResolverSystem resolver() {
        return Resolvers.use(MavenResolverSystem.class);
    }

    /**
     * Creates and returns a new {@link ConfiguredResolverSystemFactory} instance which may be used to create new
     * {@link MavenResolverSystem} instances
     *
     * @return
     */
    public static ConfigurableMavenResolverSystem configureResolver() {
        return Resolvers.configure(ConfigurableMavenResolverSystem.class);
    }

    /**
     * Configures the {@link MavenResolverSystem} with <code>settings.xml</code> and POM metadata as picked up from the
     * environment (these properties are set by the ShrinkWrap Maven Resolver Plugin). The new instance will be created
     * by the current {@link Thread#getContextClassLoader()}.
     *
     * @return
     * @throws InvalidEnvironmentException
     *             If this is executed outside the context of the ShrinkWrap Maven Resolver Plugin Environment
     */
    public static PomEquippedResolveStage configureResolverViaPlugin() throws InvalidEnvironmentException {
        return configureResolverViaPlugin(SecurityActions.getThreadContextClassLoader());
    }

    /**
     * Configures the {@link MavenResolverSystem} with <code>settings.xml</code> and POM metadata as picked up from the
     * environment (these properties are set by the ShrinkWrap Maven Resolver Plugin).
     *
     * @param cl
     *            The {@link ClassLoader} used to create the new instance; required
     * @return
     * @throws IllegalArgumentException
     *             If the {@link ClassLoader} is not specified
     * @throws InvalidEnvironmentException
     *             If this is executed outside the context of the ShrinkWrap Maven Resolver Plugin Environment
     */
    public static PomEquippedResolveStage configureResolverViaPlugin(final ClassLoader cl)
        throws InvalidEnvironmentException, IllegalArgumentException {
        final ConfigurableMavenResolverSystem resolverSystem = Resolvers.use(ConfigurableMavenResolverSystem.class, cl);
        return resolverSystem.configureViaPlugin();
    }

}
