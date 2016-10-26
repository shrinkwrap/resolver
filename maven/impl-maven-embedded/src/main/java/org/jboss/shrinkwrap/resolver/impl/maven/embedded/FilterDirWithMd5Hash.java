package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class FilterDirWithMd5Hash implements FileFilter {

    private final String downloadedZipMd5hash;
    public static String mavenBinaryZipMd5HashFile = "resolver_maven_binary_zip_md5_hash_file";
    private Logger log = Logger.getLogger(FilterDirWithMd5Hash.class.getName());

    public FilterDirWithMd5Hash(String downloadedZipMd5hash) {
        this.downloadedZipMd5hash = downloadedZipMd5hash;
    }

    @Override
    public boolean accept(File directory) {
        // get only dirs (we are interested in those UUID ones - we do not have to filter it here - it will be filtered by the presence of the has file
        if (directory.isDirectory()) {

            // get all files in the directory
            File[] files = directory.listFiles(new FileFilter() {

                @Override public boolean accept(File file) {
                    // we are interested in files with the specific name - see mavenBinaryZipMd5HashFile variable
                    if (file.isFile() && file.getName().equals(mavenBinaryZipMd5HashFile)) {

                        try {
                            // check if the hash contained within the file is same as the expected one
                            String content = FileUtils.readFileToString(file);
                            return downloadedZipMd5hash.equals(content);
                        } catch (IOException e) {
                            log.warning(
                                "A problem occurred when reading md5 hash file " + file + "\n" + e.getMessage());
                        }
                    }
                    return false;
                }
            });

            // if any file with the specific name and with the expected hash was filtered within this directory, then return true
            return files != null && files.length > 0;
        }
        return false;

    }
}