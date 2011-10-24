package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

class MavenDependencyBuilderForArtifact extends AbstractMavenDependencyResolverBase implements MavenDependencyBuilder {

    MavenDependencyBuilderForArtifact(MavenEnvironment maven, String coordinates) {
        super(maven);

        MavenDependency dependency = MavenConverter.asDepedencyWithVersionManagement(maven.getVersionManagement(),
                coordinates);
        maven.getDependencies().push(dependency);

    }

    @Override
    public MavenDependencyResolver up() {
        return new MavenDependencyResolverImpl(maven);
    }

    @Override
    public MavenDependencyBuilder artifact(String coordinates) throws ResolutionException {
        Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

        MavenDependencyBuilderForArtifact builder = new MavenDependencyBuilderForArtifact(maven, coordinates);
        return builder;

    }

    @Override
    public MavenDependencyBuilder artifacts(String... coordinates) throws ResolutionException {
        Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

        MavenDependencyBuilderForArtifacts builder = new MavenDependencyBuilderForArtifacts(maven, coordinates);
        return builder;
    }

    @Override
    public MavenDependencyBuilder scope(String scope) {
        MavenDependency dependency = maven.getDependencies().peek();
        dependency.scope(scope);

        return this;
    }

    @Override
    public MavenDependencyBuilder optional(boolean optional) {
        MavenDependency dependency = maven.getDependencies().peek();
        dependency.optional(optional);
        return this;
    }

    @Override
    public MavenDependencyBuilder exclusion(String exclusion) {
        MavenDependency dependency = maven.getDependencies().peek();
        dependency.exclusions(exclusion);
        return this;
    }

    @Override
    public MavenDependencyBuilder exclusions(String... exclusions) {
        MavenDependency dependency = maven.getDependencies().peek();
        dependency.exclusions(exclusions);
        return this;
    }

    @Override
    public MavenDependencyBuilder exclusions(Collection<String> exclusions) {
        MavenDependency dependency = maven.getDependencies().peek();
        dependency.exclusions(exclusions.toArray(new String[0]));
        return this;
    }

}
