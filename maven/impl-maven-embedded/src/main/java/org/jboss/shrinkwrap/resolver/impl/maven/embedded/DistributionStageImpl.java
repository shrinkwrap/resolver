package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.task.archive.UntarTool;
import org.arquillian.spacelift.task.archive.UnzipTool;
import org.arquillian.spacelift.task.net.DownloadTool;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuildStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.DistributionStage;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public abstract class DistributionStageImpl<NEXT_STEP extends BuildStage>
    implements DistributionStage<NEXT_STEP> {

    private String maven3BaseUrl =
        "https://archive.apache.org/dist/maven/maven-3/%version%/binaries/apache-maven-%version%-bin.tar.gz";
    private File setMavenInstalation = null;
    String mavenTargetDir = "target" + File.separator + "resolver-maven";

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
        File extracted = extract(downloaded);
        File binDirectory = retrieveBinDirectory(extracted);

        useInstallation(binDirectory);
        return returnNextStepType();
    }

    private File retrieveBinDirectory(File uncompressed){
        String[] extracted = uncompressed.list();
        if (extracted.length == 0) {
            throw new IllegalArgumentException("No directory has been extracted from the archive: " + uncompressed);
        }
        if (extracted.length > 1) {
            throw new IllegalArgumentException(
                "More than one directory has been extracted from the archive: " + uncompressed);
        }
        File binDirectory = new File(uncompressed + File.separator + extracted[0]);
        if (binDirectory.isFile()) {
            throw new IllegalArgumentException(
                "The extracted file from the archive " + uncompressed + " should be a directory");
        }
        return binDirectory;
    }

    private File extract(File downloaded){

        UUID uuid = UUID.randomUUID();
        File toExtract = new File(mavenTargetDir + File.separator + uuid);
        toExtract.mkdirs();
        String downloadedPath = downloaded.getAbsolutePath();

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

        return toExtract;
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

            Execution<File> execution = Spacelift.task(DownloadTool.class).from(mavenDistribution).to(target).execute();
            System.out.println("Resolver: downloading Maven binaries from " + mavenDistribution + " to " + target);
            while (!execution.isFinished()) {
                System.out.print(".");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println();
            downloaded = execution.await();
        }
        return downloaded;
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
                    + "downloaded" + File.separator
                    + UUID.randomUUID();
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

    protected File getSetMavenInstalation() {
        return setMavenInstalation;
    }

    protected abstract NEXT_STEP returnNextStepType();
}
