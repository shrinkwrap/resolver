/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.eclipse.aether.repository.RemoteRepository;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenRepositorySystem;
import org.jboss.shrinkwrap.resolver.impl.maven.internal.MavenModelResolver;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link MavenModelResolver}.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class MavenModelResolverTestCase {

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
        RemoteRepository remoteRepository = new RemoteRepository.Builder(initialId, "type", "url").build();
        final MavenModelResolver mavenModelResolver = new MavenModelResolver(new MavenRepositorySystem(), null,
            Arrays.asList(remoteRepository));

        // when
        final MavenModelResolver mavenModelResolverCopy = (MavenModelResolver) mavenModelResolver.newCopy();
        remoteRepository = new RemoteRepository.Builder(remoteRepository).setId("otherId").build();

        // then
        // simulate access to repositories field, internal functions uses this field, e.g. to resolve model
        final Field repositoriesField = MavenModelResolver.class.getDeclaredField("repositories");
        repositoriesField.setAccessible(true);

        @SuppressWarnings("unchecked")
        final List<RemoteRepository> value = (List<RemoteRepository>) repositoriesField.get(mavenModelResolverCopy);
        Assert.assertEquals("Internal value in copy has changed!", initialId, value.get(0).getId());
    }
}
