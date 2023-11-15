package org.jboss.shrinkwrap.resolver.impl.maven.embedded.daemon;

import java.util.concurrent.CountDownLatch;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuild;
import org.jboss.shrinkwrap.resolver.impl.maven.embedded.BuildTrigger;

public class DaemonBuildImpl implements DaemonBuild {

    private Thread daemonBuildThread;
    private DaemonRunnable daemonRunnable;

    public DaemonBuildImpl(BuildTrigger buildTrigger){
        daemonRunnable = new DaemonRunnable(buildTrigger, null);
    }

    public DaemonBuildImpl(BuildTrigger buildTrigger, String expectedRegex){
        daemonRunnable = new DaemonRunnable(buildTrigger, expectedRegex);
    }

    public boolean isAlive(){
        return daemonBuildThread.isAlive();
    }

    public BuiltProject getBuiltProject(){
        if (isAlive()){
            return null;
        } else {
            return daemonRunnable.getBuiltProject();
        }
    }

    public String getExpectedRegex() {
        return daemonRunnable.getExpectedRegex();
    }

    DaemonBuild build(CountDownLatch countDownLatch) {
        daemonRunnable.setCountDownLatch(countDownLatch);
        return build();
    }

    DaemonBuild build() {
        daemonBuildThread = new Thread(daemonRunnable);
        daemonBuildThread.start();
        return this;
    }

    private static class DaemonRunnable implements Runnable {

        private CountDownLatch countDownLatch;
        private String expectedRegex;
        private BuildTrigger buildTrigger;
        private BuiltProject builtProject;

        DaemonRunnable(BuildTrigger buildTrigger, String expectedRegex){
            this.buildTrigger = buildTrigger;
            this.expectedRegex = expectedRegex;
        }

        @Override
        public void run() {
            builtProject = buildTrigger.build(expectedRegex, countDownLatch);
        }

        BuiltProject getBuiltProject() {
            return builtProject;
        }

        void setCountDownLatch(CountDownLatch countDownLatch){
            this.countDownLatch = countDownLatch;
        }

        String getExpectedRegex() {
            return expectedRegex;
        }
    }
}
