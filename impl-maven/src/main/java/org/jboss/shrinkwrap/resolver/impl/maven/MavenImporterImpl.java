package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ResourceUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.RepositorySystemSession;

public class MavenImporterImpl implements MavenImporter {

    private Archive<?> archive;

    private final MavenRepositorySystem system;
    private final MavenDependencyResolverSettings settings;

    private RepositorySystemSession session;

    public MavenImporterImpl(Archive<?> archive) {
        this.archive = archive;

        this.system = new MavenRepositorySystem();
        this.settings = new MavenDependencyResolverSettings();

        // get session to spare time
        this.session = system.getSession(settings);
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> archiveType) {
        return archive.as(archiveType);
    }

    @Override
    public EffectivePomMavenImporter loadEffectivePom(String path, String... profiles) {

        Validate.notNullOrEmpty(path, "Path to a POM file must be specified");

        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);

        Validate.isReadable(resolvedPath, "Path to the pom.xml ('" + path + "')file must be defined and accessible");

        File pom = new File(resolvedPath);
        Model model = system.loadPom(pom, settings, session);

        MavenPackagingType mpt = MavenPackagingType.from(model.getPackaging());

        return new EffectivePomMavenImporterImpl(archive, mpt, model, system, settings, session);
    }

}
