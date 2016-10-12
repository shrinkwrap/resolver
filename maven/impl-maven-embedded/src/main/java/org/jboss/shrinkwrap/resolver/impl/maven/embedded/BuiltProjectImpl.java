package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class BuiltProjectImpl implements BuiltProject {

    private File pom;
    private File globalSettingsXml;
    private File userSettingsXml;
    private String[] profiles;
    private Model model;

    public BuiltProjectImpl(String pom, String... profiles) {
        this(new File(pom), null, null, profiles);
    }

    public BuiltProjectImpl(File pom, String... profiles) {
        this(pom, null, null, profiles);
    }

    public BuiltProjectImpl(String pom, File globalSettingsXml, File userSettingsXml, String... profiles) {
        this(new File(pom), globalSettingsXml, userSettingsXml, profiles);
    }

    public BuiltProjectImpl(File pom, File globalSettingsXml, File userSettingsXml, String... profiles) {
        this.pom = pom;
        this.profiles = profiles;
        this.globalSettingsXml = globalSettingsXml;
        this.userSettingsXml = userSettingsXml;
    }

    public Model getModel() {
        if (model == null) {
            MavenWorkingSessionImpl mavenWorkingSession = new MavenWorkingSessionImpl();
            mavenWorkingSession.configureSettingsFromFile(globalSettingsXml, userSettingsXml);
            ParsedPomFile parsedPomFile = mavenWorkingSession.loadPomFromFile(pom, profiles).getParsedPomFile();
            model = parsedPomFile.getModel();
        }
        return model;
    }

    public Archive getDefaultBuiltArchive() {
        String finalName = getModel().getBuild().getFinalName();
        String buildDirectory = getModel().getBuild().getDirectory();
        String packaging = getModel().getPackaging();
        PackagingType packagingType = PackagingType.fromCache(packaging);

        if (packagingType == null) {
            throw new IllegalArgumentException("The packaging type " + packaging + " is not supported");
        }

        if (packagingType != PackagingType.POM) {
            File zipFile = new File(buildDirectory + File.separator + finalName + "." + packaging);

            return ShrinkWrap.createFromZipFile(getArchiveRepresentation(packagingType), zipFile);
        } else {
            return null;
        }
    }

    private Class<? extends Archive> getArchiveRepresentation(PackagingType packagingType) {
        if (packagingType == PackagingType.EAR) {
            return EnterpriseArchive.class;
        } else if (packagingType == PackagingType.WAR) {
            return WebArchive.class;
        }
        return JavaArchive.class;
    }

    public BuiltProject getModule(String moduleName) {
        List<String> modules = getModel().getModules();
        File projectDirectory = getModel().getProjectDirectory();
        for (String module : modules) {
            if (moduleName.equals(module)) {
                return new BuiltProjectImpl(projectDirectory + File.separator + module + File.separator + "pom.xml");
            }
        }
        return null;
    }

    public List<BuiltProject> getModules() {
        List<String> modules = getModel().getModules();
        File projectDirectory = getModel().getProjectDirectory();
        List<BuiltProject> projects = new ArrayList<>(modules.size());
        for (String module : modules) {
            projects.add(new BuiltProjectImpl(projectDirectory + File.separator + module + File.separator + "pom.xml"));
        }
        return projects;
    }

    public File getTargetDirectory() {
        if (getModel().getBuild() == null) {
            return null;
        }
        return new File(getModel().getBuild().getDirectory());
    }

    public List<Archive> getArchives() {
        File[] allFirstLevelFiles = getFirstLevelFiles();
        if (allFirstLevelFiles == null){
            return null;
        }

        List<Archive> archives = new ArrayList<>(allFirstLevelFiles.length);
        for (File file : allFirstLevelFiles) {
            Class<? extends Archive> archiveType = getIfSupported(file);
            if (archiveType != null) {
                archives.add(ShrinkWrap.createFromZipFile(archiveType, file));
            }
        }
        return archives;
    }

    private File[] getFirstLevelFiles(){
        File targetDirectory = getTargetDirectory();
        if (targetDirectory == null) {
            return null;
        }
        return targetDirectory.listFiles();
    }

    private Class<? extends Archive> getIfSupported(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        if (!StringUtils.isEmpty(extension)) {
            PackagingType packagingType = PackagingType.fromCache(extension);
            if (packagingType != null && packagingType != PackagingType.POM) {
                return getArchiveRepresentation(packagingType);
            }
        }
        return null;
    }

    public <A extends Archive<?>> List<A> getArchives(Class<A> type) {
        File[] allFirstLevelFiles = getFirstLevelFiles();
        if (allFirstLevelFiles == null){
            return null;
        }

        List<A> archives = new ArrayList<>();
        for (File file : allFirstLevelFiles) {
            Class<? extends Archive> archiveType = getIfSupported(file);
            if (archiveType != null && archiveType.isAssignableFrom(type)) {
                archives.add(ShrinkWrap.createFromZipFile(type, file));
            }
        }
        return archives;
    }
}
