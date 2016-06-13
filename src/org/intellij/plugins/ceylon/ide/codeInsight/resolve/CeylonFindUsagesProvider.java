package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.nodes_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.TokenTypes;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.ParameterPsiIdOwner;
import org.intellij.plugins.ceylon.ide.parser.CeylonAntlrToIntellijLexerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonFindUsagesProvider implements FindUsagesProvider {

    private Logger logger = Logger.getInstance(CeylonFindUsagesProvider.class);

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new CeylonAntlrToIntellijLexerAdapter(),
                TokenSet.create(TokenTypes.LIDENTIFIER.getTokenType(), TokenTypes.UIDENTIFIER.getTokenType()),
                TokenSet.create(TokenTypes.MULTI_COMMENT.getTokenType(), TokenTypes.LINE_COMMENT.getTokenType()),
                TokenSet.create(TokenTypes.STRING_LITERAL.getTokenType())
        );
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof DeclarationPsiNameIdOwner;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "Please open an issue if you ever need this help :)";
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof CeylonPsi.AnyClassPsi) {
            return "class";
        } else if (element instanceof CeylonPsi.AnyInterfacePsi) {
            return "interface";
        } else if (element instanceof CeylonPsi.AttributeDeclarationPsi) {
            return "attribute";
        } else if (element instanceof CeylonPsi.AnyMethodPsi) {
            Tree.AnyMethod node = ((CeylonPsi.AnyMethodPsi) element).getCeylonNode();
            if (node.getDeclarationModel() != null && node.getDeclarationModel().isAnnotation()) {
                return "annotation";
            }
            return "function";
        } else if (element instanceof ParameterPsiIdOwner) {
            return "function parameter";
        } else if (element instanceof CeylonPsi.TypeParameterDeclarationPsi) {
            return "type parameter";
        } else if (element instanceof CeylonPsi.ObjectDefinitionPsi) {
            return "object";
        } else if (element instanceof CeylonPsi.ConstructorPsi) {
            return "constructor";
        } else if (element instanceof CeylonPsi.TypeAliasDeclarationPsi) {
            return "alias";
        } else if (element instanceof CeylonPsi.PackageDescriptorPsi) {
            return "package";
        } else if (element instanceof CeylonPsi.ModuleDescriptorPsi) {
            return "module";
        } else if (element instanceof CeylonPsi.VariablePsi) {
            return "variable";
        } else if (element instanceof CeylonPsi.AttributeGetterDefinitionPsi) {
            return "getter";
        } else if (element instanceof CeylonPsi.AttributeSetterDefinitionPsi) {
            return "setter";
        } else if (element instanceof CeylonPsi.EnumeratedPsi) {
            return "enumerated type";
        } else if (element instanceof CeylonPsi.ParameterPsi) {
            return "parameter";
        }

        logger.warn("Can't find type name for class " + element.getClass());

        return "declaration";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof CeylonCompositeElement) {
            CeylonFile file = (CeylonFile) element.getContainingFile();
            Node node = nodes_.get_().findNode(file.getCompilationUnit(), file.getTokens(),
                    element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());

            if (node != null) {
                if (node instanceof Tree.InitializerParameter) {
                    return ((Tree.InitializerParameter) node).getParameterModel().getName();
                }
                Tree.Declaration declaration = nodes_.get_().findDeclaration(file.getCompilationUnit(), node);

                return declaration.getDeclarationModel().getNameAsString();
            }
        }

        logger.warn("Descriptive name not implemented for " + element.getClass());

        if (element instanceof CeylonPsi.IdentifierPsi) {
            return ((CeylonPsi.IdentifierPsi) element).getCeylonNode().getText();
        }
        return "<unknown>";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiNamedElement) {
            return ((PsiNamedElement) element).getName();
        }

        throw new UnsupportedOperationException();
    }
}
