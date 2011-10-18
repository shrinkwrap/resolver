package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

class MavenDependencyBuilderForArtifact extends AbstractMavenDependencyResolverBase implements MavenDependencyBuilder {

    private MavenDependencyResolverInternal parent;

    MavenDependencyBuilderForArtifact(MavenDependencyResolverInternal parent, String coordinates) {
        super(parent.getDelegate());
        this.parent = parent;

        MavenDependency dependency = MavenConverter
                .asDepedencyWithVersionManagement(parent.getVersionManagement(), coordinates);
        parent.getDependencies().push(dependency);

    }

    @Override
    public MavenDependencyResolver up() {
        return parent;
    }

    @Override
    public MavenDependencyBuilder artifact(String coordinates) throws ResolutionException {
        Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

        MavenDependencyBuilderForArtifact builder = new MavenDependencyBuilderForArtifact(parent, coordinates);
        return builder;

    }

    @Override
    public MavenDependencyBuilder artifacts(String... coordinates) throws ResolutionException {
        Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

        MavenDependencyBuilderForArtifacts builder = new MavenDependencyBuilderForArtifacts(parent, coordinates);
        return builder;
    }

    @Override
    public MavenDependencyBuilder scope(String scope) {
        MavenDependency dependency = parent.getDependencies().peek();
        dependency.setScope(scope);

        return this;
    }

    @Override
    public MavenDependencyBuilder optional(boolean optional) {
        MavenDependency dependency = parent.getDependencies().peek();
        dependency.setOptional(optional);
        return this;
    }

    @Override
    public MavenDependencyBuilder exclusion(String exclusion) {
        MavenDependency dependency = parent.getDependencies().peek();
        dependency.addExclusions(exclusion);
        return this;
    }

    @Override
    public MavenDependencyBuilder exclusions(String... exclusions) {
        MavenDependency dependency = parent.getDependencies().peek();
        dependency.addExclusions(exclusions);
        return this;
    }

    @Override
    public MavenDependencyBuilder exclusions(Collection<String> exclusions) {
        MavenDependency dependency = parent.getDependencies().peek();
        dependency.addExclusions(exclusions.toArray(new String[0]));
        return this;
    }

}
