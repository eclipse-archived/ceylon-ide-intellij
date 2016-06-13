import ceylon.interop.java {
    javaClass
}

import com.intellij.lang.cacheBuilder {
    DefaultWordsScanner
}
import com.intellij.lang.findUsages {
    FindUsagesProvider
}
import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.psi {
    PsiElement,
    PsiNamedElement
}
import com.intellij.psi.tree {
    TokenSet
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import java.lang {
    UnsupportedOperationException
}

import org.intellij.plugins.ceylon.ide.ceylonCode.parser {
    CeylonAntlrToIntellijLexerAdapter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonPsi,
    TokenTypes
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    DeclarationPsiNameIdOwner,
    ParameterPsiIdOwner
}

shared class CeylonFindUsagesProvider() satisfies FindUsagesProvider {

    Logger logger = Logger.getInstance(javaClass<CeylonFindUsagesProvider>());

    wordsScanner
            => DefaultWordsScanner(CeylonAntlrToIntellijLexerAdapter(),
                TokenSet.create(TokenTypes.lidentifier.tokenType, TokenTypes.uidentifier.tokenType),
                TokenSet.create(TokenTypes.multiComment.tokenType, TokenTypes.lineComment.tokenType),
                TokenSet.create(TokenTypes.stringLiteral.tokenType));

    canFindUsagesFor(PsiElement psiElement)
            => psiElement is DeclarationPsiNameIdOwner;

    getHelpId(PsiElement psiElement)
            => "Please open an issue if you ever need this help :)";

    shared actual String getType(PsiElement element) {

        if (is CeylonPsi.AnyClassPsi element) {
            return "class";
        } else if (is CeylonPsi.AnyInterfacePsi element) {
            return "interface";
        } else if (is CeylonPsi.AttributeDeclarationPsi element) {
            return if (element.parent is CeylonPsi.ClassBodyPsi
                    || element.parent is CeylonPsi.InterfaceBodyPsi)
                then "attribute" else "value";
        } else if (is CeylonPsi.AnyMethodPsi element) {
            CeylonPsi.AnyMethodPsi methodPsi = element;
            for (a in methodPsi.ceylonNode.annotationList.annotations) {
                if (a.primary.token.text.equals("annotation")) {
                    return "annotation";
                }
            }
            return if (element.parent is CeylonPsi.ClassBodyPsi
                    || element.parent is CeylonPsi.InterfaceBodyPsi)
                then "method" else "function";
        } else if (is ParameterPsiIdOwner element) {
            return "function parameter";
        } else if (is CeylonPsi.ParameterPsi element) {
            return "parameter";
        } else if (is CeylonPsi.TypeParameterDeclarationPsi element) {
            return "type parameter";
        } else if (is CeylonPsi.ObjectDefinitionPsi element) {
            return "object";
        } else if (is CeylonPsi.ConstructorPsi element) {
            return "constructor";
        } else if (is CeylonPsi.EnumeratedPsi element) {
            return "value constructor";
        } else if (is CeylonPsi.TypeAliasDeclarationPsi element) {
            return "alias";
        } else if (is CeylonPsi.PackageDescriptorPsi element) {
            return "package";
        } else if (is CeylonPsi.ModuleDescriptorPsi element) {
            return "module";
        } else if (is CeylonPsi.VariablePsi element) {
            return "variable";
        } else if (is CeylonPsi.AttributeSetterDefinitionPsi element) {
            return "setter";
        }
        logger.warn("Can't find type name for class " + className(element));
        return "declaration";
    }

    shared actual String getDescriptiveName(PsiElement element) {
        if (is CeylonCompositeElement element) {
            assert (is CeylonFile file = element.containingFile);
            value node = nodes.findNode {
                node = file.compilationUnit;
                tokens = file.tokens;
                startOffset = element.textRange.startOffset;
                endOffset = element.textRange.endOffset;
            };
            if (exists node) {
                if (is Tree.InitializerParameter node) {
                    Tree.InitializerParameter initializerParameter = node;
                    return initializerParameter.identifier.text;
                } else {
                    if (exists declaration = nodes.findDeclaration(file.compilationUnit, node)) {
                        return declaration.identifier.text;
                    }
                }
            }
        }

        logger.warn("Descriptive name not implemented for " + className(element));

        if (is CeylonPsi.IdentifierPsi element) {
            CeylonPsi.IdentifierPsi id = element;
            return id.ceylonNode.text;
        }
        return "<unknown>";
    }

    shared actual String getNodeText(PsiElement element, Boolean useFullName) {
        if (is PsiNamedElement element) {
            return (element).name;
        }
        throw UnsupportedOperationException();
    }
}
