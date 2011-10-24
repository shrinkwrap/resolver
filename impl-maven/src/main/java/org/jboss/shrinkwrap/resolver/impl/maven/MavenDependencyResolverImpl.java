package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenRepositoryBuilder;
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
    public MavenDependencyResolver configureFrom(String path) {
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the settings.xml ('" + path + "') must be defined and accessible");

        this.maven = maven.execute(new DefaultSettingsBuildingRequest().setUserSettingsFile(new File(path)));
        maven.regenerateSession();
        return this;
    }

    @Override
    public EffectivePomMavenDependencyResolver loadEffectivePom(String path, String... profiles) throws ResolutionException {

        Validate.notNullOrEmpty(path, "Path to a POM file must be specified");
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the pom.xml ('" + path + "')file must be defined and accessible");

        File pom = new File(resolvedPath);
        this.maven = maven.execute(new DefaultModelBuildingRequest().setPomFile(pom));
        return new EffectivePomMavenDependencyResolverImpl(maven);
    }

    @Override
    public MavenDependencyResolver useCentralRepo(boolean useCentral) {
        this.maven = maven.useCentralRepository(useCentral);
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
    public MavenRepositoryBuilder repository(String url) {
        Validate.notNull(url, "The url of the repository must not be null");

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MavenRepositoryBuilder repositories(String... url) {
        Validate.notNullAndNoNullValues(url, "The urls of the repositories must not be null");

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MavenEnvironment getMavenEnvironment() {
        return maven;
    }

}