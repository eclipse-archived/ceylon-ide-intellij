import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight {
    CodeInsightUtilCore
}
import com.intellij.ide.actions {
    OpenFileAction
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    CommandProcessor,
    WriteCommandAction
}
import com.intellij.openapi.extensions {
    ExtensionPointName {
        create
    }
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VfsUtil
}
import com.intellij.psi {
    PsiManager
}

import java.io {
    File
}
import java.lang {
    Runnable,
    ReflectiveOperationException,
    JBoolean=Boolean
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    IdeaCeylonProject
}
import org.jetbrains.plugins.gradle.util {
    GradleConstants
}
import org.jetbrains.plugins.groovy.lang.psi {
    GroovyFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}
import com.intellij.notification {
    Notifications,
    Notification,
    NotificationType
}

shared interface AndroidStudioSupport {
    shared formal void setupModule(Module mod);
}

shared class AndroidStudioSupportImpl() satisfies AndroidStudioSupport {
    shared ExtensionPointName<AndroidStudioSupport> epName
        = create<AndroidStudioSupport>("org.intellij.plugins.ceylon.ide.androidStudioSupport");

    value sourceSet = "main.java.srcDirs += 'src/main/ceylon'\n";
    value applyCeylonPlugin = "apply plugin: 'com.athaydes.ceylon'";
    value applyCeylonAndroidPlugin = "apply plugin: 'com.redhat.ceylon.gradle.android'";

    shared actual void setupModule(Module mod) {
        value projects = mod.project.getComponent(javaClass<IdeaCeylonProjects>());
        projects.addProject(mod);
        assert(is IdeaCeylonProject ceylonProject = projects.getProject(mod));

        CommandProcessor.instance.executeCommand(object satisfies Runnable {
            shared actual void run() {
                value modified = object extends WriteCommandAction<Boolean>(mod.project) {
                    shared actual void run(Result<Boolean> result) {
                        result.setResult(updateGradleModel(mod));
                    }
                }.execute();

                if (modified.resultObject) {
                    syncGradleProject(mod);
                }

                object extends WriteCommandAction<Nothing>(mod.project) {
                    shared actual void run(Result<Nothing> result) {
                        addFacet(ceylonProject);
                    }
                }.execute();
            }
        }, "Configure Ceylon", null);
    }

    Boolean updateGradleModel(Module mod) {
        variable Boolean wasModified = false;

        if (exists buildFile = findGradleBuild(mod)) {
            value androidBlock = groovyFileManipulator.getAndroidBlock(buildFile);
            value sourceSets = groovyFileManipulator.getSourceSetsBlock(androidBlock);
            wasModified ||= groovyFileManipulator.addLastExpressionInBlockIfNeeded(sourceSet, sourceSets);

            VfsUtil.createDirectoryIfMissing(mod.moduleFile.parent, "src/main/ceylon");

            if (exists version = groovyFileManipulator.findModuleName(buildFile)) {
                value ceylonBlock = groovyFileManipulator.getCeylonBlock(buildFile);
                wasModified ||= groovyFileManipulator.addLastExpressionInBlockIfNeeded(
                    "module \"``version``\"", ceylonBlock);
            }

            wasModified ||= groovyFileManipulator.configureRepository(buildFile);

            wasModified ||= groovyFileManipulator.addApplyDirective(buildFile, applyCeylonAndroidPlugin);
            wasModified ||= groovyFileManipulator.addApplyDirective(buildFile, applyCeylonPlugin);

            if (wasModified) {
                CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(buildFile);
                OpenFileAction.openFile(buildFile.virtualFile, mod.project);
            }
        }

        return wasModified;
    }

    GroovyFile? findGradleBuild(Module mod) {
        value buildFile = File(
            File(mod.moduleFilePath).parentFile,
            GradleConstants.\iDEFAULT_SCRIPT_NAME
        );

        if (buildFile.file,
            exists vfile = VfsUtil.findFileByIoFile(buildFile, true),
            is GroovyFile file = PsiManager.getInstance(mod.project).findFile(vfile)) {

            return file;
        }
        return null;
    }

    void addFacet(IdeaCeylonProject project) {
        if (exists file = findGradleBuild(project.ideArtifact),
            exists cmp = project.ideArtifact.getComponent(javaClass<ITypeCheckerProvider>())) {

            value version = groovyFileManipulator.findAndroidVersion(file) else "unknown";

            cmp.addFacetToModule(project.ideArtifact, "android/" + version);
        }
    }

    void syncGradleProject(Module mod) {
        value cl = javaClass<AndroidStudioSupport>().classLoader;

        try {
            value cls = cl.loadClass("com.android.tools.idea.gradle.project.GradleProjectImporter");
            value instance = cls.getDeclaredMethod("getInstance").invoke(null, *empty);

            for (m in cls.declaredMethods) {
                if (m.name == "syncProjectSynchronously") {
                    m.invoke(instance, mod.project, JBoolean.\iTRUE, null);
                }
            }
        } catch (ReflectiveOperationException exception) {
            Notifications.Bus.notify(Notification("ceylon", "Configure Ceylon",
                "The gradle project could not be synced automatically, please sync it manually "
                + "to apply changes.",
                NotificationType.\iERROR
            ));
        }
    }
}
