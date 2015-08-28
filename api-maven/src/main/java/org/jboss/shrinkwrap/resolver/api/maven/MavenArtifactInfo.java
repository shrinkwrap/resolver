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
     * @return The defined coordinate (i.e. address) of this resolved artifact.
     */
    MavenCoordinate getCoordinate();

    /**
     * Returns the resolved "version" portion of this artifact's coordinates; SNAPSHOTs may declare a version field (as
     * represented by {@link MavenResolvedArtifact#getResolvedVersion()}), which must resolve to a versioned snapshot version
     * number. That resolved version number is reflected by this field. In the case of true versions (ie.
     * non-SNAPSHOTs), this call will be equal to {@link MavenCoordinate#getVersion()}.
     *
     * @return The resolved "version" portion of this artifact's coordinates
     */
    String getResolvedVersion();

    /**
     * Returns whether or not this artifact is using a SNAPSHOT version.
     *
     * @return Whether or not this artifact is using a SNAPSHOT version.
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
     * @return Artifacts dependencies.
     */
    MavenArtifactInfo[] getDependencies();

    /**
     * Returns the scope information of this artifact
     *
     * @return the scope information of this artifact
     */
    ScopeType getScope();

    /**
     * Returns true if artifact is optional.
     *
     * @return return true if artifact is optional.
     */
    boolean isOptional();
}
