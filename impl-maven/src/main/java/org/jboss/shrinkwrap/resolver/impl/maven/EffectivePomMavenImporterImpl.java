package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter.EffectivePomMavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;

class EffectivePomMavenImporterImpl implements MavenImporter.EffectivePomMavenImporter {

    private Archive<?> archive;

    private final MavenPackagingType mpt;
    private final Model model;
    private final MavenRepositorySystem system;
    private final MavenDependencyResolverSettings settings;

    private RepositorySystemSession session;

    public EffectivePomMavenImporterImpl(Archive<?> archive, MavenPackagingType mpt, Model model, MavenRepositorySystem system,
            MavenDependencyResolverSettings settings, RepositorySystemSession session) {

        this.archive = archive;
        this.mpt = mpt;
        this.model = model;
        this.system = system;
        this.settings = settings;
        this.session = session;

    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> archiveType) {
        return archive.as(archiveType);
    }

    @Override
    public EffectivePomMavenImporter importBuildOutput() {
        this.archive = mpt.enrichArchiveWithBuildOutput(archive, model);
        return this;
    }

    @Override
    public EffectivePomMavenImporter importTestBuildOutput() {
        this.archive = mpt.enrichArchiveWithTestOutput(archive, model);
        return this;
    }

    @Override
    public EffectivePomMavenImporter importTestDependencies() {
        return importAnyDependencies(new ScopeFilter("test"));
    }

    @Override
    public EffectivePomMavenImporter importAnyDependencies(MavenResolutionFilter filter) {

        Stack<MavenDependency> dependencies = MavenConverter.fromDependencies(model.getDependencies(),
                system.getArtifactTypeRegistry(session));

        MavenDependencyResolver resolver = getMavenDependencyResolver(dependencies,
                new HashMap<ArtifactAsKey, MavenDependency>());

        this.archive = mpt.enrichArchiveWithTestArtifacts(archive, resolver, filter);
        return this;
    }

    @Override
    public MavenDependencyResolver getMavenDependencyResolver() {

        ArtifactTypeRegistry stereotypes = system.getArtifactTypeRegistry(session);

        Map<ArtifactAsKey, MavenDependency> pomDefinedDependencies = MavenConverter.fromDependenciesAsMap(
                model.getDependencies(), stereotypes);
        return getMavenDependencyResolver(new Stack<MavenDependency>(), pomDefinedDependencies);
    }

    private MavenDependencyResolver getMavenDependencyResolver(Stack<MavenDependency> dependencies,
            Map<ArtifactAsKey, MavenDependency> dependencyManagement) {

        return new MavenBuilderImpl(system, session, settings, dependencies, dependencyManagement);
    }

}
