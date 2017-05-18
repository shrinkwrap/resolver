package org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon;

import java.util.concurrent.TimeoutException;

public interface DaemonBuildTriggerWithTimeout extends DaemonBuildTrigger {

    /**
     * Triggers a build  of the project using previously configured project data and environment settings.
     *
     * @return An instance of {@link DaemonBuild} as a representation of the daemon Maven build.
     * @throws TimeoutException if the previously set condition hasn't been met within the set timeout.
     */
    DaemonBuild build() throws TimeoutException;
}
