package org.jboss.shrinkwrap.resolver.api.maven.archive.assembler;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public interface UncustomizedArchiveMavenAssembler {

    /**
     * <i>Optional operation</i>. Sets whether a default build directory (usually target/classes) should be used as a source and target of compiled classes and other resources .<br>
     * If the values is set to false then there is created a new directory (resolver-randomUUID()) inside of this default directory.
     * This ensures that each run of ArchiveMavenAssembler uses clear directory, so cannot be influenced by previous runs/builds.
     * But this also can decrease a performance as for each run it needs to compile the classes again.
     * By default, the value is set to false, so the default build directory is not used.
     *
     * @param useDefaultBuildDirectory whether the default build directory should be used
     * @return Modified {@link PomlessArchiveMavenAssembler} instance
     */
    PomlessArchiveMavenAssembler usingDefaultBuildDirectory(boolean useDefaultBuildDirectory);

    /**
     * <i>Optional operation</i>. Force using a default build directory as a source and target of compiled classes and other resources (usually target/classes).<br>
     * Alias to {@link ConfiguredArchiveMavenAssembler#usingDefaultBuildDirectory(boolean)}, passing <code>true</code> as a parameter.
     *
     * @return Modified {@link PomlessArchiveMavenAssembler} instance
     */
    PomlessArchiveMavenAssembler usingDefaultBuildDirectory();

    /**
     * <i>Optional operation</i>. Sets whether resolution should be done in "offline" (ie. not connected to Internet) mode.
     * By default, resolution is done in online mode
     *
     * @param offline Whether resolution should be done in "offline". By default, resolution is done in online mode.
     *
     * @return Modified {@link PomlessArchiveMavenAssembler} instance
     */
    PomlessArchiveMavenAssembler usingOfflineMode(boolean useOfflineMode);

    /**
     * <i>Optional operation</i>. Sets that resolution should be done in "offline" (ie. not connected to Internet) mode. Alias to
     * {@link ConfiguredArchiveMavenAssembler#offline(boolean)}, passing <code>true</code> as a parameter.
     *
     * @return Modified {@link PomlessArchiveMavenAssembler} instance
     */
    PomlessArchiveMavenAssembler usingOfflineMode();
}
