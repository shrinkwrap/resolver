package org.jboss.shrinkwrap.resolver.impl.maven;

import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * Base implementation of Maven Resolve Stages
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
abstract class MavenResolveStageBaseImpl<RESOLVESTAGETYPE extends MavenResolveStageBase<RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
        extends ResolveStageBaseImpl<RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    protected MavenResolveStageBaseImpl(final MavenWorkingSession session) {
        super(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see ResolveStageBaseImpl#resolveVersionRange(String)
     */
    @Override
    public MavenVersionRangeResult resolveVersionRange(final String coordinate) throws IllegalArgumentException {
        Validate.isNullOrEmpty(coordinate);

        final MavenCoordinate mavenCoordinate = MavenCoordinates.createCoordinate(coordinate);
        return this.getMavenWorkingSession().resolveVersionRange(mavenCoordinate);
    }
}