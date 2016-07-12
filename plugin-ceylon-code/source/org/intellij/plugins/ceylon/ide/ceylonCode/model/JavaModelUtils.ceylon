import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.\imodule {
    ModuleUtil
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
    Declaration
}

import java.lang {
    Class
}

Class<IdeaCeylonProjects> ceylonProjectsClass = javaClass<IdeaCeylonProjects>();

shared IdeaCeylonProjects? getCeylonProjects(Project project)
        => project.getComponent(ceylonProjectsClass);

Class<CeylonModelManager> modelManagerClass = javaClass<CeylonModelManager>();

shared CeylonModelManager? getModelManager(Project project)
        => project.getComponent(modelManagerClass);

shared IdeaCeylonProject? getCeylonProject(PsiFile psiFile) {
    if (exists projects = getCeylonProjects(psiFile.project),
        exists mod = ModuleUtil.findModuleForFile(psiFile.virtualFile, psiFile.project),
        is IdeaCeylonProject project = projects.getProject(mod)) {
        return project;
    }
    return null;
}

shared Package? packageFromJavaPsiFile(PsiJavaFile psiFile) {
    String packageName = psiFile.packageName;
    if (exists modelLoader = getCeylonProject(psiFile)?.modelLoader) {
        return modelLoader.findPackage(JVMModuleUtil.quoteJavaKeywords(packageName)) else null;
    }
    return null;
}

shared Declaration? declarationFromPsiElement(PsiClass|PsiMethod psiElement) {
    if (is PsiJavaFile psiFile = psiElement.containingFile,
        exists pkg = packageFromJavaPsiFile(psiFile)) {
        if (exists modelLoader = getCeylonProject(psiFile)?.modelLoader) {
            return modelLoader.convertToDeclaration(pkg.\imodule, "``pkg.nameAsString ``.``getCeylonSimpleName(psiElement)  else ""``", DeclarationType.type);
        }
    }
        
    return null;
}

/*
 * returns null if it's a method with no Ceylon equivalent
 * (internal getter of a Ceylon object value)
 */
shared String? getCeylonSimpleName(PsiClass|PsiMethod dec) {
    String? name = if (is PsiMethod dec)
    then dec.name
    else dec.nameIdentifier?.text;

/*    
    TODO : convert this for the Idea APIs
 
    String nameAnnotationValue = 
            getCeylonNameAnnotationValue(dec);
    if (nameAnnotationValue != null) {
        return nameAnnotationValue;
    }
    
    if (dec instanceof IMethod) {
        if (name.startsWith(Prefix.$default$.name())) {
            name = name.substring(
                Prefix.$default$.name()
                        .length());
        } else if (name.equals("get_")) {
            boolean isStatic = false;
            int parameterCount = 0;
            IMethod method = (IMethod) dec;
            try {
                isStatic = 
                        (method.getFlags() & 
                    Flags.AccStatic) 
                        != 0;
                parameterCount = 
                        method.getParameterNames()
                        .length;
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
            if (isStatic && parameterCount == 0) {
                name = null;
            }
        } else if (name.startsWith("$")) {
            name = name.substring(1);
        } else if ((name.startsWith("get") 
            || name.startsWith("set")) 
                && name.length() > 3) {
            StringBuffer newName = 
                    new StringBuffer(
                Character.toLowerCase(
                    name.charAt(3)));
            if (name.length() > 4) { 
                newName.append(name.substring(4));
            }
            name = newName.toString();
        } else if (name.equals("toString")) {
            name = "string";
        } else if (name.equals("hashCode")) {
            name = "hash";
        } else if (name.contains("$")) {
            IMethod method = (IMethod) dec;
            JdtDefaultArgumentMethodSearch.Result searchResult = 
                    new JdtDefaultArgumentMethodSearch()
                    .search(method);
            if (searchResult.defaultArgumentName != null) {
                name = searchResult.defaultArgumentName;
            }
        }
        if (name.endsWith(Suffix.$canonical$.name())) {
            name = name.substring(0, 
                name.length() - 
                        Suffix.$canonical$.name().length());
        }
        if (name.endsWith(Suffix.$priv$.name())) {
            name = name.substring(0, 
                name.length() - 
                        Suffix.$priv$.name().length());
        }
    } else if (dec instanceof IType) {
        IType type = (IType) dec;
        if (name.endsWith("_")) {
            if (isCeylonObject(type) ||
                isCeylonMethod(type)) {
                name = name.substring(0, 
                    name.length()-1);
            }
        }
    }
 */
    return name;
}
