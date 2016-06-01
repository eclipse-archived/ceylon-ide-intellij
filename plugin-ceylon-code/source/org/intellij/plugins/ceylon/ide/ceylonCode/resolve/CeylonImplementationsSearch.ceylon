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
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.util {
    FindSubtypesVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    TypeDeclaration
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
            is TypeDeclaration decl = node.declarationModel,
            is CeylonFile ceylonFile = sourceElement.containingFile,
            exists project = findProjectForFile(ceylonFile),
            exists pus = project.typechecker?.phasedUnits?.phasedUnits) {

            for (pu in pus) {
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
        
        return true;
    }
}
