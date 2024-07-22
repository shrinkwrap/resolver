package org.jboss.shrinkwrap.resolver.plugin.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests that all properties were propagated
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ExecutionPropagationTestCase {

    @Test
    void propagatedPomFile() {
        Assertions.assertNotNull(System.getProperty("maven.execution.pom-file"), "maven.execution.pom-file was propagated");
    }

    @Test
    void propagatedUserSettings() {
        Assertions.assertNotNull(System.getProperty("maven.execution.user-settings"), "maven.execution.user-settings was propagated");
    }

    @Test
    void propagatedGlobalSettings() {
        Assertions.assertNotNull(System.getProperty("maven.execution.global-settings"), "maven.execution.global-settings was propagated");
    }

    @Test
    void propagatedOffline() {
        Assertions.assertNotNull(System.getProperty("maven.execution.offline"), "maven.execution.offline was propagated");
    }

    @Test
    void propagatedUserActiveProfiles() {
        Assertions.assertNotNull(System.getProperty("maven.execution.active-profiles"), "maven.execution.active-profiles was propagated");
    }
}
