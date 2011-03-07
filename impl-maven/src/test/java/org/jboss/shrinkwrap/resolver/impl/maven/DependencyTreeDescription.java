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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

/**
 * A validation utility for verifying Artifact resolution. It checks archive's content
 * against previously generated dependency tree.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class DependencyTreeDescription
{
   private static final Logger log = Logger.getLogger(DependencyTreeDescription.class.getName());

   private Map<ArtifactHolder, Boolean> artifacts;

   private Set<String> allowedScopes;

   /**
    * Creates a description from the file
    * @param file A file with dependency:tree output
    * @param scopes A list of allowed scopes
    * @throws ResolutionException
    */
   public DependencyTreeDescription(File file, String... scopes) throws ResolutionException
   {
      this.allowedScopes = new HashSet<String>(Arrays.asList(scopes));
      this.artifacts = load(file);
   }

   /**
    * Checks that files from dependency tree are present in the archive
    * @param archive The archive to be checked
    * @return The current dependency tree to allow chaining
    */
   public DependencyTreeDescription validateArchive(Archive<?> archive)
   {
      return validateArchive(archive, new Filter<ArchivePath>()
      {

         public boolean include(ArchivePath object)
         {
            return true;
         }
      });
   }

   /**
    * Checks that files from dependency tree are present in the archive
    * @param archive The archive to be checked
    * @param filter The filter for archive content which will be checked
    * @return The current dependency tree to allow chaining
    */
   public DependencyTreeDescription validateArchive(Archive<?> archive, Filter<ArchivePath> filter)
   {
      for (ArchivePath path : archive.getContent(filter).keySet())
      {
         String pathString = path.get();

         for (ArtifactHolder artifact : artifacts.keySet())
         {
            if (pathString.endsWith(artifact.filename()))
            {
               artifacts.put(artifact, Boolean.TRUE);
            }
         }
      }
      return this;
   }

   /**
    * Checks that files from dependency tree are present in directory
    * @param archive The archive to be checked
    * @param filter The filter for archive content which will be checked
    * @return The current dependency tree to allow chaining
    */
   public DependencyTreeDescription validateDirectory(File directory)
   {
      if (!directory.isDirectory())
      {
         throw new AssertionError(directory.getAbsolutePath() + " is not a directory");
      }
      
      return validateFiles(directory.listFiles());
   }
   
   public DependencyTreeDescription validateFiles(File...files)
   {     
      for (File file : files)
      {
         String pathString = file.getAbsolutePath();

         for (ArtifactHolder artifact : artifacts.keySet())
         {
            if (pathString.endsWith(artifact.filename()))
            {
               artifacts.put(artifact, Boolean.TRUE);
            }
         }
      }
      return this;
   }


   
   /**
    * Verifies that all artifacts present in dependency tree were found during validation
    * @throws AssertionError If not all artifacts were found.
    */
   public void results() throws AssertionError
   {
      boolean allFound = true;
      StringBuilder sb = new StringBuilder();
      for (Entry<ArtifactHolder, Boolean> entry : artifacts.entrySet())
      {
         boolean current = entry.getValue().booleanValue();
         if (current == false)
         {
            sb.append("Missing artifact: ").append(entry.getKey().filename())
                  .append(" in the archive!").append("\n");
            allFound = false;
         }
      }

      if (allFound != true)
      {
         throw new AssertionError(sb.toString());
      }

   }

   private Map<ArtifactHolder, Boolean> load(File file) throws ResolutionException
   {
      Map<ArtifactHolder, Boolean> artifacts = new HashMap<ArtifactHolder, Boolean>();

      try
      {
         BufferedReader input = new BufferedReader(new FileReader(file));

         String line = null;
         while ((line = input.readLine()) != null)
         {
            ArtifactHolder holder = new ArtifactHolder(line);
            if (!"jar".equals(holder.extension))
            {
               log.info("Removing non-JAR artifact " + holder.toString() + " from dependencies, it's dependencies are fetched");
            }
            else if (holder.root)
            {
               log.fine("Root of the tree (" + holder.toString() + ")should not be included in the artifact itself");
            }
            // add artifact if in allowed scope
            else if (allowedScopes.isEmpty() || (!allowedScopes.isEmpty() && allowedScopes.contains(holder.scope)))
            {
               artifacts.put(new ArtifactHolder(line), Boolean.FALSE);
            }
         }
      }
      catch (IOException e)
      {
         throw new ResolutionException("Unable to load dependency tree to verify", e);
      }

      return artifacts;
   }
}

