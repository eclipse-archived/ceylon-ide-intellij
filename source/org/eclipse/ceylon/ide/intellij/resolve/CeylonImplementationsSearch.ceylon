/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.psi {
    PsiElement,
    PsiClass,
    PsiMethod,
    PsiFile
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
import org.eclipse.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import org.eclipse.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import org.eclipse.ceylon.ide.common.util {
    FindSubtypesVisitor,
    FindRefinementsVisitor
}
import org.eclipse.ceylon.model.typechecker.model {
    TypeDeclaration,
    TypedDeclaration,
    Declaration
}

import org.eclipse.ceylon.ide.intellij.model {
    findProjectForFile,
    declarationFromPsiElement,
    getCeylonProject,
    concurrencyManager {
        needReadAccess
    }
}
import org.eclipse.ceylon.ide.intellij.psi {
    CeylonTreeUtil {
        findPsiElement,
        getDeclaringFile
    },
    CeylonFile,
    CeylonPsi,
    isInSourceArchive
}

shared class CeylonImplementationsSearch()
        satisfies QueryExecutor<PsiElement,SearchParameters> {

    shared actual Boolean execute(SearchParameters queryParameters,
                                  Processor<PsiElement> consumer) {
        findImplementors(queryParameters.element, consumer.process);
        return true;
    }

    void findImplementors(PsiElement sourceElement, void consumer(PsiElement element)) {
        switch (sourceElement)
        case (is PsiClass|PsiMethod) {
            if (exists psiFile = sourceElement.containingFile,
                is TypeDeclaration|TypedDeclaration decl = declarationFromPsiElement(sourceElement),
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
        else case (is CeylonPsi.DeclarationPsi) {
            if (exists node = sourceElement.ceylonNode,
                exists decl = node.declarationModel,
                is CeylonFile ceylonFile = sourceElement.containingFile,
                exists project
                        = needReadAccess(()
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
        else {}

    }

    void action(PsiFile declaringFile, Node dnode,
            void consumer(PsiElement element)) {
        if (exists psiElement = findPsiElement(dnode, declaringFile)) {
            consumer(psiElement);
        }
    }

    function findImplementations(Declaration decl, PhasedUnit pu) {
        switch (decl)
        case (is TypeDeclaration) {
            value vis = FindSubtypesVisitor(decl);
            pu.compilationUnit.visit(vis);
            return vis.declarationNodes;
        }
        case (is TypedDeclaration) {
            value vis = FindRefinementsVisitor(decl);
            pu.compilationUnit.visit(vis);
            return vis.declarationNodes;
        }
        else {
            assert (false);
        }
    }

    void scanPhasedUnits({PhasedUnit*} pus,
        Declaration decl, Tree.Declaration? node,
        PsiElement sourceElement,
        void consumer(PsiElement element)) {

        for (pu in pus) {
            for (dnode in findImplementations(decl, pu)) {
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
                } else if (exists declaringFile) {
                    action(declaringFile, dnode, consumer);
                }
            }
        }
    }
}
