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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.w3c.dom.Document;

/**
 * ClasspathWorkspaceReader
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ClasspathWorkspaceReader implements WorkspaceReader
{
   private String[] classPathEntries;

   public ClasspathWorkspaceReader()
   {
      this.classPathEntries = System.getProperty("java.class.path").split(""+File.pathSeparatorChar);
   }

   @Override
   public WorkspaceRepository getRepository()
   {
      return new WorkspaceRepository("classpath");
   }

   @Override
   public File findArtifact(Artifact artifact)
   {
      for(String classpathEntry : classPathEntries)
      {
         File directory = new File(classpathEntry);
         if(directory.isDirectory())
         {
            File pomFile = new File(directory.getParentFile().getParentFile(), "pom.xml");
            if(pomFile.isFile())
            {
               try
               {
                  // TODO: load pom using Maven Model?
                  Document pom = loadPom(pomFile);

                  XPathFactory factory = XPathFactory.newInstance();
                  XPath xpath = factory.newXPath();

                  String groupId = xpath.evaluate("/project/groupId", pom);
                  String artifactId = xpath.evaluate("/project/artifactId", pom);
                  String type = xpath.evaluate("/project/packaging", pom);
                  String version = xpath.evaluate("/project/version", pom);

                  if(groupId == null || groupId.equals("")) {
                     groupId = xpath.evaluate("/project/parent/groupId", pom);
                  }
                  if(type == null || type.equals("")) {
                     type = "jar";
                  }
                  if(version == null || version.equals("")) {
                     version = xpath.evaluate("/project/parent/version", pom);
                  }

                  // TODO: cache parsed artifacts to avoid re-parsing..
                  Artifact foundArtifact = new DefaultArtifact(groupId + ":" + artifactId + ":" + type + ":" + version);
                  foundArtifact.setFile(pomFile);

                  if(foundArtifact.getGroupId().equals(artifact.getGroupId()) && foundArtifact.getArtifactId().equals(artifact.getArtifactId()))
                  {
                     return pomFile;
                  }
               }
               catch (Exception e)
               {
                  throw new RuntimeException("Could not parse pom.xml: " + pomFile, e);
               }
            }
         }
      }
      return null;
   }

   private Document loadPom(File pom) throws Exception
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.parse(pom);
   }

   @Override
   public List<String> findVersions(Artifact artifact)
   {
      return new ArrayList<String>();
   }
}
