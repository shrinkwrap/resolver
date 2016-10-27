package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class ResolverOutputHandler implements InvocationOutputHandler {

    private final StringBuffer logBuffer;

    public ResolverOutputHandler(StringBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    @Override
    public void consumeLine(String line) {
        System.out.println("-> " + line);
        logBuffer.append(line).append("\n");
    }
}
