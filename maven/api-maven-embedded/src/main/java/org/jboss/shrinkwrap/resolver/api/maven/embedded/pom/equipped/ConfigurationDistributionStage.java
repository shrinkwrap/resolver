package org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.DistributionStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.WithTimeoutDaemonBuilder;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public interface ConfigurationDistributionStage
    extends ConfigurationStage<ConfigurationDistributionStage, WithTimeoutDaemonBuilder>,
    DistributionStage<ConfigurationStage<ConfigurationDistributionStage, WithTimeoutDaemonBuilder>, WithTimeoutDaemonBuilder> {
}
