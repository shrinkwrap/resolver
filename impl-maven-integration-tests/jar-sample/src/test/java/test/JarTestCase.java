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
package test;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.junit.Test;

/**
 * Test cases for MavenImporter with Jar
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
public class JarTestCase {

    @Test
    public void testJar() {
        JavaArchive archive = ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput()
                .as(JavaArchive.class);

        Assert.assertNotNull("Archive is not null", archive);
        Assert.assertTrue("Archive contains jar class", archive.contains("test/JarClass.class"));
        Assert.assertTrue("Archive contains main.properties", archive.contains("main.properties"));
    }

    @Test
    public void testJarWithTestClasses() {
        JavaArchive archive = ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput()
                .importTestBuildOutput().as(JavaArchive.class);

        Assert.assertNotNull("Archive is not null", archive);
        Assert.assertTrue("Archive contains jar class", archive.contains("test/JarClass.class"));
        Assert.assertTrue("Archive contains main.properties", archive.contains("main.properties"));
        Assert.assertTrue("Archive contains jar test class", archive.contains("test/JarTestCase.class"));
        Assert.assertTrue("Archive contains test.properties", archive.contains("test.properties"));
    }

    @Test
    public void testJavaArchiveAsMavenImporter() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class).addClass(Object.class).as(MavenImporter.class)
                .loadEffectivePom("pom.xml").importBuildOutput().importTestBuildOutput().as(JavaArchive.class);

        Assert.assertNotNull("Archive is not null", archive);
        Assert.assertTrue("Archive contains manually added class", archive.contains("java/lang/Object.class"));
        Assert.assertTrue("Archive contains jar class", archive.contains("test/JarClass.class"));
        Assert.assertTrue("Archive contains main.properties", archive.contains("main.properties"));
        Assert.assertTrue("Archive contains jar test class", archive.contains("test/JarTestCase.class"));
        Assert.assertTrue("Archive contains test.properties", archive.contains("test.properties"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testJarWithTestArtifacts() {
        ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput().importTestBuildOutput()
                .importTestDependencies().as(JavaArchive.class);

        Assert.fail("UnsupportedOperationException should have been thrown for jar packaging");
    }
}
