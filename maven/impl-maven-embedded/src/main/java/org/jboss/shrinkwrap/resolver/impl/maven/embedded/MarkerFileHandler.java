package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class MarkerFileHandler {

    private final File markerFile;
    private final Logger log = Logger.getLogger(MarkerFileHandler.class.getName());

    MarkerFileHandler(File destinationDir, String markerFileName) {
        markerFile = Paths.get(destinationDir.getPath(), markerFileName).toFile();
    }

    void createMarkerFile() {
        try {
            markerFile.createNewFile();
            markerFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteMarkerFile() {
        if (markerFile.exists() && !markerFile.delete()) {
            log.warning("failed to delete marker file: " + markerFile);
        }
    }

    boolean waitTillMarkerFileIsGone(long timeoutInMilliseconds, String processName) {
        int count = 0;
        while (isMarkerFilePresent() && count < timeoutInMilliseconds / 100) {
            if (count == 0) {
                System.out.printf("There is marker file %s, which means that some other process is already processing the %s -"
                        + " waiting to be completed.%n", markerFile, processName);
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

        return count == 100 && isMarkerFilePresent();
    }

    boolean isMarkerFilePresent() {
        return markerFile.exists();
    }
}
