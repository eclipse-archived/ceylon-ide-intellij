import com.intellij.notification {
    Notifications,
    Notification,
    NotificationType
}
import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    LangDataKeys
}
import com.intellij.openapi.ui {
    Messages
}
import com.redhat.ceylon.ide.common.util {
    messages,
    versionsAvailableForBoostrap
}

import java.lang {
    ObjectArray
}

import javax.swing {
    JOptionPane
}

import org.intellij.plugins.ceylon.ide.model {
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.startup {
    CeylonIdePlugin
}

shared class AddBootstrapFilesAction() extends AnAction() {
    
    shared actual void actionPerformed(AnActionEvent evt) {
        assert (exists mod = LangDataKeys.moduleContext.getData(evt.dataContext),
                exists projects = getCeylonProjects(mod.project));
        value versions = versionsAvailableForBoostrap;
        
        if (exists project = projects.getProject(mod),
            exists version
                    = JOptionPane.showInputDialog(null,
                        "Select a Ceylon version",
                        messages.bootstrap.title,
                        JOptionPane.questionMessage, null,
                        ObjectArray.with(versions),
                        versions.first)) {

            value repo = CeylonIdePlugin.embeddedCeylonDist;
            variable value success = false;
            variable value force = false;
            
            while (!success) {
                switch (ret = project.createBootstrapFiles(repo, version.string, force))
                case (is Boolean) {
                    if (ret) {
                        Notifications.Bus.notify(
                            Notification("Ceylon",
                                messages.bootstrap.title,
                                messages.bootstrap.success,
                                NotificationType.information));
                        return;
                    }
                    value result
                            = Messages.showOkCancelDialog(
                                messages.bootstrap.filesExist,
                                messages.bootstrap.title,
                                Messages.questionIcon);
                    
                    if (result == Messages.ok) {
                        force = true;
                    } else {
                        return;
                    }
                }
                else {
                    value result
                            = Messages.showOkCancelDialog(
                                messages.bootstrap.error + "\n"
                                    + ret + "\n"
                                    + messages.bootstrap.retry,
                                messages.bootstrap.title,
                                Messages.errorIcon);
                    if (result == Messages.ok) {
                        force = true;
                    } else {
                        return;
                    }
                }
            }
        }
    }
    
    shared actual void update(AnActionEvent evt) {
        value pres = evt.presentation;
        
        if (exists mod
                = LangDataKeys.moduleContext
                    .getData(evt.dataContext)) {
            pres.enabled = true;
            pres.visible = true;
        } else {
            pres.enabled = false;
            pres.visible = false;
        }        
    }
}
