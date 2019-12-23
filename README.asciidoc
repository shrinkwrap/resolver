= ShrinkWrap Resolvers image:https://travis-ci.org/shrinkwrap/resolver.svg["Build Status", link="https://travis-ci.org/shrinkwrap/resolver"]
:toc:

== Introduction to ShrinkWrap Resolvers

While ShrinkWrap offer a precise control of what is packaged into the deployment, it is difficult to be used with third party libraries. Often we don't control the construction of these libraries, and we certainly shouldn't be in the business of re-assembling them (and hence further differentiating our tests from the our production runtime deployments).  With the advent of Maven and other build systems, typically thirdparty libraries and our own dependent modules are obtained from a backing software _repository_.  In this case we supply a series of coordinates which uniquely identifies an artifact in the repository, and resolve the target files from there.

That is precisely the aim of the ShrinkWrap Resolvers project; it is a Java API to obtain artifacts from a repository system.  Currently implemented are grammars and support for Maven-based repository structures (this is separate from the use of Maven as a project management system or build tool; it's possible to use a Maven repository layout with other build systems) and basic support Gradle based projects.

ShrinkWrap Resolvers is comprised of the following modules:

|====
|_Name_|_Maven Coordinates_
|API|org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api
|SPI|org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi
|Maven API|org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven
|Maven SPI|org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi-maven
|Maven Implementation|org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven
|Maven Implementation with Archive Integration|org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven-archive
|Embedded Gradle API|org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-gradle-embedded-archive
|Embedded Gradle Implementation with Archive Integration|org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-gradle-embedded-archive
|====

The separation between the Maven and non-Maven modules is there to enforce modular design and separate out generic resolution from Maven-specific grammars, should the project support other mechanisms in the future.

== Adding ShrinkWrap Resolvers to your project

Obtaining ShrinkWrap Resolvers for use in your system can be done in a single pass by declaring a dependency upon the +depchain+ module in a Maven _pom.xml_:

[source,xml]
----
<dependencies>
    ...
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-depchain</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>test</scope>
      <type>pom</type>
    </dependency>
    ...
</dependencies>
----

This will bring the APIs into the test classpath and the SPIs and Implementation modules into the runtime classpaths (which will not be transitively inherited, as per Maven rules in +runtime+ scope).

Alternatively, you can have finer-grained control over using ShrinkWrap Resolvers by bringing in each module manually:

[source,xml]
----
 <dependencies>
    ...
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-api</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-spi</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-api-maven</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-spi-maven</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-impl-maven</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-impl-maven-archive</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>test</scope>
    </dependency>
    ...
  </dependencies>
----

[IMPORTANT]
====
If you happen to use Arquillian BOM, version prior 1.1.0.Final in +<dependencyManagement>+, it contains a ShrinkWrap Resolvers 1.x version. You must import ShrinkWrap Resolvers BOMs preceding Arquillian BOM in order to get 2.0.x version. Adding a ShrinkWrap BOM is recommended in any case.

ShrinkWrap resolved BOM can be imported via following snippet:

[source,xml]
----
<dependencyManagement>
  <dependencies>
    ...
    <!-- Override dependency resolver with latest version.
         This must go *BEFORE* the Arquillian BOM. -->
    <dependency>
      <groupId>org.jboss.shrinkwrap.resolver</groupId>
      <artifactId>shrinkwrap-resolver-bom</artifactId>
      <version>${version.shrinkwrap.resolvers}</version>
      <scope>import</scope>
      <type>pom</type>
    </dependency>
    ...
  </dependencies>
</dependencyManagement>
----
====

== Resolving dependencies

The general entry point for resolution is the convenience +org.jboss.shrinkwrap.resolver.api.maven.Maven+ class, which has static hooks to obtain a new +org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem+.
Let's cover most popular use cases for ShrinkWrap Resolver.


=== Resolution of artifacts specified by Maven coordinates

Maven coordinates, in their canonical form, are specified as following +groupId:artifactId:[packagingType:[classifier]]:version+. Often, those are referred as +G+ (groupId), +A+ (artifactId), +P+ (packagingType), +C+ (classifier) and +V+ (version). If you omit +P+ and +C+, they will get their default value, which is packaging of +jar+ and an empty classifier. ShrinkWrap Resolver additionally allows you to skip +V+ in case it has version information available, that would be explained later on.

Resolve a file using Maven coordinates::
Here, resolver locates artifact defined by +G:A:V+ and resolves it including all transitive dependencies. Result is formatted as array of +File+.
+
[source,java]
----
File[] files = Maven.resolver().resolve("G:A:V").withTransitivity().asFile();
----
+

Avoid transitive dependencies resolution::
You might want to change default Maven behavior and resolve only artifact specified by +G:A:V+, avoiding its transitive dependencies. For such use case, ShrinkWrap Resolvers provides a shorthand for changing resolution strategy, called +withoutTransitivity()+. Additionally, you might want to return a single +File+ instead of an array.
+
[source,java]
----
Maven.resolver().resolve("G:A:V").withoutTransitivity().asSingleFile();
----

Resolution of multiple artifacts::
Very often, you need to resolve more than one artifact. The method +resolve(String...)+ allows you to specify more artifacts at the same time. The result of the call will be an array of +File+ composed by artifacts defined by +G1:A1:V1+ and +G2:A2:V2+ including their transitive dependencies.
+
[source,java]
----
Maven.resolver().resolve("G1:A1:V1", "G2:A1:V1").withTransitivity().asFile();
----

Specifying dependency type::
Packaging type is specified by +P+ in +G:A:P:V+ coordinates description.
+
[source,java]
----
Maven.resolver().resolve("G:A:war:V").withTransitivity().asFile();
----
+
Packaging can be of any type, the most common are listed in following table.
+
.Packaging types
[width=80%]
|====
| jar | war | ear | ejb | rar | par | pom | test-jar | maven-plugin
|====

Specifying dependency classifier::
With classifier, such as +tests+, you need to include all +G:A:P:C:V+ parts of coordinates string.
+
[source,java]
----
Maven.resolver().resolve("G:A:test-jar:tests:V").withTransitivity().asFile();
----

Returning resolved artifacts as different type than file::
ShrinkWrap Resolvers provides shorthands for returning an +InputStream+ or +URL+ instead of +File+. Additionally, with +shrinkwrap-resolver-impl-maven-archive+, you can additionally return results +MavenCoordinate+ or as ShrinkWrap archives, such as +JavaArchive+, +WebArchive+ or +EnterpriseArchive+.
+
[source,java]
----
Maven.resolver().resolve("G:A:V").withTransitivity().as(File.class);
Maven.resolver().resolve("G:A:V").withTransitivity().as(InputStream.class);
Maven.resolver().resolve("G:A:V").withTransitivity().as(URL.class);
Maven.resolver().resolve("G:A:V").withTransitivity().as(JavaArchive.class);
Maven.resolver().resolve("G:A:war:V").withoutTransitivity().asSingle(WebArchive.class);
Maven.resolver().resolve("G:A:war:V").withTransitivity().as(MavenCoordinate.class);
----
+
[NOTE]
====
It's the responsibility of caller to close +InputStream+.
====

Working with artifact metadata::
Sometimes, you are more interested in metadata, such as dependencies of a given artifacts instead of artifact itself. ShrinkWrap Resolvers provides you an API for such use cases:
+
[source,java]
----
MavenResolvedArtifact artifact = Maven.resolver().resolve("G:A:war:V").withoutTransitivity()
  .asSingle(MavenResolvedArtifact.class);

MavenCoordinate coordinates = artifact.getCoordinate();
MavenArtifactInfo[] dependencies = artifact.getDependencies();
String version = artifact.getResolvedVersion();
ScopeType scope = artifact.getScope();
----
+
You can still retrieve resolved artifact from +MavenResolvedArtifact+:
+
[source,java]
----
File file = artifact.asFile();
----

Working with artifact coordinates::
You can also retrieve resolved artifact directly as +MavenCoordinate+, if you are not interested in more details:
+
[source,java]
----
MavenCoordinate[] coordinates = Maven.resolver().resolve("G:A:V")
   .withTransitivity().as(MavenCoordinate.class);
----

Resolution of artifacts as collection::
It might be convenient to work with +List+ interface instead of an array. For such cases, you can wrap the results of resolution using following call:
+
[source,java]
----
List<File> files = Maven.resolver().resolve("G:A:V")
    .withTransitivity().asList(File.class);
List<JavaArchive> jars = Maven.resolver().resolve("G:A:V")
    .withTransitivity().asList(JavaArchive.class);
List<MavenCoordinate> coordinates = Maven.resolver().resolve("G:A:V")
    .withTransitivity().asList(MavenCoordinate.class);
----
+

Transitive dependency exclusion::
In case you need to resolve an artifact while avoiding some of its dependencies, you can follow concept of +<exclusions>+ known for Maven. Following snippet shows how to exclude +G:B+ while resolving +G:A:V+.
+
[source,java]
----
Maven.resolver()
  .addDependencies(
    MavenDependencies.createDependency("G:A:V", ScopeType.COMPILE, false,
      MavenDependencies.createExclusion("G:B"))).resolve().withTransitivity().asFile();
----

Control resolution results by using a strategy::
In special cases, excluding a single dependency is not the behaviour you want to achieve. For instance, you want to resolve all test scoped dependencies of an artifact, you want to completely avoid some dependency while resolving multiple artifacts or maybe you're interested in optional dependencies. For those cases, ShrinkWrap Resolvers allows you to specify a +MavenResolutionStrategy+. For instance, you can exclude +G:B+ from +G:A:V+ (e.g. the same as previous examples) via following snippet:
+
[source,java]
----
Maven.resolver().resolve("G:A:V").using(new RejectDependenciesStrategy(false, "G:B")).asFile();
----
+
[NOTE]
====
Methods +withTransitivity()+ and +withoutTransitivity()+ are just a convenience methods to avoid you writing down strategy names. The first one calls +TransitiveStrategy+ while the latter calls +NotTransitiveStrategy+.
====
+
Strategies are composed of an array of +MavenResolutionFilter+ instances and +TransitiveExclusionPolicy+ instance. While defining the first allows you to transform dependency graph of resolved artifacts, the latter allows you to change default behavior when resolving transitive dependencies. By default, Maven does not resolve any dependencies in _provided_ and _test_ scope and it also skips _optional_ dependencies. ShrinkWrap resolver behaves the same way by default, but allows you to change that behaviour. This comes handy especially if when you want to for instance resolve all provided dependencies of +G:A:V+. For your convenience, ShrinkWrap Resolvers ships with strategies described in following table.
+
.Strategies available in ShrinkWrap Resolver
[cols="1,3"]
|====
| +AcceptAllStrategy+ |
Accepts all dependencies of artifacts. Equals +TransitiveStrategy+.

| +AcceptScopesStrategy+ |
Accepts only dependencies that have defined scope type.

| +CombinedStrategy+ |
This allows you to combine multiple strategies together. The behaviour defined as logical AND between combined strategies.

| +NonTransitiveStrategy+ |
Rejects all dependencies that were not directly specified for resolution. This means that all transitive dependencies of artifacts for resolution are rejected.

| +RejectDependenciesStrategy+ |
Rejects dependencies defined by +G:A+ (version is not important for comparison, so it can be omitted altogether). By default, it is transitive: +RejectDependenciesStrategy("G:A", "G:B")+ means that all dependencies that origin at +G:A+ or +G:B+ are removed as well. If you want to change that behavior to reject defined dependencies but to keep their descendants, instantiate strategy as following: +RejectDependenciesStrategy(false, "G:A", "G:B")+

| +TransitiveStrategy+ |
Acceps all dependencies of artifacts. Equals +AcceptAllStrategy+.

|====

Control sources of resolution::
ShrinkWrap Resolvers allows you to specify where do you want to resolve artifacts from. By default, it uses classpath (also known as Maven Reactor) and Maven Central repository, however you can programmatically alter the behavior.
+
[source,java]
----
Maven.configureResolver().withClassPathResolution(false).resolve("G:A:V").withTransitivity().asFile();
Maven.configureResolver().withMavenCentralRepo(false).resolve("G:A:V").withTransitivity().asFile();
Maven.configureResolver().workOffline().resolve("G:A:V").withTransitivity().asFile();
Maven.configureResolver().useLegacyLocalRepo(true).resolve("G:A:V").withTransitivity().asFile();
----
+
While classpath resolution is handy for testing SNAPSHOT artifacts that are not yet installed in any of the Maven repository, making ShrinkWrap Resolvers offline avoids accessing any repositories but local cache. You can also set to ignore origin of artifacts present in local repository via +useLegacyLocalRepo(true)+ method.
+
[NOTE]
====
If offline mode is activated, original of artifacts in local repository is automatically ignored. This is a difference from default Maven behavior. See https://cwiki.apache.org/confluence/display/MAVEN/Maven+3.x+Compatibility+Notes#Maven3.xCompatibilityNotes-ResolutionfromLocalRepository[Legacy local repository] for further reference.
====


Specify settings.xml::
While controlling classpath resolution and Maven Central comes handy, sometimes you might want to specify completely different _settings.xml_ file than default for your test execution. This can be done via following API calls:
+
[source,java]
----
Maven.configureResolver().fromFile("/path/to/settings.xml")
  .resolve("G:A:V").withTransitivity().asFile();

Maven.configureResolver().fromClassloaderResource("path/to/settings.xml")
  .resolve("G:A:V").withTransitivity().asFile();
----
+
[WARNING]
====
ShrinkWrap Resolvers will not consume settings.xml you specified on command line (+-s settings.xml+) or in the IDE. It reads settings.xml files at their standard locations, which are +~/.m2/settings.xml+ and +$M2_HOME/conf/settings.xml+ unless overridden in the API or via System property.
====

Define Maven repositories manually::
Ultimately, it is possible to define and/or override Maven repositories defined in _settings.xml_ or _pom.xml_. Repositories defined via API always take precedence. In case there is a repository with same *id* configured in either _settings.xml_ or _pom.xml_ file, it will be ignored.
+
[source,java]
----
Maven.configureResolver().withRemoteRepo("my-repository-id", "url://to/my/repository", "layout")
  .resolve("G:A:V").withTransitivity().asFile();

Maven.configureResolver().withRemoteRepo(MavenRemoteRepositories.createRemoteRepository("my-repository-id", "url://to/my/repository", "layout"))
  .resolve("G:A:V").withTransitivity().asFile();
----

=== Resolution of artifacts defined in POM files

While previous calls allow you to manually define what you want to resolve, in Maven projects, you have very likely specified this information already, in you _pom.xml_ file. ShrinkWrap Resolvers allows you to follow _DRY_ principle and it is able to load metadata included there.

ShrinkWrap Resolvers constructs so called effective POM model (simplified, that is your _pom.xml_ file plus parent hierarchy and Super POM, Maven default POM file). In order to construct the model, it uses all local repository, classpath repository and remote repositories. Once the model is loaded, you can use the metadata in there to be automatically added to artifacts to be resolved.

[TIP]
====
You can use Maven.configureResolver() to tune what repositories will be questioned during effective POM model construction.
====

Resolving an artifact with version defined in effective POM::
In case, you want to resolve +G:A:V+, you can simply specify +G:A+ instead. For artifacts with non JAR packaging type or classifier, you must use alternative syntax with question mark '+?+', such as +G:A:P:?+ or +G:A:P:C:?+.
+
[source,java]
----
Maven.resolver().loadPomFromFile("/path/to/pom.xml").resolve("G:A").withTransitivity().asFile();

Maven.resolver().loadPomFromClassLoaderResource("/path/to/pom.xml").resolve("G:A:P:?")
  .withTransitivity().asFile();
----

Resolving artifacts defined in effective POM::
ShrinkWrap Resolvers allows you to import artifacts from your POM file, select them by specific scopes and resolve them. This way, you don't need to resolve every single dependency separately or alter your tests if you change dependencies of your application. You can either use +importDependencies(ScopeType...)+ or convenience methods, that cover the most frequent usages (+importCompileAndRuntimeDependencies()+, +importRuntimeDependencies()+, +importTestDependencies()+ and +importRuntimeAndTestDependencies()+):
+
[source,java]
----
Maven.resolver().loadPomFromFile("/path/to/pom.xml")
  .importDependencies(ScopeType.TEST, ScopeType.PROVIDED)
  .resolve().withTransitivity().asFile();

Maven.resolver().loadPomFromFile("/path/to/pom.xml").importRuntimeDependencies()
  .resolve().withTransitivity().asFile();
----
+
[TIP]
====
Runtime in convenience methods means all the Maven scopes that are used in application runtime, which are +compile+, +runtime+, +import+ and +system+. If you need to select according to Maven scopes, go for +importDependencies(ScopeType...)+ instead.
====

Specifying profiles to be activated::
By default, ShrinkWrap Resolvers activates profiles based on property value, file presence, active by default profiles, operating system and JDK. However, you can force profiles in same way as you would do via +-P+ in Maven.
+
[source,java]
----
Maven.resolver().loadPomFromFile("/path/to/pom.xml", "activate-profile-1", "!disable-profile-2")
        .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
----

=== Version Range Resolution

The ShrinkWrap Resolver API allows for resolution of available versions info from a requested range. The http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html[Maven documentation] specifies the version range syntax; examples of obtaining info about versions greater or equal to 1.0.0 for a specific coordinate is presented below.

[source,java]
----
final MavenVersionRangeResult versionRangeResult = Maven.resolver().resolveVersionRange("G:A:[1.0.0]");
----
+MavenVersionRangeResult+ provides three methods:

- +getLowestVersion()+ for obtaining the lowest resolved version coordinate,
- +getHighestVersion()+ for the highest version,
- +getVersions()+ which returns a +List+ of obtained coordinates, ordered from lowest to highest version.

[source,java]
----
final MavenCoordinate lowest = versionRangeResult.getLowestVersion();
final MavenCoordinate highest = versionRangeResult.getHighestVersion();
final List<MavenCoordinate> versions = versionRangeResult.getVersions();
----

=== System properties

ShrinkWrap Resolvers allows you to override any programmatic configuration via System properties.

.System properties altering behavior of ShrinkWrap Resolvers
[cols="1,2"]
|====
| +org.apache.maven.user-settings+ |
Path to user  _settings.xml_ file. In case +org.apache.maven.global-settings+ settings is provided too, they both are merged, user one has the priority.

| +org.apache.maven.global-settings+ |
Path to global _settings.xml_ file. In case +org.apache.maven.user-settings+ settings is provided too, they are merged, user one has the priority.

| +settings.security+ (prior 2.2.0 +org.apache.maven.security-settings+) |
Path to _settings-security.xml_, that contains encrypted master password for password protected Maven repositories.

| +org.apache.maven.offline+ |
Flag there to work in offline mode.

| +org.apache.maven.flattened-pom-path+ |
Path to the https://www.mojohaus.org/flatten-maven-plugin/index.html[flattened] variant of a regular _pom.xml_. Default value: _.flattened-pom.xml_

To support https://maven.apache.org/maven-ci-friendly.html["Maven CI Friendly Versions"], the classpath resolution mechanism resolves this path relative
to the regular _pom.xml_ to look for the preferred flattened variant which contains the interpolated version string instead of just e.g. _${revision}_. 

Example: _target/my-flat-pom.xml_ would resolve to _/foo/bar/target/my-flat-pom.xml_ in case the regular file is located in _/foo/bar/pom.xml_.

| +maven.repo.local+ |
Path to local repository with cached artifacts. Overrides value defined in any of the _settings.xml_ files.

| +maven.legacyLocalRepo+ |
Flag whether to ignore origin tracking for artifacts present in local repository
|====


== Embedded Maven

You probably know the cases when you have to build some project before running another one or before running tests to use a created archive. Maven Importer provided by ShrinkWrap Resolver can partially help you with it - it compiles the classes and collects dependencies from the pom.xml file. However, you cannot use Maven plugins, profiles or some variables as it doesn't do the real Maven build - it just tries to simulate it. You can definitely imagine a situation that you don't have any Maven binary installed on you PC or that you need different Maven version for one specific build. That's why ShrinkWrap Resolver introduces a new feature: Embedded Maven.

[%hardbreaks]
Embedded Maven provides you a possibility to invoke a Maven build of some project directly from your Java code. Internally, it uses http://maven.apache.org/shared/maven-invoker/[maven-invoker] and mainly the classes https://maven.apache.org/components/shared/maven-invoker/apidocs/org/apache/maven/shared/invoker/Invoker.html[Invoker] and https://maven.apache.org/components/shared/maven-invoker/apidocs/org/apache/maven/shared/invoker/InvocationRequest.html[InvocationRequest], which basically offers the functionality of running Maven builds from the Java code.
So now there can arise some questions: Why should I use Embedded Maven? What are the benefits?
There are bunch of functions added to make the usage more user friendly. The most significant additional functions are:

* downloading and using Maven binaries that the user desires

* uncluttered API (you can write code that runs either trivial or complex builds on one single line)

* additional methods & functions (eg. for ignoring build failures or making the build output quiet)

* Java class representing a build project

* easy getting a ShrinkWrap Archive created by the build

* automatic functions such as skipping tests and formatting a build output

* possibility to use one's Invoker and InvocationRequest instances

* and more ...


=== How to use it?

Your starting point is a class link:/maven/api-maven-embedded/src/main/java/org/jboss/shrinkwrap/resolver/api/maven/embedded/EmbeddedMaven.java[EmbeddedMaven] which offers you three methods. At this point you have to decide which approach of setting Maven build options you want to use.

1. You can use ShrinkWrap Resolver API that offers you additional features and more comfortable but slightly limited way. This approach is linked with these two methods:
+
[source,java]
....
EmbeddedMaven.forProject(File pomFile)
EmbeddedMaven.forProject(String pomFile)
....
+
where you have to specify a POM file of a project you want to build.
+
Why it is limited? Contrary to second approach or to the pure maven-invoker:
+
- you can set neither output handler nor error handler because it is already set by ShrinkWrap Resolver. On the other hand, it has three positive effects:
		I) the output is automatically formatted (with a prefix "->" to make the output visibly separated)
		II) after the completion, the build output is accessible using method link:/maven/api-maven-embedded/src/main/java/org/jboss/shrinkwrap/resolver/api/maven/embedded/BuiltProject.java#L92[BuiltProject#getMavenLog()]
		III) you can easily suppress the build output using method link:/maven/api-maven-embedded/src/main/java/org/jboss/shrinkwrap/resolver/api/maven/embedded/pom/equipped/ConfigurationStage.java#L338[ConfigurationStage#setQuiet()]
