package org.intellij.plugins.ceylon.ide.structureView;

import static com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor.klass;
import static com.redhat.ceylon.ide.common.completion.overloads_.overloads;
import static com.redhat.ceylon.ide.common.util.toJavaIterable_.toJavaIterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve.CeylonReference;
import org.jetbrains.annotations.NotNull;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.structureView.impl.java.PsiFieldTreeElement;
import com.intellij.ide.structureView.impl.java.PsiMethodTreeElement;
import com.intellij.ide.util.InheritedMembersNodeProvider;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.model.loader.model.FieldValue;
import com.redhat.ceylon.model.loader.model.JavaMethod;
import com.redhat.ceylon.model.loader.model.LazyClass;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Unit;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.PSIClass;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.PSIMethod;


/**
 * Adds inherited members to the tree.
 */
class CeylonInheritedMembersNodeProvider extends InheritedMembersNodeProvider {

    @NotNull
    @Override
    public Collection<TreeElement> provideNodes(@NotNull TreeElement node) {
        if (node instanceof PsiTreeElementBase) {
            PsiElement element = ((PsiTreeElementBase) node).getElement();
            if (element instanceof CeylonPsi.DeclarationPsi) {
                Tree.Declaration declaration = ((CeylonPsi.DeclarationPsi) element).getCeylonNode();
                List<TreeElement> elements = new ArrayList<>();

                TypeDeclaration type;
                if (declaration instanceof Tree.ObjectDefinition) {
                    type = ((Tree.ObjectDefinition) declaration).getDeclarationModel().getTypeDeclaration();
                } else if (declaration instanceof Tree.ClassOrInterface) {
                    type = ((Tree.ClassOrInterface) declaration).getDeclarationModel();
                } else {
                    type = null;
                }

                if (type == null) {
                    // Maybe the file hasn't been typechecked yet
                    return elements;
                }

                Map<String, DeclarationWithProximity> decls = type.getMatchingMemberDeclarations(declaration.getUnit(), type, "", 0, null);

                for (DeclarationWithProximity dwp : decls.values()) {
                    for (Declaration decl : toJavaIterable(klass(Declaration.class),  overloads(dwp.getDeclaration()))) {
                        if (!(decl instanceof TypeParameter)) {
                            Unit unit = decl.getUnit();
                            PsiFile file = unit.equals(declaration.getUnit())
                                    ? element.getContainingFile()
                                    : CeylonTreeUtil.getDeclaringFile(unit, element.getProject());

                            if (file instanceof CeylonFile) {
                                boolean inherited = type.isInherited(decl);
                                StructureViewTreeElement treeElement = getTreeElementForDeclaration((CeylonFile) file, decl, inherited);

                                if (treeElement != null) {
                                    elements.add(treeElement);
                                }
                            } else {
                                // TODO perhaps we can provide our own TreeElements that are visually identical to the others?
                                // perhaps a Java declaration?
                                if (decl instanceof JavaMethod) {
                                    PsiMethod method = ((PSIMethod) ((JavaMethod) decl).mirror).getPsi();
                                    elements.add(new PsiMethodTreeElement(method, true));
                                } else if (decl instanceof FieldValue && decl.getScope() instanceof LazyClass) {
                                    PsiClass cls = ((PSIClass) ((LazyClass) decl.getScope()).classMirror).getPsi();
                                    PsiField field = cls.findFieldByName(decl.getName(), true);
                                    if (field != null) {
                                        elements.add(new PsiFieldTreeElement(field, true));
                                    }
                                }
                            }
                        }
                    }
                }

                return elements;
            }
        }
        return Collections.emptyList();
    }

    private StructureViewTreeElement getTreeElementForDeclaration(final CeylonFile myFile,
                                                                  final Declaration declaration,
                                                                  final boolean inherited) {
        if (declaration == null) {
            return null;
        }

        return (StructureViewTreeElement) ConcurrencyManagerForJava.outsideDumbMode(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor(declaration);
                myFile.getCompilationUnit().visit(visitor);
                Node node = visitor.getDeclarationNode();

                if (node == null) {
                    PsiElement idOwner = CeylonReference.resolveDeclaration(declaration, myFile.getProject());
                    if (idOwner instanceof CeylonCompositeElement) {
                        node = ((CeylonCompositeElement) idOwner).getCeylonNode();
                    }
                }

                if (node instanceof Tree.Declaration) {
                    return CeylonFileTreeElement.getTreeElementForDeclaration(myFile, (Tree.Declaration) node, inherited);
                }
                return null;
            }
        });

    }
}
