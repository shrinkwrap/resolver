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

import java.util.Collection;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;

/**
 * ClasspathRepositoryManagerTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ClasspathWorkspaceReaderTestCase
{
   @Test
   public void shouldBeAbleToLoadArtifactDirectlyFromClassPath() {
      // TODO: Assert something..
      Collection<JavaArchive> files = DependencyResolvers.use(MavenDependencyResolver.class)
         .loadEffectivePom("pom.xml").up()
         .artifact("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api")
         .resolveAs(JavaArchive.class);

      for(JavaArchive file : files)
      {
         System.out.println(file.getName());
      }
   }
}
