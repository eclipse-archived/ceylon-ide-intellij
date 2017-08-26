import com.intellij.execution {
    RunManagerEx
}
import com.intellij.execution.actions {
    RunConfigurationProducer,
    ConfigurationContext
}
import com.intellij.execution.jar {
    JarApplicationConfiguration,
    JarApplicationConfigurationType
}
import com.intellij.openapi.util {
    Ref
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import java.io {
    File
}
import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide.model {
    getCeylonProject
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonPsi
}

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
