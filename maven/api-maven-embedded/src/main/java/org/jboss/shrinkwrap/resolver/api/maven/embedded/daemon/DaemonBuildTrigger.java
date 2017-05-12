package org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon;

import java.util.concurrent.TimeoutException;

public interface DaemonBuildTrigger {

    /**
     * Triggers a build  of the project using previously configured project data and environment settings.
     */
    void build() throws TimeoutException;
}
