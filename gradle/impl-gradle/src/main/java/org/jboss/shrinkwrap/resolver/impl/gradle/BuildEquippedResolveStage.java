package org.jboss.shrinkwrap.resolver.impl.gradle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BuildEquippedResolveStage {

   private final String projectDirectory;
   private final Set<ScopeType> scopeTypeSet = new HashSet<>();

   public BuildEquippedResolveStage(String projectDirectory) {
      this.projectDirectory = projectDirectory;
   }

   public BuildEquippedResolveStage importDependencies(ScopeType...scopeTypes) {
      scopeTypeSet.addAll(Arrays.asList(scopeTypes));

      return this;
   }

   public BuildEquippedResolveStage importTestDependencies() {
      return importDependencies(ScopeType.TEST);
   }

   public BuildEquippedResolveStage importRuntimeAndTestDependencies() {
      return importDependencies(ScopeType.TEST, ScopeType.RUNTIME);
   }

   public BuildEquippedResolveStage importRuntime() {
      return importDependencies(ScopeType.RUNTIME);
   }

   public BuildEquippedResolveStage importCompileAndRuntime() {
      return importDependencies(ScopeType.COMPILE, ScopeType.RUNTIME);
   }

   public GradleStrategyStage resolve() {
      return new GradleStrategyStage(projectDirectory, scopeTypeSet);
   }


}
