// $ANTLR 3.4 grammar/PsiImplGen.g 2017-01-23 17:38:51
 
    package com.redhat.ceylon.compiler.typechecker.treegen;
    import static com.redhat.ceylon.compiler.typechecker.treegen.Util.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class PsiImplGenParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CARAT", "DESCRIPTION", "EXTENDS", "FIELD_NAME", "LPAREN", "MANY", "NODE_NAME", "OPTIONAL", "RPAREN", "SEMI", "TYPE_NAME", "WS", "'*'", "'<'", "'>'", "'abstract'", "'boolean'", "'string'"
    };

    public static final int EOF=-1;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int CARAT=4;
    public static final int DESCRIPTION=5;
    public static final int EXTENDS=6;
    public static final int FIELD_NAME=7;
    public static final int LPAREN=8;
    public static final int MANY=9;
    public static final int NODE_NAME=10;
    public static final int OPTIONAL=11;
    public static final int RPAREN=12;
    public static final int SEMI=13;
    public static final int TYPE_NAME=14;
    public static final int WS=15;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public PsiImplGenParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public PsiImplGenParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return PsiImplGenParser.tokenNames; }
    public String getGrammarFileName() { return "grammar/PsiImplGen.g"; }



    // $ANTLR start "nodeList"
    // grammar/PsiImplGen.g:11:1: nodeList : ( ( nodeDescription )? node )+ EOF ;
    public final void nodeList() throws RecognitionException {
        try {
            // grammar/PsiImplGen.g:11:10: ( ( ( nodeDescription )? node )+ EOF )
            // grammar/PsiImplGen.g:11:12: ( ( nodeDescription )? node )+ EOF
            {
             
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
                       

            // grammar/PsiImplGen.g:26:12: ( ( nodeDescription )? node )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= CARAT && LA2_0 <= DESCRIPTION)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // grammar/PsiImplGen.g:26:13: ( nodeDescription )? node
            	    {
            	    // grammar/PsiImplGen.g:26:13: ( nodeDescription )?
            	    int alt1=2;
            	    int LA1_0 = input.LA(1);

            	    if ( (LA1_0==DESCRIPTION) ) {
            	        alt1=1;
            	    }
            	    switch (alt1) {
            	        case 1 :
            	            // grammar/PsiImplGen.g:26:13: nodeDescription
            	            {
            	            pushFollow(FOLLOW_nodeDescription_in_nodeList41);
            	            nodeDescription();

            	            state._fsp--;


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_node_in_nodeList44);
            	    node();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            match(input,EOF,FOLLOW_EOF_in_nodeList60); 

             println("}"); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "nodeList"



    // $ANTLR start "node"
    // grammar/PsiImplGen.g:31:1: node : '^' '(' ( 'abstract' )? n= NODE_NAME ({...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) | ( extendsNode ) ) ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')' ;
    public final void node() throws RecognitionException {
        Token n=null;

        try {
            // grammar/PsiImplGen.g:31:6: ( '^' '(' ( 'abstract' )? n= NODE_NAME ({...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) | ( extendsNode ) ) ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')' )
            // grammar/PsiImplGen.g:31:8: '^' '(' ( 'abstract' )? n= NODE_NAME ({...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) | ( extendsNode ) ) ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')'
            {
            match(input,CARAT,FOLLOW_CARAT_in_node93); 

            match(input,LPAREN,FOLLOW_LPAREN_in_node95); 

             print("    public static "); 

            // grammar/PsiImplGen.g:33:8: ( 'abstract' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==19) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // grammar/PsiImplGen.g:33:9: 'abstract'
                    {
                    match(input,19,FOLLOW_19_in_node115); 

                     print("abstract "); 

                    }
                    break;

            }


             print("class "); 

            n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node155); 

            // grammar/PsiImplGen.g:38:8: ({...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) |{...}? => ( ( ':' NODE_NAME )? ) | ( extendsNode ) )
            int alt9=6;
            switch ( input.LA(1) ) {
            case EXTENDS:
                {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==NODE_NAME) ) {
                    int LA9_9 = input.LA(3);

                    if ( (( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                        alt9=1;
                    }
                    else if ( (( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                        alt9=2;
                    }
                    else if ( (( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                        alt9=3;
                    }
                    else if ( (( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                        alt9=4;
                    }
                    else if ( (( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                        alt9=5;
                    }
                    else if ( (true) ) {
                        alt9=6;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 9, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    throw nvae;

                }
                }
                break;
            case DESCRIPTION:
                {
                int LA9_2 = input.LA(2);

                if ( (( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                    alt9=1;
                }
                else if ( (( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                    alt9=2;
                }
                else if ( (( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                    alt9=3;
                }
                else if ( (( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                    alt9=4;
                }
                else if ( (( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                    alt9=5;
                }
                else if ( (true) ) {
                    alt9=6;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 2, input);

                    throw nvae;

                }
                }
                break;
            case NODE_NAME:
                {
                int LA9_3 = input.LA(2);

                if ( (( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                    alt9=1;
                }
                else if ( (( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                    alt9=2;
                }
                else if ( (( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                    alt9=3;
                }
                else if ( (( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                    alt9=4;
                }
                else if ( (( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                    alt9=5;
                }
                else if ( (true) ) {
                    alt9=6;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 3, input);

                    throw nvae;

                }
                }
                break;
            case TYPE_NAME:
                {
                int LA9_4 = input.LA(2);

                if ( (( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                    alt9=1;
                }
                else if ( (( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                    alt9=2;
                }
                else if ( (( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                    alt9=3;
                }
                else if ( (( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                    alt9=4;
                }
                else if ( (( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                    alt9=5;
                }
                else if ( (true) ) {
                    alt9=6;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 4, input);

                    throw nvae;

                }
                }
                break;
            case 20:
                {
                int LA9_5 = input.LA(2);

                if ( (( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                    alt9=1;
                }
                else if ( (( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                    alt9=2;
                }
                else if ( (( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                    alt9=3;
                }
                else if ( (( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                    alt9=4;
                }
                else if ( (( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                    alt9=5;
                }
                else if ( (true) ) {
                    alt9=6;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 5, input);

                    throw nvae;

                }
                }
                break;
            case 21:
                {
                int LA9_6 = input.LA(2);

                if ( (( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                    alt9=1;
                }
                else if ( (( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                    alt9=2;
                }
                else if ( (( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                    alt9=3;
                }
                else if ( (( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                    alt9=4;
                }
                else if ( (( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                    alt9=5;
                }
                else if ( (true) ) {
                    alt9=6;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 6, input);

                    throw nvae;

                }
                }
                break;
            case 19:
                {
                int LA9_7 = input.LA(2);

                if ( (( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                    alt9=1;
                }
                else if ( (( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                    alt9=2;
                }
                else if ( (( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                    alt9=3;
                }
                else if ( (( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                    alt9=4;
                }
                else if ( (( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                    alt9=5;
                }
                else if ( (true) ) {
                    alt9=6;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 7, input);

                    throw nvae;

                }
                }
                break;
            case RPAREN:
                {
                int LA9_8 = input.LA(2);

                if ( (( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                    alt9=1;
                }
                else if ( (( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                    alt9=2;
                }
                else if ( (( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                    alt9=3;
                }
                else if ( (( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                    alt9=4;
                }
                else if ( (( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                    alt9=5;
                }
                else if ( (true) ) {
                    alt9=6;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 8, input);

                    throw nvae;

                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // grammar/PsiImplGen.g:39:12: {...}? => ( ( ':' NODE_NAME )? )
                    {
                    if ( !(( (n!=null?n.getText():null).equals("BASE_MEMBER_EXPRESSION") )) ) {
                        throw new FailedPredicateException(input, "node", " $n.text.equals(\"BASE_MEMBER_EXPRESSION\") ");
                    }

                    // grammar/PsiImplGen.g:39:60: ( ( ':' NODE_NAME )? )
                    // grammar/PsiImplGen.g:40:14: ( ':' NODE_NAME )?
                    {
                     print("BaseMemberExpressionPsiImpl extends IdentifiableBaseMemberExpression"); 

                    // grammar/PsiImplGen.g:41:14: ( ':' NODE_NAME )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==EXTENDS) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // grammar/PsiImplGen.g:41:15: ':' NODE_NAME
                            {
                            match(input,EXTENDS,FOLLOW_EXTENDS_in_node211); 

                            match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node213); 

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // grammar/PsiImplGen.g:44:12: {...}? => ( ( ':' NODE_NAME )? )
                    {
                    if ( !(( (n!=null?n.getText():null).equals("IDENTIFIER") )) ) {
                        throw new FailedPredicateException(input, "node", " $n.text.equals(\"IDENTIFIER\") ");
                    }

                    // grammar/PsiImplGen.g:44:48: ( ( ':' NODE_NAME )? )
                    // grammar/PsiImplGen.g:45:14: ( ':' NODE_NAME )?
                    {
                     print("IdentifierPsiImpl extends ResolvableIdentifier"); 

                    // grammar/PsiImplGen.g:46:14: ( ':' NODE_NAME )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==EXTENDS) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // grammar/PsiImplGen.g:46:15: ':' NODE_NAME
                            {
                            match(input,EXTENDS,FOLLOW_EXTENDS_in_node288); 

                            match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node290); 

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 3 :
                    // grammar/PsiImplGen.g:49:12: {...}? => ( ( ':' NODE_NAME )? )
                    {
                    if ( !(( (n!=null?n.getText():null).equals("LOCAL_MODIFIER") )) ) {
                        throw new FailedPredicateException(input, "node", " $n.text.equals(\"LOCAL_MODIFIER\") ");
                    }

                    // grammar/PsiImplGen.g:49:52: ( ( ':' NODE_NAME )? )
                    // grammar/PsiImplGen.g:50:14: ( ':' NODE_NAME )?
                    {
                     print("LocalModifierPsiImpl extends ResolvableLocalModifier"); 

                    // grammar/PsiImplGen.g:51:14: ( ':' NODE_NAME )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==EXTENDS) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // grammar/PsiImplGen.g:51:15: ':' NODE_NAME
                            {
                            match(input,EXTENDS,FOLLOW_EXTENDS_in_node365); 

                            match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node367); 

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 4 :
                    // grammar/PsiImplGen.g:54:12: {...}? => ( ( ':' NODE_NAME )? )
                    {
                    if ( !(( (n!=null?n.getText():null).equals("STRING_LITERAL") )) ) {
                        throw new FailedPredicateException(input, "node", " $n.text.equals(\"STRING_LITERAL\") ");
                    }

                    // grammar/PsiImplGen.g:54:52: ( ( ':' NODE_NAME )? )
                    // grammar/PsiImplGen.g:55:14: ( ':' NODE_NAME )?
                    {
                     print("StringLiteralPsiImpl extends CeylonDocResolvable"); 

                    // grammar/PsiImplGen.g:56:14: ( ':' NODE_NAME )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==EXTENDS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // grammar/PsiImplGen.g:56:15: ':' NODE_NAME
                            {
                            match(input,EXTENDS,FOLLOW_EXTENDS_in_node442); 

                            match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node444); 

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 5 :
                    // grammar/PsiImplGen.g:59:12: {...}? => ( ( ':' NODE_NAME )? )
                    {
                    if ( !(( (n!=null?n.getText():null).equals("MODULE_DESCRIPTOR") || (n!=null?n.getText():null).equals("PACKAGE_DESCRIPTOR") )) ) {
                        throw new FailedPredicateException(input, "node", " $n.text.equals(\"MODULE_DESCRIPTOR\") || $n.text.equals(\"PACKAGE_DESCRIPTOR\") ");
                    }

                    // grammar/PsiImplGen.g:59:95: ( ( ':' NODE_NAME )? )
                    // grammar/PsiImplGen.g:60:14: ( ':' NODE_NAME )?
                    {
                     print(className((n!=null?n.getText():null)) + "PsiImpl extends NamedStatementOrArgumentPsi"); 

                    // grammar/PsiImplGen.g:61:14: ( ':' NODE_NAME )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==EXTENDS) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // grammar/PsiImplGen.g:61:15: ':' NODE_NAME
                            {
                            match(input,EXTENDS,FOLLOW_EXTENDS_in_node519); 

                            match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node521); 

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 6 :
                    // grammar/PsiImplGen.g:64:12: ( extendsNode )
                    {
                    // grammar/PsiImplGen.g:64:12: ( extendsNode )
                    // grammar/PsiImplGen.g:64:14: extendsNode
                    {
                     print(className((n!=null?n.getText():null)) + "PsiImpl"); 

                    pushFollow(FOLLOW_extendsNode_in_node579);
                    extendsNode();

                    state._fsp--;


                    }


                    }
                    break;

            }


             println("\n            implements CeylonPsi." + className((n!=null?n.getText():null)) + "Psi {"); 

             println("        public " + className((n!=null?n.getText():null)) + "PsiImpl(ASTNode astNode) { super(astNode); }" ); 

             println("        @Override public Tree." + className((n!=null?n.getText():null)) + " getCeylonNode() { return (Tree." + className((n!=null?n.getText():null)) + ") super.getCeylonNode(); }" ); 

            // grammar/PsiImplGen.g:71:8: ( ( memberDescription )? subnode )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==DESCRIPTION) ) {
                    int LA11_1 = input.LA(2);

                    if ( (LA11_1==NODE_NAME) ) {
                        alt11=1;
                    }


                }
                else if ( (LA11_0==NODE_NAME) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // grammar/PsiImplGen.g:71:9: ( memberDescription )? subnode
            	    {
            	    // grammar/PsiImplGen.g:71:9: ( memberDescription )?
            	    int alt10=2;
            	    int LA10_0 = input.LA(1);

            	    if ( (LA10_0==DESCRIPTION) ) {
            	        alt10=1;
            	    }
            	    switch (alt10) {
            	        case 1 :
            	            // grammar/PsiImplGen.g:71:9: memberDescription
            	            {
            	            pushFollow(FOLLOW_memberDescription_in_node638);
            	            memberDescription();

            	            state._fsp--;


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_subnode_in_node641);
            	    subnode();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            // grammar/PsiImplGen.g:72:8: ( ( memberDescription )? field )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==DESCRIPTION||LA13_0==TYPE_NAME||(LA13_0 >= 19 && LA13_0 <= 21)) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // grammar/PsiImplGen.g:72:9: ( memberDescription )? field
            	    {
            	    // grammar/PsiImplGen.g:72:9: ( memberDescription )?
            	    int alt12=2;
            	    int LA12_0 = input.LA(1);

            	    if ( (LA12_0==DESCRIPTION) ) {
            	        alt12=1;
            	    }
            	    switch (alt12) {
            	        case 1 :
            	            // grammar/PsiImplGen.g:72:9: memberDescription
            	            {
            	            pushFollow(FOLLOW_memberDescription_in_node653);
            	            memberDescription();

            	            state._fsp--;


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_field_in_node656);
            	    field();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            match(input,RPAREN,FOLLOW_RPAREN_in_node667); 

             println("    }\n"); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "node"



    // $ANTLR start "extendsNode"
    // grammar/PsiImplGen.g:77:1: extendsNode : ( ':' n= NODE_NAME ({...}? => () |{...}? => () |{...}? => () |) |);
    public final void extendsNode() throws RecognitionException {
        Token n=null;

        try {
            // grammar/PsiImplGen.g:77:13: ( ':' n= NODE_NAME ({...}? => () |{...}? => () |{...}? => () |) |)
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==EXTENDS) ) {
                alt15=1;
            }
            else if ( (LA15_0==DESCRIPTION||LA15_0==NODE_NAME||LA15_0==RPAREN||LA15_0==TYPE_NAME||(LA15_0 >= 19 && LA15_0 <= 21)) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // grammar/PsiImplGen.g:78:13: ':' n= NODE_NAME ({...}? => () |{...}? => () |{...}? => () |)
                    {
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_extendsNode703); 

                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_extendsNode707); 

                    // grammar/PsiImplGen.g:78:29: ({...}? => () |{...}? => () |{...}? => () |)
                    int alt14=4;
                    switch ( input.LA(1) ) {
                    case DESCRIPTION:
                        {
                        int LA14_1 = input.LA(2);

                        if ( (( (n!=null?n.getText():null).equals("DECLARATION") )) ) {
                            alt14=1;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("TYPED_ARGUMENT") )) ) {
                            alt14=2;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("PARAMETER") )) ) {
                            alt14=3;
                        }
                        else if ( (true) ) {
                            alt14=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 1, input);

                            throw nvae;

                        }
                        }
                        break;
                    case NODE_NAME:
                        {
                        int LA14_2 = input.LA(2);

                        if ( (( (n!=null?n.getText():null).equals("DECLARATION") )) ) {
                            alt14=1;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("TYPED_ARGUMENT") )) ) {
                            alt14=2;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("PARAMETER") )) ) {
                            alt14=3;
                        }
                        else if ( (true) ) {
                            alt14=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 2, input);

                            throw nvae;

                        }
                        }
                        break;
                    case TYPE_NAME:
                        {
                        int LA14_3 = input.LA(2);

                        if ( (( (n!=null?n.getText():null).equals("DECLARATION") )) ) {
                            alt14=1;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("TYPED_ARGUMENT") )) ) {
                            alt14=2;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("PARAMETER") )) ) {
                            alt14=3;
                        }
                        else if ( (true) ) {
                            alt14=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 3, input);

                            throw nvae;

                        }
                        }
                        break;
                    case 20:
                        {
                        int LA14_4 = input.LA(2);

                        if ( (( (n!=null?n.getText():null).equals("DECLARATION") )) ) {
                            alt14=1;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("TYPED_ARGUMENT") )) ) {
                            alt14=2;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("PARAMETER") )) ) {
                            alt14=3;
                        }
                        else if ( (true) ) {
                            alt14=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 4, input);

                            throw nvae;

                        }
                        }
                        break;
                    case 21:
                        {
                        int LA14_5 = input.LA(2);

                        if ( (( (n!=null?n.getText():null).equals("DECLARATION") )) ) {
                            alt14=1;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("TYPED_ARGUMENT") )) ) {
                            alt14=2;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("PARAMETER") )) ) {
                            alt14=3;
                        }
                        else if ( (true) ) {
                            alt14=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 5, input);

                            throw nvae;

                        }
                        }
                        break;
                    case 19:
                        {
                        int LA14_6 = input.LA(2);

                        if ( (( (n!=null?n.getText():null).equals("DECLARATION") )) ) {
                            alt14=1;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("TYPED_ARGUMENT") )) ) {
                            alt14=2;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("PARAMETER") )) ) {
                            alt14=3;
                        }
                        else if ( (true) ) {
                            alt14=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 6, input);

                            throw nvae;

                        }
                        }
                        break;
                    case RPAREN:
                        {
                        int LA14_7 = input.LA(2);

                        if ( (( (n!=null?n.getText():null).equals("DECLARATION") )) ) {
                            alt14=1;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("TYPED_ARGUMENT") )) ) {
                            alt14=2;
                        }
                        else if ( (( (n!=null?n.getText():null).equals("PARAMETER") )) ) {
                            alt14=3;
                        }
                        else if ( (true) ) {
                            alt14=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 7, input);

                            throw nvae;

                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 0, input);

                        throw nvae;

                    }

                    switch (alt14) {
                        case 1 :
                            // grammar/PsiImplGen.g:79:15: {...}? => ()
                            {
                            if ( !(( (n!=null?n.getText():null).equals("DECLARATION") )) ) {
                                throw new FailedPredicateException(input, "extendsNode", " $n.text.equals(\"DECLARATION\") ");
                            }

                            // grammar/PsiImplGen.g:79:52: ()
                            // grammar/PsiImplGen.g:80:17: 
                            {
                             print(" extends DeclarationPsiNameIdOwner"); 

                            }


                            }
                            break;
                        case 2 :
                            // grammar/PsiImplGen.g:83:15: {...}? => ()
                            {
                            if ( !(( (n!=null?n.getText():null).equals("TYPED_ARGUMENT") )) ) {
                                throw new FailedPredicateException(input, "extendsNode", " $n.text.equals(\"TYPED_ARGUMENT\") ");
                            }

                            // grammar/PsiImplGen.g:83:55: ()
                            // grammar/PsiImplGen.g:84:17: 
                            {
                             print(" extends TypedArgumentPsiNameIdOwner"); 

                            }


                            }
                            break;
                        case 3 :
                            // grammar/PsiImplGen.g:87:15: {...}? => ()
                            {
                            if ( !(( (n!=null?n.getText():null).equals("PARAMETER") )) ) {
                                throw new FailedPredicateException(input, "extendsNode", " $n.text.equals(\"PARAMETER\") ");
                            }

                            // grammar/PsiImplGen.g:87:50: ()
                            // grammar/PsiImplGen.g:88:17: 
                            {
                             print(" extends ParameterPsiIdOwner"); 

                            }


                            }
                            break;
                        case 4 :
                            // grammar/PsiImplGen.g:90:17: 
                            {
                             print(" extends " + className((n!=null?n.getText():null)) + "PsiImpl"); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // grammar/PsiImplGen.g:92:15: 
                    {
                     print(" extends CeylonCompositeElementImpl"); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "extendsNode"



    // $ANTLR start "nodeDescription"
    // grammar/PsiImplGen.g:95:1: nodeDescription : d= DESCRIPTION ;
    public final void nodeDescription() throws RecognitionException {
        Token d=null;

        try {
            // grammar/PsiImplGen.g:95:17: (d= DESCRIPTION )
            // grammar/PsiImplGen.g:95:19: d= DESCRIPTION
            {
            d=(Token)match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_nodeDescription971); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "nodeDescription"



    // $ANTLR start "memberDescription"
    // grammar/PsiImplGen.g:98:1: memberDescription : d= DESCRIPTION ;
    public final void memberDescription() throws RecognitionException {
        Token d=null;

        try {
            // grammar/PsiImplGen.g:98:19: (d= DESCRIPTION )
            // grammar/PsiImplGen.g:98:21: d= DESCRIPTION
            {
            d=(Token)match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_memberDescription1001); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "memberDescription"



    // $ANTLR start "subnode"
    // grammar/PsiImplGen.g:101:1: subnode : (n= NODE_NAME ( '?' )? f= FIELD_NAME |n= NODE_NAME ( '?' )? |mn= NODE_NAME '*' |mn= NODE_NAME '*' f= FIELD_NAME );
    public final void subnode() throws RecognitionException {
        Token n=null;
        Token f=null;
        Token mn=null;

        try {
            // grammar/PsiImplGen.g:101:9: (n= NODE_NAME ( '?' )? f= FIELD_NAME |n= NODE_NAME ( '?' )? |mn= NODE_NAME '*' |mn= NODE_NAME '*' f= FIELD_NAME )
            int alt18=4;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==NODE_NAME) ) {
                switch ( input.LA(2) ) {
                case 16:
                    {
                    int LA18_2 = input.LA(3);

                    if ( (LA18_2==FIELD_NAME) ) {
                        alt18=4;
                    }
                    else if ( (LA18_2==DESCRIPTION||LA18_2==NODE_NAME||LA18_2==RPAREN||LA18_2==TYPE_NAME||(LA18_2 >= 19 && LA18_2 <= 21)) ) {
                        alt18=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 2, input);

                        throw nvae;

                    }
                    }
                    break;
                case OPTIONAL:
                    {
                    int LA18_3 = input.LA(3);

                    if ( (LA18_3==FIELD_NAME) ) {
                        alt18=1;
                    }
                    else if ( (LA18_3==DESCRIPTION||LA18_3==NODE_NAME||LA18_3==RPAREN||LA18_3==TYPE_NAME||(LA18_3 >= 19 && LA18_3 <= 21)) ) {
                        alt18=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 3, input);

                        throw nvae;

                    }
                    }
                    break;
                case FIELD_NAME:
                    {
                    alt18=1;
                    }
                    break;
                case DESCRIPTION:
                case NODE_NAME:
                case RPAREN:
                case TYPE_NAME:
                case 19:
                case 20:
                case 21:
                    {
                    alt18=2;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }
            switch (alt18) {
                case 1 :
                    // grammar/PsiImplGen.g:102:11: n= NODE_NAME ( '?' )? f= FIELD_NAME
                    {
                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode1042); 

                    // grammar/PsiImplGen.g:102:23: ( '?' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==OPTIONAL) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // grammar/PsiImplGen.g:102:23: '?'
                            {
                            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subnode1044); 

                            }
                            break;

                    }


                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_subnode1049); 

                    }
                    break;
                case 2 :
                    // grammar/PsiImplGen.g:103:11: n= NODE_NAME ( '?' )?
                    {
                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode1063); 

                    // grammar/PsiImplGen.g:103:23: ( '?' )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==OPTIONAL) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // grammar/PsiImplGen.g:103:23: '?'
                            {
                            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subnode1065); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // grammar/PsiImplGen.g:104:11: mn= NODE_NAME '*'
                    {
                    mn=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode1080); 

                    match(input,16,FOLLOW_16_in_subnode1082); 

                    }
                    break;
                case 4 :
                    // grammar/PsiImplGen.g:105:11: mn= NODE_NAME '*' f= FIELD_NAME
                    {
                    mn=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode1096); 

                    match(input,16,FOLLOW_16_in_subnode1098); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_subnode1102); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "subnode"



    // $ANTLR start "field"
    // grammar/PsiImplGen.g:108:1: field : (t= TYPE_NAME f= FIELD_NAME ';' | 'boolean' f= FIELD_NAME ';' | 'string' f= FIELD_NAME ';' |l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';' | 'abstract' t= TYPE_NAME f= FIELD_NAME ';' );
    public final void field() throws RecognitionException {
        Token t=null;
        Token f=null;
        Token l=null;

        try {
            // grammar/PsiImplGen.g:108:7: (t= TYPE_NAME f= FIELD_NAME ';' | 'boolean' f= FIELD_NAME ';' | 'string' f= FIELD_NAME ';' |l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';' | 'abstract' t= TYPE_NAME f= FIELD_NAME ';' )
            int alt19=5;
            switch ( input.LA(1) ) {
            case TYPE_NAME:
                {
                int LA19_1 = input.LA(2);

                if ( (LA19_1==FIELD_NAME) ) {
                    alt19=1;
                }
                else if ( (LA19_1==17) ) {
                    alt19=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 1, input);

                    throw nvae;

                }
                }
                break;
            case 20:
                {
                alt19=2;
                }
                break;
            case 21:
                {
                alt19=3;
                }
                break;
            case 19:
                {
                alt19=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }

            switch (alt19) {
                case 1 :
                    // grammar/PsiImplGen.g:108:9: t= TYPE_NAME f= FIELD_NAME ';'
                    {
                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field1121); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field1125); 

                    match(input,SEMI,FOLLOW_SEMI_in_field1135); 

                    }
                    break;
                case 2 :
                    // grammar/PsiImplGen.g:110:9: 'boolean' f= FIELD_NAME ';'
                    {
                    match(input,20,FOLLOW_20_in_field1145); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field1149); 

                    match(input,SEMI,FOLLOW_SEMI_in_field1159); 

                    }
                    break;
                case 3 :
                    // grammar/PsiImplGen.g:112:9: 'string' f= FIELD_NAME ';'
                    {
                    match(input,21,FOLLOW_21_in_field1169); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field1173); 

                    match(input,SEMI,FOLLOW_SEMI_in_field1183); 

                    }
                    break;
                case 4 :
                    // grammar/PsiImplGen.g:114:9: l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';'
                    {
                    l=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field1195); 

                    match(input,17,FOLLOW_17_in_field1197); 

                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field1201); 

                    match(input,18,FOLLOW_18_in_field1203); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field1207); 

                    match(input,SEMI,FOLLOW_SEMI_in_field1217); 

                    }
                    break;
                case 5 :
                    // grammar/PsiImplGen.g:116:9: 'abstract' t= TYPE_NAME f= FIELD_NAME ';'
                    {
                    match(input,19,FOLLOW_19_in_field1227); 

                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field1231); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field1235); 

                    match(input,SEMI,FOLLOW_SEMI_in_field1245); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "field"

    // Delegated rules


 

    public static final BitSet FOLLOW_nodeDescription_in_nodeList41 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_node_in_nodeList44 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_EOF_in_nodeList60 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARAT_in_node93 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_LPAREN_in_node95 = new BitSet(new long[]{0x0000000000080400L});
    public static final BitSet FOLLOW_19_in_node115 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node155 = new BitSet(new long[]{0x0000000000385460L});
    public static final BitSet FOLLOW_EXTENDS_in_node211 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node213 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_EXTENDS_in_node288 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node290 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_EXTENDS_in_node365 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node367 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_EXTENDS_in_node442 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node444 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_EXTENDS_in_node519 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node521 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_extendsNode_in_node579 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_memberDescription_in_node638 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_subnode_in_node641 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_memberDescription_in_node653 = new BitSet(new long[]{0x0000000000384000L});
    public static final BitSet FOLLOW_field_in_node656 = new BitSet(new long[]{0x0000000000385020L});
    public static final BitSet FOLLOW_RPAREN_in_node667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_extendsNode703 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_extendsNode707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESCRIPTION_in_nodeDescription971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESCRIPTION_in_memberDescription1001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode1042 = new BitSet(new long[]{0x0000000000000880L});
    public static final BitSet FOLLOW_OPTIONAL_in_subnode1044 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_subnode1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode1063 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_OPTIONAL_in_subnode1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode1080 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_subnode1082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode1096 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_subnode1098 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_subnode1102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field1121 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field1125 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field1135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_field1145 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field1149 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field1159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_field1169 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field1173 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field1183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field1195 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_field1197 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field1201 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_field1203 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field1207 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field1217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_field1227 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field1231 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field1235 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field1245 = new BitSet(new long[]{0x0000000000000002L});

}