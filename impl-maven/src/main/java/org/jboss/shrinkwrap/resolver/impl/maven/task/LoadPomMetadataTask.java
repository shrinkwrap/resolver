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

import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.SettingsXmlProfileSelector;

/**
 * {@link MavenWorkingSessionTask} implementation which is configured from properties set by the Maven Resolver Maven
 * Plugin
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class LoadPomMetadataTask implements MavenWorkingSessionTask {

    private static final String[] EMPTY_ARRAY = new String[] {};
    private final File pomFile;
    private final String[] profiles;

    public LoadPomMetadataTask(final File pomFile, final String... profiles) {
        Validate.notNull(pomFile, "POM file must be specified");
        this.pomFile = pomFile;
        this.profiles = profiles == null ? EMPTY_ARRAY : profiles;
    }

    public LoadPomMetadataTask(final String pathToPomFile, final String... profiles) throws IllegalArgumentException,
        InvalidConfigurationFileException {

        Validate.notNullOrEmpty(pathToPomFile, "Path to a POM file must be specified");
        Validate.isReadable(pathToPomFile, "Path to the pom.xml ('" + pathToPomFile
            + "')file must be defined and accessible");

        this.pomFile = new File(pathToPomFile);
        this.profiles = profiles;
    }

    @Override
    public MavenWorkingSession execute(final MavenWorkingSession session) {

        Validate.notNull(pomFile, "Path to pom.xml file must not be null");
        Validate.isReadable(pomFile, "Path to the POM ('" + pomFile + "') file must be defined and accessible");
        final DefaultModelBuildingRequest request = new DefaultModelBuildingRequest()
            .setSystemProperties(SecurityActions.getProperties()).setProfiles(session.getSettingsDefinedProfiles())
            .setPomFile(pomFile).setActiveProfileIds(SettingsXmlProfileSelector.explicitlyActivatedProfiles(profiles))
            .setInactiveProfileIds(SettingsXmlProfileSelector.explicitlyDisabledProfiles(profiles));

        return session.execute(request);
    }

}
