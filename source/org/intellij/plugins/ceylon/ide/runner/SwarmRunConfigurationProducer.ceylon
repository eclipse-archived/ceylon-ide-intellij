import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}

import java.util {
    List
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

Boolean importsJavaEE(List<Tree.ImportModule> importModules) {
    for (imp in importModules) {
        if (exists quoted = imp.quotedLiteral,
            quoted.text == "\"javax:javaee-api\"") {
            return true;
        }

        if (exists path = imp.importPath) {
            value importName = ".".join {
                for (id in path.identifiers)
                id.text
            };
            if (importName == "javax.javaeeapi") {
                return true;
            }
        }
    }
    return false;
}

"Generates run configurations for Swarm apps, with a 'ceylon swarm' before-run task."
shared class SwarmRunConfigurationProducer()
        extends CeylonTaskRunConfigurationProducer() {

    getBeforeTask(String modName, String version)
            => CeylonBeforeRunTask {
                command = "swarm";
                    "--provided-module=javax:javaee-api",
                    modName + "/" + version
            };

    getGeneratedJarName(String modName, String version)
            => "``modName``-``version``-swarm.jar";

    getRunConfigName(String modName, String version)
            => "Swarm uberjar " + modName;

    isEnabledForModule(CeylonPsi.ModuleDescriptorPsi descriptor)
            => if (exists md = descriptor.ceylonNode,
                   exists iml = md.importModuleList)
            then importsJavaEE(iml.importModules)
            else false;
}
