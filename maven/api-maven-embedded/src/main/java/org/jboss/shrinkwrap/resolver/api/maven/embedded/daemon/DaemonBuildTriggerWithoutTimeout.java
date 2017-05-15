package org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon;

public interface DaemonBuildTriggerWithoutTimeout extends DaemonBuildTrigger {

    /**
     * Triggers a build  of the project using previously configured project data and environment settings.
     *
     * @return An instance of {@link DaemonBuild} as a representation of the daemon Maven build.
     */
    DaemonBuild build();
}
