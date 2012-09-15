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
package org.jboss.shrinkwrap.resolver.impl.maven.strategy;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.RejectDependenciesFilter;

/**
 * {@link MavenResolutionStrategy} implementation where specified {@link MavenDependency}s may be selectively
 * rejected
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class RejectDependenciesStrategy implements MavenResolutionStrategy {

    private final String[] coordinates;

    public RejectDependenciesStrategy(final String... coordinates) throws IllegalArgumentException,
        CoordinateParseException {
        if (coordinates.length == 0) {
            throw new IllegalArgumentException("There must be at least one coordinate specified to be rejected.");
        }

        this.coordinates = coordinates;

        // here we try to create a filter to raise an exception in an early stage
        // TODO No, we should check some "isParseable" method instead; don't count on an exception for flow control;
        // this is a weak API to rely upon
        new RejectDependenciesFilter(coordinates);
    }

    @Override
    public MavenResolutionFilter getPreResolutionFilter() {
        return new RejectDependenciesFilter(coordinates);
    }

    @Override
    public MavenResolutionFilter getResolutionFilter() {
        return new RejectDependenciesFilter(coordinates);
    }

    @Override
    public MavenResolutionFilter getPostResolutionFilter() {
        return new RejectDependenciesFilter(coordinates);
    }

}
