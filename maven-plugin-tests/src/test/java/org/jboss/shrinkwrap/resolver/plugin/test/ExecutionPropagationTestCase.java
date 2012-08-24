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
