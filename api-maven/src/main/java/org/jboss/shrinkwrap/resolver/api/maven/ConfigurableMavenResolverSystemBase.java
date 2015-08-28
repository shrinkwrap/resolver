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

import java.net.URL;

import org.jboss.shrinkwrap.resolver.api.ConfigurableResolverSystem;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepositories;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepository;

/**
 * Entry point of a Maven-based Resolver system which supports configuration. To create a new instance, pass in this
 * class reference to {@link Resolvers#use(Class)} or {@link Resolvers#use(Class, ClassLoader)}, or instead call upon
 * {@link Maven#configureResolver()}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface ConfigurableMavenResolverSystemBase<UNCONFIGURABLERESOLVERSYSTEMTYPE extends MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, CONFIGURABLERESOLVERSYSTEMTYPE extends MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, UNEQUIPPEDRESOLVESTAGETYPE extends PomlessResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage, PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE extends ConfigurableMavenResolverSystemBase<UNCONFIGURABLERESOLVERSYSTEMTYPE, CONFIGURABLERESOLVERSYSTEMTYPE, EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE>>
        extends ConfigurableResolverSystem<UNCONFIGURABLERESOLVERSYSTEMTYPE, PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE>,
        MavenResolverSystemBase<EQUIPPEDRESOLVESTAGETYPE, UNEQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    /**
     * Configures the current session from POM metadata found via the ShrinkWrap Resolver Maven Plugin; retrieves
     * information from the currently-running Maven process.
     *
     * @return A new {@code EQUIPPEDRESOLVESTAGETYPE} for the configured current session
     * @throws InvalidEnvironmentException
     * If the currently-executing environment is not under the control of the ShrinkWrap Resolver Maven
     * Plugin
     */
    EQUIPPEDRESOLVESTAGETYPE configureViaPlugin() throws InvalidEnvironmentException;

    /**
     * Sets that resolution from the ClassPath should be permitted in addition to configured repositories - defaults to
     * "true"
     *
     * @param useClassPathResolution Whether that resolution from the ClassPath should be permitted in addition to
     *                               configured repositories - defaults to "true"
     * @return Modified instance of {@code PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE}
     */
    PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE withClassPathResolution(boolean useClassPathResolution);

    /**
     * Adds a remote repository to use in resolution.
     *
     * @param name a unique arbitrary ID such as "codehaus"
     * @param url the repository URL, such as "http://snapshots.maven.codehaus.org/maven2"
     * @param layout the repository layout. Should always be "default" (may be reused one day by Maven with other values).
     * @return Modified instance of {@code PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE}
     * @throws IllegalArgumentException if name or layout are null or if layout is not "default", or if no url protocol is
     * specified, or an unknown url protocol is found, or url is null
     */
    PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE withRemoteRepo(String name, String url, String layout);

    /**
     * See {@link #withRemoteRepo(String, String, String)}
     *
     * @param name a unique arbitrary ID such as "codehaus"
     * @param url the repository URL, such as "http://snapshots.maven.codehaus.org/maven2"
     * @param layout the repository layout. Should always be "default" (may be reused one day by Maven with other values).
     * @return Modified instance of {@code PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE}
     */
    PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE withRemoteRepo(String name, URL url, String layout);

    /**
     * Adds a remote repository to use in resolution. This repository should be built with
     * {@link MavenRemoteRepositories#createRemoteRepository(String, URL, String)}
     *
     * @param repository The remote repository
     * @return Modified instance of {@code PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE}
     */
    PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE withRemoteRepo(MavenRemoteRepository repository);

    /**
     * Sets whether to consult the Maven Central Repository in resolution; defaults to true.
     *
     * @param useMavenCentral Whether to consult the Maven Central Repository in resolution; defaults to true.
     * @return Modified instance of {@code PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE}
     */
    PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE withMavenCentralRepo(boolean useMavenCentral);

    /**
     * Sets whether to consult any remote Maven Repository in resolution; defaults to false.
     * This method is able to override value defined in settings.xml if loaded later.
     *
     * @param workOffline Whether to consult any remote Maven Repository in resolution; defaults to false.
     * @return Modified instance of {@code PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE}
     */
    PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE workOffline(boolean workOffline);

    /**
     * Sets whether to consult any remote Maven Repository in resolution; ignores all remote repositories.
     *
     * @return Modified instance of {@code PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE}
     */
    PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE workOffline();

    /**
     * Sets whether to consult artifact metadata in local repository and track origin of artifacts there;
     * ignores origin of artifacts in local repository. Note that offline repository has the same behavior.
     *
     * @param useLegacyLocalReposity Whether to ignore origin of artifacts in local repository; defaults to false
     * @return Modified instance of {@code PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE}
     */
    PARTIALLYCONFIGUREDRESOLVERSYSTEMTYPE useLegacyLocalRepo(boolean useLegacyLocalReposity);
}
