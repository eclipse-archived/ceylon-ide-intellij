import ceylon.interop.java {
    javaObjectArray,
    javaClass,
    CeylonIterable,
    createJavaObjectArray
}

import com.intellij.psi {
    PsiElementFinder,
    PsiClass,
    PsiPackage
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Value,
    Declaration
}

import java.lang {
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}
import com.redhat.ceylon.ide.common.model.asjava {
    getJavaQualifiedName
}

shared class CeylonElementFinder() extends PsiElementFinder() {

    shared actual PsiClass? findClass(String fqName, GlobalSearchScope scope) {
        value p = scope.project;
        
        if (exists projects = p.getComponent(javaClass<IdeaCeylonProjects>())) {
            for (proj in projects.ceylonProjects) {
                value ijMod = proj.ideArtifact;

                if (scope.isSearchInModuleContent(ijMod),
                    exists mods = proj.modules?.fromProject,
                    exists mod = mods.find((m) => fqName.startsWith(m.nameAsString))) {
                    
                    for (pack in mod.packages) {
                        for (dec in pack.members) {
                            if (fqName == getJavaQualifiedName(dec)) {
                                if (is ClassOrInterface|Value dec) {
                                    return CeyLightClass(dec of Declaration, p);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    shared actual ObjectArray<PsiClass> findClasses(String fqName, GlobalSearchScope scope) {
        return javaObjectArray(Array<PsiClass?>({findClass(fqName, scope)}.coalesced));
    }
    
    shared actual ObjectArray<PsiClass> getClasses(PsiPackage pkg, GlobalSearchScope scope) {
        value p = scope.project;
        value fqName = pkg.qualifiedName;
        
        if (exists projects = p.getComponent(javaClass<IdeaCeylonProjects>())) {
            for (proj in projects.ceylonProjects) {
                value ijMod = proj.ideArtifact;
                
                if (scope.isSearchInModuleContent(ijMod),
                    exists mods = proj.modules?.fromProject,
                    exists mod = mods.find((m) => fqName.startsWith(m.nameAsString))) {
                    
                    for (pack in mod.packages) {
                        if (pack.qualifiedNameString == fqName) {
                            return createJavaObjectArray(
                                CeylonIterable(pack.members)
                                    .narrow<ClassOrInterface|Value>()
                                    .map((dec) => CeyLightClass(dec of Declaration, p))
                            );
                        }
                    }
                }
            }
        }
        
        return createJavaObjectArray<PsiClass>({});
    }
    
}