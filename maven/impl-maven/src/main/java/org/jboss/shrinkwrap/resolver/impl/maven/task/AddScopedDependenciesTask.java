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
package org.jboss.shrinkwrap.resolver.impl.maven.task;

import java.util.ArrayList;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;

/**
 * Adds all dependencies from Maven Metadata (e.g. declared dependencies in the session) into a list of dependencies to be
 * resolved filtered by scope type.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class AddScopedDependenciesTask implements MavenWorkingSessionTask<MavenWorkingSession> {

    private static final List<MavenDependency> EMPTY_LIST = new ArrayList<MavenDependency>(0);

    private final ScopeType[] scopes;

    /**
     * Creates a task which will include all dependencies based on passed scopes
     *
     * @param scopes Scopes of dependencies to be added for resolution
     */
    public AddScopedDependenciesTask(ScopeType... scopes) {
        this.scopes = scopes;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jboss.shrinkwrap.resolver.impl.maven.task.MavenWorkingSessionTask#execute(org.jboss.shrinkwrap.resolver.impl.maven
     * .MavenWorkingSession)
     */
    @Override
    public MavenWorkingSession execute(MavenWorkingSession session) {

        // Get all declared dependencies
        final List<MavenDependency> dependencies = new ArrayList<MavenDependency>(session.getDeclaredDependencies());

        // Filter by scope
        final MavenResolutionFilter preResolutionFilter = new ScopeFilter(scopes);

        // For all declared dependencies which pass the filter, add 'em to the Set of dependencies to be resolved for
        // this request
        for (final MavenDependency candidate : dependencies) {
            if (preResolutionFilter.accepts(candidate, EMPTY_LIST, EMPTY_LIST)) {
                session.getDependenciesForResolution().add(candidate);
            }
        }
        return session;

    }

}
