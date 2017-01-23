// $ANTLR 3.4 grammar/PsiIntfGen.g 2017-01-23 17:38:51
 
    package com.redhat.ceylon.compiler.typechecker.treegen;
    import static com.redhat.ceylon.compiler.typechecker.treegen.Util.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class PsiIntfGenParser extends Parser {
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


    public PsiIntfGenParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public PsiIntfGenParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return PsiIntfGenParser.tokenNames; }
    public String getGrammarFileName() { return "grammar/PsiIntfGen.g"; }



    // $ANTLR start "nodeList"
    // grammar/PsiIntfGen.g:11:1: nodeList : ( ( nodeDescription )? node )+ EOF ;
    public final void nodeList() throws RecognitionException {
        try {
            // grammar/PsiIntfGen.g:11:10: ( ( ( nodeDescription )? node )+ EOF )
            // grammar/PsiIntfGen.g:11:12: ( ( nodeDescription )? node )+ EOF
            {
             
                       println("package org.intellij.plugins.ceylon.ide.psi;\n");
                       println("import com.redhat.ceylon.compiler.typechecker.tree.Tree;");
                       println("import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;");
                       println("/* Generated using Antlr by PsiIntfGen.g */");
                       println("\npublic class CeylonPsi {\n");
                       println("    public static interface GuardedVariablePsi extends CeylonCompositeElement {");
                       println("        @Override CustomTree.GuardedVariable getCeylonNode();");
                       println("    }");
                       

            // grammar/PsiIntfGen.g:21:12: ( ( nodeDescription )? node )+
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
            	    // grammar/PsiIntfGen.g:21:13: ( nodeDescription )? node
            	    {
            	    // grammar/PsiIntfGen.g:21:13: ( nodeDescription )?
            	    int alt1=2;
            	    int LA1_0 = input.LA(1);

            	    if ( (LA1_0==DESCRIPTION) ) {
            	        alt1=1;
            	    }
            	    switch (alt1) {
            	        case 1 :
            	            // grammar/PsiIntfGen.g:21:13: nodeDescription
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
    // grammar/PsiIntfGen.g:26:1: node : '^' '(' ( 'abstract' )? n= NODE_NAME extendsNode ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')' ;
    public final void node() throws RecognitionException {
        Token n=null;

        try {
            // grammar/PsiIntfGen.g:26:6: ( '^' '(' ( 'abstract' )? n= NODE_NAME extendsNode ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')' )
            // grammar/PsiIntfGen.g:26:8: '^' '(' ( 'abstract' )? n= NODE_NAME extendsNode ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')'
            {
            match(input,CARAT,FOLLOW_CARAT_in_node93); 

            match(input,LPAREN,FOLLOW_LPAREN_in_node95); 

             print("    public static "); 

            // grammar/PsiIntfGen.g:28:8: ( 'abstract' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==19) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // grammar/PsiIntfGen.g:28:9: 'abstract'
                    {
                    match(input,19,FOLLOW_19_in_node115); 

                    }
                    break;

            }


             print("interface "); 

            n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node137); 

             print(className((n!=null?n.getText():null)) + "Psi"); 

            pushFollow(FOLLOW_extendsNode_in_node156);
            extendsNode();

            state._fsp--;


             println(" {"); 

             println("        @Override Tree." + className((n!=null?n.getText():null)) + " getCeylonNode();"); 

             println("    }\n"); 

            // grammar/PsiIntfGen.g:36:8: ( ( memberDescription )? subnode )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==DESCRIPTION) ) {
                    int LA5_1 = input.LA(2);

                    if ( (LA5_1==NODE_NAME) ) {
                        alt5=1;
                    }


                }
                else if ( (LA5_0==NODE_NAME) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // grammar/PsiIntfGen.g:36:9: ( memberDescription )? subnode
            	    {
            	    // grammar/PsiIntfGen.g:36:9: ( memberDescription )?
            	    int alt4=2;
            	    int LA4_0 = input.LA(1);

            	    if ( (LA4_0==DESCRIPTION) ) {
            	        alt4=1;
            	    }
            	    switch (alt4) {
            	        case 1 :
            	            // grammar/PsiIntfGen.g:36:9: memberDescription
            	            {
            	            pushFollow(FOLLOW_memberDescription_in_node193);
            	            memberDescription();

            	            state._fsp--;


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_subnode_in_node196);
            	    subnode();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            // grammar/PsiIntfGen.g:37:8: ( ( memberDescription )? field )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==DESCRIPTION||LA7_0==TYPE_NAME||(LA7_0 >= 19 && LA7_0 <= 21)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // grammar/PsiIntfGen.g:37:9: ( memberDescription )? field
            	    {
            	    // grammar/PsiIntfGen.g:37:9: ( memberDescription )?
            	    int alt6=2;
            	    int LA6_0 = input.LA(1);

            	    if ( (LA6_0==DESCRIPTION) ) {
            	        alt6=1;
            	    }
            	    switch (alt6) {
            	        case 1 :
            	            // grammar/PsiIntfGen.g:37:9: memberDescription
            	            {
            	            pushFollow(FOLLOW_memberDescription_in_node208);
            	            memberDescription();

            	            state._fsp--;


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_field_in_node211);
            	    field();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            match(input,RPAREN,FOLLOW_RPAREN_in_node222); 

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
    // grammar/PsiIntfGen.g:41:1: extendsNode : ( ':' n= NODE_NAME |);
    public final void extendsNode() throws RecognitionException {
        Token n=null;

        try {
            // grammar/PsiIntfGen.g:41:13: ( ':' n= NODE_NAME |)
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==EXTENDS) ) {
                alt8=1;
            }
            else if ( (LA8_0==DESCRIPTION||LA8_0==NODE_NAME||LA8_0==RPAREN||LA8_0==TYPE_NAME||(LA8_0 >= 19 && LA8_0 <= 21)) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }
            switch (alt8) {
                case 1 :
                    // grammar/PsiIntfGen.g:41:15: ':' n= NODE_NAME
                    {
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_extendsNode237); 

                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_extendsNode241); 

                     print(" extends " + className((n!=null?n.getText():null)) + "Psi"); 

                    }
                    break;
                case 2 :
                    // grammar/PsiIntfGen.g:43:15: 
                    {
                     print(" extends CeylonCompositeElement"); 

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
    // grammar/PsiIntfGen.g:46:1: nodeDescription : d= DESCRIPTION ;
    public final void nodeDescription() throws RecognitionException {
        Token d=null;

        try {
            // grammar/PsiIntfGen.g:46:17: (d= DESCRIPTION )
            // grammar/PsiIntfGen.g:46:19: d= DESCRIPTION
            {
            d=(Token)match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_nodeDescription297); 

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
    // grammar/PsiIntfGen.g:49:1: memberDescription : d= DESCRIPTION ;
    public final void memberDescription() throws RecognitionException {
        Token d=null;

        try {
            // grammar/PsiIntfGen.g:49:19: (d= DESCRIPTION )
            // grammar/PsiIntfGen.g:49:21: d= DESCRIPTION
            {
            d=(Token)match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_memberDescription327); 

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
    // grammar/PsiIntfGen.g:52:1: subnode : (n= NODE_NAME ( '?' )? f= FIELD_NAME |n= NODE_NAME ( '?' )? |mn= NODE_NAME '*' |mn= NODE_NAME '*' f= FIELD_NAME );
    public final void subnode() throws RecognitionException {
        Token n=null;
        Token f=null;
        Token mn=null;

        try {
            // grammar/PsiIntfGen.g:52:9: (n= NODE_NAME ( '?' )? f= FIELD_NAME |n= NODE_NAME ( '?' )? |mn= NODE_NAME '*' |mn= NODE_NAME '*' f= FIELD_NAME )
            int alt11=4;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==NODE_NAME) ) {
                switch ( input.LA(2) ) {
                case 16:
                    {
                    int LA11_2 = input.LA(3);

                    if ( (LA11_2==FIELD_NAME) ) {
                        alt11=4;
                    }
                    else if ( (LA11_2==DESCRIPTION||LA11_2==NODE_NAME||LA11_2==RPAREN||LA11_2==TYPE_NAME||(LA11_2 >= 19 && LA11_2 <= 21)) ) {
                        alt11=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 2, input);

                        throw nvae;

                    }
                    }
                    break;
                case OPTIONAL:
                    {
                    int LA11_3 = input.LA(3);

                    if ( (LA11_3==FIELD_NAME) ) {
                        alt11=1;
                    }
                    else if ( (LA11_3==DESCRIPTION||LA11_3==NODE_NAME||LA11_3==RPAREN||LA11_3==TYPE_NAME||(LA11_3 >= 19 && LA11_3 <= 21)) ) {
                        alt11=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 3, input);

                        throw nvae;

                    }
                    }
                    break;
                case FIELD_NAME:
                    {
                    alt11=1;
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
                    alt11=2;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }
            switch (alt11) {
                case 1 :
                    // grammar/PsiIntfGen.g:53:11: n= NODE_NAME ( '?' )? f= FIELD_NAME
                    {
                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode368); 

                    // grammar/PsiIntfGen.g:53:23: ( '?' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==OPTIONAL) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // grammar/PsiIntfGen.g:53:23: '?'
                            {
                            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subnode370); 

                            }
                            break;

                    }


                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_subnode375); 

                    }
                    break;
                case 2 :
                    // grammar/PsiIntfGen.g:54:11: n= NODE_NAME ( '?' )?
                    {
                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode389); 

                    // grammar/PsiIntfGen.g:54:23: ( '?' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==OPTIONAL) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // grammar/PsiIntfGen.g:54:23: '?'
                            {
                            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subnode391); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // grammar/PsiIntfGen.g:55:11: mn= NODE_NAME '*'
                    {
                    mn=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode406); 

                    match(input,16,FOLLOW_16_in_subnode408); 

                    }
                    break;
                case 4 :
                    // grammar/PsiIntfGen.g:56:11: mn= NODE_NAME '*' f= FIELD_NAME
                    {
                    mn=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode422); 

                    match(input,16,FOLLOW_16_in_subnode424); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_subnode428); 

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
    // grammar/PsiIntfGen.g:59:1: field : (t= TYPE_NAME f= FIELD_NAME ';' | 'boolean' f= FIELD_NAME ';' | 'string' f= FIELD_NAME ';' |l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';' | 'abstract' t= TYPE_NAME f= FIELD_NAME ';' );
    public final void field() throws RecognitionException {
        Token t=null;
        Token f=null;
        Token l=null;

        try {
            // grammar/PsiIntfGen.g:59:7: (t= TYPE_NAME f= FIELD_NAME ';' | 'boolean' f= FIELD_NAME ';' | 'string' f= FIELD_NAME ';' |l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';' | 'abstract' t= TYPE_NAME f= FIELD_NAME ';' )
            int alt12=5;
            switch ( input.LA(1) ) {
            case TYPE_NAME:
                {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==FIELD_NAME) ) {
                    alt12=1;
                }
                else if ( (LA12_1==17) ) {
                    alt12=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;

                }
                }
                break;
            case 20:
                {
                alt12=2;
                }
                break;
            case 21:
                {
                alt12=3;
                }
                break;
            case 19:
                {
                alt12=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // grammar/PsiIntfGen.g:59:9: t= TYPE_NAME f= FIELD_NAME ';'
                    {
                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field447); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field451); 

                    match(input,SEMI,FOLLOW_SEMI_in_field461); 

                    }
                    break;
                case 2 :
                    // grammar/PsiIntfGen.g:61:9: 'boolean' f= FIELD_NAME ';'
                    {
                    match(input,20,FOLLOW_20_in_field471); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field475); 

                    match(input,SEMI,FOLLOW_SEMI_in_field485); 

                    }
                    break;
                case 3 :
                    // grammar/PsiIntfGen.g:63:9: 'string' f= FIELD_NAME ';'
                    {
                    match(input,21,FOLLOW_21_in_field495); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field499); 

                    match(input,SEMI,FOLLOW_SEMI_in_field509); 

                    }
                    break;
                case 4 :
                    // grammar/PsiIntfGen.g:65:9: l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';'
                    {
                    l=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field521); 

                    match(input,17,FOLLOW_17_in_field523); 

                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field527); 

                    match(input,18,FOLLOW_18_in_field529); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field533); 

                    match(input,SEMI,FOLLOW_SEMI_in_field543); 

                    }
                    break;
                case 5 :
                    // grammar/PsiIntfGen.g:67:9: 'abstract' t= TYPE_NAME f= FIELD_NAME ';'
                    {
                    match(input,19,FOLLOW_19_in_field553); 

                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field557); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field561); 

                    match(input,SEMI,FOLLOW_SEMI_in_field571); 

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
    public static final BitSet FOLLOW_NODE_NAME_in_node137 = new BitSet(new long[]{0x0000000000385460L});
    public static final BitSet FOLLOW_extendsNode_in_node156 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_memberDescription_in_node193 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_subnode_in_node196 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_memberDescription_in_node208 = new BitSet(new long[]{0x0000000000384000L});
    public static final BitSet FOLLOW_field_in_node211 = new BitSet(new long[]{0x0000000000385020L});
    public static final BitSet FOLLOW_RPAREN_in_node222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_extendsNode237 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_extendsNode241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESCRIPTION_in_nodeDescription297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESCRIPTION_in_memberDescription327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode368 = new BitSet(new long[]{0x0000000000000880L});
    public static final BitSet FOLLOW_OPTIONAL_in_subnode370 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_subnode375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode389 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_OPTIONAL_in_subnode391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode406 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_subnode408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode422 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_subnode424 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_subnode428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field447 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field451 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_field471 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field475 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_field495 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field499 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field521 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_field523 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field527 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_field529 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field533 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_field553 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field557 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field561 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field571 = new BitSet(new long[]{0x0000000000000002L});

}