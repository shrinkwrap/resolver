package org.jboss.shrinkwrap.resolver.impl.maven.format;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.spi.format.FormatProcessor;

/**
 * A format processor which returns {@link MavenResolvedArtifact}. As {@link MavenResolvedArtifact} is the default format for
 * Maven resolution, this is a no-op format processor.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class MavenResolvedArtifactProcessor implements FormatProcessor<MavenResolvedArtifact, MavenResolvedArtifact> {

    @Override
    public boolean handles(final Class<?> resolvedTypeClass) {
        return MavenResolvedArtifact.class.isAssignableFrom(resolvedTypeClass);
    }

    @Override
    public boolean returns(final Class<?> returnTypeClass) {
        return MavenResolvedArtifact.class.equals(returnTypeClass);
    }

    @Override
    public MavenResolvedArtifact process(final MavenResolvedArtifact input, final Class<MavenResolvedArtifact> returnType)
            throws IllegalArgumentException {
        // no-op
        return input;
    }

}
