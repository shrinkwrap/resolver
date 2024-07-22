package org.jboss.shrinkwrap.resolver.impl.maven;


import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class WorkingSessionChainingTestCase {

    @Test
    void checkResolutionOfSingleArtifact() {

        MavenStrategyStage stage = Resolvers.use(MavenResolverSystem.class).resolve("foo:bar:2");

        Assertions.assertNotNull(stage, "Resolving an artifact is possible via API");
        Assertions.assertEquals(1, ((MavenWorkingSessionContainer) stage)
            .getMavenWorkingSession().getDependenciesForResolution().size(), "Resolver contains 1 dependency to be resolved");
    }

    @Test
    void checkResolutionOfSingleArtifactFailFast() {
        // there is no version
        Assertions.assertThrows(ResolutionException.class, () -> {
            Resolvers.use(MavenResolverSystem.class).resolve("foo:bar");
        });
    }

}
