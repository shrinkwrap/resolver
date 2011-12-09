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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests resolution from a pom file using &lt;dependencyManagement&gt; to get information about transitive dependencies
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class PomTransitivesUnitTestCase {
    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);

    }

    /**
     * Gets transitive dependency of test-deps-b overridden by &lt;dependencyManagement&gt;
     */
    @Test
    public void testIncludeFromPomWithDependencyManagement() {
        File[] jars = DependencyResolvers.use(MavenDependencyResolver.class).useCentralRepo(false)
                .loadEffectivePom("target/poms/test-depmngmt-transitive.xml").importAllDependencies().resolveAsFiles();

        Assert.assertEquals("Exactly 2 files were resolved", 2, jars.length);
        new ValidationUtil("test-deps-b-2.0.0", "test-deps-c-1.0.0").validate(jars);

    }

}