- you cannot set a project you want to build by setting base directory and a file name separately.
- there are no methods for setting Maven home and binaries, because it is set by ShrinkWrap Resolver itself.
+
+


2. In the second approach, you can use your own Invoker and InvocationRequest instances. If you use it, then it is expected that all settings is done by yourself so no automatic features are provided by ShrinkWrap Resolver. This approach is linked with the method:
+
[source,java]
....
EmbeddedMaven.withMavenInvokerSet(InvocationRequest request, Invoker invoker)
....
+
[%hardbreaks]
Why it is less comfortable? You can see the differences in these two test cases that does completely the same thing but using different approaches: link:./maven/impl-maven-embedded/src/test/java/org/jboss/shrinkwrap/resolver/impl/maven/embedded/pom/equipped/PomEquippedEmbeddedMavenForJarSampleTestCase.java[first approach] link:./maven/impl-maven-embedded/src/test/java/org/jboss/shrinkwrap/resolver/impl/maven/embedded/invoker/equipped/InvokerEquippedEmbeddedMavenForJarSampleTestCase.java[second approach]
These are the disadvantages:
+
* methods such as setGoals and setProfiles accept only a list of string.
* you have to set the property `skipTests` for each InvocationRequest if you don't want to run the tests.
* you don't have an access to the Maven build output after the build completion
* the build output is not automatically formatted and it cannot be easily suppressed
* the methods for setting Maven home or binaries are accessible in Invoker object, but it is advised not to use them as the Maven home is used by ShrinkWrap Resolver


