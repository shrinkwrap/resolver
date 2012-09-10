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
package org.jboss.shrinkwrap.resolver.api.maven.archive;

import org.jboss.shrinkwrap.resolver.api.ConfiguredResolverSystemFactory;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

/**
 * ShrinkWrap Maven Resolver. Shorthand convenience API where the call {@link ShrinkWrapMaven#resolver()} is analogous to a more
 * longhand, formal call to {@link Resolvers#use(Class)}, passing {@link MavenResolverSystem} as the argument. Also
 * supports configuration via {@link ShrinkWrapMaven#configureResolver()}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ShrinkWrapMaven {

    /**
     * Creates and returns a new {@link MavenArchiveResolverSystem} instance
     *
     * @return
     */
    public static MavenArchiveResolverSystem resolver() {
        return Resolvers.use(MavenArchiveResolverSystem.class);
    }

    /**
     * Creates and returns a new {@link ConfiguredResolverSystemFactory} instance which may be used to create new
     * {@link MavenArchiveResolverSystem} instances
     *
     * @return
     */
    public static ConfiguredResolverSystemFactory<MavenArchiveResolverSystem, ConfigurableMavenArchiveResolverSystem> configureResolver() {
        return Resolvers.configure(ConfigurableMavenArchiveResolverSystem.class);
    }

}
