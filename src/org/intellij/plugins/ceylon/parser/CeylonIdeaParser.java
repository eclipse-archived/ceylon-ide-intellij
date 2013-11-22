package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Matija Mazi <br/>
 */
public class CeylonIdeaParser implements PsiParser {

    public static final Key<Node> CEYLON_NODE_KEY = new Key<>("CEYLON-SPEC_NODE");
    public static final TokenSet COMPOSITE_ELEMENTS = TokenSet.create(IElementType.enumerate(new IElementType.Predicate() {
        @Override
        public boolean matches(IElementType type) {
            return !type.equals(CeylonTokens.WS) && !(type instanceof CeylonTokenType);
        }
    }));

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        assert root == CeylonTypes.CEYLON_FILE : root;

        final PsiFile file = builder.getUserDataUnprotected(FileContextUtil.CONTAINING_FILE_KEY);
        assert file instanceof CeylonFile : "Not a ceylon file or not found.";

        final MarkingCeylonParser parser = new MarkingCeylonParser(builder);
        final MyTree myTree = parser.getMyTree();

        final MyTree.MyMarker fileMarker = parser.mark();
        try {
            parser.compilationUnit();
        } catch (RecognitionException e) {
            // todo: what's the correct way to handle this?
            throw new RuntimeException("Unrecognized", e);
        }

        if (!builder.eof()) {
            final MyTree.MyMarker tail =  parser.mark();
            while(!builder.eof()) {
                builder.advanceLexer();
            }
            tail.error("Uparseable code found at end of file.");
        }
        fileMarker.done(root, null);

        ASTNode astRoot = builder.getTreeBuilt();

        for (ASTNode node : astRoot.getChildren(null)) {
            // these may be comments or COMPILATION_UNIT
            if (node.getElementType() == CeylonTypes.COMPILATION_UNIT) {
                CeylonASTUtil.setErrors(node, parser.getErrors());
            }
        }

        myTree.bindToRoot(astRoot);
        return astRoot;
    }

}
