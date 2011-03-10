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
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests resolution to files
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MiscUnitTestCase
{
   @BeforeClass
   public static void setRemoteRepository()
   {
      System.setProperty(MavenRepositorySettings.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
      System.setProperty(MavenRepositorySettings.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
   }

   /**
    * Tests resolution of dependencies for a POM file with parent on local file
    * system
    * 
    * @throws ResolutionException
    */
   @Test
   public void testFilesResolution() throws ResolutionException
   {
      String name = "customDependencies";

      File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
                        .artifact("org.jboss.shrinkwrap.test:test-deps-a:1.0.0")
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                        .resolveAsFiles();

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"));
      desc.validateFiles(files).results();
   }
}
