package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonClass;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTypes;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.ParameterDeclarationPsiIdOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        if (psiElement instanceof CeylonPsi.AttributeDeclarationPsi) {
            return true;
        } else if (psiElement instanceof CeylonClass) {
            return true;
        } else if (psiElement instanceof CeylonPsi.MethodDefinitionPsi) {
            return true;
        } else if (psiElement instanceof ParameterDeclarationPsiIdOwner) {
            return true;
        } else if (psiElement instanceof CeylonPsi.TypeParameterDeclarationPsi) {
            return true;
        } else if (psiElement instanceof CeylonPsi.ObjectDefinitionPsi) {
            return true;
        }
//        System.out.println("Can't find usages for " + psiElement);
        return false;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "Please open an issue if you ever need this help :)";
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element.getParent() instanceof CeylonPsi.ClassOrInterfacePsi) {
            return element.getNode().getElementType() == CeylonTypes.INTERFACE_DEFINITION ? "Interface" : "Class";
        } else if (element instanceof CeylonPsi.AttributeDeclarationPsi) {
            return "Attribute";
        } else if (element instanceof CeylonPsi.MethodDefinitionPsi) {
            return "Method";
        } else if (element instanceof ParameterDeclarationPsiIdOwner) {
            return "Function parameter";
        } else if (element instanceof CeylonPsi.TypeParameterDeclarationPsi) {
            return "Type parameter";
        } else if (element instanceof CeylonPsi.ObjectDefinitionPsi) {
            return "Object";
        }

        throw new UnsupportedOperationException(element.toString());
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof CeylonPsi.AttributeDeclarationPsi) {
            return ((CeylonPsi.AttributeDeclarationPsi) element).getCeylonNode().getDeclarationModel().getQualifiedNameString();
        } else if (element instanceof CeylonClass) {
            Tree.Declaration ceylonNode = ((CeylonClass) element).getCeylonNode();
            if (ceylonNode == null) {
                // perhaps a stub
                return ((CeylonClass) element).getQualifiedName();
            }
            Declaration model = ceylonNode.getDeclarationModel();
            return model == null ? ceylonNode.getIdentifier().getText() : model.getQualifiedNameString();
        } else if (element instanceof CeylonPsi.MethodDefinitionPsi) {
            Function model = ((CeylonPsi.MethodDefinitionPsi) element).getCeylonNode().getDeclarationModel();
            return model == null ? ((CeylonPsi.MethodDefinitionPsi) element).getCeylonNode().getIdentifier().getText() : model.getQualifiedNameString();
        } else if (element instanceof CeylonPsi.ParameterDeclarationPsi) {
            return ((CeylonPsi.ParameterDeclarationPsi) element).getCeylonNode().getTypedDeclaration().getIdentifier().getText();
        } else if (element instanceof CeylonPsi.TypeParameterDeclarationPsi) {
            return ((CeylonPsi.TypeParameterDeclarationPsi) element).getCeylonNode().getIdentifier().getText();
        } else if (element instanceof CeylonFile) {
            return ((CeylonFile) element).getName();
        } else if (element instanceof CeylonPsi.ObjectDefinitionPsi) {
            return ((CeylonPsi.ObjectDefinitionPsi) element).getCeylonNode().getIdentifier().getText();
        }

        throw new UnsupportedOperationException("Descriptive name not implemented for " + element.getClass());
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiNamedElement) {
            return ((PsiNamedElement) element).getName();
        }

        throw new UnsupportedOperationException();
    }
}
