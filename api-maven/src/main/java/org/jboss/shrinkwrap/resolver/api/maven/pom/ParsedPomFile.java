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
package org.jboss.shrinkwrap.resolver.api.maven.pom;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

/**
 * Representation of a parsed Project Object Model file
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface ParsedPomFile {

    /**
     * GroupId of the project
     *
     * @return GroupId of the project
     */
    String getGroupId();

    /**
     * ArtifactId of the project
     *
     * @return ArtifactId of the project
     */
    String getArtifactId();

    /**
     * Version of the project
     *
     * @return Version of the project
     */
    String getVersion();

    /**
     * Name of the project
     *
     * @return Name of the project
     */
    String getName();

    /**
     * Returns organization name
     *
     * @return Organization name
     */
    String getOrganizationName();

    /**
     * Returns organization url
     *
     * @return Organization url
     * @throws IllegalStateException if organization URL in PMO file does not represent a valid URL
     */
    URL getOrganizationUrl() throws IllegalStateException;

    /**
     * Final name of the project packaged as a JAR, WAR or EAR; by default it is {@code artifactId + "." + packagingType}
     *
     * @return Final name of the project packaged as a JAR, WAR or EAR
     */
    String getFinalName();

    /**
     * Packaging type of the project
     *
     * @return Packaging type of the project
     */
    PackagingType getPackagingType();

    /**
     * Returns a base directory of the project. Might be {@code null}.
     *
     * @return A base directory of the project.
     */
    File getBaseDirectory();

    /**
     * Returns a directory where project sources are stored. Might be {@code null}.t
     *
     * @return A directory where project sources are stored.
     */
    File getSourceDirectory();

    /**
     * Returns a directory where project build output is stored. Might be {@code null}.
     *
     * @return A directory where project build output is stored.
     */
    File getBuildOutputDirectory();

    /**
     * Returns a directory where project test sources are stored. Might be {@code null}.
     *
     * @return A directory where project test sources are stored.
     */
    File getTestSourceDirectory();

    /**
     * Returns a directory where project test output is stored. Might be {@code null}.
     *
     * @return A directory where project test output is stored.
     */
    File getTestOutputDirectory();

    /**
     * Returns dependencies of the Project Object Model
     *
     * @return Dependencies of the Project Object Model
     */
    Set<MavenDependency> getDependencies();

    /**
     * Returns dependency management of the Project Object Model
     *
     * @return Dependency management of the Project Object Model
     */
    Set<MavenDependency> getDependencyManagement();

    /**
     * Returns a list of defined resources for current project.
     *
     * @return A list of defined resources for current project.
     */
    List<Resource> getResources();

    /**
     * Returns a list of defined test resources for current project.
     *
     * @return A list of defined test resources for current project.
     */
    List<Resource> getTestResources();

    /**
     * Returns interpolated properties defined in the current project
     *
     * @return Interpolated properties defined in the current project
     */
    Properties getProperties();

    /**
     * Returns a plugin configuration in from of a map. Never returns {@code null}
     *
     * @param pluginKey a combination of groupId:artifactId
     * @return A plugin configuration in from of a map. Never returns {@code null}
     */
    // TODO figure out if that's really the best possible API
    Map<String, Object> getPluginConfiguration(String pluginKey);

}
