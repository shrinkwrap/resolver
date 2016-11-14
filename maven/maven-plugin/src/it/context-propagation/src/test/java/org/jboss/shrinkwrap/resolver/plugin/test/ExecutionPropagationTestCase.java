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
package org.jboss.shrinkwrap.resolver.plugin.test;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests that all properties were propagated
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ExecutionPropagationTestCase {

    @Test
    public void propagatedPomFile() {
        Assert.assertNotNull("maven.execution.pom-file was propagated", System.getProperty("maven.execution.pom-file"));
    }

    @Test
    public void propagatedUserSettings() {
        Assert.assertNotNull("maven.execution.user-settings was propagated",
            System.getProperty("maven.execution.user-settings"));
    }

    @Test
    public void propagatedGlobalSettings() {
        Assert.assertNotNull("maven.execution.global-settings was propagated",
            System.getProperty("maven.execution.global-settings"));
    }

    @Test
    public void propagatedOffline() {
        Assert.assertNotNull("maven.execution.offline was propagated", System.getProperty("maven.execution.offline"));
    }

    @Test
    public void propagatedUserActiveProfiles() {
        Assert.assertNotNull("maven.execution.active-profiles was propagated",
            System.getProperty("maven.execution.active-profiles"));
    }
}
