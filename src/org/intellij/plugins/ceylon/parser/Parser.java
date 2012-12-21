package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.PsiBuilder;

/**
* TODO
*/
public interface Parser {
    boolean parse(PsiBuilder builder, int level);
}
