package org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;

/**
 * A representation of the daemon Maven build
 */
public interface DaemonBuild {

    /**
     * Checks if this thread containing the Maven build is alive.
     *
     * @return <code>true</code> if the thread containing the Maven build  is alive;
     * <code>false</code> otherwise.
     */
    boolean isAlive();

    /**
     * If the thread containing the Maven build is not alive, then it returns an instance of @{BuiltProject} as a
     * representation of the built project; {@code null} otherwise
     *
     * @return If Maven build is not alive then an instance of @{BuiltProject} as a representation of the built project;
     * {@code null} otherwise
     */
    BuiltProject getBuiltProject();
}
