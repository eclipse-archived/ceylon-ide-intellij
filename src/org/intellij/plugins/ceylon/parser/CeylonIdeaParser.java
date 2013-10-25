package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.psi.CeylonTokens;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Matija Mazi <br/>
 */
public class CeylonIdeaParser implements PsiParser {

    public static final Key<Node> CEYLON_NODE_KEY = new Key<>("Ceylon Node");
    public static final TokenSet NON_WS_TOKEN_SET = TokenSet.create(IElementType.enumerate(new IElementType.Predicate() {
        @Override
        public boolean matches(IElementType type) {
            return !type.equals(CeylonTokens.WS);
        }
    }));

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        final CeylonParser parser = new MarkingCeylonParser(builder);
        Node result;

        try {
            if (root == CeylonTypes.COMPILATION_UNIT) {
                result = parser.compilationUnit();
/*
            // This seems to be unnecessary unless we start using chameleon tokens (see parse method's javadocs).
            else if (root == CeylonTypes.MODULE_DESCRIPTOR) {
                result = parser.moduleDescriptor();
            } else if (root == CeylonTypes.PACKAGE_DESCRIPTOR) {
                result = parser.packageDescriptor();
            } else if (root == CeylonTypes.IMPORT_MODULE_LIST) {
                result = parser.importModuleList();
            } else if (root == CeylonTypes.IMPORT_MODULE) {
                result = parser.importModule();
            } else if (root == CeylonTypes.IMPORT_LIST) {
                result = parser.importElementList();
             ...
*/
            } else {
                throw new UnsupportedOperationException(String.format("Unsupported type: %s", root));
            }
        } catch (RecognitionException e) {
            throw new RuntimeException("Unrecognized", e);
        }
        if (result == null) {
            throw new NullPointerException("CeylonParser returned null.");
        }
        final RangeMapVisitor rangeMapVisitor = new RangeMapVisitor();
        rangeMapVisitor.visitAny(result);

        final ASTNode astRoot = builder.getTreeBuilt();
        bindASTs(astRoot, result, rangeMapVisitor.getMap());
        return astRoot;
    }

    private void bindASTs(ASTNode astNode, Node specNode, Map<TextRange, Node> map) {
        if (specNode != null) {
            astNode.putUserData(CEYLON_NODE_KEY, specNode);
        }
        final ASTNode[] ijChildren = astNode.getChildren(NON_WS_TOKEN_SET);
        for (final ASTNode ijChild : ijChildren) {
            final Node specChild = map.get(ijChild.getTextRange());
            bindASTs(ijChild, specChild, map);
        }
    }
}
