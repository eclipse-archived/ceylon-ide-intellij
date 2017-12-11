/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import org.eclipse.ceylon.compiler.typechecker.parser {
    CeylonLexer
}
import com.intellij.openapi.util {
    Key
}
import java.util {
    Arrays,
    JList=List
}
import com.intellij.psi.tree {
    IElementType
}
import org.eclipse.ceylon.compiler.typechecker.tree {
    Node
}

shared object parserConstants {
    shared JList<Integer> nodesAllowedAtEof = Arrays.asList(CeylonLexer.eof,
        CeylonLexer.ws, CeylonLexer.lineComment, CeylonLexer.multiComment);

    // Leaves which will be wrapped in a CeylonCompositeElement, for example to allow refactoring them
    shared JList<IElementType> leavesToWrap = Arrays.asList(CeylonTypes.identifier,
        CeylonTypes.naturalLiteral, CeylonTypes.functionLiteral, CeylonTypes.stringLiteral,
        CeylonTypes.valueModifier, CeylonTypes.functionModifier);

    shared Key<Node> ceylonNodeKey = Key<Node>("CEYLON-SPEC_NODE");
    shared Key<Anything()> postParseAction = Key<Anything()>("POST-PARSE-ACTION");
}

