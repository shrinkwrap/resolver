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

import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ConfigureSettingsTask implements MavenWorkingSessionTask {

    private final File settingsXmlFile;

    public ConfigureSettingsTask(File settingsXmlFile) throws InvalidConfigurationFileException {
        this.settingsXmlFile = settingsXmlFile;
    }

    public ConfigureSettingsTask(String pathToSettingsXmlFile) throws InvalidConfigurationFileException {

        String resolvedPath = null;
        try {
            resolvedPath = ResourceUtil.resolvePathByQualifier(pathToSettingsXmlFile);
            Validate.isReadable(resolvedPath, "Path to the settings.xml ('" + pathToSettingsXmlFile
                + "') must be defined and accessible");
        }
        // rewrap exception
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationFileException(e.getMessage());
        }
        this.settingsXmlFile = new File(resolvedPath);
    }

    @Override
    public MavenWorkingSession execute(MavenWorkingSession session) {
        try {
            Validate.isReadable(settingsXmlFile, "Path to the settings.xml ('" + settingsXmlFile
                + "') must be defined and accessible");
        }
        // rewrap exception
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationFileException(e.getMessage());
        }

        session = session.execute(new DefaultSettingsBuildingRequest().setUserSettingsFile(settingsXmlFile));
        return session.regenerateSession();
    }

}
