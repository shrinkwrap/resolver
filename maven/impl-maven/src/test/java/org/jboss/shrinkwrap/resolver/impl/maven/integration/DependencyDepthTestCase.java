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
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.DefaultTransitiveExclusionPolicy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveExclusionPolicy;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to ensure path to the dependency is considered in filters
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public class DependencyDepthTestCase {

    @BeforeClass
    public static void setRemoteRepository() {
        System
                .setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    // -------------------------------------------------------------------------------------||
    // Tests -------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @Test
    public void resolutionDepth0Pom() {
        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-filter:pom:1.0.0")
                .using(new DepthStrategy(0)).asFile();

        Assert.assertEquals("No dependencies wer resolved for pom.xml", 0, files.length);
    }

    @Test
    public void resolutionDepth0Jar() {
        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .using(new DepthStrategy(0)).asFile();

        new ValidationUtil("test-deps-c").validate(files);
    }

    @Test
    public void resolutionDepth1Pom() {
        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-filter:pom:1.0.0")
                .using(new DepthStrategy(1)).asFile();

        new ValidationUtil("test-deps-a", "test-deps-c", "test-deps-d", "test-deps-e").validate(files);
    }

    @Test
    public void resolutionDepth1Jar() {
        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .using(new DepthStrategy(1)).asFile();

        new ValidationUtil("test-deps-b", "test-deps-c").validate(files);
    }

    @Test
    public void resolutionDepth2Pom() {
        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-filter:pom:1.0.0")
                .using(new DepthStrategy(2)).asFile();

        new ValidationUtil("test-deps-a", "test-deps-b", "test-deps-c", "test-deps-d", "test-deps-e").validate(files);
    }

    private static class DepthStrategy implements MavenResolutionStrategy {

        private int depth;

        DepthStrategy(int depth) {
            this.depth = depth;
        }

        @Override
        public TransitiveExclusionPolicy getTransitiveExclusionPolicy() {
            return DefaultTransitiveExclusionPolicy.INSTANCE;
        }

        @Override
        public MavenResolutionFilter[] getResolutionFilters() {
            return new MavenResolutionFilter[] { new DepthFilter(depth) };
        }

    }

    // allows to filter by the length of the path from ancestor
    private static class DepthFilter implements MavenResolutionFilter {

        private final int depth;

        DepthFilter(int depth) {
            this.depth = depth;
        }

        @Override
        public boolean accepts(MavenDependency dependency, List<MavenDependency> dependenciesForResolution,
                List<MavenDependency> dependencyAncestors) {
            return (dependencyAncestors == null || dependencyAncestors.size() <= depth);
        }
    }
}
