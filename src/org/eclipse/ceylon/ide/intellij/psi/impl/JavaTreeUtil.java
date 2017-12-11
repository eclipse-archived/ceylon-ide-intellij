/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
package org.eclipse.ceylon.ide.intellij.psi.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.eclipse.ceylon.compiler.typechecker.tree.CustomTree;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.intellij.lang.CeylonLanguage;
import org.eclipse.ceylon.ide.intellij.psi.CeylonCompositeElement;
import org.eclipse.ceylon.ide.intellij.psi.CeylonPsi;

import java.util.ArrayList;
import java.util.Objects;

public class JavaTreeUtil {
//    private static Logger LOGGER = Logger.getInstance(JavaTreeUtil.class);

    public static CeylonPsi.DeclarationPsi createDeclarationFromText(Project project, String code) {
        PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(CeylonLanguage.INSTANCE, code);
        return PsiTreeUtil.getParentOfType(file.findElementAt(0), CeylonPsi.DeclarationPsi.class);
    }

    /**
     * Finds a PSI element corresponding to the original Node in a given file.
     *
     * @param ceylonNode the node to look for
     * @param file where the PSI node is expected to be
     * @return the corresponding PSI element
     */
    public static PsiElement findPsiElement(Node ceylonNode, PsiFile file) {
        if (ceylonNode == null) {
            return null;
        }

        if (ceylonNode instanceof CustomTree.GuardedVariable) {
            Node identifier = ((CustomTree.GuardedVariable)ceylonNode).getIdentifier();
            if (identifier != null) {
                ceylonNode = identifier;
            }
        }

        Integer index = ceylonNode.getStartIndex();
        if (index==null) {
            return null;
        }

        PsiElement candidate = PsiUtilCore.getElementAtOffset(file, index);

//        ArrayList<Node> ceylonNodeCandidates = new ArrayList<>();
//        ArrayList<PsiElement> candidates = new ArrayList<>();
        while (!(candidate instanceof PsiFile)) {
//            candidates.add(candidate);
            if (candidate instanceof CeylonCompositeElement) {
                Node candidateCeylonNode = ((CeylonCompositeElement) candidate).getCeylonNode();
//                ceylonNodeCandidates.add(candidateCeylonNode);
                if (candidateCeylonNode instanceof Tree.ParameterDeclaration) {
                    candidateCeylonNode = ((Tree.ParameterDeclaration) candidateCeylonNode).getTypedDeclaration();
                }
                if (candidateCeylonNode == ceylonNode) {
                    return candidate;
                } else if (candidateCeylonNode !=null && candidateCeylonNode.getClass() == ceylonNode.getClass()
                        && Objects.equals(candidateCeylonNode.getStartIndex(), ceylonNode.getStartIndex())
                        && Objects.equals(candidateCeylonNode.getEndIndex(), ceylonNode.getEndIndex())) {
                    // TODO if this file has never been opened in the editor, the compilation
                    // unit is not the same as the one that contains ceylonNode, so we can't use ==
                    return candidate;
                }
            }
//            else {
//                ceylonNodeCandidates.add(null);
//            }
            candidate = candidate.getParent();
        }

//        String message = String.format("No PSI node found for ceylon node of type %s at (%d-%d) in %s.%n",
//                ceylonNode.getNodeType(), ceylonNode.getStartIndex(), ceylonNode.getEndIndex(),
//                ceylonNode.getUnit() == null ? "<null>" : ceylonNode.getUnit().getFilename());
//        message += "====================================\n";
//        message += "Searched ceylon node:\n" + ceylonNode.getNodeType() + "(" + ceylonNode.getLocation() + ")\n";
//        message += "In the following Psi Nodes:\n" ;
//        for (int i=0; i<candidates.size(); i++) {
//            message += "    " + candidates.get(i) + " -> candidate Ceylon node: " ;
//            Node ceylonNodeCandidate = ceylonNodeCandidates.get(i);
//            if (ceylonNodeCandidate == null) {
//                message += "<null>\n" ;
//            } else {
//                message += ceylonNodeCandidates.get(i).getNodeType() + "(" + ceylonNodeCandidates.get(i).getLocation() + ")\n" ;
//            }
//        }
//        message += "====================================\n";
//
//        LOGGER.warn(message);
        return null;
    }

}
