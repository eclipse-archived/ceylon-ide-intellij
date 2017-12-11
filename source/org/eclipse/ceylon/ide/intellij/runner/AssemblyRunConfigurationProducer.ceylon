/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
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
