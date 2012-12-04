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
import java.util.List;
import java.util.Map;
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
     * Final name of the project
     *
     * @return
     */
    String getFinalName();

    /**
     * Packaging type of the project
     *
     * @return
     */
    PackagingType getPackagingType();

    /**
     * Returns a base directory of the project. Might be {@code null}.
     *
     * @return
     */
    File getBaseDirectory();

    /**
     * Returns a directory where project sources are stored. Might be {@code null}.t
     *
     * @return
     */
    File getSourceDirectory();

    /**
     * Returns a directory where project build output is stored. Might be {@code null}.
     *
     * @return
     */
    File getBuildOutputDirectory();

    /**
     * Returns a directory where project test sources are stored. Might be {@code null}.
     *
     * @return
     */
    File getTestSourceDirectory();

    /**
     * Returns
     *
     * @return
     */
    Set<MavenDependency> getDependencies();

    /**
     * Returns dependency management of the Project Object Model
     *
     * @return
     */
    Set<MavenDependency> getDependencyManagement();

    /**
     * Returns a list of files defined as a resources for current project.
     *
     * @return
     */
    List<File> getProjectResources();

    /**
     * Returns a plugin configuration in from of a map. Never returns {@code null}
     *
     * @param pluginKey a combination of groupId:artifactId
     * @return
     */
    // TODO figure out if that's really the best possible API
    Map<String, Object> getPluginConfiguration(String pluginKey);

}
