package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.archive.UntarTool;
import org.arquillian.spacelift.task.archive.UnzipTool;

class FileExtractor {

   private final File fileToExtract;
   private final File destinationDir;
   private Logger log = Logger.getLogger(FileExtractor.class.getName());

   FileExtractor(File fileToExtract, File destinationDir) {
      this.fileToExtract = fileToExtract;
      this.destinationDir = destinationDir;
   }

   File extract() {
      File withExtractedDir = checkIfItIsAlreadyExtracted();
      if (withExtractedDir != null) {
         return withExtractedDir;
      }
      destinationDir.mkdirs();
      extractFileInDestinationDir();
      return destinationDir;
   }

   private File createExtractionMarkerFile() {
      try {
         final File extractionIsProcessing = markerFile();
         extractionIsProcessing.createNewFile();
         extractionIsProcessing.deleteOnExit();

         return extractionIsProcessing;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private File markerFile() {
      return Paths.get(destinationDir.getPath(), "extractionIsProcessing.tmp").toFile();
   }

   private void extractFileInDestinationDir() {
      final File markerFile = createExtractionMarkerFile();
      final String downloadedPath = fileToExtract.getAbsolutePath();

      try {
         if (downloadedPath.endsWith(".zip")) {
            Spacelift.task(fileToExtract, UnzipTool.class).toDir(destinationDir).execute().await();
         } else if (downloadedPath.endsWith(".tar.gz")) {
            Spacelift.task(fileToExtract, UntarTool.class).gzip(true).toDir(destinationDir).execute().await();
         } else if (downloadedPath.endsWith(".tar.bz2")) {
            Spacelift.task(fileToExtract, UntarTool.class).bzip2(true).toDir(destinationDir).execute().await();
         } else {
            throw new IllegalArgumentException(
               "The distribution " + fileToExtract + " is compressed by unsupported format. "
                  + "Supported formats are .zip, .tar.gz, .tar.bz2");
         }
      } catch (ExecutionException ee) {
         throw new IllegalStateException(
            "Something bad happened when the file: " + downloadedPath + " was being extracted. "
               + "For more information see the stacktrace", ee);
      }
      if (!markerFile.delete()) {
         log.warning("failed to delete temp directory: " + markerFile);
      }
      System.out.println(String.format("Resolver: Successfully extracted maven binaries from %s", fileToExtract));
   }

   private File checkIfItIsAlreadyExtracted() {
      if (destinationDir.exists() && destinationDir.isDirectory()
         && destinationDir.list().length > 0 && isExtractionFinished()) {
         return destinationDir;
      }
      return null;
   }

   private boolean isExtractionFinished() {
      int count = 0;
      while (isTempFilePresent() && count < 100) {
         if (count == 0) {
            System.out.println(
               "There is marker file, which means that some other process is already extracting the archive,"
                  + " waiting for extraction to finish");
         }
         System.out.print(".");
         try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            log.warning("Problem occurred when the thread was sleeping:\n" + e.getMessage());
         }
         count++;
      }
      System.out.println();
      if (count == 100 && isTempFilePresent()) {
         try {
            deleteFileRecursively(destinationDir.toPath());
            return false;
         } catch (IOException e) {
            throw new RuntimeException("Failed to delete directory:" + destinationDir, e);
         }
      }
      return true;
   }

   private boolean isTempFilePresent() {
      return markerFile().exists();
   }

   private void deleteFileRecursively(Path directory) throws IOException {
      Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
         @Override
         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
         }

         @Override
         public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
         }
      });
   }
}
