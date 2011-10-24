package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.sonatype.aether.RepositorySystemSession;

public class MavenDependencyDelegate {

    private MavenRepositorySystem system;
    private MavenDependencyResolverSettings settings;

    private RepositorySystemSession session;

    private Stack<MavenDependency> dependencies;

    private Set<MavenDependency> versionManagement;

    /**
     * Constructs new instance of MavenDependencies
     */
    public MavenDependencyDelegate() {
        this.system = new MavenRepositorySystem();
        this.settings = new MavenDependencyResolverSettings();
        this.dependencies = new Stack<MavenDependency>();
        this.versionManagement = new LinkedHashSet<MavenDependency>();
        // get session to spare time
        this.session = system.getSession(settings);
    }

    public MavenDependencyDelegate(MavenRepositorySystem system, RepositorySystemSession session,
            MavenDependencyResolverSettings settings, Stack<MavenDependency> dependencies,
            Set<MavenDependency> dependencyManagement) {
        this.system = system;
        this.session = session;
        this.settings = settings;
        this.dependencies = dependencies;
        this.versionManagement = new LinkedHashSet<MavenDependency>(dependencyManagement);
    }

    public MavenRepositorySystem getSystem() {
        return system;
    }

    public MavenDependencyResolverSettings getSettings() {
        return settings;
    }

    public RepositorySystemSession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(RepositorySystemSession session) {
        this.session = session;
    }

    public Stack<MavenDependency> getDependencies() {
        return dependencies;
    }

    public Set<MavenDependency> getVersionManagement() {
        return versionManagement;
    }

}
