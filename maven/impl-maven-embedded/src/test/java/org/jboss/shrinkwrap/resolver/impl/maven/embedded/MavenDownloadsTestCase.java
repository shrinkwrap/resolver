package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Test;

import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.FilterDirWithMd5Hash.mavenBinaryZipMd5HashFile;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class MavenDownloadsTestCase {

    private File targetMavenDir = new File("target" + File.separator + "resolver-maven");

    @Test
    public void testDownloadFromGivenDestinationWithoutUsingCacheAndExtract() throws IOException {
        // cleanup
        FileUtils.deleteDirectory(targetMavenDir);

        // download
        EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .useDistribution(new URL("http://github.com/shrinkwrap/resolver/archive/3.0.0-alpha-1.zip"), false);

        // verify download
        assertTrue("the target directory should be present.", targetMavenDir.exists());

        File downloaded = new File(targetMavenDir + File.separator + "downloaded");
        assertTrue("the downloaded directory should be present.", downloaded.exists());

        File[] downloadedDirs = downloaded.listFiles();
        assertEquals("the downloaded directory should contain one directory", 1, downloadedDirs.length);

        File[] files = downloadedDirs[0].listFiles();
        assertEquals("the downloaded files should contain one file", 1, files.length);

        File file = files[0];
        System.out.println(file);
        assertTrue("the file should be a file", file.isFile());
        assertEquals("the file should have the name 3.0.0-alpha-1.zip", "3.0.0-alpha-1.zip", file.getName());

        // verify the extraction
        verifyExtraction(1, "resolver-3.0.0-alpha-1");
    }

    @Test
    public void testDownloadMaven339AndExtractAndCheckCacheIsUsed() throws IOException {
        // prepare and cleanup
        String mavenCacheDir =
            System.getProperty("user.home") + File.separator
                + ".arquillian" + File.separator
                + "resolver" + File.separator
                + "maven";
        File binary = new File(mavenCacheDir + File.separator + "apache-maven-3.3.9-bin.tar.gz");
        binary.delete();
        FileUtils.deleteDirectory(targetMavenDir);

        // download
        EmbeddedMaven.forProject(pathToJarSamplePom).useMaven3Version("3.3.9");

        // verify the download
        assertTrue("the downloaded zip binary should exist", binary.exists());
        assertTrue("the downloaded zip binary should be a file", binary.isFile());

        // verify the extraction
        verifyExtraction(1, "apache-maven-3.3.9");

        // verify if it uses cache when it tries to download the binaries again
        long lastModified = binary.lastModified();
        EmbeddedMaven.forProject(pathToJarSamplePom).useMaven3Version("3.3.9");
        verifyExtraction(1, "apache-maven-3.3.9");
        assertEquals(lastModified, binary.lastModified());

        // check if new UUID dir will be created for different Maven version
        EmbeddedMaven.forProject(pathToJarSamplePom).useMaven3Version("3.1.0");
        verifyExtraction(2, "apache-maven-3.3.9", "apache-maven-3.1.0");
    }

    private void verifyExtraction(int expectedNumberOfDirs, String... expectedDirNames) {
        File[] dirsForExtraction = targetMavenDir.listFiles(new FileFilter() {
            @Override public boolean accept(File file) {
                return !file.getName().equals("downloaded");
            }
        });

        assertEquals("there should be " + expectedNumberOfDirs + " dir(s) containing extraction",
                     expectedNumberOfDirs, dirsForExtraction.length);

        ArrayList<String> allExpectedDirNames = new ArrayList<>(Arrays.asList(expectedDirNames));
        for (int i = 0; i < expectedNumberOfDirs; i++) {
            File[] allFiles = dirsForExtraction[i].listFiles();
            assertEquals("there should be one dir with extracted files and one md5 hash file in the target maven dir",
                         2, allFiles.length);

            File[] extractedDir = dirsForExtraction[i].listFiles(new FileFilter() {
                @Override public boolean accept(File file) {
                    return file.isDirectory();
                }
            });
            assertTrue("the name of the extracted dir has to be in the list of expected names: " + allExpectedDirNames,
                       allExpectedDirNames.remove(extractedDir[0].getName()));

            File[] hashFile = dirsForExtraction[i].listFiles(new FileFilter() {
                @Override public boolean accept(File file) {
                    return file.isFile();
                }
            });
            assertEquals("the name of the hash file has to be " + mavenBinaryZipMd5HashFile, mavenBinaryZipMd5HashFile,
                         hashFile[0].getName());
        }
    }

}
