package org.jboss.shrinkwrap.resolver.api.maven;

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;

/**
 * Resolved Maven-based artifact's metadata
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public interface MavenArtifactInfo {

    /**
     * Returns the defined coordinate (i.e. address) of this resolved artifact.
     *
     * @return
     */
    MavenCoordinate getCoordinate();

    /**
     * Returns the resolved "version" portion of this artifact's coordinates; SNAPSHOTs may declare a version field (as
     * represented by {@link VersionedMavenCoordinate#getVersion()}, which must resolve to a versioned snapshot version
     * number. That resolved version number is reflected by this field. In the case of true versions (ie.
     * non-SNAPSHOTs), this call will be equal to {@link VersionedMavenCoordinate#getVersion()}.
     *
     * @return
     */
    String getResolvedVersion();

    /**
     * Returns whether or not this artifact is using a SNAPSHOT version.
     *
     * @return
     */
    boolean isSnapshotVersion();

    /**
     * Returns the file extension of this artifact, ie. ("jar")
     *
     * @return The file extension, which is never null
     */
    String getExtension();

    /**
     * Returns artifacts dependencies.
     *
     * @return
     */
    MavenArtifactInfo[] getDependencies();

    /**
     * @return the scope information of this artifact
     */
    ScopeType getScope();
}
