/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;

/**
 * Ensures that a {@link ConfiguredResolveStage} may be reused to resolve N requests without stateful overlap.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class RepeatedUsageTestCase {

    /**
     * Tests a resolution of an artifact from local repository specified in settings.xml as active profile
     *
     * SHRINKRES-46
     */
    @Test
    public void reuseConfiguredResolverSystem() {

        final ConfiguredResolveStage resolver = Maven.resolver()
            .configureSettings("target/settings/profiles/settings.xml").configureFromPom("target/poms/test-bom.xml");

        final File[] firstRequest = resolver.resolve("org.jboss.shrinkwrap.test:test-deps-a").withoutTransitivity()
            .as(File.class);

        new ValidationUtil("test-deps-a-1.0.0.jar").validate(firstRequest);

        final File[] secondRequest = resolver.resolve("org.jboss.shrinkwrap.test:test-deps-d").withoutTransitivity()
            .as(File.class);

        new ValidationUtil("test-deps-d-1.0.0.jar").validate(secondRequest);
    }
}
