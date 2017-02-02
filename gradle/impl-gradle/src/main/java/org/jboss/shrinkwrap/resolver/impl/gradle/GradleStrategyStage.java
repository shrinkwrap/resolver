package org.jboss.shrinkwrap.resolver.impl.gradle;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GradleStrategyStage {

   private final String projectDirectory;
   private final Set<ScopeType> scopeTypesDependencies;

   public GradleStrategyStage(String projectDirectory, Set<ScopeType> scopeTypesDependencies) {
      this.projectDirectory = projectDirectory;
      this.scopeTypesDependencies = scopeTypesDependencies;
   }

   public Archive[] as(Class<? extends Archive> archive) {
      final List<? extends Archive> archives = asList(archive);
      return archives.toArray(new Archive[archives.size()]);
   }

   public List<? extends Archive> asList(Class<? extends Archive> archive) {

      final List<Archive> archives = new ArrayList<>();
      final GradleEffectiveModel gradleEffectiveModel = GradleRunner.getEffectiveModel(projectDirectory);

      for (ScopeType scopeType : scopeTypesDependencies) {
         final List<File> dependenciesByScope = gradleEffectiveModel.getDependenciesByScope(scopeType);
         for (File dependency : dependenciesByScope) {
            final Archive dep = ShrinkWrap.create(ZipImporter.class, dependency.getName()).importFrom(dependency).as(archive);
            archives.add(dep);
         }
      }
      return archives;
   }

}
