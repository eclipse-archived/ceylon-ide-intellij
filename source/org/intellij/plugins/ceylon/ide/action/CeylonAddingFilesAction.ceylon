import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    PlatformDataKeys
}
import com.intellij.openapi.\imodule {
    ModuleUtilCore {
        findModuleForFile
    }
}
import com.intellij.openapi.roots {
    ProjectRootManager
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.openapi.vfs.newvfs.impl {
    VirtualDirectoryImpl
}
import com.intellij.psi {
    PsiDirectory,
    PsiManager
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject,
    IdeaCeylonProjects
}

shared abstract class CeylonAddingFilesAction() extends AnAction() {

    VirtualFile? getSourceRoot(AnActionEvent e, VirtualFile eventDir) {
        return if (exists project = e.project)
        then ProjectRootManager.getInstance(project).fileIndex.getSourceRootForFile(eventDir)
        else null;
    }

    VirtualFile? findEventDir(AnActionEvent e) {
        variable VirtualFile? eventSourceFile = e.getData(PlatformDataKeys.virtualFile);
        while (exists esf = eventSourceFile,
            !esf is VirtualDirectoryImpl) {
            eventSourceFile = esf.parent;
        }
        return eventSourceFile;
    }

    shared actual void actionPerformed(AnActionEvent e) {
        value srcRootDir = findEventDir(e);
        if (exists srcRootDir,
            exists project = e.project,
            exists projects = project.getComponent(`IdeaCeylonProjects`),
            exists mod = findModuleForFile(srcRootDir, project),
            exists srcRoot = getSourceRoot(e, srcRootDir)) {

            variable value eventPath = srcRootDir.path;
            value srcRootPath = srcRoot.path;

            // eventPath + " not in " + srcRootPath
            assert(eventPath.startsWith(srcRootPath));

            // Make eventPath relative
            eventPath = if (eventPath.size<=srcRootPath.size)
                then ""
                else eventPath.substring(srcRootPath.size + 1);

            value eventPackage = eventPath.replace("/", ".");
            value eventPsiDir = PsiManager.getInstance(project).findDirectory(srcRoot);

            if (exists eventPsiDir,
                is IdeaCeylonProject p = projects.getProject(mod)) {
                createFiles(e, p, srcRoot, eventPackage, eventPsiDir);
            }
        }
    }

    shared formal void createFiles(AnActionEvent e, IdeaCeylonProject project, VirtualFile srcRoot,
        String eventPackage, PsiDirectory srcRootDir);

    shared actual void update(AnActionEvent e) {
        value eventDir = findEventDir(e);
        e.presentation.visible = if (exists eventDir) then getSourceRoot(e, eventDir) exists else false;
        e.presentation.setIcon(ceylonIcon);
    }

    shared formal Icon ceylonIcon;
}
