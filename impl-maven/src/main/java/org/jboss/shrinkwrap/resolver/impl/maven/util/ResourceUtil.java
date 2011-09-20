package org.jboss.shrinkwrap.resolver.impl.maven.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class ResourceUtil {

    private static final String CLASSPATH_QUALIFIER = "classpath:";
    private static final String FILE_QUALIFIER = "file:";

    public ResourceUtil() {
        throw new UnsupportedOperationException("Instantiation of ResourceUtil is not supported");
    }

    /**
     * Gets a resource from the TCCL and returns its name As resource in classpath.
     *
     * @param resourceName is the name of the resource in the classpath
     * @return the file path for resourceName @see {@link java.net.URL#getFile()}
     * @throws IllegalArgumentException if resourceName doesn't exist in the classpath or privileges are not granted
     */
    private static String getLocalResourcePathFromResourceName(final String resourceName) {
        final URL resourceUrl = SecurityActions.getResource(resourceName);
        Validate.notNull(resourceUrl, resourceName + " doesn't exist or can't be accessed on classpath");

        try {
            File localResource = File.createTempFile("sw_resource", "xml");
            localResource.deleteOnExit();
            IOUtil.copyWithClose(resourceUrl.openStream(), new FileOutputStream(localResource));
            return localResource.getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to open resource name specified by " + resourceName, e);
        }
    }

    public static String resolvePathByQualifier(String path) {
        if (path.startsWith(CLASSPATH_QUALIFIER)) {
            path = getLocalResourcePathFromResourceName(path.replace(CLASSPATH_QUALIFIER, ""));
        } else if (path.startsWith(FILE_QUALIFIER)) {
            path = path.replace(FILE_QUALIFIER, "");
        }
        return path;
    }

}
