/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.api.Assignable;

/**
 * An ShrinkWrap importer which is able to reuse information in a pom file in order to help you build the archive content.
 *
 * <strong>This importer in intended to run in integration-test phase</strong> as it requires Maven to prepare the artifacts.
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
public interface MavenImporter extends Assignable {

    /**
     * Configures MavenImporter using setting.xml file.
     *
     * @param userSettings A path to a settings.xml configuration file
     * @return the MavenImporter with a configuration from given file
     */
    MavenImporter loadSettings(String userSettings);

    /**
     * Loads effective pom from a given location. It will use profiles activated by default.
     *
     * @param path The path to the effective pom.
     * @return MavenImporter which is able to enrich current archive
     */
    EffectivePomMavenImporter loadEffectivePom(String path);

    /**
     * A ShrinkWrap importer which already has metadata required in order to modify the archive.
     *
     * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
     *
     */
    static interface EffectivePomMavenImporter extends Assignable {

        /**
         * Adds content made during package phase to the archive
         *
         * @return The modified archive
         */
        EffectivePomMavenImporter importBuildOutput();

        /**
         * Adds test classes and resources to the archive. This is supported only for JAR and WAR packagings.
         *
         * @return The modified archive
         */
        EffectivePomMavenImporter importTestBuildOutput();

        /**
         * Adds all dependencies defined by a pom file in scope test. This is supported only for WAR and EAR packagings.
         *
         * @return The modified archive
         */
        EffectivePomMavenImporter importTestDependencies();

        /**
         * Adds all dependencies defined by a pom file in scope test. User have to use filtering for the dependencies.
         * This is supported only for WAR and EAR packagings.
         *
         * @param filter The filter to be applied
         * @return The modified archive
         * @throws IllegalArgumentException If the filter is not specified
         */
        EffectivePomMavenImporter importTestDependencies(MavenResolutionFilter filter) throws IllegalArgumentException;

        /**
         * Adds any dependencies defined by a pom file. User have to use a filter to filter the dependencies. This is supported
         * only for WAR and EAR packagings.
         *
         * @param filter the filter to be applied
         * @return The modified archive
         * @throws IllegalArgumentException If the filter is not specified
         */
        EffectivePomMavenImporter importAnyDependencies(MavenResolutionFilter filter) throws IllegalArgumentException;

    }
}
