package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuild;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuildTriggerWithTimeout;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuildTriggerWithoutTimeout;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.WithTimeoutDaemonBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.WithoutTimeoutDaemonBuilder;

public class DaemonBuildTriggerImpl implements WithTimeoutDaemonBuilder, DaemonBuildTriggerWithoutTimeout,
    WithoutTimeoutDaemonBuilder {

    private BuildTrigger buildTrigger;

    public DaemonBuildTriggerImpl(BuildTrigger buildTrigger) {
        this.buildTrigger = buildTrigger;
    }

    @Override
    public DaemonBuild build() {
        return new DaemonBuildImpl(buildTrigger).build();
    }

    @Override
    public DaemonBuildTriggerWithTimeout withWaitUntilOutputLineMathes(String expectedRegex) {
        return new DaemonBuildTriggerWithTimeoutImpl(new DaemonBuildImpl(buildTrigger, expectedRegex));
    }

    @Override
    public DaemonBuildTriggerWithTimeout withWaitUntilOutputLineMathes(String expectedRegex, long timeout,
        TimeUnit timeoutUnit) {
        return new DaemonBuildTriggerWithTimeoutImpl(new DaemonBuildImpl(buildTrigger, expectedRegex), timeout, timeoutUnit);
    }

    class DaemonBuildTriggerWithTimeoutImpl implements DaemonBuildTriggerWithTimeout {

        private DaemonBuildImpl daemonBuild;
        private long timeout = 2;
        private TimeUnit timeoutUnit = TimeUnit.MINUTES;

        DaemonBuildTriggerWithTimeoutImpl(DaemonBuildImpl daemonBuild) {
            this.daemonBuild = daemonBuild;
        }

        DaemonBuildTriggerWithTimeoutImpl(DaemonBuildImpl daemonBuild, long timeout, TimeUnit timeoutUnit) {
            this.daemonBuild = daemonBuild;
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
        }

        @Override
        public DaemonBuild build() throws TimeoutException {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            DaemonBuild daemonBuild = this.daemonBuild.build(countDownLatch);
            try {
                countDownLatch.await(timeout, timeoutUnit);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            if (countDownLatch.getCount() > 0) {
                String message = String.format(
                    "The expected regex %s haven't matched any line of the build output within the time of %s %s",
                    this.daemonBuild.getExpectedRegex(), timeout, timeoutUnit);
                throw new TimeoutException(message);
            }
            return daemonBuild;
        }
    }
}
