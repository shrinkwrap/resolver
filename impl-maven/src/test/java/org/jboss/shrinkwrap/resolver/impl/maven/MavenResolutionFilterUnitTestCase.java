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
import java.util.Map;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenRepositorySettings;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.CombinedFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.ScopeFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.StrictFilter;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenResolutionFilterUnitTestCase
{
   @BeforeClass
   public static void setRemoteRepository()
   {
      System.setProperty(MavenRepositorySettings.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
   }

   /**
    * Tests that only directly defined artifacts are added to dependencies
    * 
    * @throws ResolutionException
    */
   @Test
   public void testStrictFilter() throws ResolutionException
   {
      String name = "strictFilter";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(MavenResolver
                           .loadPom("target/poms/test-child.xml")
                           .artifact("org.jboss.shrinkwrap.test:test-child:1.0.0")
                           .resolveAs(GenericArchive.class,new StrictFilter()));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is only one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged as test-child:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-child-1.0.0.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }

   /**
    * Tests that only directly defined artifacts are added to dependencies, the
    * artifact version is taken from a POM file
    * 
    * @throws ResolutionException
    */
   @Test
   public void testStrictFilterInferredVersion() throws ResolutionException
   {
      String name = "strictFilterInferredVersion";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(MavenResolver
                  .loadPom("target/poms/test-remote-child.xml")
                  .artifact("org.jboss.shrinkwrap.test:test-deps-c")
                  .resolveAs(GenericArchive.class,new StrictFilter()));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is only one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged as test-deps-c:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-deps-c-1.0.0.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }

   /**
    * Tests loading of a POM file with parent not available on local file system
    * 
    * @throws ResolutionException
    */
   @Test
   public void testDefaultScopeFilter() throws ResolutionException
   {
      String name = "defaultScopeFilter";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            MavenResolver.loadPom("target/poms/test-remote-child.xml")
                  .artifact("org.jboss.shrinkwrap.test:test-remote-child:1.0.0")
                  .resolveAs(GenericArchive.class, new ScopeFilter()));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged as test-remote-child:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-remote-child-1.0.0.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests limiting of the scope
    * 
    * @throws ResolutionException
    */
   @Test
   public void testRuntimeScopeFilter() throws ResolutionException
   {
      String name = "runtimeScopeFilter";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(MavenResolver
                           .loadPom("target/poms/test-parent.xml")
                           .artifact("org.jboss.shrinkwrap.test:test-dependency:1.0.0")
                           .resolveAs(GenericArchive.class, new ScopeFilter("runtime")));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged as test-deps-b:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-deps-b-1.0.0.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests limiting of the scope and strict artifacts
    * 
    * @throws ResolutionException
    */
   @Test
   public void testCombinedScopeFilter() throws ResolutionException
   {
      String name = "testCombinedScopeFilter";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(MavenResolver
                           .loadPom("target/poms/test-parent.xml")
                           .artifact("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0")
                           .scope("test")
                           .artifact("org.jboss.shrinkwrap.test:test-dependency:1.0.0")
                           .resolveAs(GenericArchive.class,new CombinedFilter(new ScopeFilter("", "test"), new StrictFilter())));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There are two jars in the package", 2, map.size());
      Assert.assertTrue("The artifact is packaged as org.jboss.shrinkwrapt.test:test-dependency-test:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-dependency-test-1.0.0.jar")));
      Assert.assertTrue("The artifact is packaged as org.jboss.shrinkwrapt.test:test-dependency:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-dependency-1.0.0.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests limiting of the scope and strict artifacts. Uses artifacts() method
    * 
    * @throws ResolutionException
    */
   @Test
   public void testCombinedScopeFilter2() throws ResolutionException
   {
      String name = "testCombinedScopeFilter2";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(MavenResolver
                           .loadPom("target/poms/test-parent.xml")
                           .artifacts("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0", "org.jboss.shrinkwrap.test:test-dependency:1.0.0")
                           .scope("test")
                           .resolveAs(GenericArchive.class,new CombinedFilter(new ScopeFilter("test"), new StrictFilter())));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There are two jars in the package", 2, map.size());
      Assert.assertTrue("The artifact is packaged as org.jboss.shrinkwrapt.test:test-dependency-test:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-dependency-test-1.0.0.jar")));
      Assert.assertTrue("The artifact is packaged as org.jboss.shrinkwrapt.test:test-dependency:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-dependency-1.0.0.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests limiting of the scope and strict artifacts
    * 
    * @throws ResolutionException
    */
   @Test
   public void testCombinedScopeFilter3() throws ResolutionException
   {
      String name = "testCombinedScopeFilter3";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(MavenResolver
                           .loadPom("target/poms/test-parent.xml")
                           .artifact("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0")
                           .scope("test")
                           .artifact("org.jboss.shrinkwrap.test:test-dependency:1.0.0")
                           .scope("provided")
                           .resolveAs(GenericArchive.class,new CombinedFilter(new ScopeFilter("provided"), new StrictFilter())));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged as org.jboss.shrinkwrap.test:test-dependency:1.0.0",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/test-dependency-1.0.0.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests resolution of dependencies for a POM file with parent on local file
    * system
    * 
    * @throws ResolutionException
    */
   @Test
   public void testPomBasedDependenciesWithScope() throws ResolutionException
   {
      String name = "pomBasedDependenciesWithScope";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addAsLibraries(MavenResolver
                           .resolveFrom("target/poms/test-child.xml", new ScopeFilter("test")));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/test-child.tree"), "test");
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }

   // filter to retrieve jar files only
   private static final Filter<ArchivePath> JAR_FILTER = new Filter<ArchivePath>()
   {
      public boolean include(ArchivePath object)
         {
            if (object.get().endsWith(".jar"))
            {
               return true;
            }

            return false;
         }
   };

}
