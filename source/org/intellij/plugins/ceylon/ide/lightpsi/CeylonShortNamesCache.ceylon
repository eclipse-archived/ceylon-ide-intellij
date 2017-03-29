import ceylon.collection {
    ArrayList
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

import org.intellij.plugins.ceylon.ide.model {
    IdeaModuleManager,
    getCeylonProjects
}

String getJavaName(Declaration decl)
        => switch(decl)
        case (is Value) decl.name + "_"
        case (is Function) if (decl.toplevel) then decl.name + "_" else decl.name
        else decl.name;

shared class CeylonShortNamesCache(Project project) extends PsiShortNamesCache() {
    
    allClassNames
            => ObjectArray.with {
                if (exists projects = getCeylonProjects(project))
                for (proj in projects.ceylonProjects)
                if (exists modules = proj.modules)
                for (mod in modules.fromProject)
                for (pack in mod.packages)
                for (m in pack.members)
                if (is ClassOrInterface|FunctionOrValue m,
                    m.unit is CeylonUnit)
                JString(getJavaName(m))
            };
    
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
                    classes.add(CeyLightClass(decl, project));
                }
                scanInners(member, name, classes);
            }
        }
    }
    
    shared actual ObjectArray<PsiClass> getClassesByName(String name, GlobalSearchScope scope) {
        value classes = ArrayList<PsiClass>();

        if (exists projects = getCeylonProjects(project)) {
            for (proj in projects.ceylonProjects) {
                if (scope.isSearchInModuleContent(proj.ideArtifact),
                    is IdeaModuleManager mm = proj.modules?.manager) {
                    
                    for (declName->source in mm.modelLoader.sourceDeclarations) {
                        switch (decl = source.modelDeclaration)
                        case (is ClassOrInterface|Value) {
                            if (name == getJavaName(decl)) {
                                classes.add(CeyLightClass(decl, project));
                            }
                            
                            scanInners(decl, name, classes);
                        }
                        case (is Function) {
                            if (name == getJavaName(decl)) {
                                classes.add(CeyLightToplevelFunction(decl, project));
                            }
                        }
                        else {}
                    }
                }
            }
        }
        
        return ObjectArray.with(classes);
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
