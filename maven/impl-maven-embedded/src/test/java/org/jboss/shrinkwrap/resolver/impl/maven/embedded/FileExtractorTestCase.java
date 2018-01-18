package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.DistributionStageImpl.MAVEN_TARGET_DIR;
import static org.jboss.shrinkwrap.resolver.impl.maven.embedded.Utils.pathToJarSamplePom;

public class FileExtractorTestCase {

   @Rule
   public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
   private File targetMavenDir = new File(MAVEN_TARGET_DIR);

   @Before
   public void cleanup() throws IOException {
      FileUtils.deleteDirectory(targetMavenDir);
      targetMavenDir.mkdirs();
   }

   @Test
   public void testDownloadDefaultMavenAndExtractUsingMultipleThreads() throws IOException, InterruptedException {
      // download once
      final URL mavenDistribution =
         new URL("https://archive.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz");
      final DistributionStageImpl embeddedMaven = (DistributionStageImpl) EmbeddedMaven.forProject(pathToJarSamplePom);
      final File downloaded = embeddedMaven.download(targetMavenDir, mavenDistribution);

      final FileExtractor fileExtractor =
         new FileExtractor(downloaded, Paths.get(MAVEN_TARGET_DIR, "948110de4aab290033c23bf4894f7d9a").toFile());
      // multiple threads are extracting
      CountDownLatch firstLatch = new CountDownLatch(1);
      CountDownLatch secondLatch = new CountDownLatch(1);
      CountDownLatch stopLatch = new CountDownLatch(3);

      createThreadWithExtract(firstLatch, stopLatch, fileExtractor).start();
      createThreadWithExtract(secondLatch, stopLatch, fileExtractor).start();
      createThreadWithExtract(secondLatch, stopLatch, fileExtractor).start();

      firstLatch.countDown();
      Thread.sleep(50);
      secondLatch.countDown();
      stopLatch.await(20, TimeUnit.SECONDS);

      String expMsg = "Resolver: Successfully extracted maven binaries from";
      Matcher matcher = Pattern.compile(expMsg).matcher(systemOutRule.getLog());
      assertThat(matcher.find()).as(
         "The log should contain one occurrence of message \"%s\" but none was found. For more information see the log",
         expMsg).isTrue();
   }

   private Thread createThreadWithExtract(final CountDownLatch startLatch, final CountDownLatch stopLatch,
      final FileExtractor fileExtractor) {
      return new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               startLatch.await();
               fileExtractor.extract();
               stopLatch.countDown();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      });
   }
}
