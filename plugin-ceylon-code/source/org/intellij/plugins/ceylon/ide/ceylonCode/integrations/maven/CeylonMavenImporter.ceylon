import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaString
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

import java.lang {
    JString=String
}
import java.util {
    JList=List,
    Map
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

    preProcess(Module \imodule, MavenProject mavenProject,
        MavenProjectChanges mavenProjectChanges,
        IdeModifiableModelsProvider ideModifiableModelsProvider)
            => noop();

    process(IdeModifiableModelsProvider ideModifiableModelsProvider,
        Module \imodule, MavenRootModelAdapter mavenRootModelAdapter,
        MavenProjectsTree mavenProjectsTree, MavenProject mavenProject,
        MavenProjectChanges mavenProjectChanges,
        Map<MavenProject,JString> map, JList<MavenProjectsProcessorTask> list)
            => noop();

    shared actual void myCollectSourceRoots(MavenProject mavenProject,
        PairConsumer<JString,JpsModuleSourceRootType<out JpsElement>> result) {

        Element? el = mavenProject.getPluginExecutionConfiguration(
            "org.ceylon-lang",
            "ceylon-maven-plugin",
            "default"
        );

        value sources = getChildren(el, "sources", "source", "directory");

        if (sources.empty) {
            result.consume(javaString("src/main/ceylon"), JavaSourceRootType.source);
        } else {
            for (element in sources) {
                result.consume(javaString(element.text), JavaSourceRootType.source);
            }
        }

        for (element in getChildren(el, "resources", "resource", "directory")) {
            result.consume(javaString(element.text), JavaResourceRootType.resource);
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
