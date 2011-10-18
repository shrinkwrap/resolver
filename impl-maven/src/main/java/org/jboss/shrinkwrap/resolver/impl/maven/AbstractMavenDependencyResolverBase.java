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
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.resolver.api.DependencyResolver;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;

public class AbstractMavenDependencyResolverBase implements DependencyResolver<MavenResolutionFilter, MavenDependency> {
    private static final Logger log = Logger.getLogger(AbstractMavenDependencyResolverBase.class.getName());
    private static final File[] FILE_CAST = new File[0];

    private MavenDependencyDelegate delegate;

    protected AbstractMavenDependencyResolverBase(MavenDependencyDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView)
            throws ResolutionException {
        return resolveAs(archiveView, AcceptAllFilter.INSTANCE);
    }

    @Override
    public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW> resolveAs(Class<ARCHIVEVIEW> archiveView,
            MavenResolutionFilter filter) throws ResolutionException {
        // Precondition checks
        if (archiveView == null) {
            throw new IllegalArgumentException("Archive view must be specified");
        }
        if (filter == null) {
            throw new IllegalArgumentException("Filter must be specified");
        }

        final File[] files = resolveAsFiles(filter);
        final Collection<ARCHIVEVIEW> archives = new ArrayList<ARCHIVEVIEW>(files.length);
        for (final File file : files) {
            final ARCHIVEVIEW archive = ShrinkWrap.create(ZipImporter.class, file.getName()).importFrom(convert(file))
                    .as(archiveView);
            archives.add(archive);
        }

        return archives;
    }

    @Override
    public File[] resolveAsFiles() throws ResolutionException {
        return resolveAsFiles(AcceptAllFilter.INSTANCE);
    }

    @Override
    public File[] resolveAsFiles(MavenResolutionFilter filter) throws ResolutionException {
        Validate.notEmpty(delegate.getDependencies(), "No dependencies were set for resolution");

        CollectRequest request = new CollectRequest(MavenConverter.asDependencies(delegate.getDependencies()),
                MavenConverter.asDependencies(new ArrayList<MavenDependency>(delegate.getVersionManagement())), delegate
                        .getSettings().getRemoteRepositories());

        // configure filter
        filter.configure(Collections.unmodifiableList(delegate.getDependencies()));

        // wrap artifact files to archives
        Collection<ArtifactResult> artifacts = null;
        try {
            artifacts = delegate.getSystem().resolveDependencies(delegate.getSession(), request, filter);
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

        Collection<File> files = new ArrayList<File>(artifacts.size());
        for (ArtifactResult artifact : artifacts) {
            Artifact a = artifact.getArtifact();
            // skip all pom artifacts
            if ("pom".equals(a.getExtension())) {
                log.info("Removed POM artifact " + a.toString() + " from archive, it's dependencies were fetched.");
                continue;
            }

            files.add(a.getFile());
        }

        return files.toArray(FILE_CAST);
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

}
