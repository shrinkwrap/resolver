package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class ResolverOutputHandler extends AbstractOutputHandler {

    public ResolverOutputHandler(StringBuffer logBuffer, String expectedRegex, CountDownLatch countDownLatch) {
        super(logBuffer, expectedRegex, countDownLatch);
    }

    public ResolverOutputHandler(StringBuffer logBuffer) {
        super(logBuffer);
    }

    @Override
    protected void printLine(String line) {
        System.out.println(line);
    }
}
