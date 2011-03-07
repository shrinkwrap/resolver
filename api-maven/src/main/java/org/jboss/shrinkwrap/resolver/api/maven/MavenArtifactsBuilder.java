package org.jboss.shrinkwrap.resolver.api.maven;

/**
 * An artifacts builder is object which holds and construct dependencies and it
 * is able to resolve them into an array of ShrinkWrap archives.
 * 
 * Artifacts builder allows chaining of artifacts, that is specifying a new
 * artifact. In this case, currently constructed artifact is stored as a
 * dependency and user is allowed to specify parameters for another artifact.
 * 
 * The special ability of this object when compared to
 * {@link MavenArtifactBuilder} is the ability to work in batch, that is allow
 * to define more artifacts at once and modify their scope etc. by a single
 * call.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @see MavenArtifactBuilder
 */
public interface MavenArtifactsBuilder extends MavenArtifactBuilder {
}