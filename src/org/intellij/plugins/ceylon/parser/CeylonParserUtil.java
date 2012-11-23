package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.PsiBuilder;

public class CeylonParserUtil extends GeneratedParserUtilBase {

    public static boolean parseCeylon(PsiBuilder builder_, int level, Parser parser) {
        ErrorState state = ErrorState.get(builder_);
        return parseAsTree(state, builder_, level, DUMMY_BLOCK, true, parser, TRUE_CONDITION);
    }
}