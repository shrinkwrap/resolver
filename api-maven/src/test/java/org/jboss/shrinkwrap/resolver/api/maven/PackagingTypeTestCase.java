package org.jboss.shrinkwrap.resolver.api.maven;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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

import org.junit.Assert;
import org.junit.Test;

/**
 * Ensures that the {@link PackagingType#toString()} contracts are as expected
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class PackagingTypeTestCase {

    @Test
    public void pom() {
        Assert.assertEquals("pom", PackagingType.POM.toString());
    }

    @Test
    public void jar() {
        Assert.assertEquals("jar", PackagingType.JAR.toString());
    }

    @Test
    public void mavenPlugin() {
        Assert.assertEquals("maven-plugin", PackagingType.MAVEN_PLUGIN.toString());
    }

    @Test
    public void ejb() {
        Assert.assertEquals("ejb", PackagingType.EJB.toString());
    }

    @Test
    public void war() {
        Assert.assertEquals("war", PackagingType.WAR.toString());
    }

    @Test
    public void ear() {
        Assert.assertEquals("ear", PackagingType.EAR.toString());
    }

    @Test
    public void rar() {
        Assert.assertEquals("rar", PackagingType.RAR.toString());
    }

    @Test
    public void par() {
        Assert.assertEquals("par", PackagingType.PAR.toString());
    }

}
