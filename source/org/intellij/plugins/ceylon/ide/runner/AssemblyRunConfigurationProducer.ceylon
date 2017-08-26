
"Generates run configurations for assemblies, with a 'ceylon assemble' before-run task."
shared class AssemblyRunConfigurationProducer()
        extends CeylonTaskRunConfigurationProducer() {

    getGeneratedJarName(String modName, String version)
            => "``modName``-``version``.cas";

    getBeforeTask(String modName, String version)
            => CeylonBeforeRunTask {
                command = "assemble";
                    "--jvm",
                    "--force",
                    "--include-runtime",
                    "--include-language",
                    modName + "/" + version
            };

    getRunConfigName(String modName, String version)
            => "assembly " + modName;
}
