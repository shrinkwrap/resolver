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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.usecases;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencyExclusion;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.junit.Assert;
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
     * <p/>
     * Resolve a single artifact without transitive dependencies as Archive<?>
     */
    @Test
    public void singleArtifactAsArchive() {

        @SuppressWarnings("unused")
        final JavaArchive longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V").withoutTransitivity()
                .asSingle(JavaArchive.class);

        @SuppressWarnings("unused")
        final JavaArchive shorthand = Maven.resolver().resolve("G:A:V").withoutTransitivity().asSingle(JavaArchive.class);
    }

    /**
     * Use case 2:
     * <p/>
     * Resolve a single artifact without transitive dependencies as File
     */
    @Test
    public void singleArtifactAsFile() {

        @SuppressWarnings("unused")
        final File longhand = Resolvers.use(MavenResolverSystem.class).resolve("groupId:artifactId:version")
                .withoutTransitivity().asSingle(File.class);

        @SuppressWarnings("unused")
        final File shortcut = Maven.resolver().resolve("groupId:artifactId:version").withoutTransitivity().asSingle(File.class);
    }

    /**
     * Use case 3:
     * <p/>
     * Resolve a single artifact without transitive dependencies, using version from a POM file
     */
    @Test
    public void singleArtifactWithPomFile() {

        @SuppressWarnings("unused")
        final File longhand = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("/path/to/file").resolve("G:A")
                .withoutTransitivity().asSingle(File.class);

        @SuppressWarnings("unused")
        final File shorthand = Maven.resolver().loadPomFromFile("/path/to/pom").resolve("G:A").withoutTransitivity()
                .asSingle(File.class);

        @SuppressWarnings("unused")
        final File fromEnvironment = Maven.configureResolverViaPlugin().resolve("G:A").withoutTransitivity()
                .asSingle(File.class);

        Maven.resolver().resolve("a:b:v1", "c:d:v2").using(new AcceptScopesStrategy(ScopeType.TEST)).asFile();
    }

    /**
     * Use case 4:
     * <p/>
     * Resolve two or more artifacts without transitive dependencies
     */
    @Test
    public void multipleArtifacts() {

        final MavenDependency dep1 = MavenDependencies.createDependency("GAV", null, false);
        final MavenDependency dep2 = MavenDependencies.createDependency("GAV2", null, false);
        @SuppressWarnings("unused")
        final File[] longhandWithDependencyBuilders = Resolvers.use(MavenResolverSystem.class).addDependencies(dep1, dep2)
                .resolve().withoutTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V", "G2:A2:V2").withoutTransitivity()
                .as(File.class);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().resolve("G:A:V", "G2:A2:V2").withoutTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] resolvedFiles = Maven.resolver().addDependencies(dep1, dep2).resolve().withoutTransitivity()
                .as(File.class);

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
     * <p/>
     * Resolve an artifact with transitive dependencies
     */
    @Test
    public void transitiveArtifact() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V").withTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().resolve("G:A:V").withTransitivity().as(File.class);
    }

    /**
     * Use case 6:
     * <p/>
     * Resolve an artifact with transitive dependencies using extra exclusion
     */
    @Test
    public void transitiveArtifactExtraExclusion() {

        final MavenDependencyExclusion exclusion = MavenDependencies.createExclusion("GA");
        final MavenDependency dependency = MavenDependencies.createDependency("GAV", null, false, exclusion);

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).addDependency(dependency).resolve().withTransitivity()
                .as(File.class);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().addDependency(dependency).resolve().withTransitivity().as(File.class);
    }

    /**
     * Use case 7:
     * <p/>
     * Resolve artifacts with transitive dependencies using extra exclusions
     */
    @Test
    public void transitiveArtifactsExtraExclusions() {

        final MavenDependencyExclusion exclusion = MavenDependencies.createExclusion("GA");
        final MavenDependencyExclusion exclusion2 = MavenDependencies.createExclusion("GA");
        final MavenDependency dependency = MavenDependencies.createDependency("GAV", null, false, exclusion, exclusion2);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().addDependency(dependency).resolve().withTransitivity().as(File.class);
    }

    /**
     * Use case 8:
     * <p/>
     * Resolve an artifact with transitive dependencies, using pom for version
     */
    @Test
    public void transitiveArtifactWithPom() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("path/to/pom").resolve("G:A")
                .withTransitivity().as(File.class);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().loadPomFromFile("path/to/pom").resolve("G:A").withTransitivity()
                .as(File.class);

        @SuppressWarnings("unused")
        final File[] fromPlugin = Maven.configureResolverViaPlugin().resolve("G:A").withTransitivity().as(File.class);
    }

    /**
     * Use case 9:
     * <p/>
     * Import the same dependencies as Maven would do.
     */
    @Test
    public void mimickMavenDependencies() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("/path/to/pom")
                .importRuntimeDependencies().resolve().withTransitivity().as(File.class);

        Assert.fail("API BROKEN HERE");

        // @SuppressWarnings("unused")
        // final JavaArchive[] shorthand = MavenArchive.resolver().configureFromPom("/path/to/pom")
        // .importDefinedDependencies().as(JavaArchive.class);

        // @SuppressWarnings("unused")
        // final JavaArchive[] environment = MavenArchive.resolver().configureFromPlugin().importDefinedDependencies()
        // .as(JavaArchive.class);

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
     * <p/>
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
     * <p/>
     * Import test dependencies and exclude arquillian/shrinkwrap/container (SHRINKRES-30)
     */
    @Test
    public void importTestDependenciesWithArquillianExclusions() {
        // TODO
        // solution 1 = enumerate within previous use case
        // solution 2 = write a GroupExclusionFilter, note that MavenDependency has no getter for groupId!
        // solution 3 = move shrinkwrap/arquillian/container to a distinct profile, then exclude it

        /*
         * ALR Note: Karel's Solution 2 above looks like the most likely candidate; this isn't really a core feature of SWR, but
         * we go need to define an easy way for users to write group exclusions such that another level can define SW, SWR, SWD,
         * ARQ etc and exclude in one go.
         */
    }

    /**
     * Use case 12:
     * <p/>
     * Import a dependency using different classloader (SHRINKRES-26)
     */
    @Test
    public void bootstrapShrinResWithDifferentClassloader() {

        final ClassLoader myCl = new URLClassLoader(new URL[]{});
        @SuppressWarnings("unused")
        final File file = Resolvers.use(MavenResolverSystem.class, myCl).resolve("G:A:V").withoutTransitivity()
                .asSingle(File.class);
    }

    /**
     * Use case 13:
     * <p/>
     * Do the same as Maven would do
     */
    @Test
    public void mimickMaven() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("/path/to/pom")
                .importRuntimeDependencies().resolve().withTransitivity().as(File.class);

        Assert.fail("API broken here");

        // @SuppressWarnings("unused")
        // final JavaArchive[] shorthand = MavenArchive.resolver().configureFromPom("/path/to/pom")
        // .importDefinedDependencies().as(JavaArchive.class);

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
     * <p/>
     * SHRINKRES-27
     */
    @Test
    @SuppressWarnings("unused")
    public void dependencyInfo() {
        final MavenResolvedArtifact longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V").withoutTransitivity()
                .asSingle(MavenResolvedArtifact.class);

        final MavenResolvedArtifact shortcut = Maven.resolver().resolve("G:A:V").withoutTransitivity()
                .asSingle(MavenResolvedArtifact.class);
        final MavenCoordinate coordinate = shortcut.getCoordinate();
        final String groupId = coordinate.getGroupId();
        final String artifactId = coordinate.getArtifactId();
        final String version = coordinate.getVersion();
        final String resolvedVersion = shortcut.getResolvedVersion();
        final String type = coordinate.getType().toString();
        final boolean isSnapshot = shortcut.isSnapshotVersion();
        final String classifier = coordinate.getClassifier();
        final File file = shortcut.asFile();
        final File file2 = shortcut.as(File.class);
        final InputStream in = shortcut.as(InputStream.class);
        final InputStream in2 = shortcut.as(InputStream.class);
        final JavaArchive archive = shortcut.as(JavaArchive.class);
    }

    /**
     * Use case 15:
     * <p/>
     * Resolve offline SHRINKRES-45
     */
    @Test
    public void offline() {
        Maven.resolver().offline().resolve("groupId:artifactId:version").withoutTransitivity().asSingle(File.class);
    }

    /**
     * Use case 16: Clear configuration. Settings = "settings.xml". Load from POM: "pom.xml"
     * <p/>
     * SHRINKRES-60 SHRINKRES-51
     */
    public void configure() {
        Resolvers.configure(ConfigurableMavenResolverSystem.class).fromFile(new File("somepath")).resolve("GAV")
                .withoutTransitivity().as(File.class);
        Resolvers.use(ConfigurableMavenResolverSystem.class).configureViaPlugin();
        Maven.configureResolver().fromFile("~/.m2/settings.xml").resolve("GAV").withoutTransitivity().as(File.class);
        Maven.configureResolver().fromClassloaderResource("settings.xml").resolve("GAV").withoutTransitivity().as(File.class);
        Maven.configureResolver().fromClassloaderResource("settings.xml").loadPomFromFile((File) null).resolve("GA")
                .withoutTransitivity().as(File.class);
        @SuppressWarnings("unused")
        final JavaArchive archive = Maven.configureResolver().fromClassloaderResource("settings.xml").resolve("GAV")
                .withoutTransitivity().asSingle(JavaArchive.class);
        Maven.configureResolverViaPlugin().resolve("GA").withoutTransitivity().asSingle(File.class);

    }
}
