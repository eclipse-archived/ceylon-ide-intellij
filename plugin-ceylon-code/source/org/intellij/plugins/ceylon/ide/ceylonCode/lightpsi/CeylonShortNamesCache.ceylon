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
    Value,
    Declaration
}

import java.lang {
    JString=String,
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    IdeaModuleManager
}

String getJavaName(Declaration decl) {
    return switch(decl)
        case (is Value) decl.name + "_"
    else decl.name;
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
                                .narrow<ClassOrInterface|Value>()
                                .map((c) => JString(getJavaName(c)))
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
    
    void scanInners(ClassOrInterface|Value decl, String name, ArrayList<PsiClass> classes) {
        value members = if (is ClassOrInterface decl) then decl.members else decl.members;
        
        for (member in CeylonIterable(members)) {
            if (is ClassOrInterface member) {
                if (name == getJavaName(member)) {
                    classes.add(CeyLightClass(decl of Declaration, project));
                }
                scanInners(member, name, classes);
            }
        }
    }
    
    shared actual ObjectArray<PsiClass> getClassesByName(String name, GlobalSearchScope scope) {
        value classes = ArrayList<PsiClass>();

        if (exists projects = project.getComponent(javaClass<IdeaCeylonProjects>())) {
            for (proj in projects.ceylonProjects) {
                value ijMod = proj.ideArtifact;
                
                if (scope.isSearchInModuleContent(ijMod),
                    is IdeaModuleManager mm = proj.modules?.manager) {
                    
                    for (declName->source in mm.modelLoader.sourceDeclarations) {
                        if (is ClassOrInterface|Value decl = source.modelDeclaration) {
                            if (name == getJavaName(decl)) {
                                classes.add(CeyLightClass(decl of Declaration, project));
                            }
                            
                            scanInners(decl, name, classes);
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
