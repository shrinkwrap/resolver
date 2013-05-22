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
package org.jboss.shrinkwrap.resolver.api.maven.archive.importer;

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;

/**
 * Instance of {@link MavenImporter} that has configuration from a POM file loaded
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface PomEquippedMavenImporter extends Assignable {

    /**
     * Build an archive based on metadata previously loaded from a Project Object Model file. Packages following scopes:
     * {@link ScopeType#COMPILE}, {@link ScopeType#IMPORT}, {@link ScopeType#RUNTIME}, {@link ScopeType#SYSTEM}
     *
     * @param strategy
     * @return
     */
    PomEquippedMavenImporter importBuildOutput();

    /**
     * Build an archive based on metadata previously loaded from a Project Object Model file. Uses passed strategy to define
     * dependencies to be packaged into the archive.
     *
     * @param strategy
     * @return
     * @throws IllegalArgumentException If no strategy is specified
     */
    PomEquippedMavenImporter importBuildOutput(MavenResolutionStrategy strategy) throws IllegalArgumentException;

}
