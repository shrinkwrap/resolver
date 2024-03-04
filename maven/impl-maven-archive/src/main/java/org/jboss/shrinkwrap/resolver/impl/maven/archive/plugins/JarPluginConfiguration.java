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

import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins.ConfigurationUtils.Key;

/**
 * Encapsulation of Maven Jar Plugin configuration
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public class JarPluginConfiguration extends AbstractPackagingPluginConfiguration {

    private static final String JAR_PLUGIN_GA = "org.apache.maven.plugins:maven-jar-plugin";

    private final String[] excludes;
    private final String[] includes;

    public JarPluginConfiguration(ParsedPomFile pomFile) {
        super(pomFile);

        Map<String, Object> rawValues = pomFile.getPluginConfiguration(JAR_PLUGIN_GA);
        this.includes = ConfigurationUtils.valueAsStringList(rawValues, new Key("includes", "include"), Collections.singletonList("**/**"))
                .toArray(new String[0]);
        this.excludes = ConfigurationUtils.valueAsStringList(rawValues, new Key("excludes", "exclude"),
                Collections.<String> emptyList()).toArray(new String[0]);
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
        return JAR_PLUGIN_GA;
    }

}
