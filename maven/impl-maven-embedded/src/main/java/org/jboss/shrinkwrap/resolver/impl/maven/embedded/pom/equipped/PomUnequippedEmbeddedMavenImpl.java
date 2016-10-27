package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.PomEquippedEmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.PomUnequippedEmbeddedMaven;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomUnequippedEmbeddedMavenImpl implements PomUnequippedEmbeddedMaven {

    @Override
    public PomEquippedEmbeddedMaven setPom(File pomFile) {
        return new PomEquippedEmbeddedMavenImpl(pomFile);
    }
}
