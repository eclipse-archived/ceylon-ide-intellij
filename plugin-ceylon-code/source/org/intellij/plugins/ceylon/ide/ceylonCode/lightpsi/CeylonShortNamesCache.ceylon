import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    createJavaObjectArray,
    javaClass,
    CeylonIterable
}

import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiMethod,
    PsiClass,
    PsiField
}
import com.intellij.psi.search {
    PsiShortNamesCache,
    GlobalSearchScope
}
import com.intellij.util {
    Processor
}
import com.intellij.util.containers {
    HashSet
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Class
}

import java.lang {
    JString=String,
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}

shared class CeylonShortNamesCache(Project project) extends PsiShortNamesCache() {
    
    shared actual ObjectArray<JString> allClassNames {
        value classes = ArrayList<JString>();
        
        if (exists projects = project.getComponent(javaClass<IdeaCeylonProjects>())) {
            for (proj in projects.ceylonProjects) {
                for (mod in proj.modules?.fromProject else []) {
                    for (pack in CeylonIterable(mod.packages)) {
                        classes.addAll(
                            CeylonIterable(pack.members)
                                .narrow<ClassOrInterface>()
                                .map((c) => JString(c.name))
                        );
                    }
                }
                
            }
        }
        
        return createJavaObjectArray(classes);
    }
    
    shared actual ObjectArray<JString> allFieldNames
            => createJavaObjectArray<JString>({});
    
    shared actual ObjectArray<JString> allMethodNames
            => createJavaObjectArray<JString>({});
    
    shared actual void getAllClassNames(HashSet<JString> hashSet)
            => allClassNames.array.coalesced.each(hashSet.add);
    
    shared actual void getAllFieldNames(HashSet<JString> hashSet)
            => allFieldNames.array.coalesced.each(hashSet.add);
    
    shared actual void getAllMethodNames(HashSet<JString> hashSet)
            => allMethodNames.array.coalesced.each(hashSet.add);
    
    shared actual ObjectArray<PsiClass> getClassesByName(String name, GlobalSearchScope scope) {
        value classes = ArrayList<PsiClass>();

        if (exists projects = project.getComponent(javaClass<IdeaCeylonProjects>())) {
            for (proj in projects.ceylonProjects) {
                value ijMod = proj.ideArtifact;
                
                if (scope.isSearchInModuleContent(ijMod),
                    exists mods = proj.modules?.fromProject) {
                    
                    for (mod in mods) {
                        for (pack in CeylonIterable(mod.packages)) {
                            for (dec in CeylonIterable(pack.members)) {
                                if (name == dec.name) {
                                    if (is Class dec) {
                                        classes.add(CeyLightClass(dec, project));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return createJavaObjectArray(classes);
    }
    
    shared actual ObjectArray<PsiField> getFieldsByName(String? string, GlobalSearchScope? globalSearchScope)
            => createJavaObjectArray<PsiField>({});
    
    shared actual ObjectArray<PsiField> getFieldsByNameIfNotMoreThan(String? string, GlobalSearchScope? globalSearchScope, Integer int)
            => createJavaObjectArray<PsiField>({});
    
    shared actual ObjectArray<PsiMethod> getMethodsByName(String? string, GlobalSearchScope? globalSearchScope)
            => createJavaObjectArray<PsiMethod>({});
    
    shared actual ObjectArray<PsiMethod> getMethodsByNameIfNotMoreThan(String? string, GlobalSearchScope? globalSearchScope, Integer int)
            => createJavaObjectArray<PsiMethod>({});
    
    shared actual Boolean processMethodsWithName(String? string, GlobalSearchScope? globalSearchScope, Processor<PsiMethod>? processor)
            => false;
}
