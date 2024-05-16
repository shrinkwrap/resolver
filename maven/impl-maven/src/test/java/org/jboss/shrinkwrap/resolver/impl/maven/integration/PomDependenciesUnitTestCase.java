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
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class PomDependenciesUnitTestCase {

    private static final String REMOTE_ENABLED_SETTINGS = "target/settings/profiles/settings.xml";

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    /**
     * Tests loading of a POM file with parent not available on local file system
     *
     */
    @Test
    public void parentPomRepositories() {

        File[] files = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("target/poms/test-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-child:1.0.0").withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child.tree"),
                ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);
    }

    /**
     * Tests loading of a POM file with parent not available on local file system
     *
     */
    @Test
    public void shortcutParentPomRepositories() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-child:1.0.0").withoutTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child-shortcut.tree"),
                ScopeType.COMPILE).validate(files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     */
    @Test
    public void parentPomRemoteRepositories() {

        File[] files = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("target/poms/test-remote-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree"))
            .validate(files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     */
    @Test
    public void shortcutParentPomRemoteRepositories() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-shortcut.tree"))
                .validate(files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     */
    @Test
    public void artifactVersionRetrievalFromPom() {
        File[] files = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("target/poms/test-remote-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c").withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
                files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     */
    @Test
    public void shortcutArtifactVersionRetrievalFromPom() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c").withoutTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-shortcut.tree"))
                .validate(files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     */
    @Test
    public void shortcutArtifactVersionRetrievalFromPomAsFile() {
        File file = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c").withoutTransitivity().asSingle(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-shortcut.tree"))
                .validate(file);
    }

    /**
     * Tests loading of a POM file with parent available on local file system. However, the artifact version is not used
     * from there, but specified manually
     *
     */
    @Test
    public void artifactVersionRetrievalFromPomOverride() {

        final MavenDependency dependency = MavenDependencies.createDependency(
                "org.jboss.shrinkwrap.test:test-deps-c:2.0.0", null, false);
        File[] files = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("target/poms/test-remote-child.xml")
                .addDependency(dependency).resolve().withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-2.tree"))
            .validate(files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system. However, the artifact version is not used
     * from there, but specified manually
     *
     */
    @Test
    public void shortcutArtifactVersionRetrievalFromPomOverride() {

        final MavenDependency dependency = MavenDependencies.createDependency(
                "org.jboss.shrinkwrap.test:test-deps-c:2.0.0", null, false);
        File file = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml").addDependency(dependency)
                .resolve().withoutTransitivity().asSingle(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-2-shortcut.tree"))
                .validate(file);
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     *
     */
    @Test
    public void pomBasedDependencies() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-child.xml")
                .importCompileAndRuntimeDependencies()
                .resolve().withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child.tree"), false,
                ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);

    }

    /**
     * Tests resolution of runtime only dependencies for a POM file with parent on local file system,
     * via test-scoped dep on a depchain POM
     * <p>
     * See: <a href="https://issues.redhat.com/browse/SHRINKRES-123">SHRINKRES-123</a>
     */
    @Test(expected=IllegalArgumentException.class)
    public void pomBasedDependenciesImportScopeInDepMgmtRuntimeOnly() {

        // this will throw IllegalArgument exception as there are no runtime dependencies
        final File[] files = Maven.resolver().loadPomFromFile("target/poms/test-testdeps-via-bom-and-depchain.xml")
            .importRuntimeDependencies().resolve().withTransitivity().as(File.class);

        Assert.assertEquals("No dependencies should be returned", 0, files.length);
    }

    /**
     * Tests resolution of runtime and test dependencies for a POM file with parent on local file system,
     * via test-scoped dep on a depchain POM
     * <p>
     * See: <a href="https://issues.redhat.com/browse/SHRINKRES-123">SHRINKRES-123</a>
     */
    @Test
    public void pomBasedDependenciesImportScopeInDepMgmtAllScopes() {

        final File[] files = Maven.resolver().loadPomFromFile("target/poms/test-testdeps-via-bom-and-depchain.xml")
                .importRuntimeAndTestDependencies().resolve().withTransitivity().as(File.class);

        new ValidationUtil("test-dependency", "test-deps-a", "test-deps-b").validate(files);
    }

    /**
     * Tests resolution of dependencies for a POM file without parent on local file system
     *
     */
    @Test
    public void pomRemoteBasedDependencies() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
                .importCompileAndRuntimeDependencies()
                .resolve().withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-remote-child.tree"),
                false, ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);
    }

    /**
     * Tests resolving a version from pom.xml which has non-default scope.
     * See: <a href="https://issues.redhat.com/browse/SHRINKRES-103">SHRINKRES-103</a>
     *
     */
    @Test
    public void scopedArtifactVersionRetrievalFromPom() {

        File[] files = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .loadPomFromFile("target/poms/test-dependency-test-scope.xml")
                .resolve("org.jboss.shrinkwrap.test:test-managed-dependency").withoutTransitivity().as(File.class);

        new ValidationUtil("test-managed-dependency").validate(files);
    }

    @Test
    public void importRuntimeDependencies() {
        File[] files = Maven.configureResolver().fromFile(REMOTE_ENABLED_SETTINGS)
                .loadPomFromFile("target/poms/test-dependency-scopes.xml")
                .importRuntimeDependencies().resolve().withTransitivity().as(File.class);

        // test-deps-g is a runtime dependency of test-dependency-scopes. test-deps-h is a compile dependency of
        // test-deps-g, which makes it also a runtime dependency of test-dependency-scopes, see the table at
        // https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope
        new ValidationUtil("test-deps-g", "test-deps-h").validate(files);
    }

    @Test
    public void importTestOnlyDependencies() {
        File[] files = Maven.configureResolver().fromFile(REMOTE_ENABLED_SETTINGS)
                .loadPomFromFile("target/poms/test-dependency-scopes.xml")
                .importTestDependencies().resolve().withTransitivity().as(File.class);

        new ValidationUtil("test-managed-dependency", "test-deps-b", "test-deps-k", "test-deps-l").validate(files);
    }

    /**
     * Tests whether the POM is filtered if multiple dependencies should be resolved using the Non-transitivity strategy.
     */
    @Test
    public void testPomResolutionInMultipleDependencies() {
        File[] file = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:pom:1.0.0", "org.jboss.shrinkwrap.test:test-deps-a:pom:1.0.0").withoutTransitivity().asFile();

        new ValidationUtil("test-deps-c-1.0.0.pom", "test-deps-a-1.0.0.pom").validate(file);
    }

    /**
     * Tests whether the POM is filtered if a single dependency should be resolved using the Non-transitivity strategy.
     */
    @Test
    public void testPomResolutionInSingleDependency() {
        File[] file = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:pom:1.0.0").withoutTransitivity().asFile();

        new ValidationUtil("test-deps-c-1.0.0.pom").validate(file);
    }

    /**
     * Tests whether the POM is filtered out using the Transitivity strategy.
     */
    @Test (expected = AssertionError.class)
    public void testPomResolutionWithTransitivity() {
        File[] file = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:pom:1.0.0").withTransitivity().asFile();

        new ValidationUtil("test-deps-c-1.0.0.pom").validate(file);
    }

}
