# Shrinkwrap Resolvers

## Introduction to ShrinkWrap Resolvers

ShrinkWrap Resolvers is a Java API designed to simplify the process of obtaining artifacts from a repository system. It simplifies the retrieval of third-party libraries and dependent modules, allowing developers to easily incorporate them into their projects.

The project primarily focuses on supporting Maven-based repository structures, enabling developers to specify coordinates that uniquely identify an artifact in the repository. It also provides basic support for Gradle-based projects.

## Requirements

## Build:

- **JDK**: Version 8 or newer.
- **Maven**: Version 3.6.3 or newer.

## Use:

- **JDK**: Version 8 or newer.
- **Maven**: Version 3.3.9 or newer.
- **Gradle**: Version 3 or newer.


## Adding ShrinkWrap Resolvers to Your Project

To include ShrinkWrap Resolvers in your project, you can add a dependency on the `shrinkwrap-resolver-depchain` module in your Maven `pom.xml`:

```xml
<dependencies>
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-depchain</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>test</scope>
      <type>pom</type>
    </dependency>
</dependencies>
```

If you are using Arquillian BOM prior to version 1.1.0.Final, ensure that you import ShrinkWrap Resolvers BOM preceding Arquillian BOM to get version 2.0.x.

## Resolving Dependencies

To initiate the resolution of dependencies, utilize the `org.jboss.shrinkwrap.resolver.api.maven.Maven` class. Below are typical scenarios for its usage.
### Resolution of Artifacts by Maven Coordinates

Maven coordinates follow `groupId:artifactId:[packagingType:[classifier]]:version` format. You can use these coordinates to resolve a file, where `G` is the group ID, `A` is the artifact ID, `P` is the packaging type, `C` is the classifier, and `V` is the version.

#### Resolve a file using Maven coordinates with transitive dependencies:
```java
File[] files = Maven.resolver().resolve("G:A:V").withTransitivity().asFile();
```

#### Resolve a single file without transitive dependencies:
```java
File file = Maven.resolver().resolve("G:A:V").withoutTransitivity().asSingleFile();
```

#### Resolve multiple artifacts:
```java
File[] files = Maven.resolver().resolve("G1:A1:V1", "G2:A2:V2").withTransitivity().asFile();
```

#### Specifying Packaging and Classifier

```java
File file = Maven.resolver().resolve("G:A:war:V").withTransitivity().asFile();
File fileWithClassifier = Maven.resolver().resolve("G:A:test-jar:tests:V").withTransitivity().asFile();
```

#### Returning Resolved Artifacts

ShrinkWrap Resolvers provides various options to retrieve resolved artifacts. You can obtain them as files, streams, URLs, or specific archive types like JavaArchive, WebArchive, or EnterpriseArchive. For example:
```java
// Get the resolved artifact as a URL
URL[] urls = Maven.resolver().resolve("G:A:V").withTransitivity().as(URL.class);
// Get the resolved artifact as a JAR
JavaArchive[] jars = Maven.resolver().resolve("G:A:V").withTransitivity().as(JavaArchive.class);
// Get the resolved artifact as a WAR
WebArchive war = Maven.resolver().resolve("G:A:war:V").withTransitivity().asSingle(WebArchive.class);

```

#### Working with Artifact Metadata
```java
MavenResolvedArtifact artifact = Maven.resolver().resolve("G:A:war:V").withoutTransitivity()
  .asSingle(MavenResolvedArtifact.class);
MavenCoordinate coordinates = artifact.getCoordinate();
MavenArtifactInfo[] dependencies = artifact.getDependencies();
String version = artifact.getResolvedVersion();
ScopeType scope = artifact.getScope();
// You can still retrieve resolved artifact from MavenResolvedArtifact
File file = artifact.asFile();
```

#### Working with artifact coordinates
```java
MavenCoordinate[] coordinates = Maven.resolver().resolve("G:A:V")
   .withTransitivity().as(MavenCoordinate.class);
```

#### Resolution of artifacts as collection
```java
List<File> files = Maven.resolver().resolve("G:A:V")
    .withTransitivity().asList(File.class);
```

