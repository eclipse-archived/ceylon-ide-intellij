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
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.model {
    IResourceAware,
    SourceAware
}
import com.redhat.ceylon.ide.common.platform {
    Status,
    platformUtils
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    ...
}

import java.util.concurrent {
    Callable
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    ConcurrencyManagerForJava
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonPsi,
    CeylonTreeUtil
}

shared PsiElement? resolveDeclaration(Referenceable declaration, Project project) {
    if (exists location
            = IdeaNavigation(project).gotoDeclaration(declaration)) {
        if (location.language != ceylonLanguage) {
            return location;
        }
        else if (exists declarationNode = nodes.getReferencedNode(declaration)) {
            return CeylonTreeUtil.findPsiElement(declarationNode, location.containingFile);
        }
    }
    return null;
}

shared class CeylonReference<T>(T element, TextRange range, Boolean soft,
            Backends backend = Backends.any)
        extends PsiReferenceBase<T>(element, range, soft)
        given T satisfies PsiElement {

    function getVirtualFile(Unit unit) {
        if (is SourceAware unit) {
            assert (exists path = unit.sourceFullPath?.string);
            value prefix = "!/" in path then "jar" else "file";
            return VirtualFileManager.instance.findFileByUrl(prefix + "://" + path);
        } else if (is IResourceAware<out Anything, VirtualFile, VirtualFile> unit) {
            return unit.resourceFile;
        } else {
            return null;
        }
    }

    value node {
        if (is CeylonPsi.ImportPathPsi myElement) {
            return myElement.ceylonNode;
        } else if (is CeylonCompositeElement parent = myElement.parent) {
            return parent.ceylonNode;
        }
        else {
            assert (false);
        }
    }

    value compilationUnit {
        assert (is CeylonFile ceylonFile = myElement.containingFile);
        LocalAnalysisResult? localAnalysisResult = ceylonFile.localAnalysisResult;
        if (!exists localAnalysisResult) {
            return null;
        }
        if (!exists compilationUnit = localAnalysisResult.typecheckedRootNode) {
            platformUtils.log(Status._DEBUG,
                "CeylonReference is not resolved because the file ``myElement.containingFile`` is not typechecked and up-to-date");
            throw platformUtils.newOperationCanceledException();
        }
        return localAnalysisResult.typecheckedRootNode;
    }

    shared actual PsiElement? resolve() {
        if (is Tree.Declaration node = this.node) {
            if (is TypedDeclaration model = node.declarationModel,
                model.originalDeclaration exists) {
                //noop
                // we need to resolve the original declaration
            } else if (ApplicationInfo.instance.build.baselineVersion >= 145) {
                // IntelliJ 15+ can show usages on ctrl-click, if we return null here
                // For older versions, we have to continue resolving
                return null;
            }
        }

        value project = myElement.project;
        value compilationUnit = this.compilationUnit;
        if (!exists compilationUnit) {
            return null;
        }

        Node? target = ConcurrencyManagerForJava.withAlternateResolution(object satisfies Callable<Node> {
            call() => IdeaNavigation(project).getTarget(compilationUnit, node, backend);
        });
        if (exists target, exists file = getVirtualFile(target.unit)) {
            value psiFile = PsiManager.getInstance(project).findFile(file);
            return CeylonTreeUtil.findPsiElement(target, psiFile);
        }
        value declaration = nodes.getReferencedModel(node);
        if (!exists declaration) {
            return null;
        }

        return if (exists location = resolveDeclaration(declaration, project))
            then location else myElement.containingFile;
    }

    variants => PsiElement.emptyArray;

    shared actual Boolean isReferenceTo(PsiElement element) {
        PsiElement? resolved = resolve();
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
            Setter setter = element.ceylonNode.declarationModel;
            Value? getter;
            if (is CeylonPsi.AttributeDeclarationPsi resolved) {
                getter = resolved.ceylonNode.declarationModel;
            } else if (is CeylonPsi.IdentifierPsi myElement,
                    is Value scope = myElement.ceylonNode.scope) {
                getter = scope;
            }
            else {
                getter = null;
            }
            if (exists s = setter.getter, exists g = getter) {
                return s == g;
            }
        }
        if (is CeylonPsi.AttributeDeclarationPsi element) {
            Value getter = element.ceylonNode.declarationModel;
            Setter? setter;
            if (is CeylonPsi.AttributeSetterDefinitionPsi resolved) {
                setter = resolved.ceylonNode.declarationModel;
            } else if (is CeylonPsi.IdentifierPsi myElement,
                    is Setter scope = myElement.ceylonNode.scope) {
                setter = scope;
            }
            else {
                setter = null;
            }
            if (exists g = getter.setter, exists s = setter) {
                return s == g;
            }
        }
        return false;
    }
}
