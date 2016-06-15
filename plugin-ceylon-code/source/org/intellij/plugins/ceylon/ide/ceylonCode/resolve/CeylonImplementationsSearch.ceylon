import ceylon.interop.java {
    CeylonIterable
}

import com.intellij.openapi.application {
    ApplicationManager
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
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    FindSubtypesVisitor,
    FindRefinementsVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    TypeDeclaration,
    TypedDeclaration
}

import java.lang {
    Runnable
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    findProjectForFile
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
        if (is CeylonPsi.DeclarationPsi sourceElement,
            exists node = sourceElement.ceylonNode,
            is TypeDeclaration|TypedDeclaration decl = node.declarationModel,
            is CeylonFile ceylonFile = sourceElement.containingFile,
            exists project = findProjectForFile(ceylonFile),
            exists modules = project.modules) {

            if (exists pus = project.typechecker?.phasedUnits) {
                scanPhasedUnits(CeylonIterable(pus.phasedUnits), decl, node, sourceElement, consumer);
            }

            if (is ExternalPhasedUnit pu = ceylonFile.localAnalysisResult?.lastPhasedUnit) {
                for (mod in modules) {
                    scanPhasedUnits(mod.phasedUnits, decl, node, sourceElement, consumer);
                }
            }
        }
        
        return true;
    }

    void scanPhasedUnits({PhasedUnit*} pus, TypeDeclaration|TypedDeclaration decl, Tree.Declaration node,
        CeylonPsi.DeclarationPsi sourceElement, Processor<PsiElement> consumer) {

        for (pu in pus) {
            Set<Node> declarationNodes;
            switch (decl)
            case (is TypeDeclaration) {
                value vis = FindSubtypesVisitor(decl);
                pu.compilationUnit.visit(vis);
                declarationNodes = vis.declarationNodes;
            }
            case (is TypedDeclaration) {
                value vis = FindRefinementsVisitor(decl);
                pu.compilationUnit.visit(vis);
                declarationNodes = vis.declarationNodes;
            }
            for (d in declarationNodes) {
                if (d!=node) {
                    value run = object satisfies Runnable {
                        shared actual void run() {
                            value declaringFile = getDeclaringFile(d.unit, sourceElement.project);
                            if (is CeylonFile declaringFile) {
                                (declaringFile).ensureTypechecked();
                            }

                            consumer.process(findPsiElement(d, declaringFile));
                        }
                    };
                    ApplicationManager.application.runReadAction(run);
                }
            }
        }
    }
}
