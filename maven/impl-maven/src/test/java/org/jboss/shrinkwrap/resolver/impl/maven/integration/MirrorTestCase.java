/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests mirror setting in Maven
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MirrorTestCase {

    /**
     * Tests a resolution of an artifact a repository specified in settings.xml within activeProfiles mirrored
     *
     */
    @Test
    public void enabledMirror() {
        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings-mirror.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().asSingle(File.class);

        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", file.getName());
    }

}
