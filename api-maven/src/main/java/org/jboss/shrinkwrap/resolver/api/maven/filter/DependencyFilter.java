/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;

/**
 * A filter which accepts only specified dependency. You can omit the version while defining dependency by Maven
 * coordinates.
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
public class DependencyFilter implements MavenResolutionFilter {

    private String allowedDependency;

    /**
     *
     */
    public DependencyFilter(String coordinates) {
        this.allowedDependency = coordinates;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter#accept(org.jboss.shrinkwrap.resolver.api.maven.
     * MavenDependency )
     */
    @Override
    public boolean accept(MavenDependency element) {
        return element.hasSameArtifactAs(allowedDependency);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter#configure(java.util.Collection,
     * java.util.Collection)
     */
    @Override
    public MavenResolutionFilter configure(Collection<MavenDependency> dependencies) {
        // no-op method
        return this;
    }

}
