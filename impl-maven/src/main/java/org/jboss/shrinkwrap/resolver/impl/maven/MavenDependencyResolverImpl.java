package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenConfigurationType;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ResourceUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

public class MavenDependencyResolverImpl implements MavenDependencyResolver, MavenEnvironmentRetrieval {

    private MavenEnvironment maven;

    public MavenDependencyResolverImpl() {
        this.maven = new MavenEnvironmentImpl();
    }

    public MavenDependencyResolverImpl(MavenEnvironment maven) {
        this.maven = maven;
    }

    @Override
    public <T extends ConfiguredMavenDependencyResolver> T configureFrom(MavenConfigurationType<T> configurationType) {
        Validate.notNull(configurationType, "ConfigurationType instance must not be null");
        return configurationType.configure(this);
    }

    @Override
    public MavenDependencyResolver loadSettings(String userSettings) {
        String resolvedPath = ResourceUtil.resolvePathByQualifier(userSettings);
        Validate.isReadable(resolvedPath, "Path to the settings.xml ('" + userSettings
            + "') must be defined and accessible");

        this.maven = maven.execute(new DefaultSettingsBuildingRequest().setUserSettingsFile(new File(resolvedPath)));
        maven.regenerateSession();
        return this;
    }

    @Override
    public EffectivePomMavenDependencyResolver loadEffectivePom(String path, String... profiles)
        throws ResolutionException {

        Validate.notNullOrEmpty(path, "Path to a POM file must be specified");
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the pom.xml ('" + path + "')file must be defined and accessible");

        File pom = new File(resolvedPath);
        DefaultModelBuildingRequest request = new DefaultModelBuildingRequest()
            .setSystemProperties(SecurityActions.getProperties()).setProfiles(maven.getSettingsDefinedProfiles())
            .setPomFile(pom).setActiveProfileIds(explicitlyActivatedProfiles(profiles))
            .setInactiveProfileIds(explicitlyDisabledProfiles(profiles));

        this.maven = maven.execute(request);
        return new EffectivePomMavenDependencyResolverImpl(maven);
    }

    @Override
    public MavenDependencyResolver disableMavenCentral() {
        this.maven = maven.useCentralRepository(false);
        return this;
    }

    @Override
    public MavenDependencyResolver goOffline() {
        this.maven = maven.goOffline(true);
        // regenerate session
        maven.regenerateSession();
        return this;
    }

    @Override
    public MavenDependencyBuilder artifact(String coordinates) throws ResolutionException {
        Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

        return new MavenDependencyBuilderForArtifact(maven, coordinates);
    }

    @Override
    public MavenDependencyBuilder artifacts(String... coordinates) throws ResolutionException {
        Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

        return new MavenDependencyBuilderForArtifacts(maven, coordinates);
    }

    @Override
    public MavenEnvironment getMavenEnvironment() {
        return maven;
    }

    // selects all profile ids to be activated
    private static List<String> explicitlyActivatedProfiles(String... profiles) {
        if (profiles.length == 0) {
            return Collections.<String> emptyList();
        }
        List<String> activated = new ArrayList<String>();
        for (String profileId : profiles) {
            Validate.notNull(profileId, "Invalid name (\"" + profileId + "\") of a profile to be activated");
            if (!(profileId.startsWith("-") || profileId.startsWith("!"))) {
                activated.add(profileId);
            }
        }

        return activated;
    }

    // selects all profiles ids to be disabled
    private static List<String> explicitlyDisabledProfiles(String... profiles) {
        if (profiles.length == 0) {
            return Collections.<String> emptyList();
        }
        List<String> disabled = new ArrayList<String>();
        for (String profileId : profiles) {
            if (profileId != null && (profileId.startsWith("-") || profileId.startsWith("!"))) {
                String disabledId = profileId.substring(1);
                Validate.notNull(disabledId, "Invalid name (\"" + profileId + "\") of a profile do be disabled");
                disabled.add(disabledId);
            }
        }

        return disabled;
    }

}