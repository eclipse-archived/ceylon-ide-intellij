import com.intellij.openapi.\imodule {
    ModuleUtilCore,
    IdeaModule=Module
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiFile,
    PsiJavaFile,
    PsiClass,
    PsiMethod
}
import com.redhat.ceylon.common {
    JVMModuleUtil
}
import com.redhat.ceylon.model.loader {
    ModelLoader {
        DeclarationType
    }
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration,
    Module
}

import org.intellij.plugins.ceylon.ide.resolve {
    ceylonSourceNavigator
}

shared IdeaCeylonProjects? getCeylonProjects(Project project)
        => project.getComponent(`IdeaCeylonProjects`);

shared CeylonModelManager? getModelManager(Project project)
        => project.getComponent(`CeylonModelManager`);

shared IdeaCeylonProject? getCeylonProject(PsiFile|IdeaModule fileOrModule) {
    if (is IdeaModule fileOrModule) {
        return if (is IdeaCeylonProject project = getCeylonProjects(fileOrModule.project)?.getProject(fileOrModule))
        then project
        else null;
    } else if (exists projects = getCeylonProjects(fileOrModule.project),
        exists mod = concurrencyManager.needReadAccess(
            ()=> ModuleUtilCore.findModuleForPsiElement(fileOrModule)),
        is IdeaCeylonProject project = projects.getProject(mod)) {

        return project;
    }
    return null;
}

shared Package? packageFromJavaPsiFile(PsiJavaFile psiFile) {
    String packageName = concurrencyManager.needReadAccess(() => psiFile.packageName);
    if (exists modelLoader = getCeylonProject(psiFile)?.modelLoader) {
        return modelLoader.findPackage(JVMModuleUtil.quoteJavaKeywords(packageName)) else null;
    }
    return null;
}

shared Declaration? declarationFromPsiElement(PsiClass|PsiMethod psiElement) {
    if (is PsiJavaFile psiFile = psiElement.containingFile,
        exists pkg = packageFromJavaPsiFile(psiFile),
        exists modelLoader = getCeylonProject(psiFile)?.modelLoader,
        exists cls = if (is PsiClass psiElement) then psiElement else psiElement.containingClass) {

        value clsDeclaration = modelLoader.convertToDeclaration(pkg.\imodule,
            "``pkg.nameAsString``.``ceylonSourceNavigator.getCeylonSimpleName(cls) else ""``",
            DeclarationType.type);

        return if (is PsiClass psiElement)
        then clsDeclaration
        else if (exists clsDeclaration)
        then ceylonSourceNavigator.findMatchingDeclaration(clsDeclaration, psiElement)
        else null;
    }
    return null;
}

shared Module? findModuleByName(Project project, String moduleName) {
    if (exists projects = getCeylonProjects(project)) {
        for (p in projects.ceylonProjects) {
            if (exists mod = p.modules?.find((m) => m.nameAsString == moduleName)) {
                return mod;
            }
        }
    }
    return null;
}
