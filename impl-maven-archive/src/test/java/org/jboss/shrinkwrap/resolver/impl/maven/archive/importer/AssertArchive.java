package org.jboss.shrinkwrap.resolver.impl.maven.archive.importer;

import junit.framework.Assert;
import org.jboss.shrinkwrap.api.Archive;

public final class AssertArchive {

    private AssertArchive() {
    }

    public static void assertContains(Archive archive, String path) {
        Assert.assertTrue(path + " should be included in archive " + archive.toString(true), archive.contains(path));
    }

    public static void assertNotContains(Archive archive, String path) {
        Assert.assertFalse(path + " should NOT be included in archive " + archive.toString(true), archive.contains(path));
    }
}
