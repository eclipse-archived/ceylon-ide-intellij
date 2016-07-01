package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import ceylon.language.Integer;
import ceylon.language.Sequence;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.model.SourceAware;
import com.redhat.ceylon.ide.common.platform.Status;
import com.redhat.ceylon.ide.common.platform.platformUtils_;
import com.redhat.ceylon.ide.common.refactoring.DefaultRegion;
import com.redhat.ceylon.ide.common.typechecker.LocalAnalysisResult;
import com.redhat.ceylon.ide.common.util.nodes_;
import com.redhat.ceylon.model.typechecker.model.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;

public class CeylonReference<T extends PsiElement> extends PsiReferenceBase<T> {

    public CeylonReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement parent = myElement.getParent();
        if (parent instanceof CeylonPsi.DeclarationPsi) {
            CeylonPsi.DeclarationPsi parentDecPsi =
                    (CeylonPsi.DeclarationPsi) parent;
            Tree.Declaration node = parentDecPsi.getCeylonNode();

            //noinspection StatementWithEmptyBody
            Declaration model = node.getDeclarationModel();
            if (model instanceof TypedDeclaration
                    && ((TypedDeclaration) model).getOriginalDeclaration() != null) {
                // we need to resolve the original declaration
            } else if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() >= 145) {
                // IntelliJ 15+ can show usages on ctrl-click, if we return null here
                // For older versions, we have to continue resolving
                return null;
            }
        }

        final CeylonFile ceylonFile = (CeylonFile) myElement.getContainingFile();
        final LocalAnalysisResult localAnalysisResult = ceylonFile.getLocalAnalysisResult();
        if (localAnalysisResult == null) {
            return null;
        }
            
        final Tree.CompilationUnit compilationUnit = localAnalysisResult.getTypecheckedRootNode();
        if (compilationUnit == null) {
            platformUtils_.get_().log(Status.getStatus$_DEBUG(),
                    "CeylonReference is not resolved because the file "
                            + myElement.getContainingFile()
                            + " is not typechecked and up-to-date");
            throw platformUtils_.get_().newOperationCanceledException();
        }

        Sequence seq = ConcurrencyManagerForJava.withAlternateResolution(new Callable<Sequence>() {
            @Override
            public Sequence call() throws Exception {
                return new IdeaNavigation(myElement.getProject())
                        .findTarget(compilationUnit,
                            localAnalysisResult.getTokens(),
                            new DefaultRegion(myElement.getTextRange().getStartOffset(),
                                              myElement.getTextRange().getLength()));
            }
        });

        if (seq != null) {
            Node target = (Node) seq.get(new Integer(1));
            VirtualFile file = null;
            Unit unit = target.getUnit();
            if (unit instanceof SourceAware) {
                String path = ((SourceAware) unit).getSourceFullPath().toString();
                file = VirtualFileManager.getInstance().findFileByUrl(
                        (path.contains("!/") ? "jar" : "file") + "://" + path
                );
            } else if (unit instanceof IResourceAware) {
                file = (VirtualFile) ((IResourceAware) unit).getResourceFile();
            }

            if (file != null) {
                PsiFile psiFile = PsiManager.getInstance(myElement.getProject()).findFile(file);
                if (psiFile instanceof CeylonFile) {
                    return CeylonTreeUtil.findPsiElement(target, psiFile);
                }
            }
        }

        
        
        Node node;
        if (myElement instanceof CeylonPsi.ImportPathPsi) {
            node = ((CeylonPsi.ImportPathPsi) myElement).getCeylonNode();
        } else {
            node = ((CeylonCompositeElement) parent).getCeylonNode();
        }

        Referenceable declaration
                = nodes_.get_()
                    .getReferencedExplicitDeclaration(node, compilationUnit);
        if (declaration == null) {
            return null;
        }

        Unit unit = declaration.getUnit();
        PsiFile containingFile = myElement.getContainingFile();

        if (unit != compilationUnit.getUnit()) {
            return resolveDeclaration(declaration, myElement.getProject());
        }

        Node declarationNode =
                nodes_.get_()
                    .getReferencedNode(declaration);

        if (declarationNode != null) {
            return CeylonTreeUtil.findPsiElement(declarationNode, containingFile);
        }
        return containingFile;
    }

    @Nullable
    public static PsiElement resolveDeclaration(Referenceable declaration, Project project) {
        PsiElement location = new IdeaNavigation(project).gotoDeclaration(declaration);

        PsiElement parent = location;
        if (location != null &&
                location.getLanguage() == CeylonLanguage.INSTANCE) {
            parent = getParentOfType(location,
                    CeylonPsi.SpecifierStatementPsi.class,
                    PsiNameIdentifierOwner.class);
        }
        return parent == null ? location : parent;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        PsiElement resolved = resolve();
        PsiManager manager = getElement().getManager();
        if (manager.areElementsEquivalent(resolved, element)) {
            return true;
        }

        // Make constructor references equivalent to their containing class
        if (resolved instanceof PsiMethod && ((PsiMethod) resolved).isConstructor()) {
            PsiClass parent = ((PsiMethod) resolved).getContainingClass();
            return manager.areElementsEquivalent(parent, element);
        }
        if (element instanceof PsiMethod && ((PsiMethod) element).isConstructor()) {
            PsiClass parent = ((PsiMethod) element).getContainingClass();
            return manager.areElementsEquivalent(resolved, parent);
        }

        // Make setters and getters equivalent
        if (element instanceof CeylonPsi.AttributeSetterDefinitionPsi) {
            CeylonPsi.AttributeSetterDefinitionPsi setterPsi =
                    (CeylonPsi.AttributeSetterDefinitionPsi) element;
            Setter setter = setterPsi.getCeylonNode().getDeclarationModel();

            Value getter = null;

            if (resolved instanceof CeylonPsi.AttributeDeclarationPsi) {
                CeylonPsi.AttributeDeclarationPsi attributePsi =
                        (CeylonPsi.AttributeDeclarationPsi) resolved;
                getter = attributePsi.getCeylonNode().getDeclarationModel();
            } else if (myElement instanceof CeylonPsi.IdentifierPsi) {
                CeylonPsi.IdentifierPsi id = (CeylonPsi.IdentifierPsi) myElement;
                Scope scope = id.getCeylonNode().getScope();
                if (scope instanceof Value) {
                    getter = (Value) scope;
                }
            }

            if (setter != null && setter.getGetter() != null) {
                return setter.getGetter().equals(getter);
            }
        }
        if (element instanceof CeylonPsi.AttributeDeclarationPsi) {
            CeylonPsi.AttributeDeclarationPsi attPsi =
                    (CeylonPsi.AttributeDeclarationPsi) element;
            Value getter = attPsi.getCeylonNode().getDeclarationModel();

            Setter setter = null;

            if (resolved instanceof CeylonPsi.AttributeSetterDefinitionPsi) {
                CeylonPsi.AttributeSetterDefinitionPsi setterPsi =
                        (CeylonPsi.AttributeSetterDefinitionPsi) resolved;
                setter = setterPsi.getCeylonNode().getDeclarationModel();
            } else if (myElement instanceof CeylonPsi.IdentifierPsi) {
                CeylonPsi.IdentifierPsi id = (CeylonPsi.IdentifierPsi) myElement;
                Scope scope = id.getCeylonNode().getScope();
                if (scope instanceof Setter) {
                    setter = (Setter) scope;
                }
            }

            if (getter != null && getter.getSetter() != null) {
                return getter.getSetter().equals(setter);
            }
        }

        return false;
    }
}
