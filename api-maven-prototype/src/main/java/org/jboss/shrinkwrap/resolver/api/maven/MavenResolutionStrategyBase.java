package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.resolver.api.TransitiveResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinateBase;

public interface MavenResolutionStrategyBase<COORDINATETYPE extends MavenCoordinateBase>
        extends
        TransitiveResolutionStrategy<COORDINATETYPE, MavenResolutionFilterBase<COORDINATETYPE>, MavenResolutionStrategyBase<COORDINATETYPE>> {

}
