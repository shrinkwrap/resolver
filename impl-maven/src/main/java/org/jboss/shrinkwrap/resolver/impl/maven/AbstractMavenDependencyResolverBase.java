package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.resolver.api.DependencyResolver;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.util.IOUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;

/**
 * Representation of the last execution point from Maven execution cycle.
 *
 * This class is responsible for resolution of the artifacts which were configured on the session.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public abstract class AbstractMavenDependencyResolverBase implements
        DependencyResolver<MavenResolutionFilter, MavenDependency>, MavenEnvironmentRetrieval {
    private static final Logger log = Logger.getLogger(AbstractMavenDependencyResolverBase.class.getName());
    private static final File[] FILE_CAST = new File[0];

    protected MavenEnvironment maven;

    protected AbstractMavenDependencyResolverBase(MavenEnvironment maven) {
        this.maven = maven;
    }

    @Override
    public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView)
            throws ResolutionException {
        return resolveAs(archiveView, AcceptAllFilter.INSTANCE);
    }

    @Override
    public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(final Class<ARCHIVEVIEW> archiveView,
            MavenResolutionFilter filter) throws ResolutionException {
        Validate.notNull(archiveView, "Archive view must be specified");
        Validate.notNull(filter, "Filter must be specified");

        final Collection<ARCHIVEVIEW> archives = new ArrayList<ARCHIVEVIEW>();
        resolve(filter, new ArtifactMapper() {
            @Override
            public void map(Artifact artifact) {
                ARCHIVEVIEW archive = null;
                // FIXME: this is not a safe assumption, file can have a different name
                if ("pom.xml".equals(artifact.getFile().getName())) {
                    archive = ShrinkWrap
                            .create(ExplodedImporter.class, artifact.getArtifactId() + "." + artifact.getExtension())
                            .importDirectory(new File(artifact.getFile().getParentFile(), "target/classes")).as(archiveView);
                } else {
                    archive = ShrinkWrap.create(ZipImporter.class, artifact.getFile().getName())
                            .importFrom(convert(artifact.getFile())).as(archiveView);
                }
                archives.add(archive);
            }
        });

        return archives;
    }

    @Override
    public File[] resolveAsFiles() throws ResolutionException {
        return resolveAsFiles(AcceptAllFilter.INSTANCE);
    }

    @Override
    public File[] resolveAsFiles(MavenResolutionFilter filter) throws ResolutionException {
        if (filter == null) {
            throw new IllegalArgumentException("Filter must be specified");
        }
        final Collection<File> files = new ArrayList<File>();
        resolve(filter, new ArtifactMapper() {
            @Override
            public void map(Artifact artifact) {
                // FIXME: this is not a safe assumption, file can have a different name
                if ("pom.xml".equals(artifact.getFile().getName())) {

                    String artifactId = artifact.getArtifactId();
                    String extension = artifact.getExtension();

                    File root = new File(artifact.getFile().getParentFile(), "target/classes");
                    try {
                        File archive = File.createTempFile(artifactId + "-", "." + extension);
                        archive.deleteOnExit();
                        IOUtil.packageDirectories(archive, root);
                        files.add(archive);
                    } catch (IOException e) {
                        throw new ResolutionException("Unable to get artifact " + artifactId + " from the classpath", e);
                    }

                } else {
                    files.add(artifact.getFile());
                }
            }

        });

        return files.toArray(FILE_CAST);
    }

    private void resolve(MavenResolutionFilter filter, ArtifactMapper mapper) {
        Validate.notEmpty(maven.getDependencies(), "No dependencies were set for resolution");

        CollectRequest request = new CollectRequest(MavenConverter.asDependencies(maven.getDependencies()),
                MavenConverter.asDependencies(new ArrayList<MavenDependency>(maven.getVersionManagement())),
                maven.getRemoteRepositories());

        // configure filter
        filter.configure(Collections.unmodifiableList(maven.getDependencies()));

        // wrap artifact files to archives
        Collection<ArtifactResult> artifacts = null;
        try {
            artifacts = maven.execute(request, filter);
        } catch (DependencyResolutionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof ArtifactResolutionException) {
                    throw new ResolutionException("Unable to get artifact from the repository", cause);
                } else if (cause instanceof DependencyCollectionException) {
                    throw new ResolutionException("Unable to collect dependency tree for given dependencies", cause);
                }
                throw new ResolutionException("Unable to collect/resolve dependency tree for a resulution", e);
            }
        }

        for (ArtifactResult artifact : artifacts) {
            Artifact a = artifact.getArtifact();
            // skip all pom artifacts
            if ("pom".equals(a.getExtension())) {
                log.info("Removed POM artifact " + a.toString() + " from archive, it's dependencies were fetched.");
                continue;
            }

            mapper.map(a);
        }
    }

    @Override
    public MavenEnvironment getMavenEnvironment() {
        return maven;
    }

    // converts a file to a ZIP file
    private ZipFile convert(File file) throws ResolutionException {
        try {
            return new ZipFile(file);
        } catch (ZipException e) {
            throw new ResolutionException("Unable to treat dependency artifact \"" + file.getAbsolutePath()
                    + "\" as a ZIP file", e);
        } catch (IOException e) {
            throw new ResolutionException("Unable to access artifact file at \"" + file.getAbsolutePath() + "\".", e);
        }
    }

    private interface ArtifactMapper {
        void map(Artifact artifact);
    }

}
