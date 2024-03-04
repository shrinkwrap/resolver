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
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * {@link MavenWorkingSessionTask} implementation which configures settings from a {@link File}-based <code>settings.xml</code>
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class ConfigureSettingsFromFileTask implements MavenWorkingSessionTask<MavenWorkingSession> {

    private final File settingsXmlFile;

    public ConfigureSettingsFromFileTask(final File settingsXmlFile) throws InvalidConfigurationFileException {
        assert settingsXmlFile != null;
        assert settingsXmlFile.exists();
        this.settingsXmlFile = settingsXmlFile;
    }

    public ConfigureSettingsFromFileTask(final String pathToSettingsXmlFile) throws InvalidConfigurationFileException {
        assert pathToSettingsXmlFile != null && !pathToSettingsXmlFile.isEmpty();
        String resolvedPath = null;
        try {
            resolvedPath = pathToSettingsXmlFile;
            Validate.isReadable(resolvedPath, "Path to the settings.xml ('" + pathToSettingsXmlFile
                    + "') must be defined and accessible");
        }
        // rewrap exception
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationFileException(e.getMessage());
        }
        this.settingsXmlFile = new File(resolvedPath);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jboss.shrinkwrap.resolver.impl.maven.task.MavenWorkingSessionTask#execute(org.jboss.shrinkwrap.resolver.impl.maven
     * .MavenWorkingSession)
     */
    @Override
    public MavenWorkingSession execute(final MavenWorkingSession session) {
        try {
            Validate.isReadable(settingsXmlFile, "Path to the settings.xml ('" + settingsXmlFile
                    + "') must be defined and accessible");
        }
        // rewrap exception
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationFileException(e.getMessage());
        }

        return session.configureSettingsFromFile(null, settingsXmlFile);
    }

}
