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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.VersionResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepository;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;

/**
 * Encapsulates Maven session
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface MavenWorkingSession {

    /**
     * Gets a set af dependency declarations stored in version management. These dependency declarations are used to get
     * versions if none are specified in {@link MavenDependency#getAddress()} address and also affect transitive
     * dependency resolutions.
     *
     * @return Set of defined {@link MavenDependency}s
     */
    Set<MavenDependency> getDependencyManagement();

    /**
     * Gets the {@link Set} of dependencies to be resolved for this session
     *
     * @return {@link MavenDependency}s to be resolved as part of the request to the backend; may be a subset of
     * {@link MavenWorkingSession#getDeclaredDependencies()} after pre-request filtering has been done
     */
    List<MavenDependency> getDependenciesForResolution();

    /**
     * Metadata for all defined <code><dependencies></code> elements
     *
     * @return
     */
    Set<MavenDependency> getDeclaredDependencies();

    /**
     * Loads an effective POM file and updates session settings accordingly.
     *
     * @param pomFile which represents Project Object Model file
     * @param profiles List of profiles to activated/disabled
     * @return Modified session instance
     */
    MavenWorkingSession loadPomFromFile(File pomFile, String... profiles) throws InvalidConfigurationFileException;

    /**
     * Loads Maven configuration and updates session settings accordingly.
     *
     * @param globalSettingsFile File which represents global settings file
     * @param userSettingsFile File which represents user settings file
     * @return Modified session instance
     */
    MavenWorkingSession configureSettingsFromFile(File globalSettingsFile, File userSettingsFile)
            throws InvalidConfigurationFileException;

    Collection<MavenResolvedArtifact> resolveDependencies(MavenResolutionStrategy strategy)
            throws ResolutionException;

    /**
     * Resolves version range request for given coordinate
     * @param coordinate
     * @return
     * @throws VersionResolutionException
     */
    MavenVersionRangeResult resolveVersionRange(MavenCoordinate coordinate) throws VersionResolutionException;

    /**
     * Returns an abstraction of Project Object Model. This abstraction can be used to get additional information about the
     * project
     *
     * @return Information about the project
     */
    ParsedPomFile getParsedPomFile();

    /**
     * Refreshes underlying Aether session in order to contain newly acquired information, such as new settings.xml
     * content
     *
     * @return Modified session instance
     */
    MavenWorkingSession regenerateSession();

    /**
     * Whether or not to set this session in "offline" mode
     *
     * @param offline
     */
    void setOffline(boolean offline);

    /**
     * Disables the classpath workspace reader which may be used to resolve from dependencies on the ClassPath
     */
    void disableClassPathWorkspaceReader();

    /**
     * Disables use of the Maven Central Repository
     */
    void disableMavenCentral();

    /**
     * Adds a remote repository to use in resolution.
     *
     * @param name a unique arbitrary ID such as "codehaus"
     * @param url the repository URL, such as "http://snapshots.maven.codehaus.org/maven2"
     * @param layout the repository layout. Should always be "default" (may be reused one day by Maven with other values).
     *
     * @throws MalformedURLException  if no protocol is specified, or an unknown protocol is found, or url is null.
     * @throws IllegalArgumentException if name or layout are null or if layout is not "default".
     */
    void addRemoteRepo(String name, String url, String layout) throws MalformedURLException;

    /**
     * Same documentation as {@link #addRemoteRepo(String, String, String)}.
     *
     */
    void addRemoteRepo(String name, URL url, String layout);

    /**
     * Adds a remote repository to use in resolution.
     * @param repository
     * @throws MalformedURLException  if no protocol is specified, or an unknown protocol is found, or url is null.
     * @throws IllegalArgumentException if argument is null
     */
    void addRemoteRepo(MavenRemoteRepository repository);
}
