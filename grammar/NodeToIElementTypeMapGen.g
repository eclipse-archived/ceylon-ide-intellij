grammar NodeToIElementTypeMapGen;

@parser::header { 
    package com.redhat.ceylon.compiler.typechecker.treegen;
    import static com.redhat.ceylon.compiler.typechecker.treegen.Util.*;
}
@lexer::header { 
    package com.redhat.ceylon.compiler.typechecker.treegen; 
}

nodeList : { 

            println("package org.intellij.plugins.ceylon.ide.psi;");
            println("");
            println("import com.intellij.psi.tree.IElementType;");
            println("import com.redhat.ceylon.compiler.typechecker.tree.Node;");
            println("import com.redhat.ceylon.compiler.typechecker.tree.Tree;");
            println("import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;");
            println("");
            println("import java.util.HashMap;");
            println("import java.util.Map;");
            println("");
            println("public class NodeToIElementTypeMap {");
            println("    private static final Map<Class<? extends Node>, IElementType> map = new HashMap<>();");
            println("");
            println("    public static IElementType get(Node node) {");
            println("        return map.get(node.getClass());");
            println("    }");
            println("");
            println("    static {");
            println("        map.put(CustomTree.ExtendedTypeExpression.class, CeylonTypes.EXTENDED_TYPE_EXPRESSION);");
            println("        map.put(CustomTree.IsCase.class, CeylonTypes.IS_CASE);");
            println("        map.put(CustomTree.MatchCase.class, CeylonTypes.MATCH_CASE);");
            println("        map.put(CustomTree.GuardedVariable.class, CeylonTypes.GUARDED_VARIABLE);");
           }
           (nodeDescription? node)+ 
           EOF
           { println("    }"); }
           { println("}"); }
           ;

node : '^' '(' 
       ('abstract')?
       n=NODE_NAME
       { println("        map.put(Tree." + className($n.text) + ".class, CeylonTypes." + $n.text + ");"); }
       extendsNode?
       (memberDescription? subnode)*
       (memberDescription? field)*
       ')' 
     ;

extendsNode : ':' NODE_NAME
            ;

nodeDescription : d=DESCRIPTION 
                  ;

memberDescription : d=DESCRIPTION 
                  ;

subnode : 
          n=NODE_NAME '?'? f=FIELD_NAME
        | n=NODE_NAME '?'?
        | mn=NODE_NAME '*'
        | mn=NODE_NAME '*' f=FIELD_NAME
        ;

field : t=TYPE_NAME f=FIELD_NAME
        ';'
      | 'boolean' f=FIELD_NAME
        ';'
      | 'string' f=FIELD_NAME
        ';'
      | l=TYPE_NAME '<' t=TYPE_NAME '>' f=FIELD_NAME
        ';'
      | 'abstract' t=TYPE_NAME f=FIELD_NAME
        ';'
      ;

NODE_NAME : ('A'..'Z'|'_')+;

FIELD_NAME : ('a'..'z') ('a'..'z'|'A'..'Z')*;
TYPE_NAME : ('A'..'Z') ('a'..'z'|'A'..'Z')*;

WS : (' ' | '\n' | '\t' | '\r' | '\u000C') { skip(); };

CARAT : '^';

LPAREN : '(';
RPAREN : ')';

MANY : '*'|'+';
OPTIONAL : '?';

EXTENDS : ':';

SEMI : ';';

DESCRIPTION : '\"' (~'\"')* '\"';
