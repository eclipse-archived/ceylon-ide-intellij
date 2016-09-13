import com.intellij.openapi.application {
    QueryExecutorBase
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiReference,
    PsiElement,
    PsiClass,
    PsiMethod,
    PsiFile,
    PsiNameIdentifierOwner
}
import com.intellij.psi.search.searches {
    ReferencesSearch
}
import com.intellij.util {
    Processor
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.util {
    FindRefinementsVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    TypedDeclaration,
    TypeDeclaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    concurrencyManager,
    getCeylonProject,
    findProjectForFile,
    declarationFromPsiElement
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    isInSourceArchive,
    CeylonFile,
    CeylonTreeUtil {
        findPsiElement,
        getDeclaringFile
    }
}

//TODO: this is currently disabled in plugin.xml, but I would like to add it in as a preference
shared class RefinementsSearch() extends
        QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>() {

    shared actual void processQuery(ReferencesSearch.SearchParameters params, Processor<PsiReference> consumer) {
        if (is CeylonPsi.DeclarationPsi element = params.elementToSearch) {
            findImplementors(element, consumer.process);
        }
    }

    void findImplementors(PsiElement sourceElement, void consumer(PsiReference element)) {
        if (is PsiClass|PsiMethod sourceElement) {
            if (exists psiFile = sourceElement.containingFile,
                is TypeDeclaration|TypedDeclaration decl = declarationFromPsiElement(sourceElement),
                decl.formal || decl.default,
                exists pus = getCeylonProject(psiFile)?.typechecker?.phasedUnits) {

                scanPhasedUnits {
                    decl = decl;
                    node = null;
                    sourceElement = sourceElement;
                    consumer = consumer;
                    for (pu in pus.phasedUnits) pu
                };
            }
        }
        else if (is CeylonPsi.DeclarationPsi sourceElement,
            exists node = sourceElement.ceylonNode,
            is TypeDeclaration|TypedDeclaration decl = node.declarationModel,
            decl.formal || decl.default,
            is CeylonFile ceylonFile = sourceElement.containingFile,
            exists project = concurrencyManager.needReadAccess(()
            => findProjectForFile(ceylonFile)),
            exists modules = project.modules) {

            if (exists pus = project.typechecker?.phasedUnits) {
                scanPhasedUnits {
                    decl = decl;
                    node = node;
                    sourceElement = sourceElement;
                    consumer = consumer;
                    for (pu in pus.phasedUnits) pu
                };
            }

            if (isInSourceArchive(ceylonFile.realVirtualFile())) {
                for (mod in modules) {
                    scanPhasedUnits {
                        pus = mod.phasedUnits;
                        decl = decl;
                        node = node;
                        sourceElement = sourceElement;
                        consumer = consumer;
                    };
                }
            }
        }
    }

    void action(PsiFile? declaringFile, Node dnode,
                void consumer(PsiReference element)) {
        variable CeylonReference<PsiElement>? ref = null;
        concurrencyManager.needReadAccess(() {
            if (is PsiNameIdentifierOwner psiElement = findPsiElement(dnode, declaringFile)) {
                if (exists id = psiElement.nameIdentifier) {
                    ref = CeylonReference {
                        element = id;
                        range = TextRange.from(0, id.textLength);
                        soft = true;
                    };
                }
            }
        });
        if (exists r = ref) {
            consumer(r);
        }
    }

    void scanPhasedUnits({PhasedUnit*} pus,
        TypeDeclaration|TypedDeclaration decl,
        Tree.Declaration? node, PsiElement sourceElement,
        void consumer(PsiReference element)) {

        for (pu in pus) {
            value vis = FindRefinementsVisitor(decl);
            pu.compilationUnit.visit(vis);
            Set<Node> declarationNodes = vis.declarationNodes;
            for (dnode in declarationNodes) {
                if (exists node) {
                    if (dnode==node) {
                        continue;
                    }
                    if (is Tree.Declaration dnode,
                        dnode.declarationModel.qualifiedNameString
                            == decl.qualifiedNameString) {
                        continue;
                    }
                }

                value declaringFile = getDeclaringFile(dnode.unit, sourceElement.project);
                if (is CeylonFile declaringFile) {
                    declaringFile.doWhenAnalyzed((_) => action(declaringFile, dnode, consumer));
                } else {
                    action(declaringFile, dnode, consumer);
                }
            }
        }
    }
}

