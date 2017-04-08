import ceylon.collection {
    ArrayList
}

import com.intellij.openapi.externalSystem.service.project {
    IdeModifiableModelsProvider
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.util {
    PairConsumer
}
import com.redhat.ceylon.common {
    Backend
}

import java.lang {
    Types {
        nativeString
    },
    JString=String
}
import java.util {
    JList=List,
    Map
}

import org.intellij.plugins.ceylon.ide.model {
    getCeylonProjects,
    CeylonProjectManager
}
import org.jdom {
    Element
}
import org.jetbrains.idea.maven.importing {
    MavenRootModelAdapter
}
import org.jetbrains.idea.maven.project {
    MavenProject,
    MavenProjectChanges,
    MavenProjectsProcessorTask,
    MavenProjectsTree
}
import org.jetbrains.jps.model {
    JpsElement
}
import org.jetbrains.jps.model.java {
    JavaSourceRootType,
    JavaResourceRootType
}
import org.jetbrains.jps.model.\imodule {
    JpsModuleSourceRootType
}

"Automatically configures Ceylon source roots from the POM."
shared class CeylonMavenImporter()
        extends WorkaroundForIssue6829("org.ceylon-lang", "ceylon-maven-plugin") {

    shared actual void preProcess(Module \imodule, MavenProject mavenProject,
        MavenProjectChanges mavenProjectChanges,
        IdeModifiableModelsProvider ideModifiableModelsProvider) {

        if (exists model = getCeylonProjects(\imodule.project)) {
            model.addProject(\imodule);
        }
    }

    function pluginConfiguration(MavenProject mavenProject, String goal) =>
            mavenProject.getPluginGoalConfiguration(
                "org.ceylon-lang",
                "ceylon-maven-plugin",
                goal
            );

    shared actual void process(IdeModifiableModelsProvider ideModifiableModelsProvider,
        Module mod, MavenRootModelAdapter mavenRootModelAdapter,
        MavenProjectsTree mavenProjectsTree, MavenProject mavenProject,
        MavenProjectChanges mavenProjectChanges,
        Map<MavenProject,JString> map, JList<MavenProjectsProcessorTask> list) {

        if (exists project = getCeylonProjects(mod.project)?.getProject(mod)) {
            if (exists el = pluginConfiguration(mavenProject, "compile")) {

                value sources = getChildren(el, "sources", "source", "directory");
                if (sources.empty) {
                    project.configuration.projectSourceDirectories = {"src/main/ceylon"};
                } else {
                    project.configuration.projectSourceDirectories = sources.map(Element.text);
                }

                value resources = getChildren(el, "resources", "resource", "directory");
                project.configuration.projectResourceDirectories = resources.map(Element.text);

                value userRepos = getChildren(el, "userRepos", "userRepo");
                project.configuration.projectLocalRepos = userRepos.map(Element.text).sequence();

                if (exists outputRepo = getChildren(el, "out").first) {
                    project.configuration.outputRepo = outputRepo.text;
                } else {
                    project.configuration.outputRepo = "target/modules";
                }

                value javacOptions = getChildren(el, "javacOptions");
                project.configuration.javacOptions = javacOptions.map(Element.text);

            }
            if (exists el = pluginConfiguration(mavenProject, "run")) {
                if (exists runConfig = getChildren(el, "module").first) {
                    project.configuration.setProjectRunToolModule(runConfig.text, Backend.java);
                }

                value args = getChildren(el, "arguments", "argument");
                project.configuration.projectRunToolArgs = ArrayList {*args.map(Element.text)};
            }

            project.configuration.save();

            CeylonProjectManager.forModule(mod).addFacetToModule(mod, null, false, false);
        }
    }

    shared actual void myCollectSourceRoots(MavenProject mavenProject,
        PairConsumer<JString,JpsModuleSourceRootType<out JpsElement>> result) {

        Element? el = pluginConfiguration(mavenProject, "compile");

        value sources = getChildren(el, "sources", "source", "directory");

        if (sources.empty) {
            result.consume(nativeString("src/main/ceylon"), JavaSourceRootType.source);
        } else {
            for (element in sources) {
                result.consume(nativeString(element.text), JavaSourceRootType.source);
            }
        }

        for (element in getChildren(el, "resources", "resource", "directory")) {
            result.consume(nativeString(element.text), JavaResourceRootType.resource);
        }
    }

    {Element*} getChildren(Element? el, String* path) {

        if (exists el) {
            function findChildren(List<Element> parents, String childName) {
                value children = ArrayList<Element>();

                for (parent in parents) {
                    children.addAll {*parent.getChildren(childName)};
                }

                return children;
            }

            variable value children = ArrayList {el};

            for (name in path) {
                children = findChildren(children, name);
            }

            return children;
        }

        return {};
    }
}
