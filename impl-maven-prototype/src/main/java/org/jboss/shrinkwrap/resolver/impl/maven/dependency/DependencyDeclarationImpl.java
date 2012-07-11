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
package org.jboss.shrinkwrap.resolver.impl.maven.dependency;

import java.text.MessageFormat;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusion;
import org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenCoordinateImpl;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class DependencyDeclarationImpl extends MavenCoordinateImpl implements DependencyDeclaration {

    private final Set<DependencyExclusion> exclusions;
    private final ScopeType scope;
    private final boolean optional;

    public DependencyDeclarationImpl(final String groupId, final String artifactId, final PackagingType type,
            final String classifier, final String version, final ScopeType scope, final boolean optional,
            final Set<DependencyExclusion> exclusions) {
        super(groupId, artifactId, type, classifier, version);

        Validate.notNull(scope, MessageFormat.format("Scope of dependency {0} must not be null", getAddress()));

        this.scope = scope;
        this.optional = optional;
        this.exclusions = exclusions;
    }

    @Override
    public Set<DependencyExclusion> getExclusions() {
        return exclusions;
    }

    @Override
    public ScopeType getScope() {
        return scope;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
