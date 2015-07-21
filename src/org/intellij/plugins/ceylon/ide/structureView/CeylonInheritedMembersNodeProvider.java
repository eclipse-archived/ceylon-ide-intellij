package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.InheritedMembersNodeProvider;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve.FindDeclarationNodeVisitor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Adds inherited members to the tree.
 */
public class CeylonInheritedMembersNodeProvider extends InheritedMembersNodeProvider {

    @NotNull
    @Override
    public Collection<TreeElement> provideNodes(@NotNull TreeElement node) {
        if (node instanceof PsiTreeElementBase) {
            PsiElement element = ((PsiTreeElementBase) node).getElement();
            if (element instanceof CeylonPsi.DeclarationPsi) {
                Tree.Declaration declaration = ((CeylonPsi.DeclarationPsi) element).getCeylonNode();
                List<TreeElement> elements = new ArrayList<>();

                if ((declaration instanceof Tree.ObjectDefinition || declaration instanceof Tree.TypeDeclaration) && declaration.getDeclarationModel() != null) {
                    TypeDeclaration type = declaration.getDeclarationModel().getReference().getType().getDeclaration();
                    Map<String, DeclarationWithProximity> decls = type.getMatchingMemberDeclarations(declaration.getUnit(), declaration.getScope(), "", 0);

                    for (DeclarationWithProximity dwp : decls.values()) {
                        Declaration decl = dwp.getDeclaration();

                        if (type.isInherited(decl)) {
                            PsiFile file = decl.getUnit().equals(declaration.getUnit()) ? element.getContainingFile() : CeylonTreeUtil.getDeclaringFile(decl.getUnit(), element.getProject());

                            StructureViewTreeElement treeElement = getTreeElementForDeclaration((CeylonFile) file, decl);

                            if (treeElement != null) {
                                elements.add(treeElement);
                            }
                        }
                    }
                }

                return elements;
            }
        }
        return Collections.emptyList();
    }

    private StructureViewTreeElement getTreeElementForDeclaration(CeylonFile myFile, Declaration declaration) {
        if (declaration == null) {
            return null;
        }

        FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor(declaration);
        myFile.getCompilationUnit().visit(visitor);
        Tree.Declaration node = visitor.getDeclarationNode();

        if (node != null) {
            return CeylonFileTreeElement.getTreeElementForDeclaration(myFile, node, true);
        }

        return null;
//        return new CeylonFileTreeElement(myFile);
    }
}
