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

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.AcceptScopesStrategy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ExclusionsUnitTestCase {

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    /**
     * Tests exclusion of the artifacts
     *
     *
     */
    @Test
    public void exclusion() {

        File[] files = Resolvers.use(MavenResolverSystem.class).configureFromPom("target/poms/test-parent.xml")
            .addDependency("org.jboss.shrinkwrap.test:test-dependency-test:jar:1.0.0").scope(ScopeType.TEST)
            .addExclusions("org.jboss.shrinkwrap.test:test-deps-f").resolve()
            .using(new AcceptScopesStrategy(ScopeType.TEST)).as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/exclusion.tree"),
            ScopeType.TEST).validate(files);
    }

    /**
     * Tests exclusion of the artifacts
     *
     *
     */
    @Test
    public void exclusions() {
        File[] files = Resolvers.use(MavenResolverSystem.class).configureFromPom("target/poms/test-parent.xml")
            .addDependency("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0").scope(ScopeType.TEST)
            .addExclusions("org.jboss.shrinkwrap.test:test-deps-f", "org.jboss.shrinkwrap.test:test-deps-g").resolve()
            .using(new AcceptScopesStrategy(ScopeType.TEST)).as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/exclusions.tree"),
            ScopeType.TEST).validate(files);
    }

    /**
     * Tests exclusion of all transitive artifacts
     */
    @Test
    public void universalExclusion() {

        File file = Resolvers.use(MavenResolverSystem.class).configureFromPom("target/poms/test-parent.xml")
            .addDependency("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0").scope(ScopeType.TEST)
            .addExclusion("*:*").resolve().using(new AcceptScopesStrategy(ScopeType.TEST)).asSingle(File.class);

        Assert.assertEquals("The file is packaged as test-dependency-test-1.0.0.jar", "test-dependency-test-1.0.0.jar",
            file.getName());
    }
}
