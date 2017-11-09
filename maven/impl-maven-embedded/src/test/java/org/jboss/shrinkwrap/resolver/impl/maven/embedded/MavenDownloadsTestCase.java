package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.arquillian.spacelift.execution.ExecutionException;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.DistributionStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class MavenDownloadsTestCase {

    private File targetMavenDir = new File(DistributionStageImpl.MAVEN_TARGET_DIR);

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Before
    public void cleanup() throws IOException {
        FileUtils.deleteDirectory(targetMavenDir);
    }

    @Test
    public void testDownloadFromGivenDestinationWithoutUsingCacheAndExtract() {
        // download
        downloadSWRArchive("3.0.0-alpha-1");

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

        // download another version
        downloadSWRArchive("3.0.0-alpha-3");

        verifyExtraction(2, "resolver-3.0.0-alpha-1", "resolver-3.0.0-alpha-3");
    }

    @Test
    public void testDownloadInMultipleThreads() throws InterruptedException {

        // multiple download
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(3);

        createThreadWithDownload(startLatch, stopLatch).start();
        createThreadWithDownload(startLatch, stopLatch).start();
        createThreadWithDownload(startLatch, stopLatch).start();

        startLatch.countDown();
        stopLatch.await(20, TimeUnit.SECONDS);

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

    private Thread createThreadWithDownload(final CountDownLatch startLatch, final CountDownLatch stopLatch) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.await();
                    downloadSWRArchive("3.0.0-alpha-1");
                    stopLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void downloadSWRArchive(String version) {
        String url = String.format("http://github.com/shrinkwrap/resolver/archive/%s.zip", version);
        try {
            EmbeddedMaven
                    .forProject(pathToJarSamplePom)
                    .useDistribution(new URL(url), false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ExecutionException.class)
    public void testFailingDownload() {
        // download from wrong destination
        downloadSWRArchive("3.0.0-alpha-");
    }

    @Test
    public void testDownloadMaven339AndExtractAndCheckCacheIsUsed() {
        // download
        EmbeddedMaven.forProject(pathToJarSamplePom).useMaven3Version("3.3.9");

        verifyDownloadAndExtraction("3.3.9");

        // check if new Dir will be created for different Maven version
        EmbeddedMaven.forProject(pathToJarSamplePom).useMaven3Version("3.1.0");
        verifyExtraction(2, "apache-maven-3.3.9", "apache-maven-3.1.0");
    }

    @Test
    public void testDownloadDefaultMavenAndExtractUsingBuild() {
        // download
        EmbeddedMaven.forProject(pathToJarSamplePom).setGoals("dependency:tree").setShowVersion(true).build();

        assertThat(systemOutRule.getLog()).contains("-> Apache Maven " + DistributionStage.DEFAULT_MAVEN_VERSION);
        verifyDownloadAndExtraction(DistributionStage.DEFAULT_MAVEN_VERSION);
    }

    @Test
    public void testDownloadDefaultMavenAndExtractUsingMethod() {
        // download
        EmbeddedMaven.forProject(pathToJarSamplePom).useDefaultDistribution();

        verifyDownloadAndExtraction(DistributionStage.DEFAULT_MAVEN_VERSION);
    }

    private void verifyDownloadAndExtraction(String version) {
        File binary =
            new File(DistributionStageImpl.MAVEN_CACHE_DIR, String.format("apache-maven-%s-bin.tar.gz", version));

        // verify the download
        assertTrue("the downloaded zip binary should exist", binary.exists());
        assertTrue("the downloaded zip binary should be a file", binary.isFile());

        // verify the extraction
        verifyExtraction(1, "apache-maven-" + version);

        // verify if it uses cache when it tries to download the binaries again
        long lastModified = binary.lastModified();
        EmbeddedMaven.forProject(pathToJarSamplePom).useMaven3Version(version);
        verifyExtraction(1, "apache-maven-" + version);
        assertEquals(lastModified, binary.lastModified());
    }

    @Test
    public void shouldExtractZipInDirWithNameMD5HashOfFile() {
        // download
        downloadSWRArchive("3.0.0-alpha-1");

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

        assertThat(dirsForExtraction)
            .as("there should be " + expectedNumberOfDirs + " dir(s) containing extraction")
            .hasSize(expectedNumberOfDirs);

        ArrayList<String> allExpectedDirNames = new ArrayList<>(Arrays.asList(expectedDirNames));
        for (int i = 0; i < expectedNumberOfDirs; i++) {
            File[] allFiles = dirsForExtraction[i].listFiles();
            assertThat(allFiles).as("there should be one dir with extracted files").hasSize(1);

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
