package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.api.Assignable;

public interface MavenImporter extends Assignable {

    EffectivePomMavenImporter loadEffectivePom(String path, String... profiles);

    static interface EffectivePomMavenImporter extends Assignable {
        EffectivePomMavenImporter importBuildOutput();

        EffectivePomMavenImporter importTestBuildOutput();

        EffectivePomMavenImporter importTestDependencies();

        EffectivePomMavenImporter importAnyDependencies(MavenResolutionFilter filter);

        MavenDependencyResolver getMavenDependencyResolver();
    }
}