#### Exclude specific dependencies while resolving an artifact
```java
File file = Maven.resolver().addDependencies(
    MavenDependencies.createDependency("G:A:V", ScopeType.COMPILE, false,
      MavenDependencies.createExclusion("G:B"))).resolve().withTransitivity().asFile();
```

#### Control Resolution Results with Strategy
```java
File file = Maven.resolver().resolve("G:A:V").using(new RejectDependenciesStrategy(false, "G:B")).asFile();
```

### ShrinkWrap Resolver Strategies

In ShrinkWrap Resolver, strategies allow you to modify the dependency graph and change the default behavior for resolving transitive dependencies.

By default, Maven and ShrinkWrap Resolver do not resolve dependencies in provided and test scope, and skip optional dependencies.

#### Available Strategies:

- **AcceptAllStrategy** (same as TransitiveStrategy):
  Accepts all dependencies of artifacts.

- **AcceptScopesStrategy**:
  Accepts only dependencies with defined scope types.

- **CombinedStrategy**:
  Combines multiple strategies with a logical AND operation.

- **NonTransitiveStrategy**:
  Rejects all transitive dependencies; only directly specified dependencies are accepted.

- **RejectDependenciesStrategy**:
  Rejects dependencies defined by G:A. By default, it is transitive, meaning all dependencies originating at G:A are removed. To change this behavior to reject defined dependencies but keep their descendants, use `RejectDependenciesStrategy(false, "G:A")`.

- **TransitiveStrategy** (same as AcceptAllStrategy):
  Accepts all dependencies of artifacts.


#### Control Sources of Resolution

ShrinkWrap Resolvers allows you to specify artifact resolution sources. By default, it uses the classpath (Maven Reactor) and Maven Central repository.

```java
// Disable classpath resolution
Maven.configureResolver().withClassPathResolution(false).resolve("G:A:V").withTransitivity().asFile();
// Disable using Maven Central repository
Maven.configureResolver().withMavenCentralRepo(false).resolve("G:A:V").withTransitivity().asFile();
// Enable offline mode to use only local cache, avoiding remote repositories
Maven.configureResolver().workOffline().resolve("G:A:V").withTransitivity().asFile();
// Ignore the origin of artifacts present in the local repository
Maven.configureResolver().useLegacyLocalRepo(true).resolve("G:A:V").withTransitivity().asFile();
```

#### Specify settings.xml
```java
Maven.configureResolver().fromFile("/path/to/settings.xml")
  .resolve("G:A:V").withTransitivity().asFile();

Maven.configureResolver().fromClassloaderResource("path/to/settings.xml")
  .resolve("G:A:V").withTransitivity().asFile();
```

<sub>**Warning:** ShrinkWrap Resolvers doesn't consume settings.xml specified on the command line or in the IDE. It reads settings.xml files at standard locations: `~/.m2/settings.xml` and `$M2_HOME/conf/settings.xml`, unless overridden in the API or via System property.</sub>

#### Define Maven repositories manually

```java
Maven.configureResolver().withRemoteRepo("my-repository-id", "url://to/my/repository", "layout")
  .resolve("G:A:V").withTransitivity().asFile();

Maven.configureResolver().withRemoteRepo(MavenRemoteRepositories.createRemoteRepository("my-repository-id", "url://to/my/repository", "layout"))
  .resolve("G:A:V").withTransitivity().asFile();
```

<sub>**Note:** If a repository with the same ID is configured in either settings.xml or pom.xml, it will be ignored.</sub>

### Resolution of Artifacts from POM Files

In Maven projects, dependencies are specified in the pom.xml file. ShrinkWrap Resolvers follow the DRY principle by loading this metadata automatically. It creates an effective POM model using your pom.xml, parent hierarchy, and Super POM, allowing you to resolve artifacts with the included metadata from local, classpath, and remote repositories.

#### Load dependencies from the POM:
  ```java
  Maven.resolver().loadPomFromFile("/path/to/pom.xml").resolve("G:A").withTransitivity().asFile();
  ```

#### Load dependencies with non-JAR packaging or classifier:
  ```java
  Maven.resolver().loadPomFromClassLoaderResource("/path/to/pom.xml").resolve("G:A:P:?").withTransitivity().asFile();
  ```

