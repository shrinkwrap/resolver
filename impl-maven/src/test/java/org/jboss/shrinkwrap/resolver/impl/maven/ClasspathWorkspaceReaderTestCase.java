/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
import java.util.Collection;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.util.FileUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests that resolution of archives for the classpath works
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ClasspathWorkspaceReaderTestCase {

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/non-existing-repository");
    }

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    public void cleanup() throws Exception {
        FileUtil.removeDirectory(new File("target/non-existing-repository"));
    }

    @Test(expected = ResolutionException.class)
    public void shouldFailWhileNotReadingReactor() {

        EffectivePomMavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadEffectivePom("pom.xml");

        // disable reactor
        ((MavenEnvironmentRetrieval) resolver).getMavenEnvironment().disableReactor();

        Collection<JavaArchive> files = resolver.artifact("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api")
            .resolveAs(JavaArchive.class);

        for (JavaArchive file : files) {
            System.out.println(file.getName());
        }
    }

    @Test
    public void shouldBeAbleToLoadArtifactDirectlyFromClassPath() {

        EffectivePomMavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadEffectivePom("pom.xml");

        Collection<JavaArchive> archives = resolver.artifact("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api")
            .resolveAs(JavaArchive.class);

        new ValidationUtil("shrinkwrap-resolver-api", "shrinkwrap-api").validate(archives);
    }

    @Test
    public void shouldBeAbleToLoadArtifactDirectlyFromClassPathAsFiles() {

        EffectivePomMavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadEffectivePom("pom.xml");

        File[] files = resolver.artifact("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api").resolveAsFiles();

        new ValidationUtil("shrinkwrap-resolver-api", "shrinkwrap-api").validate(files);
    }

}
