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
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ArtifactDependenciesTestCase {

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    public void cleanup() throws Exception {
        FileUtil.removeDirectory(new File("target/profile-repository"));
    }

    @Test
    public void pomBasedArtifact() {
        File[] files = Resolvers.use(MavenResolverSystem.class)
            .configureSettings("target/settings/profiles/settings.xml")
            .resolve("org.jboss.shrinkwrap.test:test-parent:pom:1.0.0").withTransitivity().as(File.class);
        System.out.println(files.length);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-parent.tree"),
            ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);
    }

    @Test
    public void pomBasedArtifactWithFileQualifier() {

        File[] files = Resolvers.use(MavenResolverSystem.class)
            .configureSettings("file:target/settings/profiles/settings.xml")
            .resolve("org.jboss.shrinkwrap.test:test-parent:pom:1.0.0").withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-parent.tree"),
            ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);
    }

    @Test
    public void pomBasedArtifactLocatedInClassPath() {

        File[] files = Resolvers.use(MavenResolverSystem.class).configureSettings("classpath:profiles/settings3.xml")
            .configureFromPom("classpath:poms/test-parent.xml").importRuntimeDependencies().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-parent.tree"),
            ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);

    }

    // this test won't run on IDE since it uses a surefire configuration
    @Test
    public void pomBasedArtifactLocatedInsideJar() {

        File[] files = Resolvers.use(MavenResolverSystem.class)
            .configureSettings("classpath:org/jboss/shrinkwrap/profiles/settings3.xml")
            .configureFromPom("classpath:org/jboss/shrinkwrap/poms/test-parent.xml").importRuntimeDependencies()
            .as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-parent.tree"),
            ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);

    }

}
