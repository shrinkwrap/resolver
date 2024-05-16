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
package org.jboss.shrinkwrap.resolver.api.maven.filter;

import java.util.List;

import org.jboss.shrinkwrap.resolver.api.ResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

/**
 * Determines whether a Maven {@link MavenDependency} is to be honored in resolution.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface MavenResolutionFilter extends ResolutionFilter {

    /**
     * Determines whether a {@link MavenDependency} is accepted by this filter. The filtering mechanism may
     * consult the project's explicitly-defined dependencies and <code>dependencyManagement</code> (guaranteed immutable
     * and non-null) in determining whether filtering should be applied.
     *
     * @param dependency
     *            Candidate for inclusion
     * @param dependenciesForResolution
     *            Explicitly-declared dependencies for the current session, does not include those obtained via
     *            transitivity
     * @param dependencyAncestors
     *            A list of ancestors of the candidate for inclusion.
     * @return Whether a {@link MavenDependency} is accepted by this filter.
     */
    boolean accepts(MavenDependency dependency, List<MavenDependency> dependenciesForResolution, List<MavenDependency> dependencyAncestors);

}
