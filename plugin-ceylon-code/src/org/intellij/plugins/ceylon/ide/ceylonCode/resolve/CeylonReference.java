package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import ceylon.language.Integer;
import ceylon.language.Sequence;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.redhat.ceylon.common.Backends;
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
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.ceylonLanguage_;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class CeylonReference<T extends PsiElement> extends PsiReferenceBase<T> {

    private Backends backend;

    public CeylonReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
        this.backend = Backends.ANY;
    }

    public CeylonReference(T element, TextRange range, boolean soft, Backends backend) {
        super(element, range, soft);
        this.backend = backend;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        final Node node = getNode();

        if (node instanceof Tree.Declaration) {
            Declaration model =
                    ((Tree.Declaration) node).getDeclarationModel();
            if (model instanceof TypedDeclaration
                    && ((TypedDeclaration) model).getOriginalDeclaration() != null) {
                // we need to resolve the original declaration
            } else if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() >= 145) {
                // IntelliJ 15+ can show usages on ctrl-click, if we return null here
                // For older versions, we have to continue resolving
                return null;
            }
        }

        final Project project = myElement.getProject();
        final Tree.CompilationUnit compilationUnit = getCompilationUnit();
        if (compilationUnit==null) {
            return null;
        }

        Node target = ConcurrencyManagerForJava.withAlternateResolution(
            new Callable<Node>() {
                @Override
                public Node call() throws Exception {
                return new IdeaNavigation(project)
                        .getTarget(compilationUnit, getNode(), backend);
                }
            });

        if (target != null) {
            VirtualFile file = getVirtualFile(target.getUnit());
            if (file != null) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                return CeylonTreeUtil.findPsiElement(target, psiFile);
            }
        }

        //for anything that's not a Ceylon declaration
        Referenceable declaration
                = nodes_.get_().getReferencedModel(node);
        if (declaration == null) {
            return null;
        }

        PsiElement location = resolveDeclaration(declaration, project);
        return location == null ? myElement.getContainingFile() : location;
    }

    @Nullable
    private Tree.CompilationUnit getCompilationUnit() {
        CeylonFile ceylonFile
                = (CeylonFile) myElement.getContainingFile();

        LocalAnalysisResult localAnalysisResult
                = ceylonFile.getLocalAnalysisResult();
        if (localAnalysisResult == null) {
            return null;
        }

        final Tree.CompilationUnit compilationUnit
                = localAnalysisResult.getTypecheckedRootNode();
        if (compilationUnit == null) {
            platformUtils_.get_().log(Status.getStatus$_DEBUG(),
                    "CeylonReference is not resolved because the file "
                            + myElement.getContainingFile()
                            + " is not typechecked and up-to-date");
            throw platformUtils_.get_().newOperationCanceledException();
        }

        return compilationUnit;
    }

    @Nullable
    private VirtualFile getVirtualFile(Unit unit) {
        if (unit instanceof SourceAware) {
            String path = ((SourceAware) unit).getSourceFullPath().toString();
            return VirtualFileManager.getInstance().findFileByUrl(
                    (path.contains("!/") ? "jar" : "file") + "://" + path
            );
        } else if (unit instanceof IResourceAware) {
            return (VirtualFile) ((IResourceAware) unit).getResourceFile();
        } else {
            return null;
        }
    }

    private Node getNode() {
        if (myElement instanceof CeylonPsi.ImportPathPsi) {
            CeylonPsi.ImportPathPsi path
                    = (CeylonPsi.ImportPathPsi) this.myElement;
            return path.getCeylonNode();
        } else {
            CeylonCompositeElement parent
                    = (CeylonCompositeElement) myElement.getParent();
            return parent.getCeylonNode();
        }
    }

    @Nullable
    public static PsiElement resolveDeclaration(Referenceable declaration, Project project) {
        PsiElement location =
                new IdeaNavigation(project)
                        .gotoDeclaration(declaration);
        if (location==null || location.getLanguage() != ceylonLanguage_.get_()) {
            return location;
        }
        else {
            Node declarationNode = nodes_.get_().getReferencedNode(declaration);
            if (declarationNode != null) {
                return CeylonTreeUtil.findPsiElement(declarationNode, location.getContainingFile());
            }
            else {
                return null;
            }
        }
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
