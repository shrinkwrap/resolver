package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.logging.Logger;

import org.apache.maven.model.InputLocation;
import org.apache.maven.model.building.ModelProblem.Severity;
import org.apache.maven.model.building.ModelProblemCollector;

public class LogModelProblemCollector implements ModelProblemCollector {
    private static final Logger log = Logger.getLogger(LogModelProblemCollector.class.getName());

    private boolean hasSevereFailures;

    public LogModelProblemCollector() {
        this.hasSevereFailures = false;
    }

    @Override
    public void add(Severity severity, String message, InputLocation location, Exception cause) {

        switch (severity) {
            case WARNING:
                log.warning(message + ", caused by: " + cause.getMessage());
                break;
            case ERROR:
            case FATAL:
                log.severe(message + ", caused by: " + cause.getMessage());
                this.hasSevereFailures = true;
                break;
        }

    }

    public boolean hasSevereFailures() {
        return hasSevereFailures;
    }

}
