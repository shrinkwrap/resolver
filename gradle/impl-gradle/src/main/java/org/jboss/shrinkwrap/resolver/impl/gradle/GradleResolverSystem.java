package org.jboss.shrinkwrap.resolver.impl.gradle;

import java.io.File;

/**
 * Class to set the directory to resolve build.gradle file.
 */
public class GradleResolverSystem {

   public ProjectEquippedResolveStage forProjectDirectory(final String projectDirectory) {
      return new ProjectEquippedResolveStage(projectDirectory);
   }

   public ProjectEquippedResolveStage forProjectDirectory(final File projectDirectory) {
      return forProjectDirectory(projectDirectory.getAbsolutePath());
   }

}
