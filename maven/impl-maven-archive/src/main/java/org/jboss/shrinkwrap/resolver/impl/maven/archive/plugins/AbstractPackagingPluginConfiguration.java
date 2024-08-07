/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins;

import java.util.Collections;
import java.util.Map;
import java.util.jar.Manifest;

import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins.ConfigurationUtils.Key;

/**
 * An abstraction of the plugin that contains {@link MavenArchiveConfiguration}.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public abstract class AbstractPackagingPluginConfiguration {

    private final MavenArchiveConfiguration archiveConfiguration;

    protected AbstractPackagingPluginConfiguration(ParsedPomFile pomFile) {
        Map<String, Object> rawValues = pomFile.getPluginConfiguration(getPluginGA());
        this.archiveConfiguration = new MavenArchiveConfiguration(pomFile, ConfigurationUtils.valueAsMap(rawValues, new Key(
                "archive"), Collections.emptyMap()));
    }

    /**
     * Returns groupId:artifactId for the plugin configuration
     *
     * @return The groupId:artifactId for the plugin configuration
     */
    public abstract String getPluginGA();

    /**
     * Returns an array of file patterns to be included in an archive
     *
     * @return An array of file patterns to be included in an archive
     */
    public abstract String[] getIncludes();

    /**
     * Returns an array of file patterns to be excluded from an archive
     *
     * @return An array of file patterns to be excluded from an archive
     */
    public abstract String[] getExcludes();

    /**
     * Gets Maven Archive configuration
     *
     * @return A Maven Archive configuration
     */
    public MavenArchiveConfiguration getArchiveConfiguration() {
        return archiveConfiguration;
    }

    /**
     * Gets a manifest based on configuration of the packaged archive
     *
     * @return A manifest based on configuration of the packaged archive
     */
    public Manifest constructManifest() {
        return archiveConfiguration.asManifest();
    }
}
