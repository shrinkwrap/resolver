/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.shrinkwrap.api.gradle.archive.importer.embedded;

import org.jboss.shrinkwrap.api.Assignable;

import java.io.File;
import java.net.URI;

/**
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public interface EmbeddedGradleImporter extends Assignable {

    DistributionConfigurationStage forProjectDirectory(final File projectDir);

    DistributionConfigurationStage forProjectDirectory(final String projectDir);

    DistributionConfigurationStage forThisProjectDirectory();

//
//    // EmbeddedGradleImporter
//    DistributionConfigurationStage forProjectDirectory(File projectDir); //!!
//
//    // DistributionConfigurationStage extends ConfigurationStage
//    ConfigurationStage useGradleVersion(String version);
//
//    ConfigurationStage useDistribution(URI gradleDistribution);
//
//    ConfigurationStage useInstallation(File gradleHome);
//
//    ConfigurationStage useDefaultDistribution();
//    // shortcut to build
//
//    // ConfigurationStage extends BuildStage
//    ConfigurationStage useGradleUserHomeDir(File gradleUserHomeDir);
//
//    ConfigurationStage forTasks(String... tasks);
//
//    ConfigurationStage withArguments(String... arguments);
//
//    ConfigurationStage setJavaHome(File javaHome);
//
//    ConfigurationStage setJvmArguments(String... jvmArguments);
//    // shortcut to build
//
//    // BuildStage
//    Assignable importBuildOutput();
//
//    // Assignable
//    void as();
}
