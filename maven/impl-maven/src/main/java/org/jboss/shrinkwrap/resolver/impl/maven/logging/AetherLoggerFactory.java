/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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

import java.util.logging.Level;

import org.eclipse.aether.spi.log.LoggerFactory;

/**
 * A delegating logging factory, that uses Java Util Logging to log messages from Aether. It logs debug output using FINE level,
 * whereas WARNING level is used for warning output.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class AetherLoggerFactory implements LoggerFactory {

    @Override
    public org.eclipse.aether.spi.log.Logger getLogger(String name) {
        return new Logger(java.util.logging.Logger.getLogger(name));
    }

    public static class Logger implements org.eclipse.aether.spi.log.Logger {

        private final java.util.logging.Logger log;

        public Logger(java.util.logging.Logger logger) {
            this.log = logger;
        }

        @Override
        public boolean isDebugEnabled() {
            return log.isLoggable(Level.FINE);
        }

        @Override
        public void debug(String msg) {
            log.log(Level.FINE, msg);
        }

        @Override
        public void debug(String msg, Throwable error) {
            log.log(Level.FINE, msg, error);
        }

        @Override
        public boolean isWarnEnabled() {
            return log.isLoggable(Level.WARNING);
        }

        @Override
        public void warn(String msg) {
            log.log(Level.WARNING, msg);
        }

        @Override
        public void warn(String msg, Throwable error) {
            log.log(Level.WARNING, msg, error);
        }
    }

}
