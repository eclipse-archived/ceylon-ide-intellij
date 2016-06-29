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
import com.redhat.ceylon.ide.common.model {
    CeylonUnit
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Value,
    Declaration,
    FunctionOrValue,
    Function
}

import java.lang {
    JString=String,
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    IdeaModuleManager
}

String getJavaName(Declaration decl)
        => switch(decl)
        case (is Value) decl.name + "_"
        case (is Function) if (decl.toplevel) then decl.name + "_" else decl.name
        else decl.name;

shared class CeylonShortNamesCache(Project project) extends PsiShortNamesCache() {
    
    shared actual ObjectArray<JString> allClassNames {
        value classes = ArrayList<JString>();
        
        if (exists projects = project.getComponent(javaClass<IdeaCeylonProjects>())) {
            for (proj in projects.ceylonProjects) {
                for (mod in proj.modules?.fromProject else []) {
                    for (pack in mod.packages) {
                        classes.addAll(
                            CeylonIterable(pack.members)
                                .narrow<ClassOrInterface|FunctionOrValue>()
                                .filter((c) => (c of Declaration).unit is CeylonUnit)
                                .map((c) => JString(getJavaName(c)))
                        );
                    }
                }
                
            }
        }

        print(classes);
        return createJavaObjectArray(classes);
    }
    
    allFieldNames
            => ObjectArray<JString>(0);
    
    allMethodNames
            => ObjectArray<JString>(0);
    
    getAllClassNames(HashSet<JString> hashSet)
            => allClassNames.array.coalesced.each(hashSet.add);
    
    getAllFieldNames(HashSet<JString> hashSet)
            => allFieldNames.array.coalesced.each(hashSet.add);
    
    getAllMethodNames(HashSet<JString> hashSet)
            => allMethodNames.array.coalesced.each(hashSet.add);
    
    void scanInners(ClassOrInterface|FunctionOrValue decl, String name, ArrayList<PsiClass> classes) {
        for (member in decl.members) {
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
                        } else if (is Function decl = source.modelDeclaration,
                                   name == getJavaName(decl)) {
                            classes.add(CeyLightToplevelFunction(decl, project));
                        }
                    }
                }
            }
        }
        
        return createJavaObjectArray(classes);
    }
    
    getFieldsByName(String string, GlobalSearchScope globalSearchScope)
            => ObjectArray<PsiField>(0);
    
    getFieldsByNameIfNotMoreThan(String string, GlobalSearchScope globalSearchScope, Integer int)
            => ObjectArray<PsiField>(0);
    
    getMethodsByName(String string, GlobalSearchScope globalSearchScope)
            => ObjectArray<PsiMethod>(0);
    
    getMethodsByNameIfNotMoreThan(String string, GlobalSearchScope globalSearchScope, Integer int)
            => ObjectArray<PsiMethod>(0);
    
    processMethodsWithName(String string, GlobalSearchScope globalSearchScope, Processor<PsiMethod> processor)
            => false;
}
