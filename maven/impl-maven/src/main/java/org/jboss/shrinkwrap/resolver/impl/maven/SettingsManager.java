/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;

/**
 * A manager for {@link Settings} and related options and operations,
 * it handles building of an instance of {@link Settings} when necessary and regenerating after changes.
 *
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class SettingsManager {

    private Settings settings;

    // make sure that programmatic call to offline method is always preserved
    private Boolean programmaticOffline;

    /**
     * Crates an instance of {@link Settings} and configures it from the given file.
     *
     * @param globalSettings path to global settings file
     * @param userSettings   path to user settings file
     *
     */
    public void configureSettingsFromFile(File globalSettings, File userSettings)
        throws InvalidConfigurationFileException {

        SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
        if (globalSettings != null) {
            request.setGlobalSettingsFile(globalSettings);
        }
        if (userSettings != null) {
            request.setUserSettingsFile(userSettings);
        }
        request.setSystemProperties(SecurityActions.getProperties());

        MavenSettingsBuilder builder = new MavenSettingsBuilder();
        this.settings = builder.buildSettings(request);

        // ensure we keep offline(boolean) if previously set
        propagateProgrammaticOfflineIntoSettings();
    }

    /**
     * Returns an instance of the {@link Settings}, if it hasn't been created yet, it generates it from the default settings.
     *
     * @return an instance of the {@link Settings}
     */
    protected Settings getSettings() {
        if (this.settings == null) {
            this.settings = new MavenSettingsBuilder().buildDefaultSettings();
            // ensure we keep offline(boolean) if previously set
            propagateProgrammaticOfflineIntoSettings();
        }
        return this.settings;
    }

    /**
     * Sets programmaticOffline to the given value - whether the resolver should work in offline mode or not,
     * in case that an instance of the {@link Settings} has been created, the value is propagated into it
     *
     * @param programmaticOffline whether the resolver should work in offline mode or not
     */
    protected void setOffline(Boolean programmaticOffline) {
        this.programmaticOffline = programmaticOffline;
        // propagate offline(boolean) into settings if previously created
        propagateProgrammaticOfflineIntoSettings();
    }

    /**
     * Returns whether the resolver should work in offline mode or not,
     * If the programmaticOffline hasn't been set yet, the value is taken from a {@link Settings} instance
     *
     * @return whether the resolver should work in offline mode or not
     */
    protected boolean isOffline() {
        if (this.programmaticOffline != null) {
            return this.programmaticOffline;
        }
        return this.getSettings().isOffline();
    }

    // utility methods
    private void propagateProgrammaticOfflineIntoSettings() {
        if (this.programmaticOffline != null && this.settings != null) {
            this.settings.setOffline(this.programmaticOffline);
        }
    }
}
