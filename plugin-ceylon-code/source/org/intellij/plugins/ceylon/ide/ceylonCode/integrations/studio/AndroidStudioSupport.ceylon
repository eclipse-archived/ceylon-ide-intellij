import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaClass,
    javaString,
    createJavaObjectArray
}

import com.intellij.codeInsight {
    CodeInsightUtilCore
}
import com.intellij.ide.actions {
    OpenFileAction
}
import com.intellij.notification {
    Notifications,
    Notification,
    NotificationType
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
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.vfs {
    VfsUtil,
    VirtualFileVisitor,
    VirtualFile
}
import com.intellij.psi {
    PsiManager
}
import com.redhat.ceylon.common {
    Constants,
    Backend
}
import com.redhat.ceylon.common.config {
    ConfigWriter
}

import java.io {
    File
}
import java.lang {
    Runnable,
    ReflectiveOperationException,
    JBoolean=Boolean
}
import java.util {
    Properties
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject,
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    ceylonFileFactory
}
import org.jetbrains.plugins.gradle.util {
    GradleConstants
}
import org.jetbrains.plugins.groovy.lang.psi {
    GroovyFile
}

shared interface AndroidStudioSupport {
    shared formal void setupModule(Module mod);
}

shared class AndroidStudioSupportImpl() satisfies AndroidStudioSupport {
    shared ExtensionPointName<AndroidStudioSupport> epName
        = create<AndroidStudioSupport>("org.intellij.plugins.ceylon.ide.androidStudioSupport");

    value applyCeylonPlugin = "apply plugin: 'com.athaydes.ceylon'";
    value applyCeylonAndroidPlugin = "apply plugin: 'com.redhat.ceylon.gradle.android'";

    shared actual void setupModule(Module mod) {
        assert (exists projects = getCeylonProjects(mod.project));
        projects.addProject(mod);
        assert (is IdeaCeylonProject ceylonProject = projects.getProject(mod));

        CommandProcessor.instance.executeCommand(object satisfies Runnable {
            shared actual void run() {
                value modified = object extends WriteCommandAction<Boolean>(mod.project) {
                    shared actual void run(Result<Boolean> result) {
                        variable value modified = updateGradleModel(ceylonProject);
                        modified ||= addCeylonModule(ceylonProject);
                        result.setResult(modified);
                    }
                }.execute();

                if (modified.resultObject) {
                    syncGradleProject(mod);
                    buildGradleProject(mod);
                }

                syncAptDependencies(ceylonProject);

                object extends WriteCommandAction<Nothing>(mod.project) {
                    run(Result<Nothing> result) => addFacet(ceylonProject);
                }.execute();
            }
        }, "Configure Ceylon", null);
    }

    void syncAptDependencies(IdeaCeylonProject ceylonProject) {
        value mod = ceylonProject.ideaModule;

        if (exists file = findGradleBuild(mod)) {
            value existingApt = ArrayList {
                    *(ceylonProject.configuration.ceylonConfig
                        .getOptionValues("compiler.apt")?.iterable else [])
            };

            for (dep in groovyFileManipulator.findAptDependencies(file)) {
                if (!existingApt.contains(javaString(dep))) {
                    existingApt.add(javaString(dep));
                }
            }

            ceylonProject.configuration.ceylonConfig.setOptionValues("compiler.apt",
                createJavaObjectArray(existingApt));

            ConfigWriter.instance().write(ceylonProject.configuration.ceylonConfig,
                ceylonProject.configuration.projectConfigFile);
        }
    }

    Boolean addCeylonModule(IdeaCeylonProject ceylonProject) {
        value mod = ceylonProject.ideaModule;
        assert (exists src = VfsUtil.findRelativeFile(ceylonProject.moduleRoot, "src", "main", "ceylon"));

        variable value hasModule = false;
        VfsUtil.visitChildrenRecursively(src, object extends VirtualFileVisitor<Boolean>() {
            shared actual Boolean visitFile(VirtualFile file) {
                if (file.name == Constants.moduleDescriptor) {
                    hasModule = true;
                }
                return true;
            }
        });

        if (!hasModule,
            exists buildFile = findGradleBuild(mod),
            exists modName = groovyFileManipulator.findModuleName(buildFile)) {

            value dir = VfsUtil.createDirectoryIfMissing(src, modName[0].replace(".", "/"));
            assert (exists psiDir = PsiManager.getInstance(mod.project).findDirectory(dir));

            ceylonFileFactory.createModuleDescriptor(psiDir, modName[0], modName[1], Backend.java, {
                "java.base \"7\"",
                "android \"``groovyFileManipulator.findAndroidVersion(buildFile) else "23"``\"",
                *groovyFileManipulator.findCompileDependencies(buildFile)
            });

            ceylonFileFactory.createFileFromTemplate(psiDir, "CeylonMainActivity.ceylon");

            assert (exists layout = VfsUtil.findRelativeFile(ceylonProject.moduleRoot, "src", "main", "res", "layout"));
            assert (exists layoutPsi = PsiManager.getInstance(mod.project).findDirectory(layout));
            value props = Properties();
            props.setProperty("ACTIVITY_NAME", modName[0] + ".CeylonMainActivity");
            ceylonFileFactory.createFileFromTemplate {
                dir = layoutPsi;
                templateName = "activity_main_ceylon.xml";
                properties = props;
            };

            return true;
        }
        return false;
    }

    Boolean updateGradleModel(IdeaCeylonProject ceylonProject) {
        variable Boolean wasModified = false;
        value mod = ceylonProject.ideaModule;

        if (exists buildFile = findGradleBuild(mod)) {
            VfsUtil.createDirectoryIfMissing(ceylonProject.moduleRoot, "src/main/ceylon");

            wasModified ||= groovyFileManipulator.configureSourceSet(buildFile);
            wasModified ||= groovyFileManipulator.configureLint(buildFile);
            wasModified ||= groovyFileManipulator.configureCeylonModule(buildFile);
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
            GradleConstants.defaultScriptName
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
            value instance = cls.getDeclaredMethod("getInstance").invoke(null);

            for (m in cls.declaredMethods) {
                if (m.name == "syncProjectSynchronously") {
                    m.invoke(instance, mod.project, JBoolean.true, null);
                }
            }
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
            Notifications.Bus.notify(Notification("ceylon", "Configure Ceylon",
                "The gradle project could not be synced automatically, please sync it manually "
                + "to apply changes.",
                NotificationType.error
            ));
        }
    }

    void buildGradleProject(Module mod) {
        value cl = javaClass<AndroidStudioSupport>().classLoader;

        try {
            value cls = cl.loadClass("com.android.tools.idea.gradle.invoker.GradleInvoker");
            value instance = cls.getDeclaredMethod("getInstance", javaClass<Project>()).invoke(null, mod.project);

            for (m in cls.declaredMethods) {
                if (m.name == "rebuild") {
                    m.invoke(instance);
                }
            }
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
            Notifications.Bus.notify(Notification("ceylon", "Configure Ceylon",
                "The gradle project could not be built automatically, please build it manually "
                + "to apply changes.",
                NotificationType.error
            ));
        }
    }
}
