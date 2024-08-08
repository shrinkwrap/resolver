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
import java.util.List;
import java.util.stream.Stream;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A utility to generate artifact wars
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
class WarGeneratorTestCase {

    @MethodSource("jars")
    @ParameterizedTest
    void createJars(String name, Class<?>[] classes, String[] directories) {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, name)
                .addClasses(classes)
                .addAsDirectories(directories);

        archive.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    private static Stream<Object[]> jars() {
        return Stream.of(
                new Object[]{"test-war", new Class<?>[]{Object.class, List.class}, new String[]{"html", "jsp"}},
                new Object[]{"test-war-classifier", new Class<?>[]{Arrays.class}, new String[]{"xhtml", "rf"}}
        );
    }
}
