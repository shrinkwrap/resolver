package org.jboss.shrinkwrap.resolver.maven.impl;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * A mapping for artifact resolved from POM file to a Map key. It is used to
 * retrieve a version
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
class ArtifactAsKey
{
   private final String groupId;
   private final String artifactId;
   private final String type;
   private final String classifier;

   /**
    * Creates an artifact key
    * 
    * @param groupId Group id
    * @param artifactId Artifact id
    * @param type Type, if empty or {@code null}, it is set to an empty string
    *           {@code jar}
    * @param classifier Classifier, if {@code null}, it is set to an empty
    *           string
    */
   public ArtifactAsKey(String groupId, String artifactId, String type, String classifier)
   {
      this.groupId = groupId;
      this.artifactId = artifactId;
      this.type = (type == null || type.length() == 0) ? "jar" : type;
      this.classifier = (classifier == null || classifier.length() == 0) ? "" : classifier;
   }

   public ArtifactAsKey(String coordinates) 
   {
      this(new DefaultArtifact(coordinates));
   }

   public ArtifactAsKey(org.apache.maven.model.Dependency d)
   {
      this(d.getGroupId(), d.getArtifactId(), d.getType(), d.getClassifier());
   }

   public ArtifactAsKey(Artifact a)
   {
      this(a.getGroupId(), a.getArtifactId(), a.getExtension(), a.getClassifier());
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "ArtifactAsKey [artifactId=" + artifactId + ", classifier=" + classifier + ", groupId=" + groupId + ", type=" + type + "]";
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
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      ArtifactAsKey other = (ArtifactAsKey) obj;
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
      if (groupId == null)
      {
         if (other.groupId != null)
            return false;
      }
      else if (!groupId.equals(other.groupId))
         return false;
      if (type == null)
      {
         if (other.type != null)
            return false;
      }
      else if (!type.equals(other.type))
         return false;
      return true;
   }

}