package org.jboss.shrinkwrap.resolver.impl.gradle;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;

import java.io.File;

public class GradleRunner {

   public static GradleEffectiveModel getEffectiveModel(String projectDirectory) {

      final GradleEffectiveModel gradleEffectiveModel = new GradleEffectiveModel();
      GradleConnector connector = GradleConnector.newConnector();
      connector.forProjectDirectory(new File(projectDirectory));
      ProjectConnection connection = null;

      try {
         connection = connector.connect();
         IdeaProject project = connection.getModel(IdeaProject.class);

         final DomainObjectSet<? extends IdeaModule> modules = project.getChildren();

         for (IdeaModule ideaModule: modules) {
            final DomainObjectSet<? extends IdeaDependency> dependencies = ideaModule.getDependencies();

            for (IdeaDependency ideaDependency : dependencies) {
               if (ideaDependency instanceof IdeaSingleEntryLibraryDependency) {
                  gradleEffectiveModel.addDependency((IdeaSingleEntryLibraryDependency) ideaDependency);
               }
            }

         }

      } finally {
         if(connection != null) {
            connection.close();
         }
      }

      return gradleEffectiveModel;
   }

}
