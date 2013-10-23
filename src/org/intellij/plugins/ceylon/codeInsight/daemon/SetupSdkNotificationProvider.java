package org.intellij.plugins.ceylon.codeInsight.daemon;

import com.intellij.ProjectTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.intellij.plugins.ceylon.CeylonBundle;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.sdk.CeylonSdk;
import org.jetbrains.annotations.NotNull;

/**
 * Shows a notification when no Ceylon SDK is configured for the project.
 *
 * Mostly taken from {@link com.intellij.codeInsight.daemon.impl.SetupSDKNotificationProvider}.
 */
public class SetupSdkNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {

    private static final Key<EditorNotificationPanel> KEY = Key.create("setup.ceylon.sdk");

    private final Project myProject;

    public SetupSdkNotificationProvider(Project project, final EditorNotifications notifications) {
        myProject = project;
        myProject.getMessageBus().connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
            public void rootsChanged(ModuleRootEvent event) {
                notifications.updateAllNotifications();
            }
        });
    }

    @Override
    public Key<EditorNotificationPanel> getKey() {
        return KEY;
    }

    @Override
    public EditorNotificationPanel createNotificationPanel(VirtualFile file, FileEditor fileEditor) {
        final PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
        if (psiFile == null) {
            return null;
        }

        if (psiFile.getLanguage() != CeylonLanguage.INSTANCE) {
            return null;
        }

        Sdk projectSdk = ProjectRootManager.getInstance(myProject).getProjectSdk();
        if (projectSdk != null && projectSdk.getSdkType() instanceof CeylonSdk) {
            return null;
        }

        return createPanel(myProject, psiFile, projectSdk == null);
    }

    @NotNull
    private static EditorNotificationPanel createPanel(final @NotNull Project project, final @NotNull PsiFile file, boolean isMissing) {
        final EditorNotificationPanel panel = new EditorNotificationPanel();
        if (isMissing) {
            panel.setText(CeylonBundle.message("project.sdk.not.defined"));
        } else {
            panel.setText(CeylonBundle.message("project.sdk.not.ceylon"));
        }
        panel.createActionLabel(ProjectBundle.message("project.sdk.setup"), new Runnable() {
            @Override
            public void run() {
                final Sdk projectSdk = ProjectSettingsService.getInstance(project).chooseAndSetSdk();
                if (projectSdk == null) return;
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        final Module module = ModuleUtilCore.findModuleForPsiElement(file);
                        if (module != null) {
                            ModuleRootModificationUtil.setSdkInherited(module);
                        }
                    }
                });
            }
        });
        return panel;
    }
}
