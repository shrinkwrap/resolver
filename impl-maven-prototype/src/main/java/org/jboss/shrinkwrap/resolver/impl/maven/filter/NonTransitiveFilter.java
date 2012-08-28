package org.jboss.shrinkwrap.resolver.impl.maven.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;

public class NonTransitiveFilter implements MavenResolutionFilterInternalView {

    private Set<DependencyDeclaration> allowedDeclarations;

    @Override
    public MavenResolutionFilterInternalView setDefinedDependencies(List<DependencyDeclaration> dependencies) {
        this.allowedDeclarations = new HashSet<DependencyDeclaration>(dependencies);
        return this;
    }

    @Override
    public MavenResolutionFilterInternalView setDefinedDependencyManagement(List<DependencyDeclaration> dependencyManagement) {
        return this;
    }

    @Override
    public boolean accepts(DependencyDeclaration coordinate) throws IllegalArgumentException {
        return allowedDeclarations.contains(coordinate);
    }

}
