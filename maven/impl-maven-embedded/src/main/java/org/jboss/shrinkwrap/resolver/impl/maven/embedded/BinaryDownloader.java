package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.net.DownloadTool;

public class BinaryDownloader {

    private final Logger log = Logger.getLogger(BinaryDownloader.class.getName());
    private final String target;
    private final MarkerFileHandler markerFileHandler;
    private final URL mavenDistribution;

    private BinaryDownloader(File mavenDir, URL mavenDistribution) {
        String distUrl = mavenDistribution.toString();
        this.mavenDistribution = mavenDistribution;
        target = new File(mavenDir, distUrl.substring(distUrl.lastIndexOf("/"))).getAbsolutePath();
        markerFileHandler = new MarkerFileHandler(mavenDir, new File(target).getName() + "-downloadProcess.tmp");
    }

    static File download(File mavenDir, URL mavenDistribution) {
        return new BinaryDownloader(mavenDir, mavenDistribution).download();
    }

    private File download() {
        File downloaded = checkIfItIsAlreadyDownloaded();
        if (downloaded == null) {
            markerFileHandler.createMarkerFile();

            for (int i = 0; i < 3; i++) {
                try {
                    downloaded = runDownloadExecution(mavenDistribution, target).await();
                } catch (ExecutionException ee) {
                    System.err.print("ERROR: the downloading of Maven binaries has failed. ");
                    if (2 - i > 0) {
                        System.err.println("Trying again - number of remaining attempts: " + (2 - i));
                        continue;
                    } else {
                        System.err.println("For more information see the stacktrace of an exception");
                        throw ee;
                    }
                }
                break;
            }
            markerFileHandler.deleteMarkerFile();
        }
        return downloaded;
    }

    private Execution<File> runDownloadExecution(URL mavenDistribution, String target) {
        Execution<File> execution = Spacelift.task(DownloadTool.class).from(mavenDistribution).to(target).execute();
        System.out.println("Resolver: downloading Maven binaries from " + mavenDistribution + " to " + target);

        while (!execution.isFinished()) {
            System.out.print(".");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.warning("Problem occurred when the thread was sleeping:\n" + e.getMessage());
            }
        }
        System.out.println();

        return execution;
    }

    private File checkIfItIsAlreadyDownloaded() {
        if (isExtractionFinished() && new File(target).exists()) {
            return new File(target);
        }
        return null;
    }

    private boolean isExtractionFinished() {
        boolean fileIsStillPresent = markerFileHandler.waitTillMarkerFileIsGone(60000, "download");
        if (fileIsStillPresent) {
            try {
                Files.delete(Paths.get(target));
                markerFileHandler.deleteMarkerFile();
                return false;
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete a file.", e);
            }
        }
        return true;
    }
}
