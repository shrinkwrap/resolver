package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.CombinedFilter;
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

        // store all of the <dependencies> into version management
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
    public EffectivePomMavenDependencyResolver importTestDependencies(MavenResolutionFilter filter) {
        Validate.notNull(filter, "Filter must not be null");

        return importAnyDependencies(new CombinedFilter(new ScopeFilter("test"), filter));
    }

    @Override
    public EffectivePomMavenDependencyResolver importAllDependencies() {
        return importAnyDependencies(AcceptAllFilter.INSTANCE);
    }

    @Override
    public EffectivePomMavenDependencyResolver importAnyDependencies(MavenResolutionFilter filter) {
        Validate.notNull(filter, "Filter must not be null");

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

    @Override
    public MavenDependencyBuilder artifact(String coordinates) throws ResolutionException {
        return this.up().artifact(coordinates);
    }

    @Override
    public MavenDependencyBuilder artifacts(String... coordinates) throws ResolutionException {
        return this.up().artifacts(coordinates);
    }

}