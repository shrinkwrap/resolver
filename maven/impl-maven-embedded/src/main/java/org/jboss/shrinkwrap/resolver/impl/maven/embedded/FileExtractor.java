package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.AsiExtraField;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

class FileExtractor {

   private final File fileToExtract;
   private final File destinationDir;
   private final MarkerFileHandler markerFileHandler;
   private final List<String> acceptedExtensions = Arrays.asList(".zip", ".tar.gz", ".tar.bz2", ".tgz", ".tbz2");

   private FileExtractor(File fileToExtract, File destinationDir) {
      this.fileToExtract = fileToExtract;
      this.destinationDir = destinationDir;
      this.markerFileHandler = new MarkerFileHandler(destinationDir, "extractionIsProcessing.tmp");
   }

   static File extract(File fileToExtract, File destinationDir) {
      return new FileExtractor(fileToExtract, destinationDir).extract();
   }

   private File extract() {
      File withExtractedDir = checkIfItIsAlreadyExtracted();
      if (withExtractedDir != null) {
         return withExtractedDir;
      }
      destinationDir.mkdirs();
      extractFileInDestinationDir();
      return destinationDir;
   }

   private void extractFileInDestinationDir() {
      String fileExtension = getExtension(fileToExtract);
      markerFileHandler.createMarkerFile();
      try (FileInputStream fileInputStream = new FileInputStream(fileToExtract);
           InputStream inputStream = getCompressorInputStream(fileExtension, fileInputStream);
           ArchiveInputStream<?> archiveInputStream = getArchiveInputStream(fileExtension, inputStream)) {

         Path destPath = Paths.get(destinationDir.toURI());
         ArchiveEntry entry;
         while ((entry = archiveInputStream.getNextEntry()) != null) {
            Path entryPath = destPath.resolve(entry.getName());
            if (entry.isDirectory()) {
               Files.createDirectories(entryPath);
            } else {
               Files.createDirectories(entryPath.getParent());
               try (FileOutputStream fileOutputStream = new FileOutputStream(entryPath.toFile())) {
                  byte[] buffer = new byte[1024];
                  int len;
                  while ((len = archiveInputStream.read(buffer)) != -1) {
                     fileOutputStream.write(buffer, 0, len);
                  }
               }
               int permissions = getPermissions(entry);
               if (permissions != 0) {
                  FilePermission filePermission = PermissionsUtil.toFilePermission(permissions);
                  PermissionsUtil.applyPermission(entryPath.toFile(), filePermission);
               }
            }
         }
      } catch (IOException e) {
         System.err.println("Failed to unzip file: " + e.getMessage());
      }
      markerFileHandler.deleteMarkerFile();
      System.out.printf("Resolver: Successfully extracted maven binaries from %s%n", fileToExtract);
   }

   private static InputStream getCompressorInputStream(String fileExtension, FileInputStream fileInputStream) throws IOException {
       switch (fileExtension) {
           case ".zip":
               return fileInputStream;
           case ".tar.gz":
           case ".tgz":
               return new GzipCompressorInputStream(fileInputStream);
           case ".tar.bz2":
           case ".tbz2":
               return new BZip2CompressorInputStream(fileInputStream);
           default:
               throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
       }
   }

   private static ArchiveInputStream<?> getArchiveInputStream(String fileExtension, InputStream inputStream) {
      if (fileExtension.equals(".tar.gz") || fileExtension.equals(".tgz") || fileExtension.equals(".tar.bz2") || fileExtension.equals(".tbz2")) {
         return new TarArchiveInputStream(inputStream);
      } else if (fileExtension.equals(".zip")) {
         return new ZipArchiveInputStream(inputStream);
      } else {
         throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
      }
   }

   private String getExtension(File fileName) {
      for (String extension : acceptedExtensions) {
         if (fileName.getName().endsWith(extension)) {
            return extension;
         }
      }
      throw new IllegalArgumentException("The archive is compressed by unsupported format. " +
              "Supported formats are " + acceptedExtensions);
   }

   private int getPermissions(ArchiveEntry archiveEntry) {
      if (archiveEntry instanceof TarArchiveEntry) {
         TarArchiveEntry tarArchiveEntry = (TarArchiveEntry) archiveEntry;
         return tarArchiveEntry.getMode();
      }
      if (archiveEntry instanceof ZipArchiveEntry) {
         ZipArchiveEntry zipArchiveEntry = (ZipArchiveEntry) archiveEntry;
         ZipExtraField[] extraFields = zipArchiveEntry.getExtraFields();
         for (ZipExtraField zipExtraField : extraFields) {
            if (zipExtraField instanceof AsiExtraField) {
               AsiExtraField asiExtraField = (AsiExtraField) zipExtraField;
               return asiExtraField.getMode();
            }
         }
      }
      return 0;
   }

   private File checkIfItIsAlreadyExtracted() {
      if (destinationDir.exists() && destinationDir.isDirectory()
         && Objects.requireNonNull(destinationDir.list()).length > 0 && isExtractionFinished()) {
         return destinationDir;
      }
      return null;
   }

   private boolean isExtractionFinished() {
      boolean fileIsStillPresent = markerFileHandler.waitTillMarkerFileIsGone(10000, "extraction");
      if (fileIsStillPresent) {
         try {
            deleteFileRecursively(destinationDir.toPath());
            return false;
         } catch (IOException e) {
            throw new RuntimeException("Failed to delete directory:" + destinationDir, e);
         }
      }
      return true;
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
