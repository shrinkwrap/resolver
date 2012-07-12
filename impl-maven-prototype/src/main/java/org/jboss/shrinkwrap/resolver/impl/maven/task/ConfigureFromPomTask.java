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
import org.jboss.shrinkwrap.resolver.api.maven.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.SettingsXmlProfileSelector;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ConfigureFromPomTask implements MavenWorkingSessionTask {

    private final File pomFile;
    private final String[] profiles;

    public ConfigureFromPomTask(File pomFile, String... profiles) {
        this.pomFile = pomFile;
        this.profiles = profiles;
    }

    public ConfigureFromPomTask(String pathToPomFile, String... profiles) throws IllegalArgumentException,
            InvalidConfigurationFileException {

        Validate.notNullOrEmpty(pathToPomFile, "Path to a POM file must be specified");
        String resolvedPath = ResourceUtil.resolvePathByQualifier(pathToPomFile);
        Validate.isReadable(resolvedPath, "Path to the pom.xml ('" + pathToPomFile + "')file must be defined and accessible");

        this.pomFile = new File(resolvedPath);
        this.profiles = profiles;
    }

    @Override
    public MavenWorkingSession execute(MavenWorkingSession session) {

        Validate.notNull(pomFile, "Path to pom.xml file must not be null");
        Validate.isReadable(pomFile, "Path to the pom.xml ('" + pomFile + "')file must be defined and accessible");
        DefaultModelBuildingRequest request = new DefaultModelBuildingRequest()
                .setSystemProperties(SecurityActions.getProperties()).setProfiles(session.getSettingsDefinedProfiles())
                .setPomFile(pomFile).setActiveProfileIds(SettingsXmlProfileSelector.explicitlyActivatedProfiles(profiles))
                .setInactiveProfileIds(SettingsXmlProfileSelector.explicitlyDisabledProfiles(profiles));

        return session.execute(request);
    }

}
