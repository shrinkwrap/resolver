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
package org.jboss.shrinkwrap.resolver.api.maven;

import java.util.List;

import org.jboss.shrinkwrap.resolver.api.ResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.ResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.TransitiveResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

/**
 * Defines the contract for developing a Maven-based {@link ResolutionStrategy}; this is composed by assembling
 * {@link List}s of type <code>RESOLUTIONFILTERTYPE</code> for filtering {@link MavenDependency}s before, during
 * and after the resolution request is executed.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface MavenResolutionStrategy extends
    TransitiveResolutionStrategy<MavenDependency, MavenResolutionFilter, MavenResolutionStrategy> {
    /**
     * Obtains the {@link ResolutionFilter} to be used in filtering the {@link MavenDependency} {@link List}
     * before the request is executed.
     *
     * @return
     */
    MavenResolutionFilter getPreResolutionFilter();

    /**
     * Obtains the {@link ResolutionFilter} to be used in filtering the {@link MavenDependency} {@link List}
     * during request processing (filtering is done by the backend).
     *
     * @return
     */
    MavenResolutionFilter getResolutionFilter();

    /**
     * Obtains the {@link ResolutionFilter} to be used in filtering the {@link MavenDependency} {@link List}
     * returned from the backend response.
     *
     * @return
     */
    MavenResolutionFilter getPostResolutionFilter();
}