#### Import and resolve dependencies based on specific scopes:
  ```java
  Maven.resolver().loadPomFromFile("/path/to/pom.xml").importDependencies(ScopeType.TEST, ScopeType.PROVIDED).resolve().withTransitivity().asFile();
  Maven.resolver().loadPomFromFile("/path/to/pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
  ```

#### Activate profiles:
By default, ShrinkWrap Resolvers activates profiles based on property value, file presence, active by default profiles, operating system and JDK.
  ```java
  Maven.resolver().loadPomFromFile("/path/to/pom.xml", "activate-profile-1", "!disable-profile-2").importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
  ```

### Version Range Resolution
The ShrinkWrap Resolver API allows for resolution of available versions info from a requested range. The [Maven documentation](http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html) specifies the version range syntax.
```java
final MavenVersionRangeResult versionRangeResult = Maven.resolver().resolveVersionRange("G:A:[1.0.0]");
```

### System properties

ShrinkWrap Resolvers allows you to override any programmatic configuration via System properties.

- `org.apache.maven.user-settings`: Path to user settings.xml file, which takes priority in merging over global settings if both are provided.

- `org.apache.maven.global-settings`: Path to global settings.xml file, merged with user settings if provided, with user settings taking priority.

- `settings.security`: Path to settings-security.xml, that contains encrypted master password for password protected Maven repositories.

- `org.apache.maven.offline`: Flag to indicate offline mode.

- `org.apache.maven.flattened-pom-path`: Path to the flattened variant of the regular pom.xml file. Default value is .flattened-pom.xml.

- `maven.repo.local`: Path to local repository with cached artifacts. Overrides value defined in any of the settings.xml files.

- `maven.legacyLocalRepo`: Flag whether to ignore origin tracking for artifacts present in local repository.
- `org.jboss.shrinkwrap.resolver.maven.skipCompilation`: Flag to skip compilation of resolved artifacts (true/false) - default is false.
- `org.jboss.shrinkwrap.resolver.maven.disableProjectLocal`: Flag to disable Maven 4 project-local repository (true/false) - default is false.
- `org.jboss.shrinkwrap.resolver.maven.ignoreDecryptionProblems`: Flag to ignore decryption problems in settings-security*.xml files (true/false) - default is false.


## Embedded Maven

Embedded Maven allows direct invocation of Maven builds from Java code, offering functionalities such as downloading and using desired Maven binaries, an uncluttered API for simple or complex builds, additional methods for build control (e.g., ignoring failures), and easy access to build outputs and artifacts.

### Downloading Maven Binaries

```java
// Downloads and uses the specified Maven version, cached in $HOME/.arquillian/resolver/maven/
EmbeddedMaven.forProject("path/to/pom.xml").useMaven3Version(String version);
// Uses Maven distribution from the specified URL. 
// If useCache is false, it downloads to ${project.directory}/target/resolver-maven/downloaded.
EmbeddedMaven.forProject("path/to/pom.xml").useDistribution(URL mavenDistribution, boolean useCache);
// Uses Maven installation located on the given path
EmbeddedMaven.forProject("path/to/pom.xml").useInstallation(File mavenHome);
// Uses local Maven installation available on your $PATH
EmbeddedMaven.forProject("path/to/pom.xml").useLocalInstallation();
// Uses the default Maven version, see DistributionStage#DEFAULT_MAVEN_VERSION
EmbeddedMaven.forProject("path/to/pom.xml").useDefaultDistribution();
```

### Additional Features

Embedded Maven provides features like skipping tests, ignoring failures, logging Maven build invocation commands, and accessing built projects and their artifacts.

```java
// Skips tests. Defaults to true.
EmbeddedMaven.forProject("path/to/pom.xml").skipTests(boolean skipTests);
// Sets logging level to DEBUG
EmbeddedMaven.forProject("path/to/pom.xml").setDebugLoggerLevel();
// Sets your own logger implementation. Use also to set other logger levels.
EmbeddedMaven.forProject("path/to/pom.xml").setLogger(InvokerLogger invokerLogger);
// Ignores Maven build failures. Instance of BuiltProject with non-zero value in mavenBuildExitCode is returned.
EmbeddedMaven.forProject("path/to/pom.xml").ignoreFailure().build();
```

### BuiltProject
BuiltProject is a Java class that represents a built project. An instance of this class is returned by the method build() when the Maven build is completed.

#### Examples

