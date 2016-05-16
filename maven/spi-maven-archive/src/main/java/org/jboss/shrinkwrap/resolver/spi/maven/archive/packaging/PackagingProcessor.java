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
package org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;

/**
 * Packaging procesor which is able to build an archive using data available in the Maven Working Session
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <ARCHIVETYPE> Resulting archive type
 */
public interface PackagingProcessor<ARCHIVETYPE extends Archive<ARCHIVETYPE>> {

    /**
     * Check that {@code packagingType} is supported by this packaging processor
     *
     * @param packagingType
     * @return
     */
    boolean handles(PackagingType packagingType);

    /**
     * Configures packaging processor by passing Maven working session and original archive
     *
     * @param originalArchive Original archive holder
     * @param session Current Maven working session
     * @return Modified instance for chaining
     */
    PackagingProcessor<ARCHIVETYPE> configure(Archive<?> originalArchive, MavenWorkingSession session);

    /**
     * Compiles, packages and resolve dependencies for the project. Uses {@code strategy} to define what dependencies will be
     * packaged into project.
     *
     * @param strategy The strategy defining objects to be packaged
     * @return
     * @throws IllegalArgumentException If strategy is {@code null}
     * @throws ResolutionException If a dependency of the project could not be resolved
     */
    PackagingProcessor<ARCHIVETYPE> importBuildOutput(MavenResolutionStrategy strategy) throws IllegalArgumentException,
            ResolutionException;

    /**
     * Returns archive as a ShrinkWrap archive
     *
     * @return
     */
    ARCHIVETYPE getResultingArchive();
}
