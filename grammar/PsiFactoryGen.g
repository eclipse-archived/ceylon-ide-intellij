grammar PsiFactoryGen;

@parser::header { 
    package com.redhat.ceylon.compiler.typechecker.treegen; 
    import static com.redhat.ceylon.compiler.typechecker.treegen.Util.*; 
}
@lexer::header { 
    package com.redhat.ceylon.compiler.typechecker.treegen; 
}

nodeList : { 
            println("package org.intellij.plugins.ceylon.ide.psi;\n");
            println("import com.intellij.psi.tree.IElementType;");
            println("import com.intellij.psi.PsiElement;");
            println("import com.intellij.lang.ASTNode;");
            println("import org.intellij.plugins.ceylon.ide.psi.impl.*;\n");
            println("import org.intellij.plugins.ceylon.ide.psi.CeylonPsiImpl.*;\n");
            println("import static org.intellij.plugins.ceylon.ide.psi.CeylonTypes.*;\n");
            println("/* Generated using Antlr by PsiFactoryGen.g */");
            println("public class CeylonPsiFactory {");
            println("");
            println("    public static PsiElement createElement(ASTNode node) {");
            println("        IElementType type = node.getElementType();");
            println("        if (false) {");
           }
           (DESCRIPTION? node)+ 
           EOF
           {
            println("        }");
            println("");
            println("        return new CeylonCompositeElementImpl(node);");
            println("    }");
            println("}");
           }
           ;

node : '^' '('
       (
         'abstract' NODE_NAME
         | n=NODE_NAME
         {
          println("        } else if (type == " + $n.text + ") {");
          println("            return new " + className($n.text) + "PsiImpl(node);");
         }
       )
       (':' en=NODE_NAME)?
       (DESCRIPTION? subnode)*
       (DESCRIPTION? field)*
       ')'
     ;

subnode : n=NODE_NAME '?'? f=FIELD_NAME? 
        | mn=NODE_NAME '*' f=FIELD_NAME?
        ;

field : 'abstract'? (TYPE_NAME|'boolean'|'string') FIELD_NAME ';';

NODE_NAME : ('A'..'Z'|'_')+;

FIELD_NAME : ('a'..'z') ('a'..'z'|'A'..'Z')*;
TYPE_NAME : ('A'..'Z') ('a'..'z'|'A'..'Z'|'<'|'>')*;

WS : (' ' | '\n' | '\t' | '\r' | '\u000C') { skip(); };

CARAT : '^';

LPAREN : '(';
RPAREN : ')';

MANY : '*'|'+';
OPTIONAL : '?';

EXTENDS : ':';

SEMI : ';';

DESCRIPTION : '\"' (~'\"')* '\"';
