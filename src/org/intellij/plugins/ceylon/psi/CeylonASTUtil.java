package org.intellij.plugins.ceylon.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;

import java.util.List;

public enum CeylonASTUtil {
    ;

    public static final Key<List<ParseError>> CEYLON_PARSER_ERRORS = Key.create("CEYLON_PARSER_ERRORS");

    public static void setErrors(ASTNode cuNode, List<ParseError> errors) {
        assert cuNode.getElementType() == CeylonTypes.COMPILATION_UNIT : cuNode.getElementType();
        cuNode.putUserData(CEYLON_PARSER_ERRORS, errors);
    }

    public static List<ParseError> getErrors(ASTNode cuNode) {
        assert cuNode.getElementType() == CeylonTypes.COMPILATION_UNIT;
        return cuNode.getUserData(CEYLON_PARSER_ERRORS);
    }
}
