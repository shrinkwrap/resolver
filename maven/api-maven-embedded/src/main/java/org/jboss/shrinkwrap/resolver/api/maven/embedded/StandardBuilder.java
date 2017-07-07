package org.jboss.shrinkwrap.resolver.api.maven.embedded;

import org.jboss.shrinkwrap.resolver.impl.maven.embedded.EmbeddedMavenBuildException;

public interface StandardBuilder {

    /**
     * Build project using previously configured project data and environment settings.
     *
     * @return An instance of @{BuiltProject} as a representation of the built project
     * @throws EmbeddedMavenBuildException runtime exception when build fails, containing BuiltProject
     */
    BuiltProject build();

    /**
     * If a failure of a project maven build should be ignored. Default is <code>false</code>
     *
     * @param ignoreFailure If a failure of a project maven build should be ignored
     * @return Modified EmbeddedMaven instance
     */
    StandardBuilder ignoreFailure(boolean ignoreFailure);

    /**
     * Sets that a failure of a project maven build should be ignored.
     *
     * @return Modified EmbeddedMaven instance
     */
    StandardBuilder ignoreFailure();
}
