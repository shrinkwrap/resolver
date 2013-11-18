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
package org.jboss.shrinkwrap.resolver.impl.maven.pom;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;

public class ParsedPomFileImpl implements ParsedPomFile {

    private final Model model;
    private final ArtifactTypeRegistry registry;

    public ParsedPomFileImpl(Model model, ArtifactTypeRegistry registry) {
        Validate.notNull(model, "Maven Project Object Model must not be null");
        Validate.notNull(registry, "Artifact Type Registry must not be null");
        this.model = model;
        this.registry = registry;
    }

    @Override
    public String getGroupId() {
        return model.getGroupId();
    }

    @Override
    public String getArtifactId() {
        return model.getArtifactId();
    }

    @Override
    public String getVersion() {
        return model.getVersion();
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public String getOrganizationName() {
        return model.getOrganization() != null ? model.getOrganization().getName() : null;
    }

    @Override
    public URL getOrganizationUrl() {
        if (model.getOrganization() == null) {
            return null;
        }

        String url = model.getOrganization().getUrl();
        if (Validate.isNullOrEmpty(url)) {
            return null;
        }
        else {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalStateException(url
                        + " does not represent a valid URL, unable to get Organization URL from the POM file");
            }
        }
    }

    @Override
    public String getFinalName() {
        return model.getBuild().getFinalName() + "." + model.getPackaging();
    }

    @Override
    public PackagingType getPackagingType() {
        return PackagingType.of(model.getPackaging());
    }

    @Override
    public File getBaseDirectory() {
        return model.getProjectDirectory();
    }

    @Override
    public File getSourceDirectory() {
        return new File(model.getBuild().getSourceDirectory());
    }

    @Override
    public File getBuildOutputDirectory() {
        return new File(model.getBuild().getOutputDirectory());
    }

    @Deprecated
    public List<File> getProjectResources() {
        List<File> files = new ArrayList<File>();
        List<Resource> resources = model.getBuild().getResources();
        // FIXME filtering is not set here
        for (Resource res : resources) {
            // we add resources only if they can be read
            File resourceDir = new File(res.getDirectory());
            if (!Validate.isReadable(resourceDir)) {
                continue;
            }
            for (File candidate : FileUtils.listFiles(resourceDir)) {
                // FIXME handle exclusions and inclusions here
                files.add(candidate);
            }
        }
        return files;
    }

    @Override
    public List<org.jboss.shrinkwrap.resolver.api.maven.pom.Resource> getResources() {

        List<org.jboss.shrinkwrap.resolver.api.maven.pom.Resource> list = new ArrayList<org.jboss.shrinkwrap.resolver.api.maven.pom.Resource>();

        List<Resource> resources = model.getBuild().getResources();
        // FIXME resources content should be filtered
        for (Resource res : resources) {
            // we add resources only if they can be read
            File resourceDir = new File(res.getDirectory());
            if (!Validate.isReadable(resourceDir)) {
                continue;
            }

            // add all files including includes and exclude based filtering
            String targetPrefix = res.getTargetPath();
            for (String relPath : FileUtils.listFiles(resourceDir, res.getIncludes(), res.getExcludes())) {
                list.add(new org.jboss.shrinkwrap.resolver.api.maven.pom.Resource(new File(resourceDir, relPath),
                        normalizeTargetPath(targetPrefix, relPath)));
            }
        }

        return list;
    }

    @Override
    public File getTestSourceDirectory() {
        return new File(model.getBuild().getTestSourceDirectory());
    }

    @Override
    public Set<MavenDependency> getDependencyManagement() {

        // get dependency management
        if (model.getDependencyManagement() != null) {
            final Set<MavenDependency> dependencies = MavenConverter.fromDependencies(model.getDependencyManagement()
                    .getDependencies(), registry);
            return dependencies;
        }
        return Collections.emptySet();
    }

    @Override
    public Set<MavenDependency> getDependencies() {

        // get dependency management
        if (model.getDependencies() != null) {
            return MavenConverter.fromDependencies(model.getDependencies(), registry);
        }
        return Collections.emptySet();
    }

    @Override
    public Map<String, Object> getPluginConfiguration(String pluginKey) {

        Map<String, Plugin> plugins = model.getBuild().getPluginsAsMap();
        Plugin plugin = plugins.get(pluginKey);
        if (plugin == null) {
            return Collections.emptyMap();
        }

        // get raw configuration
        Xpp3Dom rawConfiguration = (Xpp3Dom) plugin.getConfiguration();
        if (rawConfiguration == null) {
            return Collections.emptyMap();
        }
        return toMappedConfiguration(rawConfiguration);
    }

    @Override
    public Properties getProperties() {
        return new Properties(model.getProperties());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMappedConfiguration(Xpp3Dom node) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        for (Xpp3Dom child : node.getChildren()) {
            Object value;
            if (child.getChildCount() > 0) {
                value = toMappedConfiguration(child);
            } else {
                value = child.getValue();
            }
            if (map.containsKey(child.getName())) {
                Object oldValue = map.get(child.getName());
                if (!(oldValue instanceof List)) {
                    final ArrayList<Object> objects = new ArrayList<Object>();
                    objects.add(oldValue);
                    oldValue = objects;
                }
                // noinspection unchecked
                ((List<Object>) oldValue).add(value);
                value = oldValue;
            }
            map.put(child.getName(), value);
        }
        return map;
    }

    private String normalizeTargetPath(String targetPrefix, String target) {

        StringBuilder sb = new StringBuilder();
        if (targetPrefix != null) {
            sb.append(targetPrefix);
        }
        if (targetPrefix != null && !targetPrefix.endsWith("/")) {
            targetPrefix = targetPrefix.replace('\\', '/');
            sb.append("/");
        }
        if (target != null) {
            target = target.replace('\\', '/');
            sb.append(target);
        }

        return sb.toString();

    }

    /**
     * Simple directory listing utility
     *
     * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    private static final class FileUtils {

        private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };
        private static final String[] EMPTY_STRING_ARRAY = new String[0];

        public static Collection<File> listFiles(File root) {
            List<File> allFiles = new ArrayList<File>();
            Queue<File> dirs = new LinkedList<File>();
            dirs.add(root);
            while (!dirs.isEmpty()) {
                for (File f : dirs.poll().listFiles()) {
                    if (f.isDirectory()) {
                        allFiles.add(f);
                        dirs.add(f);
                    } else if (f.isFile()) {
                        allFiles.add(f);
                    }
                }
            }
            return allFiles;
        }

        public static Collection<String> listFiles(File baseDir, List<String> includes, List<String> excludes) {

            final DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(baseDir);

            if (excludes != null) {
                scanner.setExcludes(excludes.toArray(EMPTY_STRING_ARRAY));
            }
            scanner.addDefaultExcludes();

            if (includes != null && includes.size() > 0) {
                scanner.setIncludes(includes.toArray(EMPTY_STRING_ARRAY));
            } else {
                scanner.setIncludes(DEFAULT_INCLUDES);
            }

            scanner.scan();

            return Arrays.asList(scanner.getIncludedFiles());

        }
    }

}
