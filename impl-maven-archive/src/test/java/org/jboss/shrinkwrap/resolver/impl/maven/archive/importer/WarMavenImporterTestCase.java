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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Test;

/**
 * WAR import test case
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class WarMavenImporterTestCase {

    @Test
    public void importWar() {
//        When
        final WebArchive archive = doImport("src/it/war-sample/pom.xml");

//        Then
        AssertArchive.assertContains(archive, "WEB-INF/web.xml");
        AssertArchive.assertNotContains(archive, "file.toExclude");
        AssertArchive.assertNotContains(archive, "file.packagingToExclude");
        AssertArchive.assertNotContains(archive, "file.warSourceToExclude");
        Assert.assertEquals(10, archive.getContent().size());
    }

    @Test
    public void importWarWithIncludes() {
//        When
        final WebArchive archive = doImport("src/it/war-sample/pom-b.xml");

//        Then
        AssertArchive.assertContains(archive, "WEB-INF/web.xml");
        AssertArchive.assertContains(archive, "file.toExclude");
        AssertArchive.assertContains(archive, "file.packagingToExclude");
        AssertArchive.assertContains(archive, "file.warSourceToExclude");
        Assert.assertEquals(13, archive.getContent().size());
    }

    private WebArchive doImport(String pomFile) {
//        When
        WebArchive archive = ShrinkWrap.create(MavenImporter.class).loadPomFromFile(pomFile)
                .importBuildOutput()
                .as(WebArchive.class);

//        Then
        AssertArchive.assertNotContains(archive, ".svn");
        AssertArchive.assertNotContains(archive, "WEB-INF/.svn");

        AssertArchive.assertContains(archive, "WEB-INF/lib/commons-codec-1.7.jar");
        AssertArchive.assertContains(archive, "WEB-INF/classes/test/nested/NestedWarClass.class");
        AssertArchive.assertContains(archive, "WEB-INF/classes/test/WarClass.class");
        AssertArchive.assertContains(archive, "WEB-INF/classes/main.properties");

        return archive;
    }
}
