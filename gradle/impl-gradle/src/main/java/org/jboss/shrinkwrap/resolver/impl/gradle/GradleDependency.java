package org.jboss.shrinkwrap.resolver.impl.gradle;

public class GradleDependency {

   private final String groupId;
   private final String artifactId;

   private GradleDependency(String groupId, String artifactId) {
      this.groupId = groupId;
      this.artifactId = artifactId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      GradleDependency that = (GradleDependency) o;

      if (!groupId.equals(that.groupId)) return false;
      return artifactId.equals(that.artifactId);
   }

   @Override
   public int hashCode() {
      int result = groupId.hashCode();
      result = 31 * result + artifactId.hashCode();
      return result;
   }

   public static GradleDependency from(String dependency) {

      final String[] coordinate = dependency.split(":");

      if (coordinate.length == 2) {
         return new GradleDependency(coordinate[0], coordinate[1]);
      } else {
         throw new IllegalArgumentException("Dependency coordinates should only specify Group and ARtifact (G:A)");
      }
   }

   public static GradleDependency from(String groupId, String artifactId) {
      return new GradleDependency(groupId, artifactId);
   }
}
