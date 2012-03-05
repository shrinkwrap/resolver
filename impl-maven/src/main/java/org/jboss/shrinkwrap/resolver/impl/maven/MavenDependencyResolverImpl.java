package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
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
        Validate.isReadable(resolvedPath, "Path to the settings.xml ('" + userSettings + "') must be defined and accessible");

        this.maven = maven.execute(new DefaultSettingsBuildingRequest().setUserSettingsFile(new File(resolvedPath)));
        maven.regenerateSession();
        return this;
    }

    @Override
    public EffectivePomMavenDependencyResolver loadEffectivePom(String path) throws ResolutionException {

        Validate.notNullOrEmpty(path, "Path to a POM file must be specified");
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the pom.xml ('" + path + "')file must be defined and accessible");

        File pom = new File(resolvedPath);
        this.maven = maven.execute(new DefaultModelBuildingRequest().setPomFile(pom));
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

}