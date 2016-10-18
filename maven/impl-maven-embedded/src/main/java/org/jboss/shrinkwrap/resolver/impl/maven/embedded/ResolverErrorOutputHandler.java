package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class ResolverErrorOutputHandler implements InvocationOutputHandler {

    private final StringBuffer logBuffer;

    public ResolverErrorOutputHandler(StringBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    @Override
    public void consumeLine(String line) {
        System.err.println("-> " + line);
        logBuffer.append(line).append("\n");
    }
}