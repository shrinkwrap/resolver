package org.jboss.shrinkwrap.resolver.impl.maven;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Obtains the {@link Thread} Context {@link ClassLoader}; should be used inside an
 * {@link AccessController#doPrivileged(PrivilegedAction)} block.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public enum GetTcclAction implements PrivilegedAction<ClassLoader> {
    INSTANCE;

    @Override
    public ClassLoader run() {
        return Thread.currentThread().getContextClassLoader();
    }
}