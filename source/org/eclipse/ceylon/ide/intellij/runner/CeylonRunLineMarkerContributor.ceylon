/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.execution.lineMarker {
    RunLineMarkerContributor,
    ExecutorAction
}
import com.intellij.icons {
    AllIcons
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.impl.source.tree {
    LeafPsiElement
}
import org.eclipse.ceylon.compiler.typechecker.tree {
    Tree
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonPsi,
    CeylonFile,
    CeylonTokens
}

shared class CeylonRunLineMarkerContributor()
        extends RunLineMarkerContributor() {
    
    shared actual Info? getInfo(PsiElement psiElement) {
        // Note: the compilation unit has not yet been typechecked,
        // so we have to query nodes instead of the model
        if (is CeylonPsi.IdentifierPsi psiElement,
            // we are only interested in classes and functions
            is CeylonPsi.AnyClassPsi|CeylonPsi.AnyMethodPsi parent = psiElement.parent,
            exists node = parent.ceylonNode,
            is CeylonFile file = psiElement.containingFile,
            // that are toplevel
            node in file.compilationUnit.declarations,
            // have no parameter
            (parameterList(node)?.parameters?.size() else -1) == 0,
            // and are shared
            isShared(node)) {
            
            return Info(AllIcons.RunConfigurations.TestState.run,
                null, *{*ExecutorAction.getActions(0)});
        }

        // we can also target "Swarm" or "fat-jar" modules
        if (is LeafPsiElement psiElement,
            psiElement.elementType == CeylonTokens.\imodule,
            is CeylonPsi.ModuleDescriptorPsi descriptor = psiElement.parent) {

            return Info(AllIcons.RunConfigurations.TestState.run,
                null, *ExecutorAction.getActions(0));
        }

        return null;
    }
    
    Tree.ParameterList? parameterList(Tree.Declaration decl) {

        if (is Tree.AnyClass decl) {
            return decl.parameterList;
        }
        
        if (is Tree.AnyMethod decl,
            decl.parameterLists.size() == 1) {
            return decl.parameterLists[0];
        }
        
        return null;
    }
    
    Boolean isShared(Tree.Declaration clazz) {
        for (ann in clazz.annotationList.annotations) {
            if (is Tree.BaseMemberExpression name = ann.primary,
                "shared" == name.identifier.text) {
                return true;
            }
        }
        
        return false;
    }
}
