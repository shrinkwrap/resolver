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
package org.jboss.shrinkwrap.resolver.impl.maven.task;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * A task that will read a pom file and store it in current {@see MavenWorkingSession}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class LoadPomTask implements MavenWorkingSessionTask<MavenWorkingSession> {

    private static final String[] EMPTY_ARRAY = new String[] {};
    private final File pomFile;
    private final String[] profiles;

    private LoadPomTask(File pomFile, String[] profiles) {
        this.pomFile = pomFile;
        this.profiles = profiles == null ? EMPTY_ARRAY : profiles;
    }

    public static LoadPomTask loadPomFromFile(final File pomFile, final String... profiles) {
        Validate.notNull(pomFile, "POM file must be specified");
        return new LoadPomTask(pomFile, profiles);
    }

    public static LoadPomTask loadPomFromFile(final String pathToPomFile, final String... profiles)
            throws IllegalArgumentException,
            InvalidConfigurationFileException {

        Validate.notNullOrEmpty(pathToPomFile, "Path to a POM file must be specified");
        Validate.readable(pathToPomFile, "Path to the pom.xml ('" + pathToPomFile
                + "')file must be defined and accessible");

        return new LoadPomTask(new File(pathToPomFile), profiles);
    }

    public static LoadPomTask loadPomFromClassLoaderResource(final String pathToPomResource)
            throws IllegalArgumentException,
            InvalidConfigurationFileException {
        return loadPomFromClassLoaderResource(pathToPomResource, SecurityActions.getThreadContextClassLoader());
    }

    public static LoadPomTask loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl)
            throws IllegalArgumentException, InvalidConfigurationFileException {
        return loadPomFromClassLoaderResource(pathToPomResource, cl, EMPTY_ARRAY);
    }

    public static LoadPomTask loadPomFromClassLoaderResource(String pathToPomResource, ClassLoader cl,
            String... profiles)
            throws IllegalArgumentException, InvalidConfigurationFileException {
        Validate.notNullOrEmpty(pathToPomResource, "path to CL resource must be specified");
        Validate.notNull(cl, "ClassLoader must be specified");
        final File file = FileUtil.INSTANCE.fileFromClassLoaderResource(pathToPomResource, cl);
        return new LoadPomTask(file, profiles);
    }

    @Override
    public MavenWorkingSession execute(final MavenWorkingSession session) {

        Validate.notNull(pomFile, "Path to pom.xml file must not be null");
        Validate.readable(pomFile, "Path to the POM ('" + pomFile + "') file must be defined and accessible");

        return session.loadPomFromFile(pomFile, profiles);
    }

}