=== Downloading Maven binaries
In cases when there is no Maven binaries installed on the machine or when another Maven version is needed for some specific build, you can ask ShrinkWrap Resolver to download the specific version from the Apache web pages and use it. For this purpose there is a method:
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").useMaven3Version(String version)
....
[%hardbreaks]
where the desired version is expected (eg: `useMaven3Version("3.3.3")`). This version is downloaded from Apache web pages and the downloaded zip is cached in a directory `$HOME/.arquillian/resolver/maven/` to not download it over and over again. Zip file is extracted in `${project.directory}/target/resolver-maven/${file_md5hash}` and the path to the extracted binaries is set as Maven home applicable for the build.
There are three more methods for setting Maven binaries that should be used for the build.
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").useDistribution(URL mavenDistribution, boolean useCache)
....
[%hardbreaks]
where you need to specify a URL the distribution should be downloaded from. You should also specify if the cache directory should be used. If `useCache` is `false`, then the zip file is downloaded into `${project.directory}/target/resolver-maven/downloaded`.
Next method
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").useInstallation(File mavenHome)
....
[%hardbreaks]
uses Maven installation located on the given path.
And the method:
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").useLocalInstallation()
....
uses local Maven installation that is available on your PATH.

==== Default Maven binary

If no version, distribution nor installation is specified, then EmbeddedMaven uses the default version, which is currently `3.3.9`.
The very same result (downloading & using default Maven binary version) you can achieve by using method:
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").useDefaultDistribution()
....

