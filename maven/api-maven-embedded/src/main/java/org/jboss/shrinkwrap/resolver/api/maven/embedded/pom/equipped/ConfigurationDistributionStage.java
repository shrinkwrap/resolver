package org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.DistributionStage;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public interface ConfigurationDistributionStage
    extends ConfigurationStage<ConfigurationDistributionStage>, DistributionStage<ConfigurationStage> {
}