/**
 * A holder for a line generated from Maven dependency tree plugin
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
class ArtifactHolder
{

   final String groupId;
   final String artifactId;
   final String extension;
   final String classifier;
   final String version;
   final String scope;

   final boolean root;

   /**
    * Creates an artifact holder from the input lien
    * @param dependencyCoords
    */
   ArtifactHolder(String dependencyCoords)
   {
      int index = 0;
      while (index < dependencyCoords.length())
      {
         char c = dependencyCoords.charAt(index);
         if (c == '\\' || c == '|' || c == ' ' || c == '+' || c == '-')
         {
            index++;
         }
         else
         {
            break;
         }
      }

      for (int testIndex = index, i = 0; i < 4; i++)
      {
         testIndex = dependencyCoords.substring(testIndex).indexOf(":");
         if (testIndex == -1)
         {
            throw new IllegalArgumentException("Invalid format of the dependency coordinates for " + dependencyCoords);
         }
      }

      StringTokenizer st = new StringTokenizer(dependencyCoords.substring(index), ":");

      this.groupId = st.nextToken();
      this.artifactId = st.nextToken();
      this.extension = st.nextToken();

      // this is the root artifact
      if (index == 0)
      {
         this.root = true;

         if (st.countTokens() == 1)
         {
            this.classifier = "";
            this.version = st.nextToken();
         }
         else if (st.countTokens() == 2)
         {
            this.classifier = st.nextToken();
            this.version = st.nextToken();
         }
         else
         {
            throw new IllegalArgumentException("Invalid format of the dependency coordinates for " + dependencyCoords);
         }

         this.scope = "";
      }
      // otherwise
      else
      {
         this.root = false;

         if (st.countTokens() == 2)
         {
            this.classifier = "";
            this.version = st.nextToken();
            this.scope = extractScope(st.nextToken());
         }
         else if (st.countTokens() == 3)
         {
            this.classifier = st.nextToken();
            this.version = st.nextToken();
            this.scope = extractScope(st.nextToken());
         }
         else
         {
            throw new IllegalArgumentException("Invalid format of the dependency coordinates for " + dependencyCoords);
         }
      }
   }

   public String filename()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(artifactId).append("-").append(version);
      if (classifier.length() != 0)
      {
         sb.append("-").append(classifier);
      }
      sb.append(".").append(extension);

      return sb.toString();
   }

   public String toString()
   {
      StringBuilder sb = new StringBuilder();

      sb.append("groupId=").append(groupId).append(", ");
      sb.append("artifactId=").append(artifactId).append(", ");
      sb.append("type=").append(extension).append(", ");
      sb.append("version=").append(version);

      if (scope != "")
      {
         sb.append(", scope=").append(scope);
      }

      if (classifier != "")
      {
         sb.append(", classifier=").append(classifier);
      }

      return sb.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
      result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
      result = prime * result + ((extension == null) ? 0 : extension.hashCode());
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + (root ? 1231 : 1237);
      result = prime * result + ((scope == null) ? 0 : scope.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ArtifactHolder other = (ArtifactHolder) obj;
      if (artifactId == null)
      {
         if (other.artifactId != null)
            return false;
      }
      else if (!artifactId.equals(other.artifactId))
         return false;
      if (classifier == null)
      {
         if (other.classifier != null)
            return false;
      }
      else if (!classifier.equals(other.classifier))
         return false;
      if (extension == null)
      {
         if (other.extension != null)
            return false;
      }
      else if (!extension.equals(other.extension))
         return false;
      if (groupId == null)
      {
         if (other.groupId != null)
            return false;
      }
      else if (!groupId.equals(other.groupId))
         return false;
      if (root != other.root)
         return false;
      if (scope == null)
      {
         if (other.scope != null)
            return false;
      }
      else if (!scope.equals(other.scope))
         return false;
      if (version == null)
      {
         if (other.version != null)
            return false;
      }
      else if (!version.equals(other.version))
         return false;
      return true;
   }

   private String extractScope(String scope)
   {
      int lparen = scope.indexOf("(");
      int rparen = scope.indexOf(")");
      int space = scope.indexOf(" ");

      if (lparen == -1 && rparen == -1 && space == -1)
      {
         return scope;
      }
      else if (lparen != -1 && rparen != -1 && space != -1)
      {
         return scope.substring(0, space);
      }

      throw new IllegalArgumentException("Invalid format of the dependency coordinates for artifact scope: " + scope);

   }

}
