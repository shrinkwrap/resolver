package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

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
            URL redirectedMavenDistribution = checkForRedirect(mavenDistribution);
            int numberOfAttempts = 3;
            long expectedSize = getExpectedSize(redirectedMavenDistribution, numberOfAttempts);
            int remainingAttempts = numberOfAttempts;
            for (int i = 0; i < numberOfAttempts; i++) {
                remainingAttempts--;
                try {
                    downloaded = runDownloadExecution(redirectedMavenDistribution, target);
                    if (downloaded != null && (downloaded.length() == expectedSize || expectedSize == -1)) {
                        markerFileHandler.deleteMarkerFile();
                        return downloaded;
                    }
                    else {
                        throw new IOException("An error occurred during download.");
                    }
                } catch (IOException e) {
                    System.err.print("ERROR: the downloading of Maven binaries has failed. ");
                    if (remainingAttempts > 0) {
                        System.err.println("Trying again - number of remaining attempts: " + (remainingAttempts));
                    } else {
                        System.err.println("For more information see the stacktrace of an exception");
                        markerFileHandler.deleteMarkerFile();
                        throw new IllegalStateException(e);
                    }
                }
            }
            markerFileHandler.deleteMarkerFile();
            throw new IllegalStateException("Unable to download Maven binaries");
        }
        return downloaded;
    }

    private File runDownloadExecution(URL mavenDistribution, String target) throws IOException {
        System.out.println("Resolver: downloading Maven binaries from " + mavenDistribution + " to " + target);
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(mavenDistribution.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(target);
             FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        File downloadedFile = Paths.get(target).toFile();
        if (downloadedFile.exists() & downloadedFile.length() > 0) {
            return downloadedFile;
        }
        return null;
    }

    private URL checkForRedirect(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode > 300 && responseCode < 400) {
                String redirectLocation = connection.getHeaderField("Location");
                if (redirectLocation == null || redirectLocation.isEmpty()) {
                    throw new IllegalStateException("The site response code was a redirect one (" +
                            responseCode + ") but no 'Location' header was sent.");
                }
                return checkForRedirect(new URL(redirectLocation));
            }
            return url;
        } catch (IOException e) {
            throw new IllegalStateException("An error occurred during redirecting of the URL", e);
        }
    }

    private long getExpectedSize(URL url, int numberOfAttempts) {
        long expectedSize;
        int remainingAttempts = numberOfAttempts;
        for (int i = 0; i < numberOfAttempts; i++) {
            remainingAttempts--;
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                expectedSize = connection.getContentLengthLong();
                connection.disconnect();

                if (expectedSize == 0) {
                    throw new IOException("Expected size of the download cannot be zero.");
                }

                return expectedSize; // Exit the loop if successful
            } catch (IOException e) {
                System.err.print("ERROR: Unable to fetch expected size. ");
                if (remainingAttempts > 0) {
                    System.err.println("Retrying - number of remaining attempts: " + (remainingAttempts));
                } else {
                    System.err.println("Giving up after " + numberOfAttempts + " attempts.");
                    markerFileHandler.deleteMarkerFile();
                    throw new IllegalStateException("Unable to determine expected size after " + numberOfAttempts + " attempts", e);
                }
            }
        }
        return -1;
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
