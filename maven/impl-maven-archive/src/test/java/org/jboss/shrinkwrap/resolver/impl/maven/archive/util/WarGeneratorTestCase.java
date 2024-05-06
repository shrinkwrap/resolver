/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * A utility to generate artifact wars
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
@RunWith(Parameterized.class)
public class WarGeneratorTestCase {
    private final String name;
    private final Class<?>[] classes;
    private final String[] directories;

    @Parameters
    public static Collection<Object[]> jars() {
        Object[][] data = new Object[][] {
                { "test-war", new Class<?>[] { Object.class, List.class }, new String[] { "html", "jsp" } },
                { "test-war-classifier", new Class<?>[] { Arrays.class }, new String[] { "xhtml", "rf" } } };

        return Arrays.asList(data);
    }

    public WarGeneratorTestCase(String name, Class<?>[] classes, String[] directories) {
        this.name = name;
        this.classes = classes;
        this.directories = directories;
    }

    @Test
    public void createJars() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, name).addClasses(classes).addAsDirectories(directories);

        archive.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

}