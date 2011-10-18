/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Encapsulation for representation of distint packaging types in Maven.
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
enum MavenPackagingType {

    JAR {
        @Override
        public Archive<?> enrichArchiveWithBuildOutput(Archive<?> original, Model model) {
            return original.as(ExplodedImporter.class).importDirectory(getClassesDirectory(model)).as(JavaArchive.class);
        }

        @Override
        public Archive<?> enrichArchiveWithTestArtifacts(Archive<?> original, EffectivePomMavenDependencyResolver resolver,
                MavenResolutionFilter filter) {

            throw new UnsupportedOperationException(
                    "Enable to enrich archive "
                            + original.getName()
                            + " by test dependencies, this operation is not supported for packagings of type <packaging>jar</packaging>");
        }

        @Override
        public Archive<?> enrichArchiveWithTestOutput(Archive<?> original, Model model) {
            return original.as(ExplodedImporter.class).importDirectory(getTestClassesDirectory(model)).as(JavaArchive.class);
        }

        private File getClassesDirectory(Model model) {
            final String buildOutputDirectory = model.getBuild().getOutputDirectory();
            Validate.isReadable(buildOutputDirectory, "Cannot include compiled classes from " + buildOutputDirectory
                    + ", directory cannot be read.");
            return new File(buildOutputDirectory);
        }

        private File getTestClassesDirectory(Model model) {
            final String buildTestOutputDirectory = model.getBuild().getTestOutputDirectory();
            Validate.isReadable(buildTestOutputDirectory, "Cannot include compiled test classes from "
                    + buildTestOutputDirectory
                    + ", directory cannot be read. Please make sure you're running in integration-test phase.");
            return new File(buildTestOutputDirectory);
        }

    },
    WAR {
        @Override
        public Archive<?> enrichArchiveWithBuildOutput(Archive<?> original, Model model) {
            return original.as(ExplodedImporter.class).importDirectory(getWebappDirectory(model)).as(WebArchive.class);
        }

        @Override
        public Archive<?> enrichArchiveWithTestArtifacts(Archive<?> original, EffectivePomMavenDependencyResolver resolver,
                MavenResolutionFilter filter) {

            WebArchive war = original.as(WebArchive.class);
            war.addAsLibraries(resolver.resolveAsFiles(filter));

            return war;
        }

        @Override
        public Archive<?> enrichArchiveWithTestOutput(Archive<?> original, Model model) {

            JavaArchive testClasses = ShrinkWrap.create(JavaArchive.class, "test-classes.jar").as(ExplodedImporter.class)
                    .importDirectory(getTestClassesDirectory(model)).as(JavaArchive.class);

            return original.merge(testClasses, "WEB-INF/classes").as(WebArchive.class);
        }

        private File getWebappDirectory(Model model) {
            final String buildDirectory = model.getBuild().getDirectory();
            final String finalName = model.getBuild().getFinalName();
            final String path = buildDirectory + File.separator + finalName;
            Validate.isReadable(path, "Cannot include exploded war archive from " + path
                    + ", directory cannot be read. Please make sure you're running in integration-test phase.");
            return new File(path);
        }

        private File getTestClassesDirectory(Model model) {
            final String buildTestOutputDirectory = model.getBuild().getTestOutputDirectory();
            Validate.isReadable(buildTestOutputDirectory, "Cannot include compiled test classes from "
                    + buildTestOutputDirectory + ", directory cannot be read.");
            return new File(buildTestOutputDirectory);
        }

    },
    EAR {
        @Override
        public Archive<?> enrichArchiveWithBuildOutput(Archive<?> original, Model model) {
            return original.as(ExplodedImporter.class).importDirectory(getWorkDirectory(model)).as(EnterpriseArchive.class);
        }

        @Override
        public Archive<?> enrichArchiveWithTestArtifacts(Archive<?> original, EffectivePomMavenDependencyResolver resolver,
                MavenResolutionFilter filter) {

            EnterpriseArchive ear = original.as(EnterpriseArchive.class);
            ear.addAsLibraries(resolver.resolveAsFiles(filter));

            return ear;
        }

        @Override
        public Archive<?> enrichArchiveWithTestOutput(Archive<?> original, Model model) {

            throw new UnsupportedOperationException(
                    "Enable to enrich archive "
                            + original.getName()
                            + " by test classes output, this operation is not supported for packagings of type <packaging>ear</packaging>.\n"
                            + "If you want to include the test classes, you have to include the classes in a jar file and add it as library into EAR.");
        }

        private File getWorkDirectory(Model model) {
            final String buildDirectory = model.getBuild().getDirectory();
            final String finalName = model.getBuild().getFinalName();
            final String path = buildDirectory + File.separator + finalName;
            Validate.isReadable(path, "Cannot include exploded ear archive from " + path
                    + ", directory cannot be read. Please make sure you're running in integration-test phase.");
            return new File(path);
        }
    };

    /**
     * Adds build output based on model to the archive
     *
     * @param original The archive to be enriched
     * @param model the model which contains required information
     * @return the enriched archive
     */
    public abstract Archive<?> enrichArchiveWithBuildOutput(Archive<?> original, Model model);

    /**
     * Enriches an archive using metadata loaded from effective pom by a EffectivePomMavenDependencyResolver instance
     *
     * @param original the original archive to be enriched
     * @param resolver the resolver containing the metadata
     * @param filter the filter to be applied
     * @return the enriched archive
     */
    public abstract Archive<?> enrichArchiveWithTestArtifacts(Archive<?> original, EffectivePomMavenDependencyResolver resolver,
            MavenResolutionFilter filter);

    /**
     * Adds build test output based on model to the archive
     *
     * @param original The archive to be enriched
     * @param model the model which contains required information
     * @return the enriched archive
     */
    public abstract Archive<?> enrichArchiveWithTestOutput(Archive<?> original, Model model);

    /**
     * Creates a packaging from maven <packaging>type</packaging>
     *
     * @param mavenType the maven packaging
     * @return the corresponding instance
     */
    public static MavenPackagingType from(String mavenType) {
        Validate.notNullOrEmpty(mavenType, "Maven packaging type must not be empty");

        try {
            return Enum.valueOf(MavenPackagingType.class, mavenType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            StringBuilder sb = new StringBuilder("Unable to create MavenPackagingType, supported packagings are: ");
            for (MavenPackagingType mpt : values()) {
                sb.append(mpt.toString().toLowerCase()).append(" ");
            }
            throw new IllegalArgumentException(sb.toString(), e);
        }
    }
}
