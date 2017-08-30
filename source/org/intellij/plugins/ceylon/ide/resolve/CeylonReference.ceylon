import com.intellij.openapi.application {
    ApplicationInfo
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VirtualFileManager
}
import com.intellij.psi {
    ...
}
import com.redhat.ceylon.common {
    Backends
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    AnnotationVisitor
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.model {
    IResourceAware,
    SourceAware
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    ...
}

import org.intellij.plugins.ceylon.ide.lang {
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        withAlternateResolution
    }
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonPsi,
    CeylonTreeUtil
}

shared PsiElement? resolveDeclaration(Referenceable? declaration, Project project) {
    if (exists location
            = IdeaNavigation(project).gotoDeclaration(declaration)) {
        if (location.language == ceylonLanguage,
            exists file = location.containingFile,
            exists declarationNode = nodes.getReferencedNode(declaration)) {
            return CeylonTreeUtil.findPsiElement(declarationNode, file);
        }
        else {
            return location;
        }
    }
    else {
        return null;
    }
}

shared class CeylonReference(element, span = element,
                range = TextRange.from(0, span.textLength),
                backend = Backends.any)
        extends PsiReferenceBase<PsiElement>(span, range, true) {

    "The clicked identifier or annotation string"
    PsiElement element;

    "The element within which the range is located
     (the parent, in case of a package path)"
    PsiElement span;

    "The clicked text range (a subrange of the
     annotation string, in case of a doc link)"
    TextRange range;

    Backends backend;

    function getVirtualFile(Unit unit) {
        switch (unit)
        case (is SourceAware) {
            if (exists path = unit.sourceFullPath) {
                value pathWithProtocol = CeylonTreeUtil.withProtocol(path.string);
                return VirtualFileManager.instance.findFileByUrl(pathWithProtocol);
            }
        }
        else case (is IResourceAware<out Anything, out Anything, out Anything>) {
             if (is VirtualFile file = unit.resourceFile) {
                 return file;
             }
        }
        else {}
        return null;
    }

    value node {
        assert (is CeylonCompositeElement parent = element.parent);
        return parent.ceylonNode;
    }

    function resolvedElement(Node target, Project project) {
        if (exists targetUnit = target.unit,
            exists file = getVirtualFile(targetUnit),
            exists psiFile = PsiManager.getInstance(project).findFile(file)) {
            Node currentTarget;
            if (is CeylonFile psiFile,
                exists localAnalysisResult
                        = psiFile.localAnalyzer?.result,
                exists targetModel
                        = nodes.getReferencedModel(nodes.getIdentifyingNode(target) else target),
                exists targetInEditor
                        = withAlternateResolution(()
                        => nodes.getReferencedNode(targetModel, localAnalysisResult.parsedRootNode))) {
                currentTarget = targetInEditor;
            } else {
                currentTarget = target;
            }
            return CeylonTreeUtil.findPsiElement(currentTarget, psiFile);
        }
        else {
            return null;
        }
    }

    function createDocLink(Unit unit, Scope scope) {
        value docLink = Tree.DocLink(null);
        docLink.text
                = element.text.substring {
                    from = rangeInElement.startOffset;
                    end = rangeInElement.endOffset;
                };
        docLink.unit = unit;
        docLink.scope = scope;
        AnnotationVisitor().visit(docLink);
        return docLink;
    }

    value referenceNode {
        if (is CeylonDocResolvable element,
            exists unit = node.unit,
            exists scope = node.scope) {
            return createDocLink(unit, scope);
        }
        else {
            return node;
        }
    }

    shared actual PsiElement? resolve() {

        value project = element.project;

        if (is CeylonPsi.LocalModifierPsi modifier = this.element,
            exists model = modifier.ceylonNode.typeModel?.declaration,
            exists location = resolveDeclaration(original(model), project)) {

            return location;
        }

        if (is Tree.Declaration node = this.node) {
            if (is TypedDeclaration model = node.declarationModel,
                model.originalDeclaration exists) {
                //noop
                // we need to resolve the original declaration
            } else if (ApplicationInfo.instance.build.baselineVersion>=145) {
                // IntelliJ 15+ can show usages on ctrl-click, if we return null here
                // For older versions, we have to continue resolving
                return null;
            }
        }

        if (is CeylonFile ceylonFile = element.containingFile,
            exists rootNode
                    = ceylonFile.availableAnalysisResult?.typecheckedRootNode,
            exists target
                    = withAlternateResolution(()
                        => IdeaNavigation(project)
                            .getTarget(rootNode, referenceNode, backend)),
            exists result = resolvedElement(target, project)) {
            return result;
        }
        else if (exists declaration = nodes.getReferencedModel(referenceNode),
            exists location = resolveDeclaration(original(declaration), project)) {
            return location;
        }
        else {
            return null;
        }

    }

    Referenceable original(Referenceable dec)
            => if (is TypedDeclaration dec,
                   exists od = dec.originalDeclaration)
            then original(od)
            else dec;

    variants => PsiElement.emptyArray;

    shared actual Boolean isReferenceTo(PsiElement element) {
        value resolved = resolve();
        PsiManager manager = element.manager;
        if (manager.areElementsEquivalent(resolved, element)) {
            return true;
        }
        if (is PsiMethod resolved, resolved.constructor) {
            return manager.areElementsEquivalent(resolved.containingClass, element);
        }
        if (is PsiMethod element, element.constructor) {
            return manager.areElementsEquivalent(resolved, element.containingClass);
        }
        if (is CeylonPsi.AttributeSetterDefinitionPsi element) {
            Setter? setter = element.ceylonNode?.declarationModel;
            Value? getter;
            if (is CeylonPsi.AttributeDeclarationPsi resolved) {
                getter = resolved.ceylonNode?.declarationModel;
            } else if (is CeylonPsi.IdentifierPsi id = this.element,
                    is Value scope = id.ceylonNode.scope) {
                getter = scope;
            }
            else {
                getter = null;
            }
            if (exists s = setter?.getter, exists g = getter) {
                return s == g;
            }
        }
        if (is CeylonPsi.AttributeDeclarationPsi element) {
            Value? getter = element.ceylonNode?.declarationModel;
            Setter? setter;
            if (is CeylonPsi.AttributeSetterDefinitionPsi resolved) {
                setter = resolved.ceylonNode?.declarationModel;
            } else if (is CeylonPsi.IdentifierPsi id = this.element,
                    is Setter scope = id.ceylonNode?.scope) {
                setter = scope;
            }
            else {
                setter = null;
            }
            if (exists g = getter?.setter, exists s = setter) {
                return s == g;
            }
        }
        if (is CeylonPsi.TypeParameterDeclarationPsi element,
            is Tree.TypeConstraint decl = this.node,
            exists node = element.ceylonNode,
            exists dd = decl.declarationModel,
            exists nd = node.declarationModel,
            dd == nd) {
            return true;
        }
        if (is CeylonPsi.TypeConstraintPsi element,
            exists model = nodes.getReferencedModel(this.node),
            exists node = element.ceylonNode,
            exists nd = node.declarationModel,
            model == nd) {
            return true;
        }
        return false;
    }
}
