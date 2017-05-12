package org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface WithTimeoutDaemonBuilder extends DaemonBuildTrigger {

    /**
     * Resolver will wait until the specified regex matches some line of the build output.
     * If the output is not present within two minutes, then {@link TimeoutException} is thrown
     *
     * @param regex Regex a line of the build output should match
     * @return An instance of {@link DaemonBuildTrigger}
     */
    DaemonBuildTrigger withWaitUntilOutputLineMathes(String regex);

    /**
     * Resolver will wait until the specified regex matches some line of the build output.
     * If the output is not present within the given time, then {@link TimeoutException} is thrown
     *
     * @param regex Regex a line of the build output should match
     * @param timeout the maximum time to wait
     * @param timeoutUnit the time unit of the {@code timeout} argument
     * @return An instance of {@link DaemonBuildTrigger}
     */
    DaemonBuildTrigger withWaitUntilOutputLineMathes(String regex, long timeout, TimeUnit timeoutUnit);
}