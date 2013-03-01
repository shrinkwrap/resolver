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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.packaging;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.AbstractScanner;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging.PackagingProcessor;

/**
 * Packaging processor for Maven projects of JAR packaging type
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class JarPackagingProcessor extends AbstractCompilingProcessor<JavaArchive> implements PackagingProcessor<JavaArchive> {

    public static final String MAVEN_WAR_PLUGIN_KEY = "org.apache.maven.plugins:maven-jar-plugin";

    private JavaArchive archive;

    @Override
    public boolean handles(PackagingType packagingType) {
        return PackagingType.JAR.equals(packagingType);
    }

    @Override
    public JarPackagingProcessor configure(Archive<?> archive, MavenWorkingSession session) {
        super.configure(session);
        // archive is ignored so far
        this.archive = ShrinkWrap.create(JavaArchive.class, session.getParsedPomFile().getFinalName());
        return this;
    }

    @Override
    public JarPackagingProcessor importBuildOutput(MavenResolutionStrategy strategy) throws ResolutionException,
            IllegalArgumentException, UnsupportedOperationException {

        final ParsedPomFile pomFile = session.getParsedPomFile();

        // add source filed if any
        if (Validate.isReadable(pomFile.getSourceDirectory())) {
            compile(pomFile.getSourceDirectory(), pomFile.getBuildOutputDirectory(),
                    ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.PROVIDED, ScopeType.RUNTIME,
                    ScopeType.SYSTEM);

            JavaArchive classes = ShrinkWrap.create(ExplodedImporter.class, "sources.jar")
                    .importDirectory(pomFile.getBuildOutputDirectory())
                    .as(JavaArchive.class);

            archive = archive.merge(classes);
        }

        final Map<String, Object> jarConfiguration = pomFile.getPluginConfiguration(MAVEN_WAR_PLUGIN_KEY);
        // add resources
        final ListFilter listFilter = new ListFilter();
        final String[] includes = getIncludes(jarConfiguration);
        listFilter.setExcludes(getExcludes(jarConfiguration));
        listFilter.addDefaultExcludes();
        if (includes == null || includes.length == 0) {
            listFilter.setIncludes(DEFAULT_INCLUDES);
        } else {
            listFilter.setIncludes(includes);
        }
        for (File resource : listFilter.scan(pomFile.getProjectResources(), pomFile.getBaseDirectory())) {
            archive.addAsResource(resource);
        }

        return this;
    }

    @Override
    public JavaArchive getResultingArchive() {
        return archive;
    }

    @Override
    protected String[] getExcludes(Map<String, Object> configuration) {
        final List<String> excludes = extractExcludes(configuration, "excludes", "exclude");
        return excludes.toArray(new String[excludes.size()]);
    }

    @Override
    protected String[] getIncludes(Map<String, Object> configuration) {
        final List<String> includes = extractExcludes(configuration, "includes", "include");
        return includes.toArray(new String[includes.size()]);
    }

    private List<String> extractExcludes(Map<String, Object> configuration, String groupKey, String itemKey) {
        List<String> items = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        final Map<String, Object> configExcludes = (Map<String, Object>) configuration.get(groupKey);
        if(configExcludes == null) {
            return items;
        }
        final Object exclude = configExcludes.get(itemKey);
        if (exclude instanceof Iterable) {
            for (Object excludeItem : (Iterable) exclude) {
                addTokenized(items, excludeItem);
            }
        } else {
            addTokenized(items, exclude);
        }
        return items;
    }

    private class ListFilter extends AbstractScanner {
        public List<File> scan(Iterable<File> newfiles, File root) {
            setupDefaultFilters();

            final List<File> includedFiles = new ArrayList<File>();
            final int rootPathLength = root.getAbsolutePath().length();

            for (File file : newfiles) {
                String name = file.getAbsolutePath().substring(rootPathLength + 1);
                if (file.isFile()) {
                    if (isIncluded(name)) {
                        if (!isExcluded(name)) {
                            includedFiles.add(file);
                        }
                    }
                }
            }
            return includedFiles;
        }

        @Override
        public void scan() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String[] getIncludedFiles() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String[] getIncludedDirectories() {
            throw new UnsupportedOperationException();
        }

        @Override
        public File getBasedir() {
            throw new UnsupportedOperationException();
        }
    }
}
