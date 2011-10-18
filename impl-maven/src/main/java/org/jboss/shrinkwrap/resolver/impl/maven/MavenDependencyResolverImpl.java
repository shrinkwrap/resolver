package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.util.Set;
import java.util.Stack;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ResourceUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.RepositorySystemSession;

public class MavenDependencyResolverImpl implements MavenDependencyResolverInternal {

    private MavenDependencyDelegate delegate;

    public MavenDependencyResolverImpl() {
        this.delegate = new MavenDependencyDelegate();
    }

    @Override
    public MavenDependencyResolver configureFrom(String path) {
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the settings.xml ('" + path + "') must be defined and accessible");

        delegate.getSystem().loadSettings(new File(resolvedPath), delegate.getSettings());
        // regenerate session
        delegate.setSession(delegate.getSystem().getSession(delegate.getSettings()));
        return this;
    }

    @Override
    public EffectivePomMavenDependencyResolver loadEffectiveFromPom(String path, String... profiles) throws ResolutionException {

        Validate.notNullOrEmpty(path, "Path to a POM file must be specified");
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the pom.xml ('" + path + "')file must be defined and accessible");

        File pom = new File(resolvedPath);
        Model model = delegate.getSystem().loadPom(pom, delegate.getSettings(), delegate.getSession());

        EffectivePomMavenDependencyResolverImpl epmdr = new EffectivePomMavenDependencyResolverImpl(this, model);

        return epmdr;

    }

    @Override
    public MavenDependencyResolver useCentralRepo(boolean useCentral) {
        delegate.getSettings().setUseMavenCentral(useCentral);
        return this;
    }

    @Override
    public MavenDependencyResolver goOffline() {
        delegate.getSettings().setOffline(true);
        // regenerate session
        delegate.setSession(delegate.getSystem().getSession(delegate.getSettings()));
        return this;
    }

    @Override
    public MavenDependencyBuilder artifact(String coordinates) throws ResolutionException {
        Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

        return new MavenDependencyBuilderForArtifact(this, coordinates);
    }

    @Override
    public MavenDependencyBuilder artifacts(String... coordinates) throws ResolutionException {
        Validate.notNullAndNoNullValues(coordinates, "Artifacts coordinates must not be null or empty");

        return new MavenDependencyBuilderForArtifacts(this, coordinates);
    }

    @Override
    public MavenRepositorySystem getSystem() {
        return delegate.getSystem();
    }

    @Override
    public MavenDependencyResolverSettings getSettings() {
        return delegate.getSettings();
    }

    @Override
    public RepositorySystemSession getSession() {
        return delegate.getSession();
    }

    @Override
    public Stack<MavenDependency> getDependencies() {
        return delegate.getDependencies();
    }

    @Override
    public Set<MavenDependency> getVersionManagement() {
        return delegate.getVersionManagement();
    }

    @Override
    public MavenDependencyDelegate getDelegate() {
        return delegate;
    }

}
