package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

class MavenDependencyBuilderForArtifacts extends AbstractMavenDependencyResolverBase implements MavenDependencyBuilder {

    private int size;

    MavenDependencyBuilderForArtifacts(MavenEnvironment maven, String... coordinates) {
        super(maven);
        this.size = coordinates.length;

        for (String coords : coordinates) {
            MavenDependency dependency = MavenConverter.asDepedencyWithVersionManagement(maven.getVersionManagement(),
                coords);
            maven.getDependencies().push(dependency);
        }
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
        List<MavenDependency> workplace = new ArrayList<MavenDependency>();

        int i;
        for (i = 0; i < size; i++) {
            MavenDependency dependency = maven.getDependencies().pop();
            workplace.add(dependency.scope(scope));
        }

        for (; i > 0; i--) {
            maven.getDependencies().push(workplace.get(i - 1));
        }

        return this;
    }

    @Override
    public MavenDependencyBuilder optional(boolean optional) {
        List<MavenDependency> workplace = new ArrayList<MavenDependency>();

        int i;
        for (i = 0; i < size; i++) {
            MavenDependency dependency = maven.getDependencies().pop();
            workplace.add(dependency.optional(optional));
        }

        for (; i > 0; i--) {
            maven.getDependencies().push(workplace.get(i - 1));
        }

        return this;
    }

    @Override
    public MavenDependencyBuilder exclusion(String exclusion) {
        List<MavenDependency> workplace = new ArrayList<MavenDependency>();

        int i;
        for (i = 0; i < size; i++) {
            MavenDependency dependency = maven.getDependencies().pop();
            workplace.add(dependency.exclusions(exclusion));
        }

        for (; i > 0; i--) {
            maven.getDependencies().push(workplace.get(i - 1));
        }

        return this;
    }

    @Override
    public MavenDependencyBuilder exclusions(String... exclusions) {
        List<MavenDependency> workplace = new ArrayList<MavenDependency>();

        int i;
        for (i = 0; i < size; i++) {
            MavenDependency dependency = maven.getDependencies().pop();
            workplace.add(dependency.exclusions(exclusions));
        }

        for (; i > 0; i--) {
            maven.getDependencies().push(workplace.get(i - 1));
        }

        return this;
    }

    @Override
    public MavenDependencyBuilder exclusions(Collection<String> exclusions) {
        List<MavenDependency> workplace = new ArrayList<MavenDependency>();

        int i;
        for (i = 0; i < size; i++) {
            MavenDependency dependency = maven.getDependencies().pop();
            workplace.add(dependency.exclusions(exclusions.toArray(new String[0])));
        }

        for (; i > 0; i--) {
            maven.getDependencies().push(workplace.get(i - 1));
        }

        return this;
    }

}
