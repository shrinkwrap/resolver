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

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * A task that tries to resolve version for a dependency from metadata stored in current {@see MavenWorkingSession}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ResolveVersionFromMetadataTask implements MavenWorkingSessionTask<String> {
    private static final Logger log = Logger.getLogger(ResolveVersionFromMetadataTask.class.getName());

    private final MavenDependency dependency;

    public ResolveVersionFromMetadataTask(MavenDependency dependency) {
        this.dependency = dependency;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jboss.shrinkwrap.resolver.impl.maven.task.MavenWorkingSessionTask#execute(org.jboss.shrinkwrap.resolver.impl.maven
     * .MavenWorkingSession)
     */
    @Override
    public String execute(MavenWorkingSession session) {
        final String declaredVersion = dependency.getVersion();
        String resolvedVersion = declaredVersion;

        // is not able to infer anything, it was not configured
        if (Validate.isNullOrEmpty(resolvedVersion)) {

            // version is ignore here, so we have to iterate to get the dependency we are looking for
            if (session.getDependencyManagement().contains(dependency)) {

                // get the dependency from internal dependencyManagement
                MavenDependency resolved = null;
                Iterator<MavenDependency> it = session.getDependencyManagement().iterator();
                while (it.hasNext()) {
                    resolved = it.next();
                    if (resolved.equals(dependency)) {
                        break;
                    }
                }
                // we have resolved a version from dependency management
                resolvedVersion = resolved.getVersion();
                log.log(Level.FINE, "Resolved version {} from the POM file for the artifact {}", new Object[] {
                        resolved.getVersion(), dependency.toCanonicalForm() });

            }
        }

        // Still unresolved?
        if (Validate.isNullOrEmpty(resolvedVersion)) {

            // log available version management
            if (log.isLoggable(Level.FINER)) {
                StringBuilder sb = new StringBuilder("Available version management: \n");
                for (final MavenDependency depmgmt : session.getDependencyManagement()) {
                    sb.append(depmgmt).append("\n");
                }
                log.log(Level.FINER, sb.toString());
            }

            throw new ResolutionException(
                    MessageFormat
                            .format(
                                    "Unable to get version for dependency specified by {0}, it was not provided in <dependencyManagement> section.",
                                    dependency.toCanonicalForm()));
        }

        // Return
        return resolvedVersion;
    }

}
