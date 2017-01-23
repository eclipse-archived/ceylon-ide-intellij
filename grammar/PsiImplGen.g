grammar PsiImplGen;

@parser::header { 
    package com.redhat.ceylon.compiler.typechecker.treegen;
    import static com.redhat.ceylon.compiler.typechecker.treegen.Util.*;
}
@lexer::header { 
    package com.redhat.ceylon.compiler.typechecker.treegen; 
}

nodeList : { 
           println("package org.intellij.plugins.ceylon.ide.psi;\n");
           println("import com.intellij.lang.ASTNode;");
           println("import com.redhat.ceylon.compiler.typechecker.tree.Tree;");
           println("import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;");
           println("import org.intellij.plugins.ceylon.ide.resolve.*;");
           println("import org.intellij.plugins.ceylon.ide.psi.impl.*;");
           println("/* Generated using Antlr by PsiImplGen.g */");
           println("\npublic class CeylonPsiImpl {\n");
           println("    public static class GuardedVariablePsiImpl extends CeylonCompositeElementImpl");
           println("            implements CeylonPsi.GuardedVariablePsi {");
           println("        public GuardedVariablePsiImpl(ASTNode astNode) { super(astNode); }");
           println("        @Override public CustomTree.GuardedVariable getCeylonNode() { return (CustomTree.GuardedVariable) super.getCeylonNode(); }");
           println("    }");
           }
           (nodeDescription? node)+ 
           EOF
           { println("}"); }
           ;

node : '^' '(' 
       { print("    public static "); }
       ('abstract'
        { print("abstract "); }
       )?
       { print("class "); }
       n=NODE_NAME
       (
           { $n.text.equals("BASE_MEMBER_EXPRESSION") }?=> (
             { print("BaseMemberExpressionPsiImpl extends IdentifiableBaseMemberExpression"); }
             (':' NODE_NAME)?
           )
           |
           { $n.text.equals("IDENTIFIER") }?=> (
             { print("IdentifierPsiImpl extends ResolvableIdentifier"); }
             (':' NODE_NAME)?
           )
           |
           { $n.text.equals("LOCAL_MODIFIER") }?=> (
             { print("LocalModifierPsiImpl extends ResolvableLocalModifier"); }
             (':' NODE_NAME)?
           )
           |
           { $n.text.equals("STRING_LITERAL") }?=> (
             { print("StringLiteralPsiImpl extends CeylonDocResolvable"); }
             (':' NODE_NAME)?
           )
           |
           { $n.text.equals("MODULE_DESCRIPTOR") || $n.text.equals("PACKAGE_DESCRIPTOR") }?=> (
             { print(className($n.text) + "PsiImpl extends NamedStatementOrArgumentPsi"); }
             (':' NODE_NAME)?
           )
           |
           ( { print(className($n.text) + "PsiImpl"); }
             extendsNode
           )
       )
       { println("\n            implements CeylonPsi." + className($n.text) + "Psi {"); }
       { println("        public " + className($n.text) + "PsiImpl(ASTNode astNode) { super(astNode); }" ); }
       { println("        @Override public Tree." + className($n.text) + " getCeylonNode() { return (Tree." + className($n.text) + ") super.getCeylonNode(); }" ); }
       (memberDescription? subnode)*
       (memberDescription? field)*
       ')' 
       { println("    }\n"); }
     ;

extendsNode :
            ':' n=NODE_NAME (
              { $n.text.equals("DECLARATION") }?=> (
                { print(" extends DeclarationPsiNameIdOwner"); }
              )
              |
              { $n.text.equals("TYPED_ARGUMENT") }?=> (
                { print(" extends TypedArgumentPsiNameIdOwner"); }
              )
              |
              { $n.text.equals("PARAMETER") }?=> (
                { print(" extends ParameterPsiIdOwner"); }
              )
              | { print(" extends " + className($n.text) + "PsiImpl"); }
            )
            | { print(" extends CeylonCompositeElementImpl"); }
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
