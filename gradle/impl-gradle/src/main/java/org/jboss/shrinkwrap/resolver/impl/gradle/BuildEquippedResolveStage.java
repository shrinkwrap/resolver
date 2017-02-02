package org.jboss.shrinkwrap.resolver.impl.gradle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Object to configure which scope dependencies must be resolved by Gradle resolver.
 * This class is a part of the ShrinkWrap Gradle Resolver DSL.
 */
public class BuildEquippedResolveStage {

   private final String projectDirectory;
   private final Set<ScopeType> scopeTypeSet = new HashSet<>();

   public BuildEquippedResolveStage(String projectDirectory) {
      this.projectDirectory = projectDirectory;
   }

   /**
    * Import dependencies with given scope.
    * @param scopeTypes to resolve.
    * @return this instance.
    */
   public BuildEquippedResolveStage importDependencies(ScopeType...scopeTypes) {
      scopeTypeSet.addAll(Arrays.asList(scopeTypes));

      return this;
   }

   /**
    * Import all test dependencies.
    * @return this instance.
    */
   public BuildEquippedResolveStage importTestDependencies() {
      return importDependencies(ScopeType.TEST);
   }

   /**
    * Import all runtime and test dependencies.
    * @return this instance.
    */
   public BuildEquippedResolveStage importRuntimeAndTestDependencies() {
      return importDependencies(ScopeType.TEST, ScopeType.RUNTIME);
   }

   /**
    * Import runtime dependencies.
    * @return this instance.
    */
   public BuildEquippedResolveStage importRuntime() {
      return importDependencies(ScopeType.RUNTIME);
   }

   /**
    * Import compile and runtime dependencies.
    * @return this instance.
    */
   public BuildEquippedResolveStage importCompileAndRuntime() {
      return importDependencies(ScopeType.COMPILE, ScopeType.RUNTIME);
   }

   /**
    * Method termination to resolve all dependencies of configured scopes.
    * @return Class to get results.
    */
   public GradleStrategyStage resolve() {
      return new GradleStrategyStage(projectDirectory, scopeTypeSet);
   }


}
