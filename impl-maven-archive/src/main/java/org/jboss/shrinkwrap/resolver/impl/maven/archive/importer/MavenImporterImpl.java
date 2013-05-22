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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.importer;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.ConfiguredMavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.PomEquippedMavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.PomlessMavenImporter;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsFromFileTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.InferPackagingTypeTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomTask;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging.PackagingProcessor;
import org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging.PackagingProcessors;

public class MavenImporterImpl implements MavenImporter, ConfiguredMavenImporter {

    private MavenWorkingSession session;
    private Archive<?> archive;

    public MavenImporterImpl(Archive<?> archive) {
        // this is needed to boostrap session
        Resolvers.use(MavenResolverSystem.class);
        this.session = new MavenWorkingSessionImpl();
        this.archive = archive;
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> type) {
        throw new UnsupportedOperationException(
                "There were no data imported yet. Please load a pom file first using any of the loadPomFrom*() methods.");
    }

    @Override
    public ConfiguredMavenImporter configureFromClassloaderResource(String path) throws IllegalArgumentException,
            UnsupportedOperationException, InvalidConfigurationFileException {

        return this.configureFromClassloaderResource(path, SecurityActions.getThreadContextClassLoader());
    }

    @Override
    public ConfiguredMavenImporter configureFromFile(File file) throws IllegalArgumentException, UnsupportedOperationException,
            InvalidConfigurationFileException {

        Validate.notNull(file, "settings file must be specified");
        Validate.readable(file, "settings file is not readable: " + file.getAbsolutePath());
        new ConfigureSettingsFromFileTask(file).execute(session);
        return this;
    }

    @Override
    public ConfiguredMavenImporter configureFromFile(String pathToFile) throws IllegalArgumentException,
            UnsupportedOperationException, InvalidConfigurationFileException {

        Validate.isNullOrEmpty(pathToFile);
        new ConfigureSettingsFromFileTask(pathToFile).execute(session);
        return this;
    }

    @Override
    public ConfiguredMavenImporter configureFromClassloaderResource(String path, ClassLoader cl)
            throws IllegalArgumentException, UnsupportedOperationException, InvalidConfigurationFileException {

        Validate.isNullOrEmpty(path);
        Validate.notNull(cl, "ClassLoader is required");
        final File file = FileUtil.INSTANCE.fileFromClassLoaderResource(path, cl);
        return this.configureFromFile(file);
    }

    @Override
    public PomEquippedMavenImporter loadPomFromFile(File pomFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        this.session = LoadPomTask.loadPomFromFile(pomFile).execute(session);
        this.session = LoadPomDependenciesTask.INSTANCE.execute(session);
        PackagingType packagingType = InferPackagingTypeTask.INSTANCE.execute(session);
        PackagingProcessor<? extends Archive<?>> processor = PackagingProcessors.find(packagingType);
        processor.configure(archive, session);
        return new PomEquippedMavenImporterImpl(processor);
    }

    @Override
    public PomEquippedMavenImporter loadPomFromFile(File pomFile, String... profiles) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        this.session = LoadPomTask.loadPomFromFile(pomFile, profiles).execute(session);
        this.session = LoadPomDependenciesTask.INSTANCE.execute(session);
        PackagingType packagingType = InferPackagingTypeTask.INSTANCE.execute(session);
        PackagingProcessor<? extends Archive<?>> processor = PackagingProcessors.find(packagingType);
        processor.configure(archive, session);
        return new PomEquippedMavenImporterImpl(processor);
    }

    @Override
    public PomEquippedMavenImporter loadPomFromFile(String pathToPomFile) throws IllegalArgumentException,
            InvalidConfigurationFileException {

        this.session = LoadPomTask.loadPomFromFile(pathToPomFile).execute(session);
        this.session = LoadPomDependenciesTask.INSTANCE.execute(session);
        PackagingType packagingType = InferPackagingTypeTask.INSTANCE.execute(session);
        PackagingProcessor<? extends Archive<?>> processor = PackagingProcessors.find(packagingType);
        processor.configure(archive, session);
        return new PomEquippedMavenImporterImpl(processor);
    }

    @Override
    public PomEquippedMavenImporter loadPomFromFile(String pathToPomFile, String... profiles) throws IllegalArgumentException,
            InvalidConfigurationFileException {

        this.session = LoadPomTask.loadPomFromFile(pathToPomFile, profiles).execute(session);
        this.session = LoadPomDependenciesTask.INSTANCE.execute(session);
        PackagingType packagingType = InferPackagingTypeTask.INSTANCE.execute(session);
        PackagingProcessor<? extends Archive<?>> processor = PackagingProcessors.find(packagingType);
        processor.configure(archive, session);
        return new PomEquippedMavenImporterImpl(processor);
    }

    @Override
    public PomEquippedMavenImporter loadPomFromClassLoaderResource(String pathToPomResource) throws IllegalArgumentException,
            InvalidConfigurationFileException {
        this.session = LoadPomTask.loadPomFromClassLoaderResource(pathToPomResource).execute(session);
        this.session = LoadPomDependenciesTask.INSTANCE.execute(session);
        PackagingType packagingType = InferPackagingTypeTask.INSTANCE.execute(session);
        PackagingProcessor<? extends Archive<?>> processor = PackagingProcessors.find(packagingType);
        processor.configure(archive, session);
        return new PomEquippedMavenImporterImpl(processor);
    }

    @Override
    public PomEquippedMavenImporter loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl)
            throws IllegalArgumentException, InvalidConfigurationFileException {
        this.session = LoadPomTask.loadPomFromClassLoaderResource(pathToPomResource, cl).execute(session);
        this.session = LoadPomDependenciesTask.INSTANCE.execute(session);
        PackagingType packagingType = InferPackagingTypeTask.INSTANCE.execute(session);
        PackagingProcessor<? extends Archive<?>> processor = PackagingProcessors.find(packagingType);
        processor.configure(archive, session);
        return new PomEquippedMavenImporterImpl(processor);
    }

    @Override
    public PomEquippedMavenImporter loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl, String... profiles)
            throws IllegalArgumentException, InvalidConfigurationFileException {
        this.session = LoadPomTask.loadPomFromClassLoaderResource(pathToPomResource, cl, profiles).execute(session);
        this.session = LoadPomDependenciesTask.INSTANCE.execute(session);
        PackagingType packagingType = InferPackagingTypeTask.INSTANCE.execute(session);
        PackagingProcessor<? extends Archive<?>> processor = PackagingProcessors.find(packagingType);
        processor.configure(archive, session);
        return new PomEquippedMavenImporterImpl(processor);
    }

    @Override
    public PomlessMavenImporter offline(boolean offline) {
        session.setOffline(true);
        return this;
    }

    @Override
    public PomlessMavenImporter offline() {
        return this.offline(true);
    }

}
