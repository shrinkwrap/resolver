package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.archive.UntarTool;
import org.arquillian.spacelift.task.archive.UnzipTool;
import org.arquillian.spacelift.task.net.DownloadTool;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuildStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.DistributionStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuildTrigger;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public abstract class DistributionStageImpl<NEXT_STEP extends BuildStage<DAEMON_TRIGGER_TYPE>, DAEMON_TRIGGER_TYPE extends DaemonBuildTrigger>
    implements DistributionStage<NEXT_STEP, DAEMON_TRIGGER_TYPE> {

    private String maven3BaseUrl =
            "https://archive.apache.org/dist/maven/maven-3/%version%/binaries/apache-maven-%version%-bin.tar.gz";
    private File setMavenInstalation = null;
    private String mavenTargetDir = "target" + File.separator + "resolver-maven";
    private Logger log = Logger.getLogger(DistributionStage.class.getName());

    @Override
    public NEXT_STEP useMaven3Version(String version) {
        try {
            useDistribution(new URL(maven3BaseUrl.replaceAll("%version%", version)), true);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        return returnNextStepType();
    }

    @Override
    public NEXT_STEP useDistribution(URL mavenDistribution, boolean useCache) {

        File mavenDir = prepareMavenDir(useCache);
        File downloaded = download(mavenDir, mavenDistribution);
        String downloadedZipMd5hash = getMd5hash(downloaded);
        File withExtractedDir = null;
        if (downloadedZipMd5hash != null) {
            withExtractedDir = checkIfItIsAlreadyExtracted(downloadedZipMd5hash);
            if (withExtractedDir == null) {
                withExtractedDir = extract(downloaded, downloadedZipMd5hash);
            }
            File binDirectory = retrieveBinDirectory(withExtractedDir);

            useInstallation(binDirectory);
        }

        return returnNextStepType();
    }

    private File checkIfItIsAlreadyExtracted(final String downloadedZipMd5hash) {

        File[] dirs = new File(mavenTargetDir).listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                    return file.isDirectory() && downloadedZipMd5hash.equals(file.getName());
            }
        });
        if (dirs != null && dirs.length > 0) {
            return dirs[0];
        }
        return null;
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

    private File extract(File downloaded, String downloadedZipMd5hash) {

            File toExtract = new File(mavenTargetDir + File.separator + downloadedZipMd5hash);
            toExtract.mkdirs();
            String downloadedPath = downloaded.getAbsolutePath();

            try {
                if (downloadedPath.endsWith(".zip")) {
                    Spacelift.task(downloaded, UnzipTool.class).toDir(toExtract).execute().await();

                } else if (downloadedPath.endsWith(".tar.gz")) {
                    Spacelift.task(downloaded, UntarTool.class).gzip(true).toDir(toExtract).execute().await();

                } else if (downloadedPath.endsWith(".tar.bz2")) {
                    Spacelift.task(downloaded, UntarTool.class).bzip2(true).toDir(toExtract).execute().await();

                } else {
                    throw new IllegalArgumentException(
                            "The distribution " + downloaded + " is compressed by unsupported format. "
                                    + "Supported formats are .zip, .tar.gz, .tar.bz2");
                }
            } catch (ExecutionException ee) {
                throw new IllegalStateException(
                        "Something bad happened when the file: " + downloadedPath + " was being extracted. "
                                + "For more information see the stacktrace");
            }

        return toExtract;
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

    private File download(File mavenDir, URL mavenDistribution) {
        String distUrl = mavenDistribution.toString();
        String target = mavenDir + distUrl.substring(distUrl.lastIndexOf("/"));
        File downloaded = null;

        for (File file : mavenDir.listFiles()) {
            if (file.getAbsolutePath().equals(target)) {
                downloaded = file;
            }
        }
        if (downloaded == null) {

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

    private File prepareMavenDir(boolean useCache) {
        String dirPath;
        if (useCache) {
            dirPath =
                    System.getProperty("user.home") + File.separator
                            + ".arquillian" + File.separator
                            + "resolver" + File.separator
                            + "maven";
        } else {
            dirPath =
                    mavenTargetDir + File.separator
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
        this.setMavenInstalation = mavenHome;
        return returnNextStepType();
    }

    @Override
    public NEXT_STEP useDefaultDistribution() {
        return returnNextStepType();
    }

    protected File getSetMavenInstallation() {
        return setMavenInstalation;
    }

    protected abstract NEXT_STEP returnNextStepType();

}
