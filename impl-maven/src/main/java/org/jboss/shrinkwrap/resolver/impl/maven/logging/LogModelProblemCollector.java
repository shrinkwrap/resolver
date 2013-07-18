/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven.logging;

import java.util.logging.Logger;

import org.apache.maven.model.InputLocation;
import org.apache.maven.model.building.ModelProblem.Severity;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;

/**
 * {@link ModelProblemCollector} implementation which pipes {@link Exception}s to the log.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class LogModelProblemCollector implements ModelProblemCollector {
    private static final Logger log = Logger.getLogger(LogModelProblemCollector.class.getName());

    private boolean hasSevereFailures;

    public LogModelProblemCollector() {
        this.hasSevereFailures = false;
    }

    @Override
    public void add(ModelProblemCollectorRequest modelProblemCollectorRequest) {


        switch (modelProblemCollectorRequest.getSeverity()) {
            case WARNING:
                log.warning(modelProblemCollectorRequest.getMessage() + ", caused by: " + modelProblemCollectorRequest.getException().getMessage());
                break;
            case ERROR:
            case FATAL:
                log.severe(modelProblemCollectorRequest.getMessage() + ", caused by: " + modelProblemCollectorRequest.getException().getMessage());
                this.hasSevereFailures = true;
                break;
        }
    }

    public boolean hasSevereFailures() {
        return hasSevereFailures;
    }

}
