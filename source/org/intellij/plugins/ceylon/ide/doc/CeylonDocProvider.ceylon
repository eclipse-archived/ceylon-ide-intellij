import com.intellij.codeInsight.documentation {
    DocumentationManager
}
import com.intellij.lang.documentation {
    AbstractDocumentationProvider
}
import com.intellij.navigation {
    ItemPresentation
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.\imodule {
    ModuleUtilCore {
        findModuleForFile
    },
    ModuleManager
}
import com.intellij.openapi.progress {
    ProcessCanceledException
}
import com.intellij.openapi.util {
    Key
}
import com.intellij.pom {
    Navigatable
}
import com.intellij.psi {
    PsiElement,
    PsiFile,
    PsiManager
}
import com.intellij.psi.impl {
    PsiElementBase
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    UnexpectedError
}
import com.redhat.ceylon.ide.common.correct {
    specifyTypeQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable
}

import java.lang {
    CharArray
}
import java.util.concurrent {
    TimeUnit
}

import org.intellij.plugins.ceylon.ide.correct {
    IdeaQuickFixData
}
import org.intellij.plugins.ceylon.ide.doc {
    IdeaDocGenerator
}
import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.lightpsi {
    CeylonLightElement
}
import org.intellij.plugins.ceylon.ide.model {
    getCeylonProjects,
    FakeCompletionDeclaration,
    getCeylonProject
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonTokens {
        ...
    },
    descriptions,
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.psi.impl {
    DeclarationPsiNameIdOwner
}
import org.intellij.plugins.ceylon.ide.resolve {
    resolveDeclaration,
    CeylonSourceNavigator
}

shared class CeylonDocProvider() extends AbstractDocumentationProvider() {

    value typesToIgnore = [
        ws, lineComment,
        comma, semicolon,
        lbrace, rbrace,
        lbracket, rbracket,
        lparen, rparen
    ];

    shared actual String? getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (is CeylonCompositeElement element,
            exists str = descriptions.descriptionForPsi(element)) {
            return highlighter.highlight(str, element.project)
                .replace("<font", "<span")
                .replace("</font>", "</span>");
        }
        else {
            return null;
        }
    }

    function moduleForFile(PsiFile file)
            => if (exists virtualFile = file.virtualFile)
            then findModuleForFile(virtualFile, file.project)
            else null;

    function typeCheckerForElement(PsiElement? element) {
        if (exists element, element.valid) {
            if (exists file = element.containingFile) {
                if (is CeylonFile file,
                    exists typechecker = file.typechecker) {
                    return typechecker;
                }
                if (exists project = getCeylonProject(file)) {
                    return project.typechecker;
                }
            }
            if (exists ceylonProjects = getCeylonProjects(element.project)) {
                for (mod in ModuleManager.getInstance(element.project).modules) {
                    if (exists ceylonProject = ceylonProjects.getProject(mod),
                        exists modules = ceylonProject.modules,
                        exists typechecker = ceylonProject.typechecker) {
                        return typechecker;
                    }
                }
            }
        }
        return null;
    }

    function phasedUnit(PsiElement element) {
        if (is CeylonFile file = element.containingFile) {
            return file.waitForUpToDatePhasedUnit(3, TimeUnit.seconds);
        }
        return null;
    }

    shared actual String? generateDoc(PsiElement element, PsiElement? originalElement) {
        try {
            value typeChecker
                    = typeCheckerForElement(element)
                    else typeCheckerForElement(originalElement);
            if (!exists typeChecker) {
                return null;
            }
            value generator = IdeaDocGenerator(typeChecker);

            //usual case
            if (exists phasedUnit = phasedUnit(element)) {
                value params = generator.DocParams(phasedUnit, element.project);
                if (is CeylonLightElement|DummyPsiElement element) {
                    return generator.getDocumentationText {
                        rootNode = phasedUnit.compilationUnit;
                        model = switch (element)
                                case (is CeylonLightElement) element.declaration
                                case (is DummyPsiElement) element.referenceable;
                        node = null;
                        cmp = params;
                    };
                } else {
                    return generator.getDocumentation {
                        rootNode = phasedUnit.compilationUnit;
                        offset = element.textRange.startOffset;
                        cmp = params;
                    };
                }
            }

            PsiElement? sourcePsiElement
                    = let (navigationElement = element.navigationElement)
                    if (is CeylonSourceNavigator.DelegatingClass navigationElement)
                    then navigationElement.delegate
                    else navigationElement;
            
            //special case for Navigate > Class
            if (is CeylonPsi.DeclarationPsi sourcePsiElement,
                exists phasedUnit = phasedUnit(sourcePsiElement),
                exists node = sourcePsiElement.ceylonNode) {
    
                return generator.getDocumentationText {
                    rootNode = phasedUnit.compilationUnit;
                    model = node.declarationModel;
                    node = node;
                    cmp = generator.DocParams(phasedUnit, element.project);
                };
            }

        }
        catch (ProcessCanceledException e) {
            throw e;
        }
        catch (AssertionError|Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    getCustomDocumentationElement(Editor editor, PsiFile file, PsiElement? contextElement)
            => if (is CeylonFile file, exists contextElement, !contextElement.node.elementType in typesToIgnore)
            then contextElement else null;

    shared actual PsiElement? getDocumentationElementForLookupItem(PsiManager psiManager, Object arg, PsiElement element) {
        if (is Referenceable arg,
            exists decl
                    = if (is FakeCompletionDeclaration arg)
                    then arg.realDeclaration
                    else arg) {

            value target = resolveDeclaration(decl, element.project);
            return if (is DeclarationPsiNameIdOwner target)
                then target.nameIdentifier
                else target;
        }
        else {
            return null;
        }
    }

    shared actual PsiElement? getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        if (exists tc = typeCheckerForElement(context),
            is CeylonFile file = context.containingFile,
            exists analysisResult = file.availableAnalysisResult,
            exists phasedUnit = analysisResult.typecheckedPhasedUnit) {

            value rootNode = phasedUnit.compilationUnit;

            if (link.startsWith("stp:"),
                is Integer offset = Integer.parse(link[4...]),
                is Tree.Type node = nodes.findNode {
                    node = rootNode;
                    tokens = null;
                    startOffset = offset;
                    endOffset = offset+1;
                }) {

                if (exists hint
                        = DocumentationManager.getInstance(context.project).docInfoHint) {
                    hint.cancel();
                    file.navigate(true);
                }
                object extends WriteCommandAction<Nothing>(context.project) {
                    shared actual void run(Result<Nothing> result) {
                        assert (exists doc = file.viewProvider.document,
                                exists mod = moduleForFile(file),
                                exists ceylonProj = getCeylonProjects(mod.project)?.getProject(mod));
                        value data = IdeaQuickFixData {
                            message = UnexpectedError(null, null);
                            nativeDoc = doc;
                            rootNode = rootNode;
                            phasedUnit = phasedUnit;
                            node = node;
                            ideaModule = mod;
                            annotation = null;
                            ceylonProject = ceylonProj;
                        };
                        specifyTypeQuickFix.createProposal(node, data);
                    }
                }.execute();
                return context;
            }

            value gen = IdeaDocGenerator(tc);
            if (exists target
                    = gen.getLinkedModel(link, gen.DocParams(phasedUnit, context.project))) {

                if (link.startsWith("doc:")) {
                    return DummyPsiElement(target, file);
                } else if (link.startsWith("dec:"),
                            is Navigatable psiDecl
                                = resolveDeclaration(target, context.project)) {
                    psiDecl.navigate(true);
                }
            }
        }
        return null;
    }

}

final class DummyPsiElement(referenceable, containingFile)
        extends PsiElementBase() {

    shared Referenceable referenceable;
    shared actual CeylonFile containingFile;

    presentation
            => object satisfies ItemPresentation {
                presentableText => referenceable.nameAsString;
                locationString => null;
                getIcon(Boolean unused) => null;
            };

    valid => true;

    project => containingFile.project;

    language => CeylonLanguage.instance;

    children => PsiElement.emptyArray;

    parent => containingFile;

    textRange => null;

    startOffsetInParent => 0;

    textLength => 0;

    findElementAt(Integer offset) => null;

    textOffset => 0;

    text => null;

    textToCharArray() => CharArray(0);

    node => null;

    string => "";

    shared actual T? getCopyableUserData<T>(Key<T>? key)
            given T satisfies Object => null;

    shared actual void putCopyableUserData<T>(Key<T>? key, T? t)
            given T satisfies Object {}

}
