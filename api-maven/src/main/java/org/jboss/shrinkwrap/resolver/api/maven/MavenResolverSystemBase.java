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

import org.jboss.shrinkwrap.resolver.api.ResolverSystem;
import org.jboss.shrinkwrap.resolver.api.Resolvers;

/**
 * Entry point of a Maven-based Resolver system which does not suppport configuration. To create a new instance, pass in
 * this class reference to {@link Resolvers#use(Class)} or {@link Resolvers#use(Class, ClassLoader)}, or instead call
 * upon {@link MavenResolverSystemShortcutImpl#INSTANCE}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, UNEQUIPPEDRESOLVESTAGETYPE extends PomlessResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
    extends ResolverSystem,
    PomlessResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    /**
     * Sets whether resolution should be done in "offline" (ie. not connected to Internet) mode.
     *
     * Make sure that you call this method before loading POM file. Also, loading settings.xml might change this flag.
     * This method is deprecated. Please use {@link ConfigurableMavenResolverSystem#workOffline()} by calling Maven.configureResolver().workOffline(boolean) instead which does not have
     * similar issues - offline flag is set before settings.xml and POM are loaded and it is able to persist different flag value
     * in settings.xml
     *
     * @param offline
     * @return
     */
    @Deprecated
    UNEQUIPPEDRESOLVESTAGETYPE offline(boolean offline);

    /**
     * Sets that resolution should be done in "offline" (ie. not connected to Internet) mode. Alias to
     * {@link MavenResolverSystemBase#offline(boolean)}, passing <code>true</code> as a parameter.
     *
     * Make sure that you call this method before loading POM file. Also, loading settings.xml might change this flag.
     * This method is deprecated. Please use {@link ConfigurableMavenResolverSystem#workOffline()} by calling {@code Maven.configureResolver().workOffline() instead which does not have
     * similar issues - offline flag is set before settings.xml and POM are loaded and it is able to persist different flag value
     * in settings.xml
     *
     * @return
     */
    @Deprecated
    UNEQUIPPEDRESOLVESTAGETYPE offline();
}
