package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuildStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.DistributionStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuildTrigger;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public abstract class DistributionStageImpl<NEXT_STEP extends BuildStage<DAEMON_TRIGGER_TYPE>, DAEMON_TRIGGER_TYPE extends DaemonBuildTrigger>
    implements DistributionStage<NEXT_STEP, DAEMON_TRIGGER_TYPE> {
    private static final String MAVEN_3_BASE_URL =
            "https://archive.apache.org/dist/maven/maven-3/%version%/binaries/apache-maven-%version%-bin.tar.gz";
    public static final String MAVEN_TARGET_DIR = "target" + File.separator + "resolver-maven";
    public static final String MAVEN_CACHE_DIR =
        System.getProperty("user.home") + File.separator
            + ".arquillian" + File.separator
            + "resolver" + File.separator
            + "maven";

    private File setMavenInstallation = null;
    private boolean useLocalInstallation = false;
    private Logger log = Logger.getLogger(DistributionStage.class.getName());

    @Override
    public NEXT_STEP useMaven3Version(String version) {
        try {
            useDistribution(new URL(MAVEN_3_BASE_URL.replaceAll("%version%", version)), true);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        return returnNextStepType();
    }

    @Override
    public NEXT_STEP useDistribution(URL mavenDistribution, boolean useCache) {
        synchronized (MAVEN_3_BASE_URL) {
            File mavenDir = prepareMavenDir(useCache);
            File downloaded = BinaryDownloader.download(mavenDir, mavenDistribution);
            String downloadedZipMd5hash = getMd5hash(downloaded);
            File withExtractedDir;
            if (downloadedZipMd5hash != null) {
                withExtractedDir =
                    FileExtractor.extract(downloaded, Paths.get(MAVEN_TARGET_DIR, downloadedZipMd5hash).toFile());
                File binDirectory = retrieveBinDirectory(withExtractedDir);
                useInstallation(binDirectory);
            }
        }
        return returnNextStepType();
    }

    private File retrieveBinDirectory(File uncompressed) {
        File[] extracted = uncompressed.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        if (extracted.length == 0) {
            throw new IllegalArgumentException("No directory has been extracted from the archive: " + uncompressed);
        }
        if (extracted.length > 1) {
            throw new IllegalArgumentException(
                    "More than one directory has been extracted from the archive: " + uncompressed);
        }
        return extracted[0];
    }



    private String getMd5hash(File downloaded) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(downloaded);
            return DigestUtils.md5Hex(fis);
        } catch (IOException e) {
            log.warning("A problem occurred when md5 hash of a file " + downloaded + " was being retrieved:\n"
                    + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.warning("A problem occurred when FileInputStream of a file " + downloaded
                            + "was being closed:\n" + e.getMessage());
                }
            }
        }
        return null;
    }

    private File prepareMavenDir(boolean useCache) {
        String dirPath;
        if (useCache) {
            dirPath = MAVEN_CACHE_DIR;
        } else {
            dirPath =
                MAVEN_TARGET_DIR + File.separator
                            + "downloaded" + File.separator;
        }

        File mavenDir = new File(dirPath);
        if (!mavenDir.exists()) {
            mavenDir.mkdirs();
        }
        return mavenDir;
    }

    @Override
    public NEXT_STEP useInstallation(File mavenHome) {
        this.setMavenInstallation = mavenHome;
        return returnNextStepType();
    }

    @Override
    public NEXT_STEP useDefaultDistribution() {
        useMaven3Version(DEFAULT_MAVEN_VERSION);
        return returnNextStepType();
    }

    @Override
    public NEXT_STEP useLocalInstallation() {
        useLocalInstallation = true;
        return returnNextStepType();
    }

    protected File getSetMavenInstallation() {
        return setMavenInstallation;
    }

    protected boolean shouldUseLocalInstallation() {
        return useLocalInstallation;
    }

    protected abstract NEXT_STEP returnNextStepType();

}
