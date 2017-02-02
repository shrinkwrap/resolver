package org.jboss.shrinkwrap.resolver.impl.gradle;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class that resolves all dependencies of given scope to ShrinkWrap archive.
 */
public class GradleStrategyStage {

   private final String projectDirectory;
   private final Set<ScopeType> scopeTypesDependencies;

   public GradleStrategyStage(String projectDirectory, Set<ScopeType> scopeTypesDependencies) {
      this.projectDirectory = projectDirectory;
      this.scopeTypesDependencies = scopeTypesDependencies;
   }

   /**
    * Gets all dependencies (and the transitive ones too) as given ShrinkWrap Archive type.
    * @param archive ShrinkWrap archive.
    * @return Array of dependencies and transitive ones for configured state.
    */
   public Archive[] as(Class<? extends Archive> archive) {
      final List<? extends Archive> archives = asList(archive);
      return archives.toArray(new Archive[archives.size()]);
   }

   /**
    * Gets all dependencies (and the transitive ones too) as given ShrinkWrap Archive type.
    * @param archive ShrinkWrap archive.
    * @return List of dependencies and transitive ones for configured state.
    */
   public List<? extends Archive> asList(Class<? extends Archive> archive) {

      final List<Archive> archives = new ArrayList<>();
      final GradleEffectiveDependencies gradleEffectiveDependencies = GradleRunner.getEffectiveDependencies(projectDirectory);

      for (ScopeType scopeType : scopeTypesDependencies) {
         final List<File> dependenciesByScope = gradleEffectiveDependencies.getDependenciesByScope(scopeType);
         for (File dependency : dependenciesByScope) {
            final Archive dep = ShrinkWrap.create(ZipImporter.class, dependency.getName()).importFrom(dependency).as(archive);
            archives.add(dep);
         }
      }
      return archives;
   }

}
