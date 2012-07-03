package org.jboss.shrinkwrap.resolver.impl.maven.task;

import java.io.File;

import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.maven.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.SettingsXmlProfileSelector;

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
