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
package org.jboss.shrinkwrap.resolver.impl.maven.prototyping;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.archive.ArchiveFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.archive.MavenArchive;
import org.jboss.shrinkwrap.resolver.api.archive.MavenArchiveResolverSystem;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.FileFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.InputStreamFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
@Ignore
// TODO
// This won't actually run until we provide the implementation, though we DO want to test compilation and observe the
// API grammars in action for each use case
public class UseCasesTestCase {

    /**
     * Use case 1:
     *
     * Resolve a single artifact without transitive dependencies as Archive<?>
     */
    @Test
    public void singleArtifactAsArchive() {

        @SuppressWarnings("unused")
        final JavaArchive longhand = Resolvers.use(MavenArchiveResolverSystem.class).resolve("G:A:V")
            .withoutTransitivity().asSingle(JavaArchive.class);

        @SuppressWarnings("unused")
        final JavaArchive shorthand = MavenArchive.resolver().resolve("G:A:V").withoutTransitivity()
            .asSingle(JavaArchive.class);
    }

    /**
     * Use case 2:
     *
     * Resolve a single artifact without transitive dependencies as File
     */
    @Test
    public void singleArtifactAsFile() {

        @SuppressWarnings("unused")
        final File longhand = Resolvers.use(MavenResolverSystem.class).resolve("groupId:artifactId:version")
            .withoutTransitivity().asSingle(File.class);

        @SuppressWarnings("unused")
        final File shortcut = Maven.resolver().resolve("groupId:artifactId:version").withoutTransitivity()
            .asSingle(File.class);
    }

    /**
     * Use case 3:
     *
     * Resolve a single artifact without transitive dependencies, using version from a POM file
     */
    @Test
    public void singleArtifactWithPomFile() {

        @SuppressWarnings("unused")
        final File longhand = Resolvers.use(MavenResolverSystem.class).configureFromPom("/path/to/pom")
            .resolve("G:A:V").withoutTransitivity().asSingle(File.class);

        @SuppressWarnings("unused")
        final File shorthand = Maven.resolver().configureFromPom("/path/to/pom").resolve("G:A:V").withoutTransitivity()
            .asSingle(File.class);

        @SuppressWarnings("unused")
        final File fromEnvironment = Maven.resolver().configureFromPlugin().resolve("G:A:V").withoutTransitivity()
            .asSingle(File.class);
    }