=== Explanation of additional features:

====== Skipping tests:
Using ShrinkWrap Resolver API approach, there is no need to set the `skipTests` property if you don't want to run any test as it is set automatically. If you still want to run tests, then you can use method: link:/maven/api-maven-embedded/src/main/java/org/jboss/shrinkwrap/resolver/api/maven/embedded/pom/equipped/ConfigurationStage.java#L155[ConfigurationStage#skipTests(false)]

====== Ignoring failures:
If the Maven build fails, then an `IllegalStateException` is thrown by default. If you use method link:/maven/api-maven-embedded/src/main/java/org/jboss/shrinkwrap/resolver/api/maven/embedded/BuildStage.java#L45[BuildStage#ignoreFailure()], then failures of the Maven build is ignored and a BuiltProject instance with a non-zero value stored in mavenBuildExitCode variable is returned.


=== Maven build invocation command & Logging

To display command that is used for Maven build invocation you need to set logging to `DEBUG` level. You can simply do it using method:
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").setDebugLoggerLevel()
....
This method takes the current logger and sets the threshold to the corresponding level.

MavenInvoker uses its own logger - https://maven.apache.org/shared/maven-invoker/apidocs/org/apache/maven/shared/invoker/InvokerLogger.html[InvokerLogger] - to set your own logger implementation or to set other logger level, you have to use method:
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").setLogger(yourInvokerLogger)
....


