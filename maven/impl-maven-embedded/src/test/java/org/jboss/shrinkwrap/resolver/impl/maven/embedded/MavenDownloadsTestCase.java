package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import org.apache.commons.io.FileUtils;
import org.arquillian.spacelift.execution.ExecutionException;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class MavenDownloadsTestCase {

    private File targetMavenDir = new File("target" + File.separator + "resolver-maven");

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void testDownloadFromGivenDestinationWithoutUsingCacheAndExtract() throws IOException {
        // cleanup
        FileUtils.deleteDirectory(targetMavenDir);

        // download
        downloadSWR300Alpha1();

        // verify download
        assertTrue("the target directory should be present.", targetMavenDir.exists());

        File downloaded = new File(targetMavenDir + File.separator + "downloaded");
        assertTrue("the downloaded directory should be present.", downloaded.exists());

        File[] downloadedFiles = downloaded.listFiles();
        assertEquals("the downloaded directory should contain one file", 1, downloadedFiles.length);

        File file = downloadedFiles[0];
        assertTrue("the file should be a file", file.isFile());
        assertEquals("the file should have the name 3.0.0-alpha-1.zip", "3.0.0-alpha-1.zip", file.getName());

        // verify the extraction
        verifyExtraction(1, "resolver-3.0.0-alpha-1");

        EmbeddedMaven
                .forProject(pathToJarSamplePom)
                .useDistribution(new URL("http://github.com/shrinkwrap/resolver/archive/3.0.0-alpha-3.zip"), false);

        verifyExtraction(2, "resolver-3.0.0-alpha-1", "resolver-3.0.0-alpha-3");
    }

    @Test
    public void testDownloadInMultipleThreads() throws IOException, InterruptedException {
        // cleanup
        FileUtils.deleteDirectory(targetMavenDir);
        final CountDownLatch latch = new CountDownLatch(1);

        // multiple download
        createThreadWithDownload(latch).start();
        createThreadWithDownload(latch).start();
        latch.countDown();
        downloadSWR300Alpha1();

        // verify
        String expMsg = "Resolver: downloading Maven binaries from";
        Matcher matcher = Pattern.compile(expMsg).matcher(systemOutRule.getLog());
        assertThat(matcher.find()).as(String.format(
                "The log should contain one occurrence of message \"%s\" but none was found. For more information see the log", expMsg))
                                  .isTrue();
        assertThat(matcher.find()).as(String.format(
                "The log should contain only one occurrence of message \"%s\" but more than one was found. For more information see the log",
                expMsg)).isFalse();
    }

    private Thread createThreadWithDownload(final CountDownLatch latch) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downloadSWR300Alpha1();
            }
        });
    }

    private void downloadSWR300Alpha1() {
        try {
            EmbeddedMaven
                    .forProject(pathToJarSamplePom)
                    .useDistribution(new URL("http://github.com/shrinkwrap/resolver/archive/3.0.0-alpha-1.zip"), false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ExecutionException.class)
    public void testFailingDownload() throws IOException {
        // cleanup
        FileUtils.deleteDirectory(targetMavenDir);

        // download from wrong destination
        EmbeddedMaven
            .forProject(pathToJarSamplePom)
            .useDistribution(new URL("http://github.com/shrinkwrap/resolver/archive/3.0.0-alpha-.zip"), false);
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

        // check if new Dir will be created for different Maven version
        EmbeddedMaven.forProject(pathToJarSamplePom).useMaven3Version("3.1.0");
        verifyExtraction(2, "apache-maven-3.3.9", "apache-maven-3.1.0");
    }

    @Test
    public void shouldExtractZipInDirWithNameMD5HashOfFile() throws IOException {
        // cleanup
        FileUtils.deleteDirectory(targetMavenDir);

        // download
        EmbeddedMaven
                .forProject(pathToJarSamplePom)
                .useDistribution(new URL("http://github.com/shrinkwrap/resolver/archive/3.0.0-alpha-1.zip"), false);


        String expectedDir = targetMavenDir + File.separator + "bcec5b9abbc8837dd7b62c673e312882";

        File file = new File(expectedDir);
        assertTrue("the bcec5b9abbc8837dd7b62c673e312882 directory should be present.", file.exists());
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
            assertEquals("there should be one dir with extracted files",
                         1, allFiles.length);

            File[] extractedDir = dirsForExtraction[i].listFiles(new FileFilter() {
                @Override public boolean accept(File file) {
                    return file.isDirectory();
                }
            });
            assertTrue("the name of the extracted dir has to be in the list of expected names: " + allExpectedDirNames,
                       allExpectedDirNames.remove(extractedDir[0].getName()));

        }
    }


}
