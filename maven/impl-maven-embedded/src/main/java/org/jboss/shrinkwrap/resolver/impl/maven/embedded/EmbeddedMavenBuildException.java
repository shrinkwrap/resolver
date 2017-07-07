package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;

public class EmbeddedMavenBuildException extends RuntimeException {

    private final BuiltProject builtProject;

    public EmbeddedMavenBuildException(String message, Throwable cause, BuiltProject builtProject) {
        super(message, cause);
        this.builtProject = builtProject;
    }

    public BuiltProject getBuiltProject() {
        return builtProject;
    }
}
