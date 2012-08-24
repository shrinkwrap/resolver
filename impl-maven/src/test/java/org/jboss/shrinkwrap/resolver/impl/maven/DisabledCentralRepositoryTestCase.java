package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;

/**
 * @author <a href="mailto:tommy.tynja@diabol.se">Tommy Tynj&auml;</a>
 */
public class DisabledCentralRepositoryTestCase {

    /**
     * Tests the disabling of the Maven central repository
     */
    @Test
    public void shouldHaveCentralMavenRepositoryDisabled() throws Exception {
        String name = "disabledCentralRepoArchive.war";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name).addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class).disableMavenCentral()
                .loadEffectivePom("target/poms/test-child.xml").importAllDependencies()
                .artifact("org.jboss.shrinkwrap.test:test-child:1.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
            "src/test/resources/dependency-trees/test-child.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name), true);
    }

}
