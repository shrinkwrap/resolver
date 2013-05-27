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
package org.jboss.shrinkwrap.resolver.impl.maven.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Simple shared utility to convert {@link URL} instances to {@link File} representation
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public enum FileUtil {

    INSTANCE;

    /**
     * Creates a {@link File} representation from the given path in the specified {@link ClassLoader}, ensuring
     *
     * @return
     */
    public File fileFromClassLoaderResource(final String path, final ClassLoader cl) {
        assert path != null && path.length() > 0 : "path must be specified";
        assert cl != null : "ClassLoader is required";
        final URL url = cl.getResource(path);
        Validate.notNull(url, path + " doesn't exist or can't be accessed on from " + cl);
        // Safe file conversion adapted via: http://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html
        File file = null;
        try {
            file = new File(url.toURI());
            Validate.readable(file, "Should be readable");
        } catch (final IllegalArgumentException iae) {
            try {
                file = new File(url.getPath());
                Validate.readable(file, "Should be readable");
            } catch (final RuntimeException re) {
                file = null;
                // We'll deal with this later
            }
        } catch (final URISyntaxException e) {
            try {
                file = new File(url.getPath());
            } catch (final RuntimeException re) {
                // We'll deal with this later
                file = null;
            }
        }
        // Likely at this point we could obtain the resource, but it's nested in a JAR where we can't get it as a File,
        // so..
        if (file == null) {
            // Copy and retrieve from a tmp file
            try {
                final String localResourcePath = this.getLocalResourcePathFromResourceName(path, cl);
                file = new File(localResourcePath);
            } catch (final RuntimeException re) {
                // OK, give up
                throw new IllegalArgumentException("Resource + " + path + " in " + cl + " points to " + url.toString()
                        + ", and cannot be resolved as a " + File.class.getName());
            }
        }
        return file;
    }

    /**
     * Gets a resource from the TCCL and returns its name as resource in classpath.
     *
     * @param resourceName
     * is the name of the resource in the classpath
     * @return the file path for resourceName @see {@link java.net.URL#getFile()}
     * @throws IllegalArgumentException
     * if resourceName doesn't exist in the classpath or privileges are not granted
     */
    private String getLocalResourcePathFromResourceName(final String resourceName, final ClassLoader cl) {
        final URL resourceUrl = cl.getResource(resourceName);
        Validate.notNull(resourceUrl, resourceName + " doesn't exist or can't be accessed on classpath");

        try {
            File localResource = temporaryFile(resourceName);
            System.out.println(localResource.getAbsolutePath());
            localResource.deleteOnExit();
            IOUtil.copyWithClose(resourceUrl.openStream(), new FileOutputStream(localResource));
            return localResource.getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to open resource name specified by " + resourceName, e);
        }

    }

    private File temporaryFile(final String resourceName) {
        File tmpDir = new File(SecurityActions.getProperty("java.io.tmpdir"));

        Validate.writeableDirectory(tmpDir.getAbsolutePath(),
                "Unable to access temporary directory at " + tmpDir.getAbsolutePath());

        File localResource = new File(tmpDir, resourceName.replaceAll("/", "-").replaceAll("\\\\", "-")
                .replaceAll(File.pathSeparator, "-").replaceAll("\\s", "-"));
        localResource.deleteOnExit();
        return localResource;
    }
}