    /**
     * Use case 4:
     *
     * Resolve two or more artifacts without transitive dependencies
     */
    @Test
    public void multipleArtifacts() {

        @SuppressWarnings("unused")
        final File[] longhandWithDependencyBuilders = Resolvers.use(MavenResolverSystem.class).addDependency()
            .groupId("G").artifactId("A").version("V").and("G2:A2:V2").and().groupId("G3").artifactId("A3")
            .version("V3").resolve().withoutTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V", "G2:A2:V2")
            .withoutTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().resolve("G:A:V", "G2:A2:V2").withoutTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] resolvedFiles = Maven.resolver().addDependency().groupId("groupId").artifactId("artifactId")
            .version("1.0.0").and("G2:A2:V2").resolve().withoutTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] analagous1 = Maven.resolver().resolve("org.jboss:jboss-something:1.0.0", "junit:junit:4.10")
            .withoutTransitivity().as(File.class);

        // DependencyResolvers.use(MavenDependencyResolver.class).artifact("G:A:V").artifact("G:B:V")
        // .resolveAsFiles(new StrictFilter());
        //
        // // or
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).artifacts("G:A:V", "G:B:V").resolveAsFiles(new
        // StrictFilter());
        //
        // // or
        //
        // DependencyResolvers.use(MavenDependencyShortcut.class).resolveAsFiles("G:A:V", "G:B:V");
        //
        // // or
        //
        // Maven.resolveAsFiles("G:A:V", "G:B:V");
    }

    /**
     * Use case 5:
     *
     * Resolve an artifact with transitive dependencies
     */
    @Test
    public void transitiveArtifact() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V").withTransitivity()
            .as(File.class);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().resolve("G:A:V").withTransitivity().as(File.class);
    }

    /**
     * Use case 6:
     *
     * Resolve an artifact with transitive dependencies using extra exclusion
     */
    @Test
    public void transitiveArtifactExtraExclusion() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).addDependency("G:A:V").addExclusion()
            .groupId("G1").artifactId("A1").endExclusion().addExclusion("G2:A2").resolve().withTransitivity()
            .as(File.class);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().addDependency("G:A:V").addExclusion().groupId("G1").artifactId("A1")
            .endExclusion().addExclusion("G2:A2").resolve().withTransitivity().as(File.class);

        // TODO
        // DependencyResolvers.use(MavenDependencyResolver.class).artifact("G:A:V").exclusion("G:B").resolveAsFiles();
        //
        // // or
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).artifact("G:A:V").resolveAsFiles(new
        // ExclusionFilter("G:B"));
    }

    /**
     * Use case 7:
     *
     * Resolve artifacts with transitive dependencies using extra exclusions
     */
    @Test
    public void transitiveArtifactsExtraExclusions() {

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().addDependency("G:A:V").addExclusion().groupId("G1").artifactId("A1")
            .endExclusion().addExclusion("G2:A2").and("G4:A3:V4").addExclusion("G5:A5").resolve().withTransitivity()
            .as(File.class);

        // TODO The above clearly shows that some API work needs to be done. "and" makes a new dependency? When we add
        // the last exclusion, is that to the last dependency or to *all*?

        // DependencyResolvers.use(MavenDependencyResolver.class).artifact("G:A:V").exclusion("G:B").artifact("G:B:V")
        // .exclusion("G:C").resolveAsFiles();
        //
        // // or
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).artifact("G:A:V").artifact("G:B:V")
        // .resolveAsFiles(new ExclusionsFilter("G:B", "G:C"));
        //
        // // or
        // // note, this does exclusion of both exclusions for both artifacts which is not same!
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).artifacts("G:A:V", "G:B:V").exclusions("G:B", "G:C")
        // .resolveAsFiles();
    }

    /**
     * Use case 8:
     *
     * Resolve an artifact with transitive dependencies, using pom for version
     */
    @Test
    public void transitiveArtifactWithPom() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).configureFromPom("path/to/pom").resolve("G:A")
            .withTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().configureFromPom("path/to/pom").resolve("G:A").withTransitivity()
            .as(File.class);

        @SuppressWarnings("unused")
        final File[] fromPlugin = Maven.resolver().configureFromPlugin().resolve("G:A").withTransitivity()
            .as(File.class);
    }

    /**
     * Use case 9:
     *
     * Import the same dependencies as Maven would do.
     */
    @Test
    public void mimickMavenDependencies() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).configureFromPom("/path/to/pom")
            .importDefinedDependencies().as(File.class);

        @SuppressWarnings("unused")
        final JavaArchive[] shorthand = MavenArchive.resolver().configureFromPom("/path/to/pom")
            .importDefinedDependencies().as(JavaArchive.class);

        @SuppressWarnings("unused")
        final JavaArchive[] environment = MavenArchive.resolver().configureFromPlugin().importDefinedDependencies()
            .as(JavaArchive.class);

        // TODO Does the above account for scopes?

        // TODO
        // DependencyResolvers.use(MavenDependencyResolver.class).loadSettings("settings.xml").loadEffectivePom("pom.xml")
        // .importAnyDependencies(new ScopeFilter("compile", "runtime", "")).resolveAsFiles();
        //
        // // or using ShrinkWrap Maven plugin and current Maven execution
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).configureFrom(MavenConfigurationTypes.ENVIRONMENT)
        // .importAnyDependencies(new ScopeFilter("compile", "runtime", "")).resolveAsFiles();
        //
        // // or using MavenImporter, which does a bit different thing
        //
        // ShrinkWrap.create(MavenImporter.class).loadSettings("settings.xml").loadEffectivePom("pom.xml")
        // .importAnyDependencies(new ScopeFilter("compile", "runtime", ""));
    }

    /**
     * Use case 10:
     *
     * Import test dependencies and exclude G:A:V
     */
    @Test
    public void importTestDependenciesWithExtraExclusion() {

        // TODO
        // DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
        // .importTestDependencies(new ExclusionFilter("G:A")).resolveAsFiles();
        //
        // // or
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml").importTestDependencies()
        // .resolveAsFiles(new ExclusionFilter("G:A:V"));
        //
        // // or
        // // note this would not work if G:A:V is a transitive dependency!
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
        // .importAnyDependencies(new CombinedFilter(new ScopeFilter("test"), new ExclusionFilter("G:A:V")))
        // .resolveAsFiles();
    }

    /**
     * Use case 11:
     *
     * Import test dependencies and exclude arquillian/shrinkwrap/container (SHRINKRES-30)
     */
    @Test
    public void importTestDependenciesWithArquillianExclusions() {
        // TODO
        // solution 1 = enumerate within previous use case
        // solution 2 = write a GroupExclusionFilter, note that MavenDependency has no getter for groupId!
        // solution 3 = move shrinkwrap/arquillian/container to a distinct profile, then exclude it

        /*
         * ALR Note: Karel's Solution 2 above looks like the most likely candidate; this isn't really a core feature of
         * SWR, but we go need to define an easy way for users to write group exclusions such that another level can
         * define SW, SWR, SWD, ARQ etc and exclude in one go.
         */
    }

    /**
     * Use case 12:
     *
     * Import a dependency using different classloader (SHRINKRES-26)
     */
    @Test
    public void bootstrapShrinResWithDifferentClassloader() {

        final ClassLoader myCl = new URLClassLoader(new URL[] {});
        @SuppressWarnings("unused")
        final File file = Resolvers.use(MavenResolverSystem.class, myCl).resolve("G:A:V").withoutTransitivity()
            .asSingle(File.class);
    }

    /**
     * Use case 13:
     *
     * Do the same as Maven would do
     */
    @Test
    public void mimickMaven() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).configureFromPom("/path/to/pom")
            .importDefinedDependencies().as(File.class);

        @SuppressWarnings("unused")
        final JavaArchive[] shorthand = MavenArchive.resolver().configureFromPom("/path/to/pom")
            .importDefinedDependencies().as(JavaArchive.class);

        // TODO Does this above fulfill this use case?

        // TODO
        // ShrinkWrap
        // .create(WebArchive.class)
        // .addClasses(Class.class)
        // .addAsResource("resources")
        // .addAsLibraries(
        // DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
        // .importAnyDependencies(new ScopeFilter("compile", "", "runtime")).resolveAsFiles());
        //
        // // or
        // // note current implementation is expecting mvn package to be run first (SHRINKRES-18)
        //
        // ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput();
        //
        // // note usage of ENVIRONMENT configuration is not possible
    }

    /**
     * Use Case 14: Expose dependency information
     *
     * SHRINKRES-27
     */
    @Test
    @SuppressWarnings("unused")
    public void dependencyInfo() {
        final ResolvedArtifactInfo longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V")
            .withoutTransitivity().asSingle(ResolvedArtifactInfo.class);

        final ResolvedArtifactInfo shortcut = Maven.resolver().resolve("G:A:V").withoutTransitivity()
            .asSingle(ResolvedArtifactInfo.class);
        final MavenCoordinate coordinate = shortcut.getCoordinate();
        final String groupId = coordinate.getGroupId();
        final String artifactId = coordinate.getArtifactId();
        final String version = coordinate.getVersion();
        final String resolvedVersion = shortcut.getResolvedVersion();
        final String type = coordinate.getType();
        final boolean isSnapshot = shortcut.isSnapshotVersion();
        final String classifier = coordinate.getClassifier();
        final File file = shortcut.getArtifact(FileFormatProcessor.INSTANCE);
        final File file2 = shortcut.getArtifact(File.class);
        final InputStream in = shortcut.getArtifact(InputStreamFormatProcessor.INSTANCE);
        final InputStream in2 = shortcut.getArtifact(InputStream.class);
        final JavaArchive archive = shortcut.getArtifact(new ArchiveFormatProcessor<JavaArchive>(JavaArchive.class));
    }
}
