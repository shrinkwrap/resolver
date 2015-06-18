package org.jboss.shrinkwrap.resolver.impl.maven;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipFile;

/**
 *
 * @author <a href="mailto:olivts@free.fr">Olivier Spieser</a>
 *
 */
public class PackageDirHelperTestCase {

    /**
     * Test zip archive creation from directory. Check if directory entries are added to the archive.
     */
    @Test
    public void testPackageDirectories() throws IOException {
        File outputFile = File.createTempFile("testPackageDirectories-" + (new Date().getTime()), ".zip");
        File inputFiles = new File ("target/test-classes/test-package/classes");

        MavenResolvedArtifactImpl.PackageDirHelper.packageDirectories(outputFile, inputFiles);

        ZipFile zipFile = new ZipFile(outputFile);

        //Check if existing files and folders and in zip.
        Assert.assertNotNull(zipFile.getEntry("b/c"));
        Assert.assertNotNull(zipFile.getEntry("a/a.file"));

        //Check if non existing items are null !
        Assert.assertNull(zipFile.getEntry("a/non-exist/"));
    }
}