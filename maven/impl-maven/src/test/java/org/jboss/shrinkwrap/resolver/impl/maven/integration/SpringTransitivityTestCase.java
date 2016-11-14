/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.hamcrest.CoreMatchers;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;

/**
 * This is a reproducer for SHRINKRES-232 - Resolve from pom.xml: old version is picked
 *
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class SpringTransitivityTestCase {

    @Test
    public void testVersionOfAOP() {
        File[] libs =
            Maven.resolver().loadPomFromFile("target/poms/test-spring.xml").importRuntimeAndTestDependencies().resolve()
                .withTransitivity().asFile();

        boolean found = false;
        for (File file : libs){
            if (file.getName().startsWith("spring-aop")) {
                Assert.assertThat(file.getName(), CoreMatchers.containsString("4.2.1.RELEASE"));
                found = true;
                break;
            }
        }

        Assert.assertTrue("The transitive dependency spring-aop should have been found", found);
    }
}
