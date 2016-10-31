package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class ResolverErrorOutputHandler implements InvocationOutputHandler {

    private final StringBuffer logBuffer;
    private boolean quiet = false;

    public ResolverErrorOutputHandler(StringBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    @Override
    public void consumeLine(String line) {
        if (!quiet) {
            System.err.println("-> " + line);
        }
        logBuffer.append(line).append("\n");
    }

    public void setQuiet(boolean quiet){
        this.quiet = quiet;
    }
}