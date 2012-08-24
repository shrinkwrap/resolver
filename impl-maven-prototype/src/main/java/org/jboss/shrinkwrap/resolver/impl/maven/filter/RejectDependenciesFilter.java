/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven.filter;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusion;
import org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenCoordinateParser;
import org.jboss.shrinkwrap.resolver.impl.maven.dependency.DependencyDeclarationImpl;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class RejectDependenciesFilter implements MavenResolutionFilter {

    private Set<DependencyDeclaration> bannedDependencies;

    public RejectDependenciesFilter(String... coordinates) throws IllegalArgumentException, CoordinateParseException {
        if (coordinates.length == 0) {
            throw new IllegalArgumentException("There must be at least one coordinate specified to be rejected.");
        }

        for (String coords : coordinates) {
            bannedDependencies.add(build(coords));
        }

    }

    @Override
    public MavenResolutionFilter setDefinedDependencies(List<DependencyDeclaration> dependencies) {
        return this;
    }

    @Override
    public MavenResolutionFilter setDefinedDependencyManagement(List<DependencyDeclaration> dependencyManagement) {
        return this;
    }

    @Override
    public boolean accepts(DependencyDeclaration coordinate) throws IllegalArgumentException {

        if (bannedDependencies.contains(coordinate)) {
            return false;
        }

        return true;
    }

    private DependencyDeclaration build(String coordinates) throws CoordinateParseException {
        MavenCoordinateParser parser = MavenCoordinateParser.parse(coordinates);

        return new DependencyDeclarationImpl(parser.getGroupId(), parser.getArtifactId(), parser.getPackaging(),
            parser.getClassifier(), parser.getVersion(), ScopeType.COMPILE, false,
            Collections.<DependencyExclusion> emptySet());

    }

}
