package org.jboss.shrinkwrap.resolver.impl.gradle;

import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that groups dependencies by scope.
 */
public class GradleEffectiveDependencies {

   private final Map<ScopeType, List<File>> effectiveModelGroupedByScope = new HashMap<>();

   public void addDependency(IdeaSingleEntryLibraryDependency ideaSingleEntryLibraryDependency) {
      addDependenciesByScope(ideaSingleEntryLibraryDependency);
   }

   public List<File> getDependenciesByScope(final ScopeType scopeType) {
      if (effectiveModelGroupedByScope.containsKey(scopeType)) {
         return Collections.unmodifiableList(effectiveModelGroupedByScope.get(scopeType));
      } else {
         return Collections.unmodifiableList(new ArrayList<File>());
      }
   }

   private void addDependenciesByScope(final IdeaSingleEntryLibraryDependency ideaSingleEntryLibraryDependency) {
      final List<File> dependenciesByScope;
      final ScopeType scopeType = ScopeType.valueOf(ideaSingleEntryLibraryDependency.getScope().getScope());
      if (effectiveModelGroupedByScope.containsKey(scopeType)) {
         dependenciesByScope = effectiveModelGroupedByScope.get(scopeType);
      } else {
         dependenciesByScope = new ArrayList<>();
      }

      dependenciesByScope.add(ideaSingleEntryLibraryDependency.getFile());
      effectiveModelGroupedByScope.put(scopeType, dependenciesByScope);
   }

}
