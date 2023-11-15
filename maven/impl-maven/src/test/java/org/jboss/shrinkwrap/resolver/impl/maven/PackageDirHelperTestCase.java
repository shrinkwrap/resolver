package org.jboss.shrinkwrap.resolver.impl.maven;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertTrue;

/**
 * @author Florian Besser
 */
public class PackageDirHelperTestCase {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void packageDirectories_empty_canUnzip() throws Exception {
        File output = tempFolder.newFile("output.zip");

        MavenResolvedArtifactImpl.PackageDirHelper.packageDirectories(output);

        File outputFolder = tempFolder.newFolder("outputFolder");
        assertTrue(canUnzip(output, outputFolder));
    }

    @Test
    public void packageDirectories_singleEntry_canUnzip() throws Exception {
        File output = tempFolder.newFile("output.zip");

        File inputFolder = tempFolder.newFolder("inputFolder");
        FileUtils.forceMkdir(inputFolder);
        File inputFile = new File(inputFolder, "exampleInput.foo");
        FileUtils.write(inputFile, "some data", Charset.defaultCharset());

        MavenResolvedArtifactImpl.PackageDirHelper.packageDirectories(output, inputFolder);

        File outputFolder = tempFolder.newFolder("outputFolder");
        assertTrue(canUnzip(output, outputFolder));
    }

    @Test
    public void packageDirectories_singleEntryWithSubEntries_canUnzip() throws Exception {
        File output = tempFolder.newFile("output.zip");

        File inputRootFolder = tempFolder.newFolder("inputRootFolder");
        File inputSubFolder = new File(inputRootFolder, "inputSubFolder");
        FileUtils.forceMkdir(inputSubFolder);

        File inputFile = new File(inputSubFolder, "exampleInput.foo");
        FileUtils.write(inputFile, "some data", Charset.defaultCharset());

        MavenResolvedArtifactImpl.PackageDirHelper.packageDirectories(output, inputRootFolder);

        File outputFolder = tempFolder.newFolder("outputFolder");
        assertTrue(canUnzip(output, outputFolder));
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
                FileUtils.forceMkdir(newFile.getParentFile());

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