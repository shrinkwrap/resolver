package org.jboss.shrinkwrap.resolver.impl.gradle;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that resolves all dependencies of given scope to ShrinkWrap archive.
 */
public class GradleStrategyStage {
   private static final Logger log = Logger.getLogger(GradleStrategyStage.class.getName());

   private final String projectDirectory;
   private final Set<ScopeType> scopeTypesDependencies;

   public GradleStrategyStage(final String projectDirectory, final Set<ScopeType> scopeTypesDependencies) {
      this.projectDirectory = projectDirectory;
      this.scopeTypesDependencies = scopeTypesDependencies;
   }

   /**
    * Gets all dependencies (and the transitive ones too) as given ShrinkWrap Archive type.
    * @param archive ShrinkWrap archive.
    * @return Array of dependencies and transitive ones for configured state.
    */
   public Archive[] as(final Class<? extends Archive> archive) {
      final List<? extends Archive> archives = asList(archive);
      return archives.toArray(new Archive[archives.size()]);
   }

   /**
    * Gets all dependencies (and the transitive ones too) as given ShrinkWrap Archive type.
    * @param archive ShrinkWrap archive.
    * @return List of dependencies and transitive ones for configured state.
    */
   public List<? extends Archive> asList(final Class<? extends Archive> archive) {

      final List<Archive> archives = new ArrayList<>();
      final GradleEffectiveDependencies gradleEffectiveDependencies = GradleRunner.getEffectiveDependencies(projectDirectory);

      for (ScopeType scopeType : scopeTypesDependencies) {
         final List<File> dependenciesByScope = gradleEffectiveDependencies.getDependenciesByScope(scopeType);
         for (File dependency : dependenciesByScope) {
            try {
               final Archive dep = ShrinkWrap.create(ZipImporter.class, dependency.getName()).importFrom(dependency).as(archive);
               archives.add(dep);
            } catch (Exception e) {
               log.log(Level.WARNING, "Cannot import gradle dependency " + dependency + ". Not a zip-like format", e);
            }
         }
      }
      return archives;
   }

}
