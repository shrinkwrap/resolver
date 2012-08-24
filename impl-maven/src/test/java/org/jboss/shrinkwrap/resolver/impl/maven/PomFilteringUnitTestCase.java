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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ExclusionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ExclusionsFilter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class PomFilteringUnitTestCase {
    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    @Test
    public void testIncludeFromPomWithExclusionFilter() {
        File[] jars = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadEffectivePom("target/poms/test-filter.xml")
            .importAnyDependencies(new ExclusionFilter("org.jboss.shrinkwrap.test:test-deps-c")).resolveAsFiles();

        Assert.assertEquals("Exactly 3 files were resolved", 3, jars.length);
        new ValidationUtil("test-deps-a", "test-deps-d", "test-deps-e").validate(jars);

    }

    @Test
    public void testIncludeFromPomWithExclusionsFilter() {

        File[] jars = DependencyResolvers
            .use(MavenDependencyResolver.class)
            .loadEffectivePom("target/poms/test-filter.xml")
            .importAnyDependencies(
            // this is applied before resolution, e.g. has no information about transitive dependencies
            // it means:
            // 1. it excludes whole tree of the exclusion
            // 2. it does not affect transitive dependencies of other elements
                new ExclusionsFilter("org.jboss.shrinkwrap.test:test-deps-a", "org.jboss.shrinkwrap.test:test-deps-c",
                    "org.jboss.shrinkwrap.test:test-deps-d")).resolveAsFiles();

        Assert.assertEquals("Exactly 1 file was resolved", 1, jars.length);
        new ValidationUtil("test-deps-e").validate(jars);
    }

}
