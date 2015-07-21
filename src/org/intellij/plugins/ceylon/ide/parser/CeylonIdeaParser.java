package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.CeylonStubTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author Matija Mazi <br/>
 */
@Deprecated
public class CeylonIdeaParser implements PsiParser {

    public static final TokenSet COMPOSITE_ELEMENTS = TokenSet.create(IElementType.enumerate(new IElementType.Predicate() {
        @Override
        public boolean matches(IElementType type) {
            return !type.equals(CeylonTokens.WS) && (type instanceof CeylonElementType);
        }
    }));

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        assert root == CeylonStubTypes.CEYLON_FILE : root;

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
            final MyTree.MyMarker tail = parser.mark();
            while (!builder.eof()) {
                builder.advanceLexer();
            }
            tail.error("Uparseable code found at end of file.");
        }
        fileMarker.done(root, null);

        ASTNode astRoot = builder.getTreeBuilt();

        myTree.bindToRoot(astRoot);
        return astRoot;
    }

}
