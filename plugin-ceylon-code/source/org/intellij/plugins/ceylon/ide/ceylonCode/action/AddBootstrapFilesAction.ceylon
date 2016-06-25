import ceylon.interop.java {
    javaClass,
    createJavaStringArray
}

import com.intellij.notification {
    Notifications,
    Notification,
    NotificationType
}
import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    DataKeys
}
import com.intellij.openapi.extensions {
    Extensions
}
import com.intellij.openapi.ui {
    Messages
}
import com.redhat.ceylon.ide.common.util {
    messages,
    versionsAvailableForBoostrap
}

import javax.swing {
    JOptionPane
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerInvoker
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}

shared class AddBootstrapFilesAction() extends AnAction() {
    
    shared actual void actionPerformed(AnActionEvent evt) {
        assert(exists mod = DataKeys.moduleContext.getData(evt.dataContext));
        value projects = mod.project.getComponent(javaClass<IdeaCeylonProjects>());
        value versions = versionsAvailableForBoostrap;
        
        if (exists project = projects.getProject(mod),
            exists tcInvoker
                    = Extensions.getExtensions(ITypeCheckerInvoker.epName)[0],
            exists version
                    = JOptionPane.showInputDialog(null,
                        "Select a Ceylon version",
                        messages.bootstrap.title,
                        JOptionPane.questionMessage, null,
                        createJavaStringArray(versions),
                        versions.first)) {

            value repo = tcInvoker.embeddedCeylonDist;
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
                = DataKeys.moduleContext
                    .getData(evt.dataContext)) {
            pres.enabled = true;
            pres.visible = true;
        } else {
            pres.enabled = false;
            pres.visible = false;
        }        
    }
}
