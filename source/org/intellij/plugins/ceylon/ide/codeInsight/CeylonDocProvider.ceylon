/*import ceylon.language {
    Tuple
}

import com.intellij.lang {
    ASTNode,
    Language
}
import com.intellij.lang.documentation {
    AbstractDocumentationProvider
}
import com.intellij.navigation {
    ItemPresentation
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
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
import com.intellij.psi.tree {
    IElementType
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Referenceable
}

import java.lang {
    ObjectArray,
    CharArray
}
import java.util {
    Arrays,
    List
}
import java.util.concurrent {
    Callable
}

import javax.swing {
    Icon
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
    CeyLightClass
}
import org.intellij.plugins.ceylon.ide.model {
    ConcurrencyManagerForJava
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonTokens,
    descriptions
}
import org.intellij.plugins.ceylon.ide.psi.impl {
    DeclarationPsiNameIdOwner
}
import org.intellij.plugins.ceylon.ide.resolve {
    CeylonReference {
        resolveDeclaration
    }
}

shared class CeylonDocProvider() 
        extends AbstractDocumentationProvider() {

    List<IElementType> ignoredTypes
        = Arrays.asList(CeylonTokens.\iWS,
            CeylonTokens.\iLINE_COMMENT,
            CeylonTokens.\iCOMMA,
            CeylonTokens.\iSEMICOLON,
            CeylonTokens.\iLBRACE,
            CeylonTokens.\iRBRACE,
            CeylonTokens.\iLBRACKET,
            CeylonTokens.\iRBRACKET,
            CeylonTokens.\iLPAREN,
            CeylonTokens.\iRPAREN);

    shared actual String? getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (is CeylonCompositeElement element,
            exists str = descriptions.descriptionForPsi(element)) {
            String highlighted = highlighter.highlight(str, element.project);
            return highlighted.replace("<font", "<span").replace("</font>", "</span>");
        }
        return null;
    }

    shared actual String? generateDoc(PsiElement element, PsiElement originalElement) {
        try {
            TypeChecker? tc
                = TypeCheckerProvider.getFor(element) 
                else TypeCheckerProvider.getFor(originalElement);
            if (! exists tc) { 
                //System.err.println("Can't get type checker for element " + element);
                return null;
            }
            IdeaDocGenerator generator = IdeaDocGenerator(tc);
            if (is DummyPsiElement element) {
                Referenceable referenceable = element.referenceable;
                assert (is CeylonFile file = element.containingFile);
                PhasedUnit pu = file.phasedUnit;
                Tree.CompilationUnit cu = file.compilationUnit;
                return generator.getDocumentationText(referenceable, null, cu,
                    generator.DocParams(pu, element.project));
            }
            if (element.containingFile exists) {
                value puRef = ObjectArray<PhasedUnit>.with { null };
                ConcurrencyManagerForJava.withAlternateResolution(object satisfies Callable<Object> {
                    shared actual Object? call() {
                        assert (is CeylonFile file = element.containingFile);
                            puRef.set(0, file.ensureTypechecked());
                        return null;
                    }
                });

                if (exists pu = puRef[0]) {
                    Tree.CompilationUnit cu = pu.compilationUnit;
                    IdeaDocGenerator.DocParams params = generator.DocParams(pu, element.project);
                    String? doc;
                    if (is CeyLightClass element) {
                        doc = generator.getDocumentationText((element).delegate, null, cu, params);
                    } else {
                        doc = generator.getDocumentation(cu, element.textRange.startOffset, params);
                    }
                    return doc;
                } 
                else { 
                    //LOGGER.warn("No phased unit for file " + element.containingFile.virtualFile.path);
                }
            }
        }
        catch (AssertionError|Exception e) {
            e.printStackTrace();
            throw e;
        }

        return null;
    }

    shared actual PsiElement? getCustomDocumentationElement(Editor editor, PsiFile file, PsiElement? contextElement) {
        if (exists contextElement,
            !contextElement.node.elementType in ignoredTypes) {
            return contextElement;
        }
        return null;
    }

    shared actual PsiElement? getDocumentationElementForLookupItem(PsiManager psiManager, Object tup, PsiElement element) {
        if (is Tuple<Anything,Anything,Anything[]> tup) {
            if (is Declaration first = tup.first) {
                PsiElement? target 
                    = resolveDeclaration(first, element.project);
                if (is DeclarationPsiNameIdOwner target) {
                    return target.nameIdentifier;
                }
                return target;
            }
        }
        return null;
    }

    shared actual PsiElement? getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        TypeChecker? tc = TypeCheckerProvider.getFor(context);
        if (!exists tc) {
            return null;
        }
        assert (is CeylonFile file = context.containingFile);
        Tree.CompilationUnit? cu = file.compilationUnit;
        PhasedUnit? pu = file.phasedUnit;
        if (! exists cu) {
            return null;
        }
        if (! exists pu) {
            return null;
        } 
        *//*if (link.startsWith("stp:")) {
            assert (exists offset = parseInteger(link.substring(4)));
            Node? node = nodes.findNode(cu, null, offset, offset + 1);
            if (is Tree.Type node) {
                if (exists hint = DocumentationManager.getInstance(context.project).docInfoHint) {
                    hint.cancel();
                    context.containingFile.navigate(true);
                }
                object extends WriteCommandAction<Nothing>(context.project) {
                    shared actual void run(Result<Nothing> result) {
                        Document doc = context.containingFile.viewProvider.document;
                        IdeaQuickFixData data = IdeaQuickFixData { 
                            message = null; 
                            nativeDoc = doc; 
                            rootNode = cu; 
                            phasedUnit = pu; 
                            node = node; 
                            project = null; 
                            annotation = null; 
                            ceylonProject = null; };
                        specifyTypeQuickFix.createProposal(node, data);
                    }
                }
                .execute();
                return context;
            }
        }*//*
        
        IdeaDocGenerator gen = IdeaDocGenerator(tc);
        if (exists target = gen.getLinkedModel(link, gen.DocParams(pu, context.project))) {
            if (link.startsWith("doc:")) {
                return DummyPsiElement(target, context.containingFile);
            } else if (link.startsWith("dec:")) {
                PsiElement psiDecl = resolveDeclaration(target, context.project);
                if (is Navigatable psiDecl) {
                        psiDecl.navigate(true);
                }
            }
        }
        return null;
    }

}

class DummyPsiElement(shared Referenceable referenceable, PsiFile containingFile)
        extends PsiElementBase() {

    shared actual String string => "DUMMY";

    shared actual ItemPresentation presentation {
        return object satisfies ItemPresentation {
            shared actual String presentableText {
                return referenceable.nameAsString;
            }
            shared actual String? locationString {
                return null;
            }

            shared actual Icon? getIcon(Boolean unused) {
                return null;
            }
        };
    }

    shared actual Boolean valid {
        return true;
    }

    shared actual Project project {
        return containingFile.project;
    }

    shared actual Language language {
        return CeylonLanguage.instance;
    }

    shared actual ObjectArray<PsiElement> children {
        return PsiElement.\iEMPTY_ARRAY;
    }

    shared actual PsiElement parent {
        return containingFile;
    }

    shared actual TextRange? textRange {
        return null;
    }

    shared actual Integer startOffsetInParent {
        return 0;
    }

    shared actual Integer textLength {
        return 0;
    }

    shared actual PsiElement? findElementAt(Integer offset) {
        return null;
    }

    shared actual Integer textOffset {
        return 0;
    }

    shared actual String? text {
        return null;
    }

    shared actual CharArray textToCharArray() {
        return CharArray(0);
    }

    shared actual ASTNode? node {
        return null;
    }
}*/
