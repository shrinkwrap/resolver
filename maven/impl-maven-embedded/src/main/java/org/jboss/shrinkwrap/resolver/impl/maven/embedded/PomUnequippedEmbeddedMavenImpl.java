package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.PomEquippedEmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.PomUnequippedEmbeddedMaven;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomUnequippedEmbeddedMavenImpl implements PomUnequippedEmbeddedMaven {

    @Override
    public PomEquippedEmbeddedMaven setPom(File pomFile) {
        return new PomEquippedEmbeddedMavenImpl(pomFile);
    }
}
