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

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.RejectDependenciesStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.AfterClass;
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
        final File[] jars = Maven.resolver().loadPomFromFile("target/poms/test-filter.xml")
            .importRuntimeDependencies(new RejectDependenciesStrategy("org.jboss.shrinkwrap.test:test-deps-c"))
            .as(File.class);

        // We should not bring in b and c, as b is transitive from c, and we excluded c above.
        new ValidationUtil("test-deps-a", "test-deps-d", "test-deps-e").validate(jars);

    }

    @Test
    public void testIncludeFromPomWithExclusionsFilter() {

        final File jar = Maven
            .resolver()
            .loadPomFromFile("target/poms/test-filter.xml")
            .importRuntimeDependencies(
            // this is applied before resolution, e.g. has no information about transitive dependencies
            // it means:
            // 1. it excludes whole tree of the exclusion /
                new RejectDependenciesStrategy("org.jboss.shrinkwrap.test:test-deps-a",
                    "org.jboss.shrinkwrap.test:test-deps-c", "org.jboss.shrinkwrap.test:test-deps-d"))
            .asSingle(File.class);

        new ValidationUtil("test-deps-e").validate(jar);
    }

}
