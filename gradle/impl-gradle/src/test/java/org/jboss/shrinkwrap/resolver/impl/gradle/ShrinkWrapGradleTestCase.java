package org.jboss.shrinkwrap.resolver.impl.gradle;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ShrinkWrapGradleTestCase {

   @Test
   public void should_return_dependencies_by_scope_in_simple_build_script() {
      final List<? extends Archive> archives = Gradle.resolver().forProjectDirectory("src/test/resources/simple")
              .importCompileAndRuntime()
              .resolve().asList(JavaArchive.class);

      assertThat(archives).extracting("name").contains("slf4j-simple-1.7.5.jar", "slf4j-api-1.7.5.jar");
   }

   @Test
   public void should_return_dependencies_by_scope_in_dependency_management_build_script() {
      final List<? extends Archive> archives = Gradle.resolver().forProjectDirectory("src/test/resources/dependencymanager")
              .importRuntime()
              .resolve().asList(JavaArchive.class);

      assertThat(archives).extracting("name").contains("deltaspike-core-impl-1.7.1.jar");
   }

}
