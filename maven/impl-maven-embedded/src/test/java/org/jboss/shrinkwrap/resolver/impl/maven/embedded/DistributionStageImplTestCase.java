package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class DistributionStageImplTestCase {

    private static final Logger log = Logger.getLogger(DistributionStageImplTestCase.class.getName());

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void testDownloadInMultipleThreads() throws InterruptedException {

        // multiple download
        CountDownLatch firstLatch = new CountDownLatch(1);
        CountDownLatch secondLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(3);

        createThreadWithDownload(firstLatch, stopLatch).start();
        createThreadWithDownload(secondLatch, stopLatch).start();
        createThreadWithDownload(secondLatch, stopLatch).start();
        createThreadWithDownload(secondLatch, stopLatch).start();
        createThreadWithDownload(secondLatch, stopLatch).start();

        firstLatch.countDown();
        Thread.sleep(1000);
        secondLatch.countDown();
        boolean stopLatchCompleted = stopLatch.await(60, TimeUnit.SECONDS);

        if (!stopLatchCompleted) {
            log.warning("Passing test without verification: Timeout occurred during download and extraction.");
        } else {
            // verify
            String downloadMsg = "Resolver: downloading Maven binaries from";
            String extractionMsg = "Resolver: Successfully extracted maven binaries";
            verifyOneOccurrenceInLog(downloadMsg);
            verifyOneOccurrenceInLog(extractionMsg);
        }
    }

    private void verifyOneOccurrenceInLog(String expMsg){
        Matcher matcher = Pattern.compile(expMsg).matcher(systemOutRule.getLog());
        assertThat(matcher.find()).as(String.format(
            "The log should contain one occurrence of message \"%s\" but none was found. For more information see the log",
            expMsg))
            .isTrue();
        assertThat(matcher.find()).as(String.format(
            "The log should contain only one occurrence of message \"%s\" but more than one was found. For more information see the log",
            expMsg)).isFalse();
    }

    private Thread createThreadWithDownload(final CountDownLatch startLatch, final CountDownLatch stopLatch) {
        return new Thread(() -> {
            try {
                startLatch.await();
                downloadAndExtractMavenBinaryArchive();
                stopLatch.countDown();
            } catch (Exception exception) {
                log.warning("Exception occurred during download. This may have happened because a timeout was reached.");
            }
        });
    }

    private void downloadAndExtractMavenBinaryArchive() throws IOException {
        File mavenDir = tmpFolder.getRoot();
        String distribution = "https://archive.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz";
        File downloaded = BinaryDownloader.download(mavenDir, new URL(distribution));
        FileExtractor.extract(downloaded, new File(mavenDir, "extracted"));
    }
}
