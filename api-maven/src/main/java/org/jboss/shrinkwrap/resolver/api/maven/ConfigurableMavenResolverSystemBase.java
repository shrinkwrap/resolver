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

import org.jboss.shrinkwrap.resolver.api.ConfigurableResolverSystem;
import org.jboss.shrinkwrap.resolver.api.Resolvers;

/**
 * Entry point of a Maven-based Resolver system which supports configuration. To create a new instance, pass in this
 * class reference to {@link Resolvers#use(Class)} or {@link Resolvers#use(Class, ClassLoader)}, or instead call upon
 * {@link Maven#configureResolver()}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface ConfigurableMavenResolverSystemBase<UNCONFIGURABLERESOLVERSYSTEMTYPE extends MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, CONFIGURABLERESOLVERSYSTEMTYPE extends MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, UNEQUIPPEDRESOLVESTAGETYPE extends PomlessResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
    extends ConfigurableResolverSystem<UNCONFIGURABLERESOLVERSYSTEMTYPE>,
    MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    /**
     * Configures the current session from POM metadata found via the ShrinkWrap Resolver Maven Plugin; retrieves
     * information from the currently-running Maven process.
     *
     * @return
     * @throws InvalidEnvironmentException
     *             If the currently-executing environment is not under the control of the ShrinkWrap Resolver Maven
     *             Plugin
     */
    EQUIPPEDRESOLVESTAGETYPE configureViaPlugin() throws InvalidEnvironmentException;
}
