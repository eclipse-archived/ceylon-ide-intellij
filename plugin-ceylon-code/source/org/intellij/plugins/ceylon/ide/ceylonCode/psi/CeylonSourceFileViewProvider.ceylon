import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    IdeaCeylonProject
}
import com.intellij.openapi.\imodule {
    ModuleUtil
}
import ceylon.interop.java {
    javaClass
}
import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType,
    CeylonLanguage
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    SingleRootFileViewProvider,
    PsiManager
}

shared class CeylonSourceFileViewProvider(
            PsiManager manager,
            VirtualFile virtualFile,
            Boolean eventSystemEnabled) 
        extends SingleRootFileViewProvider(
                manager, 
                virtualFile, 
                eventSystemEnabled, 
                CeylonLanguage.instance, 
                CeylonFileType.instance) {
    value ideaProject = manager.project;
    value model = ideaProject.getComponent(javaClass<IdeaCeylonProjects>());
    value ideaModule = ModuleUtil.findModuleForFile(virtualFile, ideaProject) else null;
    assert(is IdeaCeylonProject? ceylonProject = model.getProject(ideaModule));

    shared CeylonLocalAnalyzer ceylonLocalAnalyzer = CeylonLocalAnalyzer { 
        ceylonProject = ceylonProject; 
        virtualFileAccessor() => virtualFile;
        ideaProjectAccessor() => manager.project;
    };    
}