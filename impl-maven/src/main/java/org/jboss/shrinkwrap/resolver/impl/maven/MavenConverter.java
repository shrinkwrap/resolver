/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Repository;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenRepository;
import org.jboss.shrinkwrap.resolver.impl.maven.util.StringUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactType;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.Proxy;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.util.artifact.ArtifactProperties;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.DefaultArtifactType;

/**
 * An utility class which provides conversion between Maven and Aether objects. It allows creation of Aether object from
 * different objects than Maven objects as well.
 *
 * @author Benjamin Bentmann
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MavenConverter {

    private static final Logger log = Logger.getLogger(MavenConverter.class.getName());

    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?(:([^: ]+))?");

    private static final int DEPENDENCY_GROUP_ID = 1;
    private static final int DEPENDENCY_ARTIFACT_ID = 2;
    private static final int DEPENDENCY_TYPE_ID = 4;
    private static final int DEPENDENCY_CLASSIFIER_ID = 6;
    private static final int DEPENDENCY_VERSION_ID = 8;

    private static final Pattern EXCLUSION_PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?");

    private static final int EXCLUSION_GROUP_ID = 1;
    private static final int EXCLUSION_ARTIFACT_ID = 2;
    private static final int EXCLUSION_TYPE_ID = 4;
    private static final int EXCLUSION_CLASSIFIER_ID = 6;

    // disable instantiation
    private MavenConverter() {
        throw new AssertionError("Utility class MavenConverter cannot be instantiated.");
    }

    /**
     * Converts String coordinates to a MavenDependency representation
     *
     * @param coordinates The coordinates to be parsed
     * @return The MavenDepedency based on coordinates
     */
    public static MavenDependencyImpl asDependency(String coordinates) {

        Matcher m = DEPENDENCY_PATTERN.matcher(coordinates);
        if (!m.matches()) {
            throw new ResolutionException("Bad artifact coordinates"
                    + ", expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]");
        }

        MavenDependencyImpl mavenDependency = new MavenDependencyImpl();
        mavenDependency.setGroupId(m.group(DEPENDENCY_GROUP_ID));
        mavenDependency.setArtifactId(m.group(DEPENDENCY_ARTIFACT_ID));

        String type = m.group(DEPENDENCY_TYPE_ID);
        String classifier = m.group(DEPENDENCY_CLASSIFIER_ID);
        String version = m.group(DEPENDENCY_VERSION_ID);

        // some logic with numbers of provided groups
        int noOfColons = StringUtil.numberOfOccurences(coordinates, ':');

        if (noOfColons == 1) {
            mavenDependency.setVersion("?");
        } else if (noOfColons == 2) {
            mavenDependency.setVersion(type);
        } else if (noOfColons == 3) {
            mavenDependency.setType(type);
            mavenDependency.setVersion(classifier);
        } else {
            mavenDependency.setType(type);
            mavenDependency.setClassifier(classifier);
            mavenDependency.setVersion(version);
        }

        return mavenDependency;
    }

    /**
     * Tries to resolve artifact version from internal dependencies from a fetched POM file. If no version is found, it simply
     * returns original coordinates
     *
     * @param dependencyManagement The map including dependency information retrieved from the POM file
     * @param coordinates The coordinates excluding the {@code version} part
     * @return Either coordinates with appended {@code version} or original coordinates
     */
    public static MavenDependency asDepedencyWithVersionManagement(Set<MavenDependency> dependencyManagement, String coordinates) {

        MavenDependencyImpl dependency = asDependency(coordinates);

        if ("?".equals(dependency.getVersion()) && dependencyManagement.contains(dependency)) {

            // get the dependency from internal dependencyManagement
            MavenDependency internal = null;
            Iterator<MavenDependency> it = dependencyManagement.iterator();
            while (it.hasNext()) {
                internal = it.next();
                if (internal.equals(dependency)) {
                    break;
                }
            }

            // safely convert MavenDependency to MavenDependencyImpl
            MavenDependencyImpl resolved = MavenConverter.asDependency(internal.coordinates());
            String version = resolved.getVersion();
            log.fine("Resolved version " + version + " from the POM file for the artifact: " + coordinates);
            dependency.setVersion(resolved.getVersion());
        }

        return dependency;
    }

    /**
     * Converts MavenDepedency to Dependency representation used in Aether
     *
     * @param dependency the Maven dependency
     * @return the corresponding Aether dependency
     */
    public static Dependency asDependency(MavenDependency dependency) {
        return new Dependency(asArtifact(dependency.coordinates()), dependency.scope(), dependency.optional(),
                asExclusions(Arrays.asList(dependency.exclusions())));
    }

    public static List<Dependency> asDependencies(List<MavenDependency> dependencies) {
        List<Dependency> list = new ArrayList<Dependency>(dependencies.size());
        for (MavenDependency d : dependencies) {
            list.add(asDependency(d));
        }

        return list;
    }

    public static Artifact asArtifact(String coordinates) throws ResolutionException {

        Validate.notNullOrEmpty(coordinates, "Cannot create artifact from empty coordinates.");

        if (coordinates.endsWith("?")) {
            throw new ResolutionException("Unable to create artifact from coordinates "
                    + coordinates.substring(0, coordinates.length() - 2)
                    + ", version information is not available. Check the POM file you're loading and specified coordinates.");
        }

        try {
            return new DefaultArtifact(coordinates);
        } catch (IllegalArgumentException e) {
            throw new ResolutionException("Unable to create artifact from invalid coordinates " + coordinates, e);
        }
    }

    /**
     * Converts string coordinates to Aether exclusion object
     *
     * @param coordinates Coordinates specified in the format specified in the format
     *        {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}, an empty string or {@code *} will match all
     *        exclusions, you can pass an {@code *} instead of any part of the coordinates to match all possible values
     * @return Exclusion object based on the coordinates
     * @throws ResolutionException If coordinates cannot be converted
     */
    public static Exclusion asExclusion(String coordinates) {
        Validate.notNull(coordinates, "Exclusion string must not be null");

        if (coordinates.length() == 0 || coordinates.equals("*")) {
            return new Exclusion("*", "*", "*", "*");
        }

        Matcher m = EXCLUSION_PATTERN.matcher(coordinates);
        if (!m.matches()) {
            throw new ResolutionException("Bad exclusion coordinates"
                    + ", expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]]");
        }

        String group = m.group(EXCLUSION_GROUP_ID);
        String artifact = m.group(EXCLUSION_ARTIFACT_ID);
        String type = m.group(EXCLUSION_TYPE_ID);
        String classifier = m.group(EXCLUSION_CLASSIFIER_ID);

        group = (group == null || group.length() == 0) ? "*" : group;
        artifact = (artifact == null || artifact.length() == 0) ? "*" : artifact;
        type = (type == null || type.length() == 0) ? "*" : type;
        classifier = (classifier == null || classifier.length() == 0) ? "*" : classifier;

        return new Exclusion(group, artifact, classifier, type);
    }

    /**
     * Converts a collection of string coordinates to Aether exclusions objects
     *
     * @param coordinates A collection of coordinates specified in the format specified in the format
     *        {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]}
     * @return List of Exclusion objects based on the coordinates
     * @throws ResolutionException If coordinates cannot be converted
     */
    public static List<Exclusion> asExclusions(Collection<String> coordinates) {
        List<Exclusion> list = new ArrayList<Exclusion>(coordinates.size());
        for (String coords : coordinates) {
            list.add(asExclusion(coords));
        }
        return list;
    }

    public static String fromExclusion(org.apache.maven.model.Exclusion exclusion) {
        StringBuilder sb = new StringBuilder();
        sb.append(exclusion.getGroupId()).append(":");
        sb.append(exclusion.getArtifactId());

        return sb.toString();
    }

    public static String fromExclusion(Exclusion exclusion) {
        StringBuilder sb = new StringBuilder();
        sb.append(exclusion.getGroupId()).append(":");
        sb.append(exclusion.getArtifactId());

        String type = exclusion.getExtension();
        if (type != null && type.length() != 0) {
            sb.append(":").append(type);
        }

        String classifier = exclusion.getClassifier();
        if (classifier != null && classifier.length() != 0) {
            sb.append(":").append(classifier);
        }

        return sb.toString();
    }

    public static Collection<String> fromExclusions(Collection<Exclusion> exclusions) {
        List<String> list = new ArrayList<String>(exclusions.size());
        for (Exclusion e : exclusions) {
            list.add(fromExclusion(e));
        }
        return list;
    }

    public static String fromArtifact(Artifact artifact) {
        StringBuilder sb = new StringBuilder();
        sb.append(artifact.getGroupId()).append(":");
        sb.append(artifact.getArtifactId()).append(":");

        String extension = artifact.getExtension();
        sb.append(extension.length() == 0 ? "jar" : extension).append(":");
        String classifier = artifact.getClassifier();
        if (classifier.length() != 0) {
            sb.append(classifier).append(":");
        }
        sb.append(artifact.getVersion());

        return sb.toString();
    }

    public static MavenDependency fromDependency(Dependency dependency) {
        MavenDependencyImpl result = new MavenDependencyImpl();

        Artifact artifact = dependency.getArtifact();
        result.setGroupId(artifact.getGroupId());
        result.setArtifactId(artifact.getArtifactId());
        result.setType(artifact.getExtension());
        result.setClassifier(artifact.getClassifier());
        result.setVersion(artifact.getVersion());
        result.optional(dependency.isOptional());
        result.scope(dependency.getScope());
        result.exclusions(fromExclusions(dependency.getExclusions()).toArray(new String[0]));
        return result;
    }

    /**
     * Converts Maven {@link org.apache.maven.model.Dependency} to Aether {@link org.sonatype.aether.graph.Dependency}
     *
     * @param dependency the Maven dependency to be converted
     * @param registry the Artifact type catalog to determine common artifact properties
     * @return Equivalent Aether dependency
     */
    public static MavenDependency fromDependency(org.apache.maven.model.Dependency dependency, ArtifactTypeRegistry registry) {
        ArtifactType stereotype = registry.get(dependency.getType());
        if (stereotype == null) {
            stereotype = new DefaultArtifactType(dependency.getType());
        }

        boolean system = dependency.getSystemPath() != null && dependency.getSystemPath().length() > 0;

        Map<String, String> props = null;
        if (system) {
            props = Collections.singletonMap(ArtifactProperties.LOCAL_PATH, dependency.getSystemPath());
        }

        Artifact artifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(),
                dependency.getClassifier(), null, dependency.getVersion(), props, stereotype);

        List<String> exclusions = new ArrayList<String>();
        for (org.apache.maven.model.Exclusion e : dependency.getExclusions()) {
            exclusions.add(fromExclusion(e));
        }

        MavenDependencyImpl result = new MavenDependencyImpl();
        result.setGroupId(artifact.getGroupId());
        result.setArtifactId(artifact.getArtifactId());
        result.setType(artifact.getExtension());
        result.setClassifier(artifact.getClassifier());
        result.setVersion(artifact.getVersion());
        result.optional(dependency.isOptional());
        result.scope(dependency.getScope());
        result.exclusions(exclusions.toArray(new String[0]));
        return result;
    }

    public static Set<MavenDependency> fromDependencies(Collection<org.apache.maven.model.Dependency> dependencies,
            ArtifactTypeRegistry registry) {

        Set<MavenDependency> set = new LinkedHashSet<MavenDependency>();
        for (org.apache.maven.model.Dependency d : dependencies) {
            set.add(fromDependency(d, registry));
        }

        return set;
    }

    /**
     * Converts Maven {@link Repository} to Aether {@link RemoteRepository}
     *
     * @param repository the Maven repository to be converted
     * @return Equivalent remote repository
     */
    public static RemoteRepository asRemoteRepository(org.apache.maven.model.Repository repository) {

        return new RemoteRepository().setId(repository.getId()).setContentType(repository.getLayout())
                .setUrl(repository.getUrl()).setPolicy(true, asRepositoryPolicy(repository.getSnapshots()))
                .setPolicy(false, asRepositoryPolicy(repository.getReleases()));
    }

    /**
     * Converts Maven {@link Repository} to Aether {@link RemoteRepository}
     *
     * @param repository the Maven repository to be converted
     * @return Equivalent remote repository
     */
    public static RemoteRepository asRemoteRepository(org.apache.maven.settings.Repository repository) {
        return new RemoteRepository().setId(repository.getId()).setContentType(repository.getLayout())
                .setUrl(repository.getUrl()).setPolicy(true, asRepositoryPolicy(repository.getSnapshots()))
                .setPolicy(false, asRepositoryPolicy(repository.getReleases()));
    }

    /**
     * Converts MavenRepository to Aether {@link RemoteRepository}
     *
     * @param repository the repository to be converted
     * @return Equivalent remote repository
     */
    public static RemoteRepository asRemoteRepository(MavenRepository repository) {
        // FIXME this does not allow user to set policies
        // neither authentication
        return new RemoteRepository().setId(repository.id()).setContentType(repository.layout()).setUrl(repository.url());
    }

    /**
     * Converts Maven Proxy to Aether Proxy
     *
     * @param proxy the Maven proxy to be converted
     * @return Aether proxy equivalent
     */
    public static Proxy asProxy(org.apache.maven.settings.Proxy proxy) {
        Proxy aetherProxy = new Proxy(proxy.getProtocol(), proxy.getHost(), proxy.getPort(), null);
        if (proxy.getUsername() != null || proxy.getPassword() != null) {
            aetherProxy.setAuthentication(new Authentication(proxy.getUsername(), proxy.getPassword()));
        }
        return aetherProxy;
    }

    // converts repository policy
    private static RepositoryPolicy asRepositoryPolicy(org.apache.maven.model.RepositoryPolicy policy) {
        boolean enabled = true;
        String checksums = RepositoryPolicy.CHECKSUM_POLICY_WARN;
        String updates = RepositoryPolicy.UPDATE_POLICY_DAILY;

        if (policy != null) {
            enabled = policy.isEnabled();
            if (policy.getUpdatePolicy() != null) {
                updates = policy.getUpdatePolicy();
            }
            if (policy.getChecksumPolicy() != null) {
                checksums = policy.getChecksumPolicy();
            }
        }

        return new RepositoryPolicy(enabled, updates, checksums);
    }

    // converts repository policy
    private static RepositoryPolicy asRepositoryPolicy(org.apache.maven.settings.RepositoryPolicy policy) {
        boolean enabled = true;
        String checksums = RepositoryPolicy.CHECKSUM_POLICY_WARN;
        String updates = RepositoryPolicy.UPDATE_POLICY_DAILY;

        if (policy != null) {
            enabled = policy.isEnabled();
            if (policy.getUpdatePolicy() != null) {
                updates = policy.getUpdatePolicy();
            }
            if (policy.getChecksumPolicy() != null) {
                checksums = policy.getChecksumPolicy();
            }
        }

        return new RepositoryPolicy(enabled, updates, checksums);
    }

}
