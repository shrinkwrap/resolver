package org.jboss.shrinkwrap.resolver.impl.maven;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenRepositorySystem;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.MavenModelResolver;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Test for {@link MavenModelResolver}.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class MavenModelResolverTest {

    /**
     * Tests if newCopy() gives independent instances.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void shouldGiveNewIndependentRepositories() throws NoSuchFieldException, IllegalAccessException {
        // given
        final String initialId = "id";
        final RemoteRepository remoteRepository = new RemoteRepository(initialId, "type", "url");
        final MavenModelResolver mavenModelResolver = new MavenModelResolver(new MavenRepositorySystem(), null,
            Arrays.asList(remoteRepository));

        // when
        final MavenModelResolver mavenModelResolverCopy = (MavenModelResolver) mavenModelResolver.newCopy();
        remoteRepository.setId("otherId");

        // then
        // simulate access to repositories field, internal functions uses this field, e.g. to resolve model
        final Field repositoriesField = MavenModelResolver.class.getDeclaredField("repositories");
        repositoriesField.setAccessible(true);

        @SuppressWarnings("unchecked")
        final List<RemoteRepository> value = (List<RemoteRepository>) repositoriesField.get(mavenModelResolverCopy);
        Assert.assertEquals("Internal value in copy has changed!", initialId, value.get(0).getId());
    }
}
