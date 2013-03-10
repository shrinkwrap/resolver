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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.importer;

import junit.framework.Assert;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Test;

/**
 * JAR import test case
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class JarMavenImporterTestCase {

    @Test
    public void importJar() {
        //        When
        final Archive archive = doImport("src/it/jar-sample/pom.xml");

        //        Then
        AssertArchive.assertContains(archive, "main.properties");
        AssertArchive.assertNotContains(archive, "file.toExclude");
        Assert.assertEquals(5, archive.getContent().size());
    }

    @Test
    public void importJarWithIncludes() {
        //        When
        final Archive archive = doImport("src/it/jar-sample/pom-b.xml");

        //        Then
        AssertArchive.assertNotContains(archive, "main.properties");
        AssertArchive.assertContains(archive, "file.toExclude");
        Assert.assertEquals(5, archive.getContent().size());
    }

    private Archive doImport(String pomFile) {
        //        When
        WebArchive archive = ShrinkWrap.create(MavenImporter.class).loadPomFromFile(pomFile)
                .importBuildOutput()
                .as(WebArchive.class);

        //        Then
        AssertArchive.assertNotContains(archive, ".svn");
        AssertArchive.assertNotContains(archive, "WEB-INF/.svn");

        return archive;
    }


}
