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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;

/**
 * Encapsulates Maven session
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface MavenWorkingSession {

    /**
     * Gets a set af dependency declarations stored in version management. These dependency declarations are used to get
     * versions if none are specified in {@link DependencyDeclaration#getAddress()} address and also affect transitive
     * dependency resolutions.
     *
     * @return Set of defined {@link DependencyDeclaration}s
     */
    Set<DependencyDeclaration> getDependencyManagement();

    /**
     * Gets the {@link Set} of dependencies to be resolved for this session
     *
     * @return {@link DependencyDeclaration}s to be resolved as part of the request to the backend; may be a subset of
     *         {@link MavenWorkingSession#getDeclaredDependencies()} after pre-request filtering has been done
     */
    List<DependencyDeclaration> getDependencies();

    /**
     * Metadata for all defined <code><dependencies></code> elements
     *
     * @return
     */
    Set<DependencyDeclaration> getDeclaredDependencies();

    /**
     * Loads an effective POM file and updates session settings accordingly.
     *
     * @param request
     *            Request to load the effective POM file
     * @return Modified session instance
     */
    MavenWorkingSession execute(ModelBuildingRequest request) throws InvalidConfigurationFileException;

    /**
     * Loads Maven configuration and updates session settings accordingly.
     *
     * @param request
     *            Request to load settings.xml file
     * @return Modified session instance
     */
    MavenWorkingSession execute(SettingsBuildingRequest request) throws InvalidConfigurationFileException;

    Collection<ArtifactResult> execute(CollectRequest request, MavenResolutionFilter filter)
        throws DependencyResolutionException;

    /**
     * Returns a list of remote repositories enabled from Maven settings. If an effective pom was loaded, and it
     * actually contains any repositories, these are added as well.
     *
     * @return List of currently active repositories
     * @throws IllegalStateException
     *             If currently active repositories cannot be resolved
     */
    List<RemoteRepository> getRemoteRepositories() throws IllegalStateException;

    /**
     * Returns underlying Maven model for parsed POM file. This is useful when you need to extract additional
     * information from the model.
     *
     * @return Maven model for parsed POM file.
     */
    Model getModel();

    /**
     * Gets a list of profiles defined in settings.xml.
     *
     * @return List of defined profiles
     */
    List<Profile> getSettingsDefinedProfiles();

    /**
     * Refreshes underlying Aether session in order to contain newly acquired information, such as new settings.xml
     * content
     *
     * @return Modified session instance
     */
    MavenWorkingSession regenerateSession();

    /**
     * Gets registry of the known artifact types based on underlying session
     *
     * @return the registry
     */
    ArtifactTypeRegistry getArtifactTypeRegistry();

    /**
     * Whether or not to set this session in "offline" mode
     *
     * @param offline
     */
    void setOffline(boolean offline);

}
