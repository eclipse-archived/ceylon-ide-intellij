grammar PsiIntfGen;

@parser::header { 
    package com.redhat.ceylon.compiler.typechecker.treegen;
    import static com.redhat.ceylon.compiler.typechecker.treegen.Util.*;
}
@lexer::header { 
    package com.redhat.ceylon.compiler.typechecker.treegen; 
}

nodeList : { 
           println("package org.intellij.plugins.ceylon.ide.psi;\n");
           println("import com.redhat.ceylon.compiler.typechecker.tree.Tree;");
           println("import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;");
           println("/* Generated using Antlr by PsiIntfGen.g */");
           println("\npublic class CeylonPsi {\n");
           println("    public static interface GuardedVariablePsi extends CeylonCompositeElement {");
           println("        @Override CustomTree.GuardedVariable getCeylonNode();");
           println("    }");
           }
           (nodeDescription? node)+ 
           EOF
           { println("}"); }
           ;

node : '^' '(' 
       { print("    public static "); }
       ('abstract')?
       { print("interface "); }
       n=NODE_NAME 
       { print(className($n.text) + "Psi"); }
       extendsNode
       { println(" {"); }
       { println("        @Override Tree." + className($n.text) + " getCeylonNode();"); }
       { println("    }\n"); }
       (memberDescription? subnode)*
       (memberDescription? field)*
       ')' 
     ;

extendsNode : ':' n=NODE_NAME 
              { print(" extends " + className($n.text) + "Psi"); }
            | { print(" extends CeylonCompositeElement"); }
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
