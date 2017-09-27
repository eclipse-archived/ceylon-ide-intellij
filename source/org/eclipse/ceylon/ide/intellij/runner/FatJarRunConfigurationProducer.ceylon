
"Generates run configurations for fat-jar apps, with a 'ceylon fat-jar' before-run task."
shared class FatJarRunConfigurationProducer()
        extends CeylonTaskRunConfigurationProducer() {

    getGeneratedJarName(String modName, String version)
            => "``modName``-``version``.jar";

    getBeforeTask(String modName, String version)
            => CeylonBeforeRunTask {
                command = "fat-jar";
                    "--force",
                    modName + "/" + version
            };

    getRunConfigName(String modName, String version)
            => "fat jar " + modName;
}
