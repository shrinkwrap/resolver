package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.DistributionStage;
import org.junit.Assume;
import org.junit.Test;

public class DefaultMavenVersionTestCase {

    @Test
    public void default_version_should_be_same_as_dependency_version() {
        String depVersion = System.getProperty("version.org.apache.maven.dependency");
        Assume.assumeNotNull(depVersion);
        Assertions.assertThat(DistributionStage.DEFAULT_MAVEN_VERSION).isEqualTo(depVersion);
    }
}
