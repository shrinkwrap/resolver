/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package test;

import java.util.Set;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.filter.DependenciesFilter;
import org.junit.Test;

public class EarTestCase {

    @Test
    public void testEar() {
        EnterpriseArchive archive = ShrinkWrap.create(MavenBuilder.class, "test.ear")
            .loadEffectivePom("../ear-sample/pom.xml").importBuildOutput().as(EnterpriseArchive.class);

        Assert.assertNotNull("Archive is not null", archive);
        Assert.assertTrue("Archive contains test.xml", archive.contains("test.xml"));
        Assert.assertTrue("Archive contains application.xml", archive.contains("META-INF/application.xml"));

        Assert.assertEquals("Archive contains one library", 1, getLibraries(archive).size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEarWithTestClasses() {
        ShrinkWrap.create(MavenBuilder.class, "testWithTestClasses.ear").loadEffectivePom("../ear-sample/pom.xml")
            .importBuildOutput().importTestBuildOutput().as(EnterpriseArchive.class);

        Assert.fail("EAR test build import is not supported");
    }

    @Test
    public void testEnterpriseArchiveAsMavenBuilder() {
        EnterpriseArchive archive = ShrinkWrap
            .create(EnterpriseArchive.class, "testEnterpriseArchiveAsMavenBuilder.ear").as(MavenBuilder.class)
            .loadEffectivePom("../ear-sample/pom.xml").importBuildOutput().as(EnterpriseArchive.class);

        Assert.assertNotNull("Archive is not null", archive);
        Assert.assertTrue("Archive contains test.xml", archive.contains("test.xml"));
        Assert.assertTrue("Archive contains application.xml", archive.contains("META-INF/application.xml"));

        Assert.assertEquals("Archive contains one library", 1, getLibraries(archive).size());
    }

    @Test
    public void testEarWithTestArtifacts() {
        EnterpriseArchive archive = ShrinkWrap.create(MavenBuilder.class, "testWithTestArtifacts.ear")
            .loadEffectivePom("../ear-sample/pom.xml").importBuildOutput()
            .importTestDependencies(new DependenciesFilter("junit:junit")).as(EnterpriseArchive.class);
        System.out.println(archive.toString(true));

        Assert.assertNotNull("Archive is not null", archive);
        Assert.assertTrue("Archive contains test.xml", archive.contains("test.xml"));
        Assert.assertTrue("Archive contains application.xml", archive.contains("META-INF/application.xml"));

        Assert.assertTrue("Archive contains more than one library", 1 < getLibraries(archive).size());
    }

    private Set<ArchivePath> getLibraries(Archive<?> archive) {
        return archive.getContent(new Filter<ArchivePath>() {

            public boolean include(ArchivePath arg0) {
                String path = arg0.get();
                return path.endsWith(".jar");
            }
        }).keySet();
    }

}
