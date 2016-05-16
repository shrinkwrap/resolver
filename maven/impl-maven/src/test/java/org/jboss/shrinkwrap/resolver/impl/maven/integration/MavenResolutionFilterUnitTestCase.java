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
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.CombinedStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.NonTransitiveStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class MavenResolutionFilterUnitTestCase {

    private static final String REMOTE_ENABLED_SETTINGS = "target/settings/profiles/settings.xml";

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    /**
     * Tests that only directly defined artifacts are added to dependencies
     *
     *
     */
    @Test
    public void nonTransitiveFilter() {

        File file = Maven.resolver().loadPomFromFile("target/poms/test-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-child:1.0.0").withoutTransitivity().asSingle(File.class);

        new ValidationUtil("test-child-1.0.0.jar").validate(file);
    }

    /**
     * Tests that only directly defined artifacts are added to dependencies, the artifact version is taken from a POM
     * file
     *
     *
     */
    @Test
    public void nonTransitiveFilterInferredVersion() {

        File file = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c").withoutTransitivity().asSingle(File.class);

        new ValidationUtil("test-deps-c-1.0.0.jar").validate(file);
    }

    /**
     * Tests limiting of the scope
     */
    @Test
    public void runtimeScopeFilter() {

        // no pom.xml file was loaded here
        File file = Maven.configureResolver().fromFile(REMOTE_ENABLED_SETTINGS)
            .resolve("org.jboss.shrinkwrap.test:test-dependency:1.0.0")
            .using(new AcceptScopesStrategy(ScopeType.RUNTIME)).asSingle(File.class);

        new ValidationUtil("test-deps-b-1.0.0.jar").validate(file);
    }

    /**
     * Tests limiting of the scope and strict artifacts
     */
    @Test
    public void combinedScopeNonTransitiveFilter() {

        final MavenDependency dependency = MavenDependencies.createDependency(
            "org.jboss.shrinkwrap.test:test-dependency-test:1.0.0", ScopeType.TEST, false);
        File[] files = Maven
            .resolver()
            .loadPomFromFile("target/poms/test-parent.xml")
            .addDependency(dependency)
            .resolve("org.jboss.shrinkwrap.test:test-dependency:1.0.0")
            .using(
                new CombinedStrategy(NonTransitiveStrategy.INSTANCE, new AcceptScopesStrategy(ScopeType.COMPILE,
                    ScopeType.TEST))).as(File.class);

        new ValidationUtil("test-dependency-test-1.0.0.jar", "test-dependency-1.0.0.jar").validate(files);

    }

    /**
     * Tests limiting of the scope and strict artifacts. Uses artifacts() method
     */
    @Test
    public void combinedScopeNonTransitiveFilter2() {

        final MavenDependency dependency = MavenDependencies.createDependency(
                "org.jboss.shrinkwrap.test:test-dependency-test:1.0.0", ScopeType.TEST, false);
        final MavenDependency dependency2 = MavenDependencies.createDependency(
                "org.jboss.shrinkwrap.test:test-dependency:1.0.0", ScopeType.TEST, false);
        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-parent.xml").addDependency(dependency)
                .addDependency(dependency2).resolve()
                .using(new CombinedStrategy(NonTransitiveStrategy.INSTANCE, new AcceptScopesStrategy(ScopeType.TEST)))
                .as(File.class);

        new ValidationUtil("test-dependency-test-1.0.0.jar", "test-dependency-1.0.0.jar").validate(files);
    }

    /**
     * Tests limiting of the scope and strict artifacts
     *
     *
     */
    @Test
    public void combinedScopeNonTransitiveFilter3() {

        final MavenDependency dependency = MavenDependencies.createDependency(
                "org.jboss.shrinkwrap.test:test-dependency-test:1.0.0", ScopeType.TEST, false);
        final MavenDependency dependency2 = MavenDependencies.createDependency(
                "org.jboss.shrinkwrap.test:test-dependency:1.0.0", ScopeType.PROVIDED, false);
        File file = Maven.resolver().loadPomFromFile("target/poms/test-parent.xml")
                .addDependencies(dependency, dependency2).resolve()
                .using(new CombinedStrategy(NonTransitiveStrategy.INSTANCE, new AcceptScopesStrategy(ScopeType.PROVIDED)))
                .asSingle(File.class);

        new ValidationUtil("test-dependency-1.0.0.jar").validate(file);
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     *
     *
     */
    @Test
    public void pomBasedDependenciesWithScope() {

        final File[] files = Maven.resolver().loadPomFromFile("target/poms/test-child.xml")
                .importTestDependencies().resolve().withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child.tree"), false,
                ScopeType.TEST).validate(files);
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     *
     *
     */
    @Test
    public void pomBasedDependenciesWithScopeAsList() {

        final List<File> files = Maven.resolver().loadPomFromFile("target/poms/test-child.xml")
                .importTestDependencies().resolve().withTransitivity().asList(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child.tree"), false,
                ScopeType.TEST).validate(files);
    }

}
