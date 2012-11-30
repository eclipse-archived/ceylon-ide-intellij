package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.PsiBuilder;

import static org.intellij.plugins.ceylon.parser.CeylonParser.specifier;
import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;

public class CeylonParserUtil extends GeneratedParserUtilBase {

    public static boolean parseCeylon(PsiBuilder builder_, int level, Parser parser) {
        ErrorState state = ErrorState.get(builder_);
        return parseAsTree(state, builder_, level, DUMMY_BLOCK, true, parser, TRUE_CONDITION);
    }

    public static boolean parseParameterRef(PsiBuilder builder, int level) {
        if (!recursion_guard_(builder, level, "memberName")) return false;
        if (!nextTokenIs(builder, LIDENTIFIER)) return false;
        PsiBuilder.Marker marker = builder.mark();
        boolean result = consumeToken(builder, LIDENTIFIER);

        if (result && (nextTokenIs(builder, OP_COMMA) || nextTokenIs(builder, OP_EQUALS) || nextTokenIs(builder, OP_RPAREN))) {
            marker.done(MEMBER_NAME);
            specifier(builder, level + 1);
        } else {
            marker.rollbackTo();
            result = false;
        }

        return result;
    }
}