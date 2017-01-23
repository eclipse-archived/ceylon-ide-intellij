import com.intellij.psi {
    PsiElementFinder,
    PsiClass,
    PsiPackage
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.intellij.util.containers {
    ContainerUtil {
        newArrayList
    }
}
import com.redhat.ceylon.ide.common.model {
    CeylonUnit
}
import com.redhat.ceylon.ide.common.model.asjava {
    javaQualifiedName
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Value,
    FunctionOrValue,
    Function
}

import java.lang {
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.model {
    getCeylonProjects
}

shared class CeylonElementFinder() extends PsiElementFinder() {

    shared actual PsiClass? findClass(String fqName, GlobalSearchScope scope) {
        if (exists p = scope.project,
            exists projects = getCeylonProjects(p)) {
            for (proj in projects.ceylonProjects) {
                value ijMod = proj.ideArtifact;

                if (scope.isSearchInModuleContent(ijMod),
                    exists mods = proj.modules?.fromProject,
                    exists mod = mods.find((m) => fqName.startsWith(m.nameAsString))) {
                    
                    for (pack in newArrayList(mod.packages)) {
                        for (dec in newArrayList(pack.members)) {
                            if (fqName == javaQualifiedName(dec)) {
                                if (is ClassOrInterface|Value dec,
                                    is CeylonUnit unit = dec.unit) {
                                    return CeyLightClass(dec, p);
                                } else if (is Function dec, dec.toplevel) {
                                    return CeyLightToplevelFunction(dec, p);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    findClasses(String fqName, GlobalSearchScope scope)
            => if (exists cl = findClass(fqName, scope))
            then ObjectArray(1, cl)
            else PsiClass.emptyArray;
    
    shared actual ObjectArray<PsiClass> getClasses(PsiPackage pkg, GlobalSearchScope scope) {
        value fqName = pkg.qualifiedName;
        
        if (exists p = scope.project,
            exists projects = getCeylonProjects(p)) {
            for (proj in projects.ceylonProjects) {
                value ijMod = proj.ideArtifact;
                
                if (scope.isSearchInModuleContent(ijMod),
                    exists mods = proj.modules?.fromProject,
                    exists mod = mods.find((m) => fqName.startsWith(m.nameAsString))) {
                    
                    for (pack in newArrayList(mod.packages)) {
                        if (pack.qualifiedNameString == fqName) {
                            return ObjectArray<PsiClass>.with {
                                for (dec in pack.members)
                                if (is ClassOrInterface|FunctionOrValue dec,
                                    dec.unit is CeylonUnit)
                                if (is Function dec)
                                then CeyLightToplevelFunction(dec, p)
                                else CeyLightClass(dec, p)
                            };
                        }
                    }
                }
            }
        }
        
        return PsiClass.emptyArray;
    }
    
}