package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;

public class EffectivePomMavenDependencyResolverImpl extends AbstractMavenDependencyResolverBase implements
        EffectivePomMavenDependencyResolver {

    public EffectivePomMavenDependencyResolverImpl(MavenEnvironment maven) {
        super(maven);

        ArtifactTypeRegistry stereotypes = maven.getArtifactTypeRegistry();

        // store all dependency information to be able to retrieve versions later
        if (maven.getModel().getDependencyManagement() != null) {
            Set<MavenDependency> pomDependencyMngmt = MavenConverter.fromDependencies(maven.getModel()
                    .getDependencyManagement().getDependencies(), stereotypes);
            maven.getVersionManagement().addAll(pomDependencyMngmt);
        }

        Set<MavenDependency> pomDefinedDependencies = MavenConverter.fromDependencies(maven.getModel().getDependencies(),
                stereotypes);

        maven.getVersionManagement().addAll(pomDefinedDependencies);

    }

    @Override
    public MavenDependencyResolver up() {
        return new MavenDependencyResolverImpl(maven);
    }

    @Override
    public EffectivePomMavenDependencyResolver importTestDependencies() {
        return importAnyDependencies(new ScopeFilter("test"));
    }

    @Override
    public EffectivePomMavenDependencyResolver importAllDependencies() {
        return importAnyDependencies(AcceptAllFilter.INSTANCE);
    }

    @Override
    public EffectivePomMavenDependencyResolver importAnyDependencies(MavenResolutionFilter filter) {

        ArtifactTypeRegistry stereotypes = maven.getArtifactTypeRegistry();

        // store all dependency information to be able to retrieve versions later
        Set<MavenDependency> pomDefinedDependencies = MavenConverter.fromDependencies(maven.getModel().getDependencies(),
                stereotypes);

        // configure filter
        MavenResolutionFilter configuredFilter = filter.configure(pomDefinedDependencies);

        for (MavenDependency candidate : pomDefinedDependencies) {
            if (configuredFilter.accept(candidate)) {
                maven.getDependencies().push(candidate);
            }
        }

        return this;
    }

}