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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins.ConfigurationUtils.Key;

/**
 * Encapsulation of Maven War Plugin configuration
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public class WarPluginConfiguration extends AbstractPackagingPluginConfiguration {

    private static final String WAR_PLUGIN_GA = "org.apache.maven.plugins:maven-war-plugin";

    private final File warSourceDirectory;

    private final String[] includes;
    private final String[] excludes;

    public WarPluginConfiguration(ParsedPomFile pomFile) {
        super(pomFile);
        Map<String, Object> rawValues = pomFile.getPluginConfiguration(WAR_PLUGIN_GA);

        this.warSourceDirectory = ConfigurationUtils.valueAsFile(rawValues, new Key("warSourceDirectory"),
                pomFile.getBaseDirectory(), new File(pomFile.getBaseDirectory(), "src/main/webapp"));

        // excludes
        List<String> excludes = new ArrayList<String>();
        excludes.addAll(ConfigurationUtils.valueAsStringList(rawValues, new Key("excludes", "exclude"),
                Collections.<String> emptyList()));
        excludes.addAll(ConfigurationUtils.valueAsStringList(rawValues, new Key("packagingExcludes"),
                Collections.<String> emptyList()));
        excludes.addAll(ConfigurationUtils.valueAsStringList(rawValues, new Key("warSourceExcludes"),
                Collections.<String> emptyList()));
        this.excludes = excludes.toArray(new String[0]);

        // includes
        List<String> includes = new ArrayList<String>();
        includes.addAll(ConfigurationUtils.valueAsStringList(rawValues, new Key("includes", "include"), Collections.singletonList("**/**")));
        includes.addAll(ConfigurationUtils.valueAsStringList(rawValues, new Key("packagingIncludes"),
                Collections.<String> emptyList()));
        includes.addAll(ConfigurationUtils.valueAsStringList(rawValues, new Key("warSourceIncludes"),
                Collections.<String> emptyList()));
        this.includes = includes.toArray(new String[0]);
    }

    /**
     * Gets directory where WAR sources are stored
     *
     * @return A directory where WAR sources are stored
     */
    public File getWarSourceDirectory() {
        return warSourceDirectory;
    }

    @Override
    public String[] getIncludes() {
        return includes;
    }

    @Override
    public String[] getExcludes() {
        return excludes;
    }

    @Override
    public String getPluginGA() {
        return WAR_PLUGIN_GA;
    }
}
