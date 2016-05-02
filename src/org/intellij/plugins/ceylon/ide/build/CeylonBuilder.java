package org.intellij.plugins.ceylon.ide.build;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.module.Module;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing.ProgressIndicatorMonitor;
import org.intellij.plugins.ceylon.ide.settings.CeylonSettings;

public class CeylonBuilder implements CompileTask {

    @Override
    public boolean execute(CompileContext compileContext) {
        if (CeylonSettings.getInstance().isUseOutProcessBuild()) {
            return true;
        }
        IdeaCeylonProjects projects = compileContext.getProject()
                .getComponent(IdeaCeylonProjects.class);

        if (projects != null) {
            for (Module mod : compileContext.getCompileScope().getAffectedModules()) {
                IdeaCeylonProject project = (IdeaCeylonProject) projects.getProject(mod);

                if (project != null && project.getCompileToJava()) {
                    ProgressIndicatorMonitor monitor = new ProgressIndicatorMonitor(
                            ProgressIndicatorMonitor.wrap_,
                            compileContext.getProgressIndicator()
                    );

                    project.getBuild().performBuild(monitor);
                }
            }
        }
        return true;
    }
}
