import ceylon.interop.java {
    javaClass
}

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
import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.\imodule {
    ModuleUtilCore
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
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
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
    Declaration,
    Referenceable
}

import java.lang {
    CharArray
}
import java.util {
    Arrays
}
import java.util.concurrent {
    Callable
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    IdeaQuickFixData
}
import org.intellij.plugins.ceylon.ide.ceylonCode.doc {
    IdeaDocGenerator
}
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.lightpsi {
    CeylonLightElement
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    ConcurrencyManagerForJava,
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonTokens,
    descriptions
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    DeclarationPsiNameIdOwner
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    resolveDeclaration
}

shared class CeylonDocProvider() extends AbstractDocumentationProvider() {

    value logger = Logger.getInstance(javaClass<CeylonDocProvider>());

    value typesToIgnore = Arrays.asList(
        CeylonTokens.ws,
        CeylonTokens.lineComment,
        CeylonTokens.comma,
        CeylonTokens.semicolon,
        CeylonTokens.lbrace,
        CeylonTokens.rbrace,
        CeylonTokens.lbracket,
        CeylonTokens.rbracket,
        CeylonTokens.lparen,
        CeylonTokens.rparen);

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

    function moduleForFile(CeylonFile file)
            => ModuleUtilCore.findModuleForFile(file.virtualFile, file.project);

    function typeCheckerForElement(PsiElement element) {
        if (is CeylonFile file = element.containingFile ) {
            if (exists typechecker = file.typechecker) {
                return typechecker;
            }
            if (exists mod = moduleForFile(file)) {
                value provider = mod.getComponent(javaClass<ITypeCheckerProvider>());
                return provider.typeChecker;
            }
        }
        return null;
    }
    shared actual String? generateDoc(PsiElement element, PsiElement originalElement) {
        try {
            value typeChecker
                    = typeCheckerForElement(element)
                    else typeCheckerForElement(originalElement);
            if (!exists typeChecker) {
                logger.warn("No type checker for file " + element.containingFile.virtualFile.path);
                return null;
            }

            value generator = IdeaDocGenerator(typeChecker);
            if (is DummyPsiElement element) {
                assert (exists pu = element.containingFile.localAnalysisResult.lastPhasedUnit);
                return generator.getDocumentationText {
                    model = element.referenceable;
                    node = null;
                    rootNode = element.containingFile.compilationUnit;
                    cmp = generator.DocParams(pu, element.project);
                };
            }
            if (is CeylonFile file = element.containingFile,
                exists phasedUnit =
                        ConcurrencyManagerForJava.withAlternateResolution(object
                                satisfies Callable<PhasedUnit> {
                            shared actual PhasedUnit? call()
                                    => file.ensureTypechecked();
                        })) {
                value params = generator.DocParams(phasedUnit, element.project);
                if (is CeylonLightElement element) {
                    return generator.getDocumentationText {
                        model = element.declaration;
                        node = null;
                        rootNode = phasedUnit.compilationUnit;
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
            else {
                logger.warn("No phased unit for file " + element.containingFile.virtualFile.path);
            }
        }
        catch (AssertionError|Exception e) {
            e.printStackTrace();
            throw e;
        }

        return null;
    }

    getCustomDocumentationElement(Editor editor, PsiFile file, PsiElement? contextElement)
            => if (exists contextElement, !contextElement.node.elementType in typesToIgnore)
            then contextElement else null;

    shared actual PsiElement? getDocumentationElementForLookupItem(PsiManager psiManager, Object arg, PsiElement element) {
        if (is Sequence<Anything> arg, is Declaration first = arg.first) {
            value target = resolveDeclaration(first, element.project);
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
            exists localAnalysisResult = file.localAnalysisResult,
            exists cu = localAnalysisResult.typecheckedRootNode,
            exists pu = localAnalysisResult.lastPhasedUnit) {

            if (link.startsWith("stp:"),
                exists offset = parseInteger(link.substring(4)),
                is Tree.Type node = nodes.findNode(cu, null, offset, offset+1)) {
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
                            rootNode = cu;
                            phasedUnit = pu;
                            node = node;
                            project = mod;
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
                    = gen.getLinkedModel(link, gen.DocParams(pu, context.project))) {
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

class DummyPsiElement(referenceable, containingFile)
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
