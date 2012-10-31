package org.jboss.shrinkwrap.resolver.api.maven.formatprocessor;

import org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;

/**
 * A format processor which returns {@link MavenResolvedArtifact}. As {@link MavenResolvedArtifact} is the default format for
 * Maven resolution, this is a no-op format processor.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MavenResolvedArtifactProcessor implements FormatProcessor<MavenResolvedArtifact, MavenResolvedArtifact> {

    @Override
    public boolean handles(Class<?> resolvedTypeClass) {
        return MavenResolvedArtifact.class.isAssignableFrom(resolvedTypeClass);
    }

    @Override
    public boolean returns(Class<?> returnTypeClass) {
        return MavenResolvedArtifact.class.equals(returnTypeClass);
    }

    @Override
    public MavenResolvedArtifact process(MavenResolvedArtifact input, Class<MavenResolvedArtifact> returnType)
            throws IllegalArgumentException {
        // no-op
        return input;
    }

}
