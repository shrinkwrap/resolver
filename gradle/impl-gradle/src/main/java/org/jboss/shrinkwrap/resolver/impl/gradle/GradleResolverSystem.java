package org.jboss.shrinkwrap.resolver.impl.gradle;

import java.io.File;

/**
 * Class to set the directory to resolve build.gradle file.
 */
public class GradleResolverSystem {

   public BuildEquippedResolveStage forProjectDirectory(String projectDirectory) {
      return new BuildEquippedResolveStage(projectDirectory);
   }

   public BuildEquippedResolveStage forProjectDirectory(File projectDirectory) {
      return forProjectDirectory(projectDirectory.getAbsolutePath());
   }

}
