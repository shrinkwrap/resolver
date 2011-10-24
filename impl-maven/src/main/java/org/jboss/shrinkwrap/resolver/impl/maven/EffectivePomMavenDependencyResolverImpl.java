package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Set;
import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;

public class EffectivePomMavenDependencyResolverImpl extends AbstractMavenDependencyResolverBase implements
        EffectivePomMavenDependencyResolver, EffectivePomMavenDependencyResolverInternal {

    private MavenDependencyResolverInternal parent;

    private Model model;

    public EffectivePomMavenDependencyResolverImpl(MavenDependencyResolverInternal parent, Model model) {
        super(parent.getDelegate());
        this.parent = parent;
        this.model = model;

        ArtifactTypeRegistry stereotypes = parent.getSystem().getArtifactTypeRegistry(parent.getSession());

        // store all dependency information to be able to retrieve versions later
        if (model.getDependencyManagement() != null) {
            Set<MavenDependency> pomDependencyMngmt = MavenConverter.fromDependencies(model.getDependencyManagement()
                    .getDependencies(), stereotypes);
            parent.getVersionManagement().addAll(pomDependencyMngmt);
        }

        Set<MavenDependency> pomDefinedDependencies = MavenConverter.fromDependencies(model.getDependencies(), stereotypes);

        parent.getVersionManagement().addAll(pomDefinedDependencies);

    }

    @Override
    public MavenDependencyResolver up() {
        return parent;
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

        ArtifactTypeRegistry stereotypes = parent.getSystem().getArtifactTypeRegistry(parent.getSession());

        // store all dependency information to be able to retrieve versions later
        Set<MavenDependency> pomDefinedDependencies = MavenConverter.fromDependencies(model.getDependencies(), stereotypes);

        // configure filter
        MavenResolutionFilter configuredFilter = filter.configure(pomDefinedDependencies);

        for (MavenDependency candidate : pomDefinedDependencies) {
            if (configuredFilter.accept(candidate)) {
                parent.getDependencies().push(candidate);
            }
        }

        return this;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public MavenDependencyResolverInternal getDelegate() {
        return parent;
    }
}