=== BuiltProject
link:/maven/api-maven-embedded/src/main/java/org/jboss/shrinkwrap/resolver/api/maven/embedded/BuiltProject.java[BuiltProject] is a Java class that represents a built project. An instance of this class is returned by the method `build()` when the Maven build is completed. The most useful method is probably:
[source,java]

....
builtProject.getDefaultBuiltArchive()
....
that returns an archive with a default name that was created by the Maven build. As a "default archive name" is understood:

* either combination of artifactId + version + packaging suffix (eg.
* or a finalName set in `<build>` section of project's POM file + packaging suffix

if no archive with a corresponding name is found, then `null` is returned. `Null` is returned also for project with `packaging=pom` as it is usually a parent project with a set of modules. To get all modules that are specified use the method:
[source,java]
....
builtProject.getModules()
....
which returns list of BuiltProject instances. If you know the name (string within an element `<module>` in the parent's POM file) of a module you are interested in, you can use:
[source,java]
....
builtProject.getModule(String moduleName)
....
There are several other useful methods provided by this Java class. For more information see link:/maven/api-maven-embedded/src/main/java/org/jboss/shrinkwrap/resolver/api/maven/embedded/BuiltProject.java[BuiltProject]

=== Examples
First example is just package a project and get the default archive:
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").setGoals("package").build().getDefaultBuiltArchive();
....
Then let's say that we want to build some project using goals `clean` and `package` and with activated profile `production`:
[source,java]
....
BuiltProject builtProject = EmbeddedMaven
                                .forProject("path/to/pom.xml")
				.setGoals("clean", "package")
				.setProfiles("production")
				.build();
....
Then you can get the default archive:
[source,java]
....
Archive archive = builtProject.getDefaultBuiltArchive();
....
or all Java archives, that are contained in the build directory:
[source,java]
....
List<JavaArchive> javaArchives = builtProject.getArchives(JavaArchive.class);
....

Let's say that we want to use Maven 3.1.0 for building a project with a goal `install` and property `wildfly=true`. We also don't want to display the build output a we want to ignore all possible build failures:
[source,java]
....
EmbeddedMaven
    .forProject("path/to/pom.xml")
    .useMaven3Version("3.1.0")
    .setGoals("install")
    .addProperty("wildfly", "true")
    .setQuiet()
    .ignoreFailure()
    .build();
....
Some additional examples can be found in integration tests link:/maven/impl-maven-embedded/src/test/java/org/jboss/shrinkwrap/resolver/impl/maven/embedded/pom/equipped[here] and link:/maven/impl-maven-embedded/src/test/java/org/jboss/shrinkwrap/resolver/impl/maven/embedded/invoker/equipped[here].


=== Daemon build
In some cases, you need to run a Maven build in the background. The first case is when the build itself starts some application (eg. `mvn spring-boot:run`). The second case is when you are building a project that is too big so can do some other operation in the meantime let the Maven build run in the background.
In these cases you can specify that the build should be used as a daemon, which means that ShrinkWrap Resolver runs the build in a new separated thread:
[source,java]
....
EmbeddedMaven.forProject("path/to/pom.xml").setGoals("spring-boot:run").useAsDaemon().build();
....
In the case of running some application using the Maven build, it could be useful to wait till the application is started. To do so, use one of the methods:
[source,java]
....
...useAsDaemon().withWaitUntilOutputLineMathes(".*Started Application.*").build();
...useAsDaemon().withWaitUntilOutputLineMathes(".*Started Application.*", 50, TimeUnit.SECONDS).build();
....
In the first case, it waits until some line matches the given regex. If there is no line matched within the default timeout (which is two minutes) then TimeoutException is thrown.
The second case is the same but it sets the timeout to given value.
ShrinkWrap Resolver stops the main thread and waits until some line matches the given regex and then continues. The build itself continues running as well.

In the case you need to build a huge project and let it run on the background and then come back to it later, you can use the object `DaemonBuild` that is returned by the method `build()`:
....
DaemonBuild build = EmbeddedMaven.forProject("path/to/pom.xml").setGoals("package").useAsDaemon().build();
....
This object offers a methods `isAlive()` that says if the thread containing the Maven build is alive or not; and a method `getBuiltProject()` that returns an instance of `BuiltProject` when the thread is not alive, `null` otherwise.


== Experimental features

[WARNING]
====
Following features are in their early development stages. However, they should work for the most common use case. Feel free to report a bug in https://issues.jboss.org/browse/SHRINKRES[SHRINKRES] project if that not your case.
====

=== Debugging and logging

ShrinkWrap Resolver allows you to get internal details of its session. This is handy especially if you are resolving artifacts from a pom file or if you are interested what dependency coordinates will have their version automatically resolved in tests. In order to get access to internal data, perform cast of resolver object (in any stage) to +MavenWorkingSessionContainer+ and retrieve the session. _Important: +MavenWorkingSession+ represents an interal API and can be changed in future versions. Use it only for debugging or in ShrinkWrap Resolver extensions, interacting with the session from tests should be avoided._

[source,java]
----
MavenResolverSystem resolver = Maven.resolver();
MavenWorkingSession session = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession();
----

ShrinkWrap Resolvers uses Java Util Logging for logging purposes. If you want to increase verbosity, provide _logging.properties_ file and make sure it is loaded in Java VM by specifying +-Djava.util.logging.config.file=/path/to/logging.properties+. See following example, which enables logging of interaction with Maven Repositories into console output:

[source,properties]
----
# Specify the handlers to create in the root logger
# (all loggers are children of the root logger)
# The following creates two handlers
handlers= java.util.logging.ConsoleHandler

# Set the default logging level for new ConsoleHandler instances
java.util.logging.ConsoleHandler.level= FINEST

# Set global verbose level
.level= INFO

# Set log verbose level for ShrinkWrap Resolvers
org.jboss.shrinkwrap.resolver.impl.maven.logging.LogTransferListener.level= FINEST
org.jboss.shrinkwrap.resolver.impl.maven.logging.LogRepositoryListener.level= FINEST
org.jboss.shrinkwrap.resolver.impl.maven.logging.LogModelProblemCollector.level= FINEST

----

=== ShrinkWrap Resolver Maven Plugin

ShrinkWrap Resolver Maven plugin allows you to propagate settings you specified on command line into test execution. Settings comprises of: paths to the _pom.xml_ file and _settings.xml_ files, activated/disabled profiles, offline flag and path to local repository. No support for IDE exists at this moment.

In order to activate the plugin, you need to add following snippet into +<build>+ section of your _pom.xml_ file.

[source,xml]
----
<plugin>
  <groupId>org.jboss.shrinkwrap.resolver</groupId>
  <artifactId>shrinkwrap-resolver-maven-plugin</artifactId>
  <version>${version.shrinkwrap.resolvers}</version>
  <executions>
    <execution>
      <goals>
        <goal>propagate-execution-context</goal>
      </goals>
    </execution>
  </executions>
</plugin>
----

Then, in your test you can do the following:

[source,java]
----
Maven.configureResolverViaPlugin().resolve("G:A").withTransitivity().asFile();
----

=== Maven Importer

MavenImporter is the most advanced feature of ShrinkWrap Resolvers. Instead of you being responsible for specifying how testing archive should look like, it reuses information defined in your _pom.xml_ in order to construct the archive. So, no matter how your project looks like, you can get a full archive, as you would deploy it into the application server within a single line of code.

MavenImporter is able to compile sources, construct _MANIFEST.MF_, fetch the dependencies and construct archive as Maven would do. It does not required any data to be prepared by Maven, however it can profit from those if they exist.

[source,java]
----
ShrinkWrap.create(MavenImporter.class)
  .loadPomFromFile("/path/to/pom.xml").importBuildOutput().as(WebArchive.class);

ShrinkWrap.create(MavenImporter.class)
  .loadPomFromFile("/path/to/pom.xml", "activate-profile-1", "!disable-profile-2")
  .importBuildOutput().as(WebArchive.class);

ShrinkWrap.create(MavenImporter.class).configureFromFile("/path/to/settings.xml")
  .loadPomFromFile("/path/to/pom.xml").importBuildOutput().as(JavaArchive.class);
----

[IMPORTANT]
====
Maven Importer does not currently support other packagings but JAR and WAR. Also, it does not honor many of Maven plugins, currently it supports their limited subset.
====

=== Gradle Resolver

Initial support for resolving dependencies defined in `build.gradle` file is supported.
Because of nature of `Gradle Tooling API` currently Gradle resolver can only resolve dependencies by scope.

[source, java]
----
final List<? extends Archive> archives = Gradle.resolver()
              .forProjectDirectory(".")
              .importCompileAndRuntime()
              .resolve()
              .asList(JavaArchive.class);
----

Previous snippet resolve all compile and runtime dependencies from root project directory providing them as a list of `JavaArchive`.

=== Gradle Importer

Gradle Importer realizes functions similar to Maven Importer however for Gradle using Gradle Tooling API. It includes support for multi-module projects.
Importer is configured to execute by default `build --exclude-task test`, what means it skips the tests execution. It's possible to alter this behaviour
just by using appropriate API methods like e.g. +forTasks+ or +withArguments+.

[source,java]
----
ShrinkWrap.create(EmbeddedGradleImporter.class)
  .forThisProjectDirectory().importBuildOutput().as(WebArchive.class);

ShrinkWrap.create(EmbeddedGradleImporter.class)
  .forProjectDirectory("/path/to/dir").importBuildOutput("/path/to/result/war").as(WebArchive.class);

ShrinkWrap.create(EmbeddedGradleImporter.class)
  .forProjectDirectory("/path/to/dir").forTasks("task1","task2").withArguments("arg1","arg2")
  .importBuildOutput().as(WebArchive.class);
----

If you execute some custom tasks which modifies the build result you might want to perform Gradle Importer build in a custom directory. To do this you need
to pass an argument `-PbuildDir=your-build-directory` and then use the `importBuildOutput("your-build-directory/libs/your-output-file")` method.

Additional Gradle Importer full usage example can be found under https://github.com/mmatloka/arquillian-gradle-sample .

=== Other

Additionally, using different JDK for running tests and compiling sources is not supported, although it should work if you are for instance compiling sources targeting JDK6 while being bootstrapped on JDK7.

Sometimes running tests from IDE might not work correctly. The most common cause is the working directory is set to project when it should be the module.
