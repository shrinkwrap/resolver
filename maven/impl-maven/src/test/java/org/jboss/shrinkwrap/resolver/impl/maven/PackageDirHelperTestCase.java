package org.jboss.shrinkwrap.resolver.impl.maven;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * @author Florian Besser
 */
class PackageDirHelperTestCase {


    @Test
    void packageDirectories_empty_canUnzip(@TempDir Path tempPath) throws Exception {
        Path output = tempPath.resolve("output.zip");
        Files.createFile(output);

        MavenResolvedArtifactImpl.PackageDirHelper.packageDirectories(output.toFile());

        Path outputFolder = tempPath.resolve("outputFolder");
        Files.createDirectory(outputFolder);
        Assertions.assertTrue(canUnzip(output.toFile(), outputFolder.toFile()));
    }

    @Test
    void packageDirectories_singleEntry_canUnzip(@TempDir Path tempPath) throws Exception {
        Path output = tempPath.resolve("output.zip");
        Files.createFile(output);

        Path inputFolder = tempPath.resolve("inputFolder");
        Files.createDirectory(inputFolder);
        Path inputFile = inputFolder.resolve("exampleInput.foo");
        Files.write(inputFile, "some data".getBytes(Charset.defaultCharset()));

        MavenResolvedArtifactImpl.PackageDirHelper.packageDirectories(output.toFile(), inputFolder.toFile());

        Path outputFolder = tempPath.resolve("outputFolder");
        Files.createDirectory(outputFolder);
        Assertions.assertTrue(canUnzip(output.toFile(), outputFolder.toFile()));
    }

    @Test
    void packageDirectories_singleEntryWithSubEntries_canUnzip(@TempDir Path tempPath) throws Exception {
        Path output = tempPath.resolve("output.zip");
        Files.createFile(output);

        Path inputRootFolder = tempPath.resolve("inputRootFolder");
        Files.createDirectory(inputRootFolder);

        Path inputSubFolder = inputRootFolder.resolve("inputSubFolder");
        Files.createDirectory(inputSubFolder);

        Path inputFile = inputSubFolder.resolve("exampleInput.foo");
        Files.write(inputFile, "some data".getBytes(Charset.defaultCharset()));

        MavenResolvedArtifactImpl.PackageDirHelper.packageDirectories(output.toFile(), inputRootFolder.toFile());

        Path outputFolder = tempPath.resolve("outputFolder");
        Files.createDirectory(outputFolder);
        Assertions.assertTrue(canUnzip(output.toFile(), outputFolder.toFile()));
    }

    private boolean canUnzip(File zipFile, File outputFolder) {

        byte[] buffer = new byte[1024];

        try (ZipInputStream zis =
                     new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {

            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder, fileName);

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                Files.createDirectories(newFile.getParentFile().toPath());

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

                ze = zis.getNextEntry();
            }

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}