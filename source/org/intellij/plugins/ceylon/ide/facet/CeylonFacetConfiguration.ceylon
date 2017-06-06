import com.intellij.facet {
    FacetConfiguration
}
import com.intellij.facet.ui {
    FacetEditorContext,
    FacetEditorTab,
    FacetValidatorsManager
}
import com.intellij.openapi.\imodule {
    Module
}

import java.lang {
    ObjectArray
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject,
    getCeylonProject,
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.project {
    CeylonConfigForm,
    CeylonPageOne,
    CeylonPageTwo
}
import org.jdom {
    Element
}

"Settings for the Ceylon facet. Uses the same component than the 'new module' wizard."
shared class CeylonFacetConfiguration satisfies FacetConfiguration {
    shared static String compilationTab = "Compilation";
    shared static String reposTab = "Repositories";
    variable IdeaCeylonProject? ceylonProject = null;

    shared new () {}

    createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager)
            => ObjectArray<FacetEditorTab>.with {
                CeylonFacetTab(compilationTab, CeylonPageOne()),
                CeylonFacetTab(reposTab, CeylonPageTwo().init())
            };

    shared actual void readExternal(Element element) {}

    shared actual void writeExternal(Element element) {
        if (exists proj = ceylonProject) {
            value conf = proj.ideConfiguration;
            element.addContent(Element("option")
                .setAttribute("name", "compileForJvm")
                .setAttribute("value", conf.compileToJvm?.string else "false"));
            element.addContent(Element("option")
                .setAttribute("name", "compileToJs")
                .setAttribute("value", conf.compileToJs?.string else "false"));
            element.addContent(Element("option")
                .setAttribute("name", "systemRepository")
                .setAttribute("value", conf.systemRepository?.string else ""));
            element.addContent("Do not edit, modify .config/ide-config instead");
            conf.save();
        }
    }

    shared void setModule(Module mod) {
        ceylonProject
                = if (exists p = getCeylonProject(mod))
                    then p
                else if (exists ceylonModel = getCeylonProjects(mod.project),
                        ceylonModel.addProject(mod),
                        is IdeaCeylonProject p = ceylonModel.getProject(mod))
                    then p
                else null;
    }

    class CeylonFacetTab(String tabName, CeylonConfigForm form)
            extends FacetEditorTab() {

        createComponent() => form.panel;

        modified => if (exists p = ceylonProject)
                then form.isModified(p)
                else false;

        shared actual void reset() {
            if (exists p = ceylonProject) {
                form.load(p);
            }
        }

        shared actual void apply() {
            if (exists p = ceylonProject) {
                form.apply(p);
            }
            ceylonProject?.configuration?.save();
            ceylonProject?.ideConfiguration?.save();
        }

        shared actual void disposeUIResources() {}

        displayName => tabName;

    }
}
