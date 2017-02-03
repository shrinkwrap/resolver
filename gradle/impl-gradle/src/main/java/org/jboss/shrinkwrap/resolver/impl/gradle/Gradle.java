package org.jboss.shrinkwrap.resolver.impl.gradle;

/**
 * Main class of Gradle resolver. This is the root of ShrinkWrap Gradle Resolver DSL.
 */
public class Gradle {

   public static GradleResolverSystem resolver() {
      return new GradleResolverSystem();
   }

}
