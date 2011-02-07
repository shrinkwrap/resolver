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
package org.jboss.shrinkwrap.dependencies;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;
import org.jboss.shrinkwrap.dependencies.impl.MavenRepositorySettings;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to ensure Dependencies resolves dependencies correctly
 * 
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 * @version $Revision: $
 */
public class DependenciesUnitTestCase
{
   @BeforeClass
   public static void setRemoteRepository() {
      System.setProperty(MavenRepositorySettings.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
      System.setProperty(MavenRepositorySettings.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
   }
   
   
   // -------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Tests that artifact is cannot be packaged, but is is resolved right.
    * This test is not executed but shows that some jars cannot be packaged
    */
   //@Test(expected = org.jboss.shrinkwrap.api.importer.ArchiveImportException.class)
   //@Ignore
   public void testSimpleResolutionWrongArtifact() throws DependencyException
   {
      String name = "simpleResolutionWrongArtifact";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(Dependencies.artifact("org.apache.maven.plugins:maven-compiler-plugin:2.3.2").resolve());

      // it will fail here
      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests a resolution of an artifact from central
    * @throws DependencyException
    */
   @Test
   public void testSimpleResolution() throws DependencyException
   {
      String name = "simpleResolution";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(Dependencies.artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                                      .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/test-deps-c.tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests a resolution of an artifact from central with custom settings
    * @throws DependencyException
    */
   @Test
   public void testSimpleResolutionWithCustomSettings() throws DependencyException
   {
      String name = "simpleResolutionWithCustomSettings";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(Dependencies.use(MavenDependencies.class)
                                      .configureFrom("target/settings/profiles/settings.xml")
                                      .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                                      .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/test-deps-c.tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }

   /**
    * Tests passing invalid path to a settings XML
    * @throws DependencyException
    */
   @Test(expected = IllegalArgumentException.class)
   public void testInvalidSettingsPath() throws DependencyException
   {

      // this should fail
      ShrinkWrap.create(WebArchive.class, "testSimpleResolutionWithCustomSettings.war")
            .addAsLibraries(Dependencies.use(MavenDependencies.class)
                                      .configureFrom("src/test/invalid/custom-settings.xml")
                                      .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                                      .resolve());

   }

   /**
    * Tests a resolution of two artifacts from central
    * @throws DependencyException
    */
   @Test
   public void testMultipleResolution() throws DependencyException
   {
      String name = "multipleResolution";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(Dependencies.artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                                      .artifact("org.jboss.shrinkwrap.test:test-deps-g:1.0.0")
                                      .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/test-deps-c+g.tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }

   /**
    * Tests a resolution of two artifacts from central using single call
    * @throws DependencyException
    */
   @Test
   public void testMultipleResolutionSingleCall() throws DependencyException
   {
      String name = "multipleResolutionSingleCall";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(Dependencies.artifacts("org.jboss.shrinkwrap.test:test-deps-c:1.0.0",
                                                 "org.jboss.shrinkwrap.test:test-deps-g:1.0.0")
                                      .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/test-deps-c+g.tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }

   /**
    * Tests direct usage of MavenDependencies implementation
    * @throws DependencyException
    */
   @Test
   public void testCustomDependencies() throws DependencyException
   {
      String name = "customDependencies";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(Dependencies.use(MavenDependencies.class)
                                      .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                                      .artifact("org.jboss.shrinkwrap.test:test-deps-g:1.0.0")
                                      .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/test-deps-c+g.tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }
}
