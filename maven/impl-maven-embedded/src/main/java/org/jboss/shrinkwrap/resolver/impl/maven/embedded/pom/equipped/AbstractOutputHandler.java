package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

public abstract class AbstractOutputHandler implements InvocationOutputHandler {

    private final StringBuffer logBuffer;
    private Pattern expectedPattern;
    private CountDownLatch countDownLatch;
    private boolean quiet = false;

    public AbstractOutputHandler(StringBuffer logBuffer, String expectedRegex, CountDownLatch countDownLatch) {
        this.logBuffer = logBuffer;
        if (expectedRegex != null) {
            expectedPattern = Pattern.compile(expectedRegex);
            this.countDownLatch = countDownLatch;
        }
    }

    public AbstractOutputHandler(StringBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    @Override
    public void consumeLine(String line) {
        if (!quiet) {
            printLine("-> " + line);
        }
        if (expectedPattern == null) {
            logBuffer.append(line).append("\n");

        } else if (countDownLatch != null && countDownLatch.getCount() > 0 && !Validate.isNullOrEmpty(line)) {
            if (line.matches(expectedPattern.toString())) {
                countDownLatch.countDown();
            }
        }
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    protected abstract void printLine(String line);
}
