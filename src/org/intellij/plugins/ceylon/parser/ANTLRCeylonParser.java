package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.*;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public class ANTLRCeylonParser implements PsiParser {

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        final PsiBuilder.Marker rootMarker = builder.mark();
        final PsiBuilder.Marker ceylonList = builder.mark();
        while (!builder.eof()) {
            System.out.println(builder.getTokenType());
            builder.advanceLexer();
        }
        ceylonList.done(CeylonTypes.WHILE_LOOP);  //FIXME
        rootMarker.done(root);
        return builder.getTreeBuilt();

//        ANTLRStringStream input = new ANTLRStringStream(builder.getOriginalText().toString());
//        CeylonLexer lexer = new CeylonLexer(input);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        CeylonParser parser = new CeylonParser(tokens);
//
//        try {
//            GeneratedParserUtilBase.DummyBlock dummyBlock = new GeneratedParserUtilBase.DummyBlock();
//            dummyBlock.addChild(parser.compilationUnit());
//
//            while (!builder.eof()) {
//                System.out.println(builder.getTokenType());
//                builder.advanceLexer();
//            }
//
//            final PsiBuilder.Marker rootMarker = builder.mark();
//            rootMarker.done(CeylonTypes.WHILE_BLOCK);
//
//            return dummyBlock;
//        } catch (RecognitionException e) {
//            e.printStackTrace();
//        }
//
//        // TODO
//        return builder.getTreeBuilt();
    }
}
