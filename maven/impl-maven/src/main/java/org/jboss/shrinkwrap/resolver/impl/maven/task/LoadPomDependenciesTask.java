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

import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * A task which loads metadata from loaded Maven pom file and stores dependencies and dependency management section into current
 * {@link MavenWorkingSession}.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public enum LoadPomDependenciesTask implements MavenWorkingSessionTask<MavenWorkingSession> {
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

        Validate.stateNotNull(session.getParsedPomFile(),
                "Could not load dependencies defined in a pom file. An effective POM must be resolved first.");

        // store dependency management section
        session.getDependencyManagement().addAll(session.getParsedPomFile().getDependencyManagement());

        // store all of the <dependencies> into depMgmt and explicitly-declared dependencies
        final Set<MavenDependency> pomDefinedDependencies = session.getParsedPomFile().getDependencies();
        session.getDeclaredDependencies().addAll(pomDefinedDependencies);
        session.getDependencyManagement().addAll(pomDefinedDependencies);

        return session;
    }
}
