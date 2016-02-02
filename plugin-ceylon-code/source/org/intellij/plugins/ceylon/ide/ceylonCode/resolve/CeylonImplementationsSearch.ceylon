import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleUtil
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.search.searches {
    DefinitionsScopedSearch {
        SearchParameters
    }
}
import com.intellij.util {
    Processor,
    QueryExecutor
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.util {
    FindSubtypesVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    TypeDeclaration
}

import java.lang {
    Runnable
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonTreeUtil {
        findPsiElement,
        getDeclaringFile
    },
    CeylonFile,
    CeylonPsi
}

shared class CeylonImplementationsSearch()
        satisfies QueryExecutor<PsiElement,SearchParameters> {
    
    shared actual Boolean execute(SearchParameters queryParameters,
        Processor<PsiElement> consumer) {
        
        PsiElement sourceElement = queryParameters.element;
        if (is CeylonPsi.DeclarationPsi sourceElement) {
            Tree.Declaration node = sourceElement.ceylonNode;
            Declaration decl = node.declarationModel;

            value ceylonFile = sourceElement.containingFile;
            Module mod = ModuleUtil.findModuleForFile(ceylonFile.virtualFile, 
                ceylonFile.project);
            value provider = mod.getComponent(javaClass<ITypeCheckerProvider>());

            if (is TypeDeclaration decl, exists tc = provider.typeChecker) {
                for (pu in tc.phasedUnits.phasedUnits) {
                    value cu = pu.compilationUnit;
                    value vis = FindSubtypesVisitor(decl);
                    cu.visit(vis);
                    for (d in vis.declarationNodes) {
                        if (!d.equals(node)) {
                            Node declNode = d;
                            value run = object satisfies Runnable {
                                shared actual void run() {
                                    value declaringFile = getDeclaringFile(
                                        declNode.unit, sourceElement.project);
                                    if (is CeylonFile declaringFile) {
                                        (declaringFile).ensureTypechecked();
                                    }
                                    
                                    consumer.process(findPsiElement(declNode,
                                        declaringFile));
                                }
                            };
                            ApplicationManager.application.runReadAction(run);
                        }
                    }
                }
            }
        }
        
        return true;
    }
}
