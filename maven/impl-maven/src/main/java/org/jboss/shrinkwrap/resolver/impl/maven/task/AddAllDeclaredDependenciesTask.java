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
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

/**
 * Adds all dependencies from Maven Metadata (e.g. declared dependencies in the session) into a list of dependencies to be
 * resolved.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public enum AddAllDeclaredDependenciesTask implements MavenWorkingSessionTask<MavenWorkingSession> {
    INSTANCE;

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

        // For all declared dependencies, add 'em to the Set of dependencies to be resolved for
        // this request
        for (final MavenDependency candidate : dependencies) {
            session.getDependenciesForResolution().add(candidate);
        }
        return session;
    }

}