Package the project and get the default archive:
```java
Archive defaultArchive = EmbeddedMaven.forProject("path/to/pom.xml").setGoals("package").build().getDefaultBuiltArchive();
```

Get built project using goals and active profile:
```java
BuiltProject builtProject = EmbeddedMaven
        .forProject("path/to/pom.xml")
        .setGoals("clean", "package")
        .setProfiles("production")
        .build();
```

Get default archive or all archives in the build directory:
```java
Archive archive = builtProject.getDefaultBuiltArchive();
List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
```

Using Maven 3.1.0 to build a project with goal `install`, property `wildfly=true`, suppressing build output, and ignoring all failures:
```java
BuiltProject builtProject = EmbeddedMaven
    .forProject("path/to/pom.xml")
    .useMaven3Version("3.1.0")
    .setGoals("install")
    .addProperty("wildfly", "true")
    .setQuiet()
    .ignoreFailure()
    .build();
```


### Daemon Build

For running Maven builds in the background, Embedded Maven offers daemon build functionality, enabling asynchronous execution of builds and waiting until specific output lines are matched before proceeding.

```java
EmbeddedMaven.forProject("path/to/pom.xml").setGoals("spring-boot:run").useAsDaemon().build();
```

## Experimental Features

### Debugging

Obtain internal details of ShrinkWrap Resolver sessions for debugging purposes by casting the resolver object to MavenWorkingSessionContainer and retrieving the session.
  ```java
  MavenResolverSystem resolver = Maven.resolver();
  MavenWorkingSession session = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession();
  ```

### Logging

Configure Java Util Logging for increased verbosity, including logging interactions with Maven Repositories, by providing a `logging.properties` file and specifying its path with `-Djava.util.logging.config.file=/path/to/logging.properties`.

### ShrinkWrap Resolver Maven Plugin

Propagate settings specified on the command line into test execution using the `shrinkwrap-resolver-maven-plugin` in the `<build>` section of your `pom.xml`. 
```java
Maven.configureResolverViaPlugin().resolve("G:A").withTransitivity().asFile();
```

### Maven Importer

Maven Importer automates archive construction using your project's pom.xml. With minimal effort, it compiles sources, creates MANIFEST.MF, and retrieves dependencies.

```java
ShrinkWrap.create(MavenImporter.class)
  .loadPomFromFile("/path/to/pom.xml").importBuildOutput().as(WebArchive.class);

ShrinkWrap.create(MavenImporter.class)
  .loadPomFromFile("/path/to/pom.xml", "activate-profile-1", "!disable-profile-2")
  .importBuildOutput().as(WebArchive.class);

ShrinkWrap.create(MavenImporter.class).configureFromFile("/path/to/settings.xml")
  .loadPomFromFile("/path/to/pom.xml").importBuildOutput().as(JavaArchive.class);
```
### Gradle

Gradle Resolver and Gradle Importer are only experimental features, and therefore, the support for it is limited.

#### Gradle Resolver

Resolve dependencies defined in build.gradle files.

```java
final List<? extends Archive> archives = Gradle.resolver()
              .forProjectDirectory(".")
              .importCompileAndRuntime()
              .resolve()
              .asList(JavaArchive.class);
```

#### Gradle Importer

Import build outputs from Gradle projects

```java
ShrinkWrap.create(EmbeddedGradleImporter.class)
  .forThisProjectDirectory().importBuildOutput().as(WebArchive.class);

ShrinkWrap.create(EmbeddedGradleImporter.class)
  .forProjectDirectory("/path/to/dir").importBuildOutput("/path/to/result/war").as(WebArchive.class);

ShrinkWrap.create(EmbeddedGradleImporter.class)
  .forProjectDirectory("/path/to/dir").forTasks("task1","task2").withArguments("arg1","arg2")
  .importBuildOutput().as(WebArchive.class);
```

<sub>Gradle Importer full usage example can be found at [https://github.com/mmatloka/arquillian-gradle-sample](https://github.com/mmatloka/arquillian-gradle-sample).</sub>


### Issues and Improvements

If you encounter any issues or have suggestions for improving ShrinkWrap Resolver, please report them on our issue tracker at [SHRINKRES](https://issues.redhat.com/projects/SHRINKRES/issues) project.