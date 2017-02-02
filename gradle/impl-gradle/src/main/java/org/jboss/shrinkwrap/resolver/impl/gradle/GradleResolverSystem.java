package org.jboss.shrinkwrap.resolver.impl.gradle;

import java.io.File;

/**
 * Class to set the directory to resolve build.gradle file.
 */
public class GradleResolverSystem {

   public ProjectEquippedResolveStage forProjectDirectory(String projectDirectory) {
      return new ProjectEquippedResolveStage(projectDirectory);
   }

   public ProjectEquippedResolveStage forProjectDirectory(File projectDirectory) {
      return forProjectDirectory(projectDirectory.getAbsolutePath());
   }

}
