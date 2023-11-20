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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins.ConfigurationUtils.Key;

/**
 * Encapsulation of Maven Compiler Plugin configuration
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class CompilerPluginConfiguration {

    private static final String COMPILER_PLUGIN_GA = "org.apache.maven.plugins:maven-compiler-plugin";
    private static final String DEFAULT_RELEASE_VERSION = "1.8";

    // TODO include more compiler plugin configuration values
    private final boolean verbose;
    private final String sourceVersion;
    private final String targetVersion;
    private final String encoding;

    private final String additionalCompilerArgument;
    private final Map<String, String> additionalCompilerArguments;
    private final List<String> additionalCompilerArgs;

    public CompilerPluginConfiguration(ParsedPomFile pomFile) {
        Map<String, Object> rawValues = pomFile.getPluginConfiguration(COMPILER_PLUGIN_GA);
        Properties properties = pomFile.getProperties();

        String releaseVersion = DEFAULT_RELEASE_VERSION;
        String configReleaseVersion = ConfigurationUtils.valueAsString(rawValues, new Key("release"),
                properties.getProperty("maven.compiler.release"));
        if (!(configReleaseVersion == null) && !configReleaseVersion.isEmpty()) {
            releaseVersion = configReleaseVersion.equals("8") ? "1.8" : configReleaseVersion;
        }

        this.verbose = ConfigurationUtils.valueAsBoolean(rawValues, new Key("verbose"), false);
        this.sourceVersion = ConfigurationUtils.valueAsString(rawValues, new Key("source"),
            properties.getProperty("maven.compiler.source", releaseVersion));
        this.targetVersion = ConfigurationUtils.valueAsString(rawValues, new Key("target"),
            properties.getProperty("maven.compiler.target", releaseVersion));
        this.encoding = ConfigurationUtils.valueAsString(rawValues,
            new Key("encoding"),
            properties.getProperty("project.build.sourceEncoding", ""));
        this.additionalCompilerArguments = prependKeysWithDash(ConfigurationUtils.valueAsMapOfStrings(rawValues, new Key(
            "compilerArguments"), Collections.<String, String> emptyMap()));
        this.additionalCompilerArgs = ConfigurationUtils.valueAsStringList(rawValues,
            new Key("compilerArgs", "arg"),
            Collections.<String> emptyList());
        this.additionalCompilerArgument = ConfigurationUtils.valueAsString(rawValues, new Key("compilerArgument"), "");

    }

    public boolean isVerbose() {
        return verbose;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public String getEncoding() {
        return encoding;
    }

    public Map<String, String> getAdditionalCompilerArgs() {
        // merge old and new argument definitions together
        Map<String, String> compilerArgumentsAsMap = new HashMap<>(additionalCompilerArguments.size()
                + additionalCompilerArgs.size() + 1);

        if (additionalCompilerArgument.length() > 0) {
            compilerArgumentsAsMap.put(additionalCompilerArgument, null);
        }
        if (additionalCompilerArguments.size() > 0) {
            compilerArgumentsAsMap.putAll(additionalCompilerArguments);
        }
        if (additionalCompilerArgs.size() > 0) {
            for (String value : additionalCompilerArgs) {
                compilerArgumentsAsMap.put(value, null);
            }
        }

        return compilerArgumentsAsMap;
    }

    public CompilerConfiguration asCompilerConfiguration() {
        CompilerConfiguration configuration = new CompilerConfiguration();

        configuration.setVerbose(this.isVerbose());
        configuration.setSourceVersion(this.getSourceVersion());
        configuration.setTargetVersion(this.getTargetVersion());

        // setup encoding if it was set either via property or compiler configuration
        if (encoding != null && !encoding.isEmpty()) {
            configuration.setSourceEncoding(encoding);
        }

        configuration.setCustomCompilerArgumentsAsMap(getAdditionalCompilerArgs());

        // FIXME this should be handled better
        configuration.setWorkingDirectory(new File("."));

        return configuration;
    }

    private static Map<String, String> prependKeysWithDash(Map<String, String> original) {
        Map<String, String> map = new HashMap<>(original.size());
        for (Map.Entry<String, String> entry : original.entrySet()) {
            map.put("-" + entry.getKey(), entry.getValue());
        }
        return map;
    }

}
