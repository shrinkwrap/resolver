package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a reproducer for <a href="https://issues.redhat.com/browse/SHRINKRES-232">SHRINKRES-232</a> - Resolve from pom.xml: old version is picked
 *
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
class SpringTransitivityTestCase {

    @Test
    void testVersionOfAOP() {
        File[] libs =
            Maven.resolver().loadPomFromFile("target/poms/test-spring.xml")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        boolean found = false;
        for (File file : libs){
            if (file.getName().startsWith("spring-aop")) {
                Assertions.assertTrue(file.getName().contains("4.2.1.RELEASE"));
                found = true;
                break;
            }
        }

        Assertions.assertTrue(found, "The transitive dependency spring-aop should have been found");
    }
}
