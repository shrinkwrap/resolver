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
package org.jboss.shrinkwrap.resolver.util;

import java.io.File;
import java.io.IOException;

/**
 * An utility to work with file system.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class FileUtil {

    /**
     * Deletes a directory from file system. It simply ignores non-existing directories
     *
     * @param directory
     *            the directory to be deleted
     * @throws IOException
     *             if the directory cannot be deleted
     */
    public static void removeDirectory(File directory) throws IOException {
        if (directory == null || !directory.exists() || !directory.canWrite() || !directory.canExecute()) {
            return;
        }

        for (File entry : directory.listFiles()) {
            if (entry.isDirectory()) {
                removeDirectory(entry);
            } else if (!entry.delete()) {
                System.gc();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }

                if (!entry.delete()) {
                    throw new IOException("Could not delete file " + entry.getAbsolutePath());
                }
            }
        }

        if (!directory.delete()) {
            throw new IOException("Could not delete directory " + directory.getAbsolutePath());
        }
    }
}