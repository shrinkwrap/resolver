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

package org.jboss.shrinkwrap.impl.gradle.archive.importer.embedded;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.ConfigurationStage;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.DistributionConfigurationStage;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class EmbeddedGradleImporterImpl implements EmbeddedGradleImporter, DistributionConfigurationStage {

    private final Archive<?> archive;

    private final GradleConnector connector = GradleConnector.newConnector();

    private String[] tasks = new String[] { "clean", "build" };
    private String[] arguments = new String[] { "-x", "test" };

    private String projectDir;

    private BuildLauncher buildLauncher;

    private ProjectConnection projectConnection;

    private BuildLauncher getBuildLauncher() {
        if (buildLauncher == null) {
            projectConnection = connector.connect();
            buildLauncher = projectConnection.newBuild();
        }
        return buildLauncher;
    }

    public EmbeddedGradleImporterImpl(Archive<?> archive) {
        this.archive = archive;
    }

    @Override
    public DistributionConfigurationStage forProjectDirectory(final File projectDir) {
        Validate.notNull(projectDir, "Project directory file can not be null!");

        if (!projectDir.isDirectory()) {
            throw new IllegalArgumentException("Give File is not a directory");
        }
        connector.forProjectDirectory(projectDir);
        return this;
    }

    @Override
    public DistributionConfigurationStage forProjectDirectory(final String projectDir) {
        Validate.notNullOrEmpty(projectDir, "Project directory path can not be null or empty");

        this.projectDir = projectDir;
        return this.forProjectDirectory(new File(projectDir));
    }

    @Override
    public <TYPE extends Assignable> TYPE as(final Class<TYPE> clazz) {
        final GradleProject gradleProject = projectConnection.getModel(GradleProject.class);

        final File projectDir = new File(this.projectDir);
        final File buildDir = new File(projectDir, "build");
        final File libsDir = new File(buildDir, "libs");
        final File result = libsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.startsWith(gradleProject.getName());
            }
        })[0];

        return ShrinkWrap.create(ZipImporter.class).importFrom(result).as(clazz);
    }

    @Override
    public ConfigurationStage useGradleVersion(final String version) {
        connector.useGradleVersion(version);
        return this;
    }

    @Override
    public ConfigurationStage useDistribution(final URI gradleDistribution) {
        connector.useDistribution(gradleDistribution);
        return this;
    }

    @Override
    public ConfigurationStage useInstallation(final File gradleHome) {
        connector.useInstallation(gradleHome);
        return this;
    }

    @Override
    public ConfigurationStage useDefaultDistribution() {
        return this;
    }

    @Override
    public ConfigurationStage useGradleUserHomeDir(final File gradleUserHomeDir) {
        connector.useGradleUserHomeDir(gradleUserHomeDir);
        return this;
    }

    @Override
    public ConfigurationStage forTasks(final String... tasks) {
        this.tasks = tasks;
        return this;
    }

    @Override
    public ConfigurationStage withArguments(final String... arguments) {
        this.arguments = arguments;
        return this;
    }

    @Override
    public ConfigurationStage setJavaHome(final File javaHome) {
        getBuildLauncher().setJavaHome(javaHome);
        return this;
    }

    @Override
    public ConfigurationStage setJvmArguments(final String... jvmArguments) {
        getBuildLauncher().setJvmArguments(jvmArguments);
        return this;
    }

    @Override
    public Assignable importBuildOutput() {
        getBuildLauncher().forTasks(tasks).withArguments(arguments).run();
        return this;
    }
}
