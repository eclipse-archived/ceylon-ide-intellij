package org.intellij.plugins.ceylon.ide.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.project.CeylonConfigForm;
import org.intellij.plugins.ceylon.ide.project.PageOne;
import org.intellij.plugins.ceylon.ide.project.PageTwo;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Settings for the Ceylon facet. Uses the same component than the "new module" wizard.
 */
public class CeylonFacetConfiguration implements FacetConfiguration {

    public static final String COMPILATION_TAB = "Compilation";
    public static final String REPOS_TAB = "Repositories";

    private IdeaCeylonProject ceylonProject;

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{
                new CeylonFacetTab(COMPILATION_TAB, new PageOne()),
                new CeylonFacetTab(REPOS_TAB, new PageTwo())
        };
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        CeylonIdeConfig conf = ceylonProject.getIdeConfiguration();

        element.addContent(new Element("option")
                .setAttribute("name", "compileForJvm")
                .setAttribute("value", conf.getCompileToJvm() == null ? "false" : conf.getCompileToJvm().toString()));
        element.addContent(new Element("option")
                .setAttribute("name", "compileToJs")
                .setAttribute("value", conf.getCompileToJs() == null ? "false" : conf.getCompileToJs().toString()));
        element.addContent(new Element("option")
                .setAttribute("name", "systemRepository")
                .setAttribute("value", conf.getSystemRepository() == null ? "" : conf.getSystemRepository().toString()));

        element.addContent("Do not edit, modify .config/ide-config instead");

        conf.save();
    }

    public void setModule(Module module) {
        IdeaCeylonProjects ceylonModel = module.getProject().getComponent(IdeaCeylonProjects.class);
        ceylonProject = (IdeaCeylonProject) ceylonModel.getProject(module);

        if (ceylonProject == null && ceylonModel.addProject(module)) {
            ceylonProject = (IdeaCeylonProject) ceylonModel.getProject(module);
        }
    }

    private class CeylonFacetTab extends FacetEditorTab {

        private String tabName;
        private CeylonConfigForm form;

        private CeylonFacetTab(String tabName, CeylonConfigForm form) {
            this.tabName = tabName;
            this.form = form;
        }

        @NotNull
        @Override
        public JComponent createComponent() {
            return form.getPanel();
        }

        @Override
        public boolean isModified() {
            return form.isModified(ceylonProject);
        }

        @Override
        public void reset() {
            form.load(ceylonProject);
        }

        @Override
        public void apply() throws ConfigurationException {
            form.apply(ceylonProject);
            ceylonProject.getConfiguration().save();
            ceylonProject.getIdeConfiguration().save();
        }

        @Override
        public void disposeUIResources() {
        }

        @Nls
        @Override
        public String getDisplayName() {
            return tabName;
        }
    }
}
