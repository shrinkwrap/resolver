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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

/**
 * Sets a set of files and checks that returned files start with the same names. This means no exclusion is needed which
 * defining a collection of required files.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class FileValidationUtil {

    private String[] files;

    private Map<String, Boolean> flags;

    public FileValidationUtil(String... allowedFiles) {
        this.files = allowedFiles;
        this.flags = new HashMap<String, Boolean>(files.length);
        for (String file : allowedFiles) {
            flags.put(file, Boolean.FALSE);
        }
    }

    public void validate(File[] array) throws AssertionError {
        Assert.assertEquals("There must total " + files.length + " files resolved", files.length, array.length);

        for (File f : array) {
            for (String fname : files) {
                if (f.getName().startsWith(fname)) {
                    flags.put(fname, Boolean.TRUE);
                }
            }
        }

        StringBuilder sb = new StringBuilder("Missing files were:\n");
        boolean success = true;

        for (Map.Entry<String, Boolean> entry : flags.entrySet()) {
            if (!entry.getValue()) {
                success = false;
                sb.append(entry.getKey()).append("\n");
            }
        }

        if (!success) {
            throw new AssertionError(sb.toString());
        }
    }
}