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
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonTokenType;
import org.intellij.plugins.ceylon.psi.CeylonTokens;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author Matija Mazi <br/>
 */
public class CeylonIdeaParser implements PsiParser {

    public static final Key<Node> CEYLON_NODE_KEY = new Key<>("Ceylon Node");
    public static final TokenSet COMPOSITE_ELEMENTS = TokenSet.create(IElementType.enumerate(new IElementType.Predicate() {
        @Override
        public boolean matches(IElementType type) {
            return !type.equals(CeylonTokens.WS) && !(type instanceof CeylonTokenType);
        }
    }));

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        assert root == CeylonTypes.COMPILATION_UNIT : root;

        final PsiFile file = builder.getUserDataUnprotected(FileContextUtil.CONTAINING_FILE_KEY);
        final MyTree myTree = ((CeylonFile)file).getMyTree();
        final MarkingCeylonParser parser = new MarkingCeylonParser(builder, myTree);

        final MyTree.MyMarker cuMarker = parser.mark("compilationUnit");
        final Tree.CompilationUnit unit;
        try {
            unit = parser.compilationUnit();
        } catch (RecognitionException e) {
            // todo: what's the correct way to handle this?
            throw new RuntimeException("Unrecognized", e);
        }

        if (!builder.eof()) {
            final PsiBuilder.Marker tail = builder.mark();
            while(!builder.eof()) {
                builder.advanceLexer();
            }
            tail.done(CeylonTypes.UNPARSED_TAIL);
        }
        parser.end(cuMarker, unit);

        ASTNode astRoot = builder.getTreeBuilt();
        myTree.bindToRoot(astRoot);
        return astRoot;
    }

}
