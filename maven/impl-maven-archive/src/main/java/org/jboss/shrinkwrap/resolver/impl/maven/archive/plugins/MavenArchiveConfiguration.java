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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporterException;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins.ConfigurationUtils.Key;

/**
 * Representation of Maven Archiver configuration shared in between all the archives.
 *
 * @see <a href="http://maven.apache.org/shared/maven-archiver/">maven-archiver</a>
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MavenArchiveConfiguration {

    private final ParsedPomFile pomFile;

    // FIXME not supported
    private final boolean addMavenDescriptor = true;

    // FIXME not supported
    private final boolean compress = true;

    // FIXME not supported
    private final boolean forced = true;

    // FIXME not supported
    private final boolean index = false;

    private final File manifestFile;

    // FIXME not supported
    private final File pomPropertiesFile;

    // FIXME not supported
    private final boolean manifestAddClasspath = false;

    private final boolean manifestAddDefaultImplementationEntries;

    private final boolean manifestAddDefaultSpecificationEntries;

    // FIXME not supported
    private final boolean manifestAddExtensions = false;

    // FIXME not supported
    private final String manifestClasspathLayoutType = "simple";

    // FIXME not supported
    private final boolean manifestClasspathMavenRepositoryLayout = false;

    // FIXME not supported
    private final String manifestClassPathPrefix = "";

    private final String manifestMainClass;

    private final String manifestPackageName;

    private final Map<String, Map<String, String>> manifestSections;

    private final Map<String, String> manifestEntries;

    public MavenArchiveConfiguration(ParsedPomFile pomFile, Map<String, Object> configuration) {
        this.pomFile = pomFile;
        this.manifestEntries = new HashMap<>();
        this.manifestEntries.putAll(ConfigurationUtils.valueAsMapOfStrings(configuration, new Key("manifestEntries"),
                Collections.emptyMap()));
        this.manifestFile = ConfigurationUtils.valueAsFile(configuration, new Key("manifestFile"), pomFile.getBaseDirectory(),
                null);
        this.pomPropertiesFile = ConfigurationUtils.valueAsFile(configuration, new Key("pomPropertiesFile"),
                pomFile.getBaseDirectory(), null);

        Map<String, Object> manifestConfiguration = ConfigurationUtils.valueAsMap(configuration, new Key("manifest"),
                Collections.emptyMap());

        this.manifestMainClass = ConfigurationUtils.valueAsString(manifestConfiguration, new Key("mainClass"), null);
        this.manifestPackageName = ConfigurationUtils.valueAsString(manifestConfiguration, new Key("packageName"), null);
        this.manifestAddDefaultImplementationEntries = ConfigurationUtils.valueAsBoolean(manifestConfiguration, new Key(
                "addDefaultImplementationEntries"), false);
        this.manifestAddDefaultSpecificationEntries = ConfigurationUtils.valueAsBoolean(manifestConfiguration, new Key(
                "addDefaultSpecificationEntries"), false);

        this.manifestSections = parseManifestSections(configuration);
    }

    public File getManifestFile() {
        return manifestFile;
    }

    public Map<String, String> getManifestEntries() {
        return manifestEntries;
    }

    public String getManifestPackageName() {
        return manifestPackageName;
    }

    public boolean isManifestAddDefaultImplementationEntries() {
        return manifestAddDefaultImplementationEntries;
    }

    public boolean isManifestAddDefaultSpecificationEntries() {
        return manifestAddDefaultSpecificationEntries;
    }

    public String getManifestMainClass() {
        return manifestMainClass;
    }

    public Map<String, Map<String, String>> getManifestSections() {
        return manifestSections;
    }

    public ParsedPomFile getPomFile() {
        return pomFile;
    }

    public Manifest asManifest() throws MavenImporterException {
        return new ManifestBuilder().addCustomEntries().addDefaultImplementationEntries().addDefaultSpecificationEntries()
                .addManifestEntries().addManifestSections().addMainClass()
                // loadFile() needs to be the last step as we might need to overwrite values generated by ShrinkWrap
                .loadFile().build();
    }

    private Map<String, Map<String, String>> parseManifestSections(Map<String, Object> configuration) {

        Map<String, Map<String, String>> map = new HashMap<>();

        Object rawOrSectionMap = configuration.get("manifestSections");
        if (rawOrSectionMap == null || !(rawOrSectionMap instanceof Map<?, ?>)) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
        Object sections = ((Map<String, Object>) rawOrSectionMap).get("manifestSection");
        if (sections instanceof Map<?, ?>) {
            // if single section was defined, wrap it into iterable element
            sections = Collections.singletonList(sections);
        } else if (sections == null || !(sections instanceof Iterable<?>)) {
            return Collections.emptyMap();
        }

        for (Object rawOrSection : (Iterable<?>) sections) {
            // ignore manifest section if not defined correctly
            if (!(rawOrSection instanceof Map<?, ?>)) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> section = (Map<String, Object>) rawOrSection;
            String name = ConfigurationUtils.valueAsString(section, new Key("name"), null);
            Map<String, String> values = ConfigurationUtils.valueAsMapOfStrings(section, new Key("manifestEntries"),
                    Collections.emptyMap());
            map.put(name, values);
        }

        return map;

    }

    /**
     * Constructs a manifest using configuration stored in MavenArchiveConfiguration
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    private class ManifestBuilder {

        private final Manifest manifest;

        ManifestBuilder() {
            this.manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        }

        ManifestBuilder loadFile() throws MavenImporterException {
            if (Validate.isReadable(getManifestFile())) {
                try (InputStream is = Files.newInputStream(getManifestFile().toPath())) {
                    Manifest userSupplied = new Manifest(is);
                    ManifestMerger.merge(userSupplied, manifest);
                } catch (IOException e) {
                    throw new MavenImporterException("Unable to build MANIFEST.MF from file "
                            + getManifestFile().getAbsolutePath(), e);
                }
            }
            return this;
        }

        ManifestBuilder addManifestEntries() {
            if (!getManifestEntries().isEmpty()) {
                for (Map.Entry<String, String> entry : getManifestEntries().entrySet()) {
                    addMainAttribute(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        ManifestBuilder addManifestSections() {
            if (!getManifestSections().isEmpty()) {
                for (Map.Entry<String, Map<String, String>> entry : getManifestSections().entrySet()) {
                    for (Map.Entry<String, String> attrs : entry.getValue().entrySet()) {
                        addSectionAttribute(entry.getKey(), attrs.getKey(), attrs.getValue());
                    }
                }
            }
            return this;
        }

        ManifestBuilder addCustomEntries() {

            return addMainAttribute("Created-By", "ShrinkWrap Maven Resolver")
                    .addMainAttribute("Built-by", SecurityActions.getProperty("user.name"))
                    .addMainAttribute("Built-Jdk", SecurityActions.getProperty("java.version"))
                    .addMainAttribute("Package", getManifestPackageName());
        }

        ManifestBuilder addDefaultSpecificationEntries() {
            if (isManifestAddDefaultSpecificationEntries()) {
                return addMainAttribute("Specification-Title", getPomFile().getName()).addMainAttribute(
                        "Specification-Version", getPomFile().getVersion()).addMainAttribute("Specification-Vendor",
                        getPomFile().getOrganizationName());
            }
            return this;
        }

        ManifestBuilder addDefaultImplementationEntries() {
            if (isManifestAddDefaultImplementationEntries()) {
                return addMainAttribute("Implementation-Title", getPomFile().getName())
                        .addMainAttribute("Implementation-Version", getPomFile().getVersion())
                        .addMainAttribute("Implementation-Vendor-Id", getPomFile().getGroupId())
                        .addMainAttribute("Implementation-Vendor", getPomFile().getOrganizationName());
            }
            return this;
        }

        ManifestBuilder addMainClass() {
            addMainAttribute(Attributes.Name.MAIN_CLASS.toString(), getManifestMainClass());
            return this;
        }

        Manifest build() {
            return manifest;
        }

        private ManifestBuilder addMainAttribute(String name, String value) {
            if (!Validate.isNullOrEmpty(value)) {
                manifest.getMainAttributes().putValue(name, value);
            }
            return this;
        }

        private ManifestBuilder addSectionAttribute(String section, String name, String value) {
            if (!Validate.isNullOrEmpty(value)) {
                Attributes attrs = manifest.getAttributes(section);
                // put attributes section if not defined yet
                if (attrs == null) {
                    attrs = new Attributes();
                    Map<String, Attributes> entries = manifest.getEntries();
                    entries.put(section, attrs);
                }
                attrs.putValue(name, value);
            }
            return this;
        }
    }

    private static class ManifestMerger {

        /**
         * Merges source Manifest into target Manifest
         *
         * @param source The source {@link Manifest} whose attributes will be merged into the target.
         * @param target The target {@link Manifest} into which the attributes will be merged.
         * @return The modified target {@link Manifest} with merged attributes.
         */
        static Manifest merge(Manifest source, Manifest target) {

            // merge main attributes
            mergeAttributes(source.getMainAttributes(), target.getMainAttributes());

            // merge other sections
            for (Map.Entry<String, Attributes> entry : source.getEntries().entrySet()) {
                if (target.getAttributes(entry.getKey()) == null) {
                    if (entry.getValue() != null) {
                        target.getEntries().put(entry.getKey(), new Attributes(entry.getValue()));
                    }
                } else {
                    mergeAttributes(entry.getValue(), target.getEntries().get(entry.getKey()));
                }
            }
            return target;
        }

        static Attributes mergeAttributes(Attributes source, Attributes target) {
            for (Object key : source.keySet()) {
                target.put(key, source.get(key));
            }
            return target;
        }
    }
}