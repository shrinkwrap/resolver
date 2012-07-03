package org.jboss.shrinkwrap.resolver.impl.maven.task;

import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;

public interface MavenWorkingSessionTask {

    MavenWorkingSession execute(MavenWorkingSession session);
}
