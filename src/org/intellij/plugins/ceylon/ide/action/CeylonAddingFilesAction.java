package org.intellij.plugins.ceylon.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.ceylonFileFactory_;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.openapi.module.ModuleUtilCore.findModuleForFile;

abstract class CeylonAddingFilesAction extends AnAction {
    protected  ceylonFileFactory_ ceylonFileFactory = ceylonFileFactory_.get_();

    private VirtualFile getSourceRoot(AnActionEvent e, VirtualFile eventDir) {
        return ProjectRootManager.getInstance(e.getProject()).getFileIndex()
                .getSourceRootForFile(eventDir);
    }

    VirtualFile findEventDir(AnActionEvent e) {
        VirtualFile eventSourceFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);

        while (eventSourceFile != null && !(eventSourceFile instanceof VirtualDirectoryImpl)) {
            eventSourceFile = eventSourceFile.getParent();
        }

        return eventSourceFile;
    }

    public void actionPerformed(final AnActionEvent e) {
        final VirtualFile srcRootDir = findEventDir(e);

        if (srcRootDir != null && e.getProject() != null) {
            Module mod = findModuleForFile(srcRootDir, e.getProject());
            IdeaCeylonProjects projects = e.getProject().getComponent(IdeaCeylonProjects.class);
            final VirtualFile srcRoot = getSourceRoot(e, srcRootDir);

            if (projects != null && mod != null && srcRoot != null) {
                String eventPath = srcRootDir.getPath();
                final String srcRootPath = srcRoot.getPath();
                assert eventPath.startsWith(srcRootPath) : eventPath + " not in " + srcRootPath;

                // Make eventPath relative
                eventPath = eventPath.length() <= srcRootPath.length()
                        ? ""
                        : eventPath.substring(srcRootPath.length() + 1);

                final String eventPackage = eventPath.replace('/', '.');
                final PsiDirectory eventPsiDir = PsiManager.getInstance(e.getProject()).findDirectory(srcRoot);
                createFiles(e, (IdeaCeylonProject) projects.getProject(mod), srcRoot, eventPackage, eventPsiDir);
            }
        }
    }

    protected abstract void createFiles(AnActionEvent e, @Nullable IdeaCeylonProject project,
                                        VirtualFile srcRoot, String eventPackage,
                                        PsiDirectory srcRootDir);

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile eventDir = findEventDir(e);
        e.getPresentation().setVisible(eventDir != null && getSourceRoot(e, eventDir) != null);
        e.getPresentation().setIcon(getCeylonIcon());
    }

    protected abstract Icon getCeylonIcon();
}
