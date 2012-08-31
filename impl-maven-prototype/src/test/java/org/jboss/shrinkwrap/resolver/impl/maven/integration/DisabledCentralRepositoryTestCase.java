package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Test;

/**
 * @author <a href="mailto:tommy.tynja@diabol.se">Tommy Tynj&auml;</a>
 */
public class DisabledCentralRepositoryTestCase {

    /**
     * Tests the disabling of the Maven central repository
     */
    @Test
    public void shouldHaveCentralMavenRepositoryDisabled() {

        File[] files = Resolvers.use(MavenResolverSystem.class).configureFromPom("target/poms/test-child.xml")
            .importDefinedDependencies().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child.tree"),
            ScopeType.COMPILE).validate(files);

        Assert.fail("There is no way how to disable Maven Central, do we still need it?");
    }

}
