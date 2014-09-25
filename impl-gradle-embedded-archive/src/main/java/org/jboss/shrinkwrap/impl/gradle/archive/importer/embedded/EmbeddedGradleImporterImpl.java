/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
import org.gradle.tooling.model.DomainObjectSet;
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

    private static final String BUILD_DIR_NAME = "build-swr";

    private final Archive<?> archive;

    private final GradleConnector connector = GradleConnector.newConnector();

    private String[] tasks = new String[] { "build" };
    private String[] arguments = new String[] { "--exclude-task", "test", "-PbuildDir=" + BUILD_DIR_NAME };

    private BuildLauncher buildLauncher;

    private ProjectConnection projectConnection;

    private String projectName;

    private File buildResult;

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

        final File absoluteFile = projectDir.getAbsoluteFile();
        if (!absoluteFile.exists()) {
            throw new IllegalArgumentException("Given project dir do not exist: " + absoluteFile);
        } else if (!absoluteFile.isDirectory()) {
            throw new IllegalArgumentException("Given project dir is not a directory" + absoluteFile);
        }

        projectName = absoluteFile.getName();
        connector.forProjectDirectory(absoluteFile);
        return this;
    }

    @Override
    public DistributionConfigurationStage forProjectDirectory(final String projectDir) {
        Validate.notNull(projectDir, "Project directory path can not be null or empty");

        return this.forProjectDirectory(new File(projectDir));
    }

    @Override
    public DistributionConfigurationStage forThisProjectDirectory() {
        return forProjectDirectory("");
    }

    @Override
    public <TYPE extends Assignable> TYPE as(final Class<TYPE> clazz) {
        final File result;
        if (buildResult == null) {
            result = importFromDefaultLibsDirectory();
        } else {
            result = importFromConfiguredPath();
        }
        return ShrinkWrap.create(ZipImporter.class, archive.getName()).importFrom(result).as(clazz);
    }

    private File importFromDefaultLibsDirectory() {
        final GradleProject currentGradleProject = findCurrentGradleProject();
        final File buildDir = new File(currentGradleProject.getBuildDirectory().getParentFile(), BUILD_DIR_NAME);
        final File libsDir = new File(buildDir, "libs");
        final File[] results = libsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.startsWith(currentGradleProject.getName());
            }
        });

        if (results == null || results.length == 0) {
            throw new IllegalArgumentException(
                "Wrong project directory is used. Tests have to be run from working directory which is a current sub-module directory.");
        }

        return results[0];
    }

    private File importFromConfiguredPath() {
        return buildResult;
    }

    private GradleProject findCurrentGradleProject() {
        final GradleProject rootGradleProject = projectConnection.getModel(GradleProject.class);
        if (!rootGradleProject.getName().equals(projectName)) {
            final GradleProject child = findChildProject(rootGradleProject, projectName);
            if (child != null) {
                return child;

            }
        }
        return rootGradleProject;
    }

    private GradleProject findChildProject(GradleProject gradleProject, String childProjectName) {
        final DomainObjectSet<? extends GradleProject> children = gradleProject.getChildren();
        for (GradleProject child : children) {
            if (child.getName().equals(childProjectName)) {
                return child;
            }
        }

        for (GradleProject child : children) {
            final GradleProject foundChild = findChildProject(child, childProjectName);
            if (foundChild != null) {
                return foundChild;
            }
        }

        return null;
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

    @Override
    public Assignable importBuildOutput(File buildResult) {
        this.buildResult = buildResult.getAbsoluteFile();
        return importBuildOutput();
    }

    @Override
    public Assignable importBuildOutput(String buildResult) {
        return importBuildOutput(new File(buildResult));
    }
}
