package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuildTrigger;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.WithTimeoutDaemonBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.WithoutTimeoutDaemonBuilder;

public class DaemonBuildTriggerImpl implements WithTimeoutDaemonBuilder, WithoutTimeoutDaemonBuilder {

    private BuildTrigger buildTrigger;
    private String expectedRegex;
    private long timeout = 2;
    private TimeUnit timeoutUnit = TimeUnit.MINUTES;

    public DaemonBuildTriggerImpl(BuildTrigger buildTrigger) {
        this.buildTrigger = buildTrigger;
    }

    @Override
    public void build() throws TimeoutException {
        int countDown = expectedRegex != null ? 1 : 0;
        final CountDownLatch countDownLatch = new CountDownLatch(countDown);
        new Thread(new Runnable() {
            @Override
            public void run() {
                buildTrigger.build(expectedRegex, countDownLatch);
            }
        }).start();
        try {
            countDownLatch.await(timeout, timeoutUnit);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        if (countDownLatch.getCount() > 0){
            String message = String.format(
                "The expected regex %s haven't matched any line of the build output within the time of %s %s",
                expectedRegex, timeout, timeoutUnit);
            throw new TimeoutException(message);
        }
    }

    @Override
    public DaemonBuildTrigger withWaitUntilOutputLineMathes(String expectedRegex) {
        this.expectedRegex = expectedRegex;
        return this;
    }

    @Override
    public DaemonBuildTrigger withWaitUntilOutputLineMathes(String expectedRegex, long timeout, TimeUnit timeoutUnit) {
        this.expectedRegex = expectedRegex;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        return this;
    }
}
