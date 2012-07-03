package org.jboss.shrinkwrap.resolver.impl.maven.task;

import java.io.File;

import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;

public class ConfigureSettingsTask implements MavenWorkingSessionTask {

    private final File settingsXmlFile;

    public ConfigureSettingsTask(File settingsXmlFile) {
        this.settingsXmlFile = settingsXmlFile;
    }

    public ConfigureSettingsTask(String pathToSettingsXmlFile) {
        String resolvedPath = ResourceUtil.resolvePathByQualifier(pathToSettingsXmlFile);
        Validate.isReadable(resolvedPath, "Path to the settings.xml ('" + pathToSettingsXmlFile
                + "') must be defined and accessible");
        this.settingsXmlFile = new File(resolvedPath);
    }

    @Override
    public MavenWorkingSession execute(MavenWorkingSession session) {
        Validate.isReadable(settingsXmlFile, "Path to the settings.xml ('" + settingsXmlFile
                + "') must be defined and accessible");

        session = session.execute(new DefaultSettingsBuildingRequest().setUserSettingsFile(settingsXmlFile));
        return session.regenerateSession();
    }

}
