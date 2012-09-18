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
 */
package org.jboss.shrinkwrap.resolver.api.maven.strategy;

import java.util.List;

import org.jboss.shrinkwrap.resolver.api.ResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;

/**
 * Defines the contract for developing a Maven-based {@link ResolutionStrategy}; this is accomplished by assembling
 * chains of {@link MavenResolutionFilter}s for filtering {@link MavenDependency}s before and during resolution request
 * execution.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface MavenResolutionStrategy extends
    ResolutionStrategy<MavenDependency, MavenResolutionFilter, MavenResolutionStrategy> {
    /**
     * Obtains the {@link MavenResolutionFilter} chain to be used in filtering a {@link MavenDependency} {@link List}
     * before the request is executed. If any filters in the chain return <code>false</code> when
     * {@link MavenResolutionFilter#accepts(MavenDependency, List)} is invoked, <code>false</code> must be the result of
     * the chain's invocation. In other words, the chain acts as a logical <code>AND</code> for all
     * {@link MavenResolutionFilter}s.
     *
     * @return
     */
    MavenResolutionFilter[] getPreResolutionFilters();

    /**
     * Obtains the {@link MavenResolutionFilter} chain to be used in filtering a {@link MavenDependency} {@link List}
     * during request processing (filtering is done by the backend). If any filters in the chain return
     * <code>false</code> when {@link MavenResolutionFilter#accepts(MavenDependency, List)} is invoked,
     * <code>false</code> must be the result of the chain's invocation. In other words, the chain acts as a logical
     * <code>AND</code> for all {@link MavenResolutionFilter}s.
     *
     * @return
     */
    MavenResolutionFilter[] getResolutionFilters();
}
