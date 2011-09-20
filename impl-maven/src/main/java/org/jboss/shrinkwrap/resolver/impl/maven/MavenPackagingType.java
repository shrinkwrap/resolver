package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

enum MavenPackagingType {

    JAR {
        @Override
        public Archive<?> enrichArchiveWithBuildOutput(Archive<?> original, Model model) {
            return original.as(ExplodedImporter.class).importDirectory(getClassesDirectory(model)).as(JavaArchive.class);
        }

        @Override
        public Archive<?> enrichArchiveWithTestArtifacts(Archive<?> original, MavenDependencyResolver resolver,
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
            Validate.isReadable(buildOutputDirectory, "Cannot include compiled classes from " + buildOutputDirectory);
            return new File(buildOutputDirectory);
        }

        private File getTestClassesDirectory(Model model) {
            final String buildTestOutputDirectory = model.getBuild().getTestOutputDirectory();
            Validate.isReadable(buildTestOutputDirectory, "Cannot include compiled test classes from "
                    + buildTestOutputDirectory);
            return new File(buildTestOutputDirectory);
        }

    },
    WAR {
        @Override
        public Archive<?> enrichArchiveWithBuildOutput(Archive<?> original, Model model) {
            // TODO Auto-generated method stub
            return original;
        }

        @Override
        public Archive<?> enrichArchiveWithTestArtifacts(Archive<?> original, MavenDependencyResolver resolver,
                MavenResolutionFilter filter) {

            WebArchive war = original.as(WebArchive.class);
            war.addAsLibraries(resolver.resolveAsFiles(filter));

            return war;
        }

        @Override
        public Archive<?> enrichArchiveWithTestOutput(Archive<?> original, Model model) {
            // TODO Auto-generated method stub
            return original;
        }

    },
    EAP {
        @Override
        public Archive<?> enrichArchiveWithBuildOutput(Archive<?> original, Model model) {
            // TODO Auto-generated method stub
            return original;
        }

        @Override
        public Archive<?> enrichArchiveWithTestArtifacts(Archive<?> original, MavenDependencyResolver resolver,
                MavenResolutionFilter filter) {

            EnterpriseArchive ear = original.as(EnterpriseArchive.class);
            ear.addAsLibraries(resolver.resolveAsFiles(filter));

            return ear;
        }

        @Override
        public Archive<?> enrichArchiveWithTestOutput(Archive<?> original, Model model) {
            // TODO Auto-generated method stub
            return original;
        }
    };

    public abstract Archive<?> enrichArchiveWithBuildOutput(Archive<?> original, Model model);

    public abstract Archive<?> enrichArchiveWithTestArtifacts(Archive<?> original, MavenDependencyResolver resolver,
            MavenResolutionFilter filter);

    public abstract Archive<?> enrichArchiveWithTestOutput(Archive<?> original, Model model);

    public static MavenPackagingType from(String mavenType) {
        Validate.notNullOrEmpty(mavenType, "Maven packaging type must not be empty");

        try {
            return Enum.valueOf(MavenPackagingType.class, mavenType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            StringBuilder sb = new StringBuilder("Unable to create MavenPackagingType, supported packagings are: ");
            for (MavenPackagingType mpt : values()) {
                sb.append(mpt.toString().toLowerCase());
            }
            throw new IllegalArgumentException(sb.toString(), e);
        }
    }
}
