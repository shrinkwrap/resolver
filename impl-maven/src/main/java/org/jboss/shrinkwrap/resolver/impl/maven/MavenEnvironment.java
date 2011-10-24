
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;

/**
 * Encapsulates complete Maven environment including intermediate data
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
interface MavenEnvironment {

    Set<MavenDependency> getVersionManagement();

    Stack<MavenDependency> getDependencies();

    List<RemoteRepository> getRemoteRepositories();

    Model getModel();

    /**
     * Regenerates session environment to match latest update
     *
     * @return Modified instance to allow chaining
     */
    MavenEnvironment regenerateSession();

    MavenEnvironment goOffline(boolean value);

    MavenEnvironment useCentralRepository(boolean useCentralRepository);

    ArtifactTypeRegistry getArtifactTypeRegistry();

    /**
     *
     * Loads an effective POM file and updates settings settings accordingly.
     *
     * @param request Request to load the effective POM file
     * @return Model representing the POM file
     */
    MavenEnvironment execute(ModelBuildingRequest request);

    MavenEnvironment execute(SettingsBuildingRequest request);

    Collection<ArtifactResult> execute(CollectRequest request, MavenResolutionFilter filter)
            throws DependencyResolutionException;

    ArtifactResult execute(ArtifactRequest request) throws ArtifactResolutionException;

}
