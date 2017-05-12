package org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon;

public interface DaemonBuilder<DAEMON_TRIGGER_TYPE extends DaemonBuildTrigger> {

    /**
     * Ensures that the build of the project will be triggered in separated thread.
     *
     * @return Instance of some implementation of {@link DaemonBuildTrigger}
     */
    DAEMON_TRIGGER_TYPE useAsDaemon();
}
