import com.intellij.psi {
    PsiElementFinder,
    PsiClass,
    PsiPackage
}
import java.lang {
    ObjectArray
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import ceylon.interop.java {
    javaObjectArray,
    javaClass,
    CeylonIterable,
    createJavaObjectArray
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}
import com.redhat.ceylon.model.typechecker.model {
    Class,
    ClassOrInterface
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
                    
                    for (pack in CeylonIterable(mod.packages)) {
                        for (dec in CeylonIterable(pack.members)) {
                            if (fqName == dec.qualifiedNameString.replace("::", ".")) {
                                if (is Class dec) {
                                    return CeyLightClass(dec, p);
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
                    
                    for (pack in CeylonIterable(mod.packages)) {
                        if (pack.qualifiedNameString == fqName) {
                            return createJavaObjectArray(
                                CeylonIterable(pack.members)
                                    .narrow<ClassOrInterface>()
                                    .map((dec) => CeyLightClass(dec, p))
                            );
                        }
                    }
                }
            }
        }
        
        return createJavaObjectArray<PsiClass>({});
    }
    
}