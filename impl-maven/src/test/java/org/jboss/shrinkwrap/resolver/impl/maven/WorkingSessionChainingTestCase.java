package org.jboss.shrinkwrap.resolver.impl.maven;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class WorkingSessionChainingTestCase {

    @Test
    public void checkResolutionOfSingleArtifact() {

        MavenStrategyStage stage = Resolvers.use(MavenResolverSystem.class).resolve("foo:bar:2");

        Assert.assertNotNull("Resolving an artifact is possible via API", stage);
        Assert.assertEquals("Resolver contains 1 dependency to be resolved", 1, ((MavenWorkingSessionContainer) stage)
            .getMavenWorkingSession().getDependenciesForResolution().size());
    }

    @Test(expected = ResolutionException.class)
    public void checkResolutionOfSingleArtifactFailFast() {
        // there is no version
        Resolvers.use(MavenResolverSystem.class).resolve("foo:bar");
    }

}
