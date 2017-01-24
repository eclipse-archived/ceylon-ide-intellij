// $ANTLR 3.4 grammar/NodeToIElementTypeMapGen.g 2017-01-24 18:07:50
 
    package com.redhat.ceylon.compiler.typechecker.treegen;
    import static com.redhat.ceylon.compiler.typechecker.treegen.Util.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class NodeToIElementTypeMapGenParser extends Parser {
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


    public NodeToIElementTypeMapGenParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public NodeToIElementTypeMapGenParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return NodeToIElementTypeMapGenParser.tokenNames; }
    public String getGrammarFileName() { return "grammar/NodeToIElementTypeMapGen.g"; }



    // $ANTLR start "nodeList"
    // grammar/NodeToIElementTypeMapGen.g:11:1: nodeList : ( ( nodeDescription )? node )+ EOF ;
    public final void nodeList() throws RecognitionException {
        try {
            // grammar/NodeToIElementTypeMapGen.g:11:10: ( ( ( nodeDescription )? node )+ EOF )
            // grammar/NodeToIElementTypeMapGen.g:11:12: ( ( nodeDescription )? node )+ EOF
            {
             

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
                        println("        map.put(CustomTree.GuardedVariable.class, CeylonTypes.GUARDED_VARIABLE);");
                       

            // grammar/NodeToIElementTypeMapGen.g:35:12: ( ( nodeDescription )? node )+
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
            	    // grammar/NodeToIElementTypeMapGen.g:35:13: ( nodeDescription )? node
            	    {
            	    // grammar/NodeToIElementTypeMapGen.g:35:13: ( nodeDescription )?
            	    int alt1=2;
            	    int LA1_0 = input.LA(1);

            	    if ( (LA1_0==DESCRIPTION) ) {
            	        alt1=1;
            	    }
            	    switch (alt1) {
            	        case 1 :
            	            // grammar/NodeToIElementTypeMapGen.g:35:13: nodeDescription
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

             println("    }"); 

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
    // grammar/NodeToIElementTypeMapGen.g:41:1: node : '^' '(' ( 'abstract' )? n= NODE_NAME ( extendsNode )? ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')' ;
    public final void node() throws RecognitionException {
        Token n=null;

        try {
            // grammar/NodeToIElementTypeMapGen.g:41:6: ( '^' '(' ( 'abstract' )? n= NODE_NAME ( extendsNode )? ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')' )
            // grammar/NodeToIElementTypeMapGen.g:41:8: '^' '(' ( 'abstract' )? n= NODE_NAME ( extendsNode )? ( ( memberDescription )? subnode )* ( ( memberDescription )? field )* ')'
            {
            match(input,CARAT,FOLLOW_CARAT_in_node106); 

            match(input,LPAREN,FOLLOW_LPAREN_in_node108); 

            // grammar/NodeToIElementTypeMapGen.g:42:8: ( 'abstract' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==19) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // grammar/NodeToIElementTypeMapGen.g:42:9: 'abstract'
                    {
                    match(input,19,FOLLOW_19_in_node119); 

                    }
                    break;

            }


            n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node132); 

             println("        map.put(Tree." + className((n!=null?n.getText():null)) + ".class, CeylonTypes." + (n!=null?n.getText():null) + ");"); 

            // grammar/NodeToIElementTypeMapGen.g:45:8: ( extendsNode )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==EXTENDS) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // grammar/NodeToIElementTypeMapGen.g:45:8: extendsNode
                    {
                    pushFollow(FOLLOW_extendsNode_in_node150);
                    extendsNode();

                    state._fsp--;


                    }
                    break;

            }


            // grammar/NodeToIElementTypeMapGen.g:46:8: ( ( memberDescription )? subnode )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==DESCRIPTION) ) {
                    int LA6_1 = input.LA(2);

                    if ( (LA6_1==NODE_NAME) ) {
                        alt6=1;
                    }


                }
                else if ( (LA6_0==NODE_NAME) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // grammar/NodeToIElementTypeMapGen.g:46:9: ( memberDescription )? subnode
            	    {
            	    // grammar/NodeToIElementTypeMapGen.g:46:9: ( memberDescription )?
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==DESCRIPTION) ) {
            	        alt5=1;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // grammar/NodeToIElementTypeMapGen.g:46:9: memberDescription
            	            {
            	            pushFollow(FOLLOW_memberDescription_in_node161);
            	            memberDescription();

            	            state._fsp--;


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_subnode_in_node164);
            	    subnode();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            // grammar/NodeToIElementTypeMapGen.g:47:8: ( ( memberDescription )? field )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DESCRIPTION||LA8_0==TYPE_NAME||(LA8_0 >= 19 && LA8_0 <= 21)) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // grammar/NodeToIElementTypeMapGen.g:47:9: ( memberDescription )? field
            	    {
            	    // grammar/NodeToIElementTypeMapGen.g:47:9: ( memberDescription )?
            	    int alt7=2;
            	    int LA7_0 = input.LA(1);

            	    if ( (LA7_0==DESCRIPTION) ) {
            	        alt7=1;
            	    }
            	    switch (alt7) {
            	        case 1 :
            	            // grammar/NodeToIElementTypeMapGen.g:47:9: memberDescription
            	            {
            	            pushFollow(FOLLOW_memberDescription_in_node176);
            	            memberDescription();

            	            state._fsp--;


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_field_in_node179);
            	    field();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            match(input,RPAREN,FOLLOW_RPAREN_in_node190); 

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
    // grammar/NodeToIElementTypeMapGen.g:51:1: extendsNode : ':' NODE_NAME ;
    public final void extendsNode() throws RecognitionException {
        try {
            // grammar/NodeToIElementTypeMapGen.g:51:13: ( ':' NODE_NAME )
            // grammar/NodeToIElementTypeMapGen.g:51:15: ':' NODE_NAME
            {
            match(input,EXTENDS,FOLLOW_EXTENDS_in_extendsNode205); 

            match(input,NODE_NAME,FOLLOW_NODE_NAME_in_extendsNode207); 

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
    // grammar/NodeToIElementTypeMapGen.g:54:1: nodeDescription : d= DESCRIPTION ;
    public final void nodeDescription() throws RecognitionException {
        Token d=null;

        try {
            // grammar/NodeToIElementTypeMapGen.g:54:17: (d= DESCRIPTION )
            // grammar/NodeToIElementTypeMapGen.g:54:19: d= DESCRIPTION
            {
            d=(Token)match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_nodeDescription230); 

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
    // grammar/NodeToIElementTypeMapGen.g:57:1: memberDescription : d= DESCRIPTION ;
    public final void memberDescription() throws RecognitionException {
        Token d=null;

        try {
            // grammar/NodeToIElementTypeMapGen.g:57:19: (d= DESCRIPTION )
            // grammar/NodeToIElementTypeMapGen.g:57:21: d= DESCRIPTION
            {
            d=(Token)match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_memberDescription260); 

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
    // grammar/NodeToIElementTypeMapGen.g:60:1: subnode : (n= NODE_NAME ( '?' )? f= FIELD_NAME |n= NODE_NAME ( '?' )? |mn= NODE_NAME '*' |mn= NODE_NAME '*' f= FIELD_NAME );
    public final void subnode() throws RecognitionException {
        Token n=null;
        Token f=null;
        Token mn=null;

        try {
            // grammar/NodeToIElementTypeMapGen.g:60:9: (n= NODE_NAME ( '?' )? f= FIELD_NAME |n= NODE_NAME ( '?' )? |mn= NODE_NAME '*' |mn= NODE_NAME '*' f= FIELD_NAME )
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
                    // grammar/NodeToIElementTypeMapGen.g:61:11: n= NODE_NAME ( '?' )? f= FIELD_NAME
                    {
                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode301); 

                    // grammar/NodeToIElementTypeMapGen.g:61:23: ( '?' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==OPTIONAL) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // grammar/NodeToIElementTypeMapGen.g:61:23: '?'
                            {
                            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subnode303); 

                            }
                            break;

                    }


                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_subnode308); 

                    }
                    break;
                case 2 :
                    // grammar/NodeToIElementTypeMapGen.g:62:11: n= NODE_NAME ( '?' )?
                    {
                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode322); 

                    // grammar/NodeToIElementTypeMapGen.g:62:23: ( '?' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==OPTIONAL) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // grammar/NodeToIElementTypeMapGen.g:62:23: '?'
                            {
                            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subnode324); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // grammar/NodeToIElementTypeMapGen.g:63:11: mn= NODE_NAME '*'
                    {
                    mn=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode339); 

                    match(input,16,FOLLOW_16_in_subnode341); 

                    }
                    break;
                case 4 :
                    // grammar/NodeToIElementTypeMapGen.g:64:11: mn= NODE_NAME '*' f= FIELD_NAME
                    {
                    mn=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode355); 

                    match(input,16,FOLLOW_16_in_subnode357); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_subnode361); 

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
    // grammar/NodeToIElementTypeMapGen.g:67:1: field : (t= TYPE_NAME f= FIELD_NAME ';' | 'boolean' f= FIELD_NAME ';' | 'string' f= FIELD_NAME ';' |l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';' | 'abstract' t= TYPE_NAME f= FIELD_NAME ';' );
    public final void field() throws RecognitionException {
        Token t=null;
        Token f=null;
        Token l=null;

        try {
            // grammar/NodeToIElementTypeMapGen.g:67:7: (t= TYPE_NAME f= FIELD_NAME ';' | 'boolean' f= FIELD_NAME ';' | 'string' f= FIELD_NAME ';' |l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';' | 'abstract' t= TYPE_NAME f= FIELD_NAME ';' )
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
                    // grammar/NodeToIElementTypeMapGen.g:67:9: t= TYPE_NAME f= FIELD_NAME ';'
                    {
                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field380); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field384); 

                    match(input,SEMI,FOLLOW_SEMI_in_field394); 

                    }
                    break;
                case 2 :
                    // grammar/NodeToIElementTypeMapGen.g:69:9: 'boolean' f= FIELD_NAME ';'
                    {
                    match(input,20,FOLLOW_20_in_field404); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field408); 

                    match(input,SEMI,FOLLOW_SEMI_in_field418); 

                    }
                    break;
                case 3 :
                    // grammar/NodeToIElementTypeMapGen.g:71:9: 'string' f= FIELD_NAME ';'
                    {
                    match(input,21,FOLLOW_21_in_field428); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field432); 

                    match(input,SEMI,FOLLOW_SEMI_in_field442); 

                    }
                    break;
                case 4 :
                    // grammar/NodeToIElementTypeMapGen.g:73:9: l= TYPE_NAME '<' t= TYPE_NAME '>' f= FIELD_NAME ';'
                    {
                    l=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field454); 

                    match(input,17,FOLLOW_17_in_field456); 

                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field460); 

                    match(input,18,FOLLOW_18_in_field462); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field466); 

                    match(input,SEMI,FOLLOW_SEMI_in_field476); 

                    }
                    break;
                case 5 :
                    // grammar/NodeToIElementTypeMapGen.g:75:9: 'abstract' t= TYPE_NAME f= FIELD_NAME ';'
                    {
                    match(input,19,FOLLOW_19_in_field486); 

                    t=(Token)match(input,TYPE_NAME,FOLLOW_TYPE_NAME_in_field490); 

                    f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field494); 

                    match(input,SEMI,FOLLOW_SEMI_in_field504); 

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
    public static final BitSet FOLLOW_CARAT_in_node106 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_LPAREN_in_node108 = new BitSet(new long[]{0x0000000000080400L});
    public static final BitSet FOLLOW_19_in_node119 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node132 = new BitSet(new long[]{0x0000000000385460L});
    public static final BitSet FOLLOW_extendsNode_in_node150 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_memberDescription_in_node161 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_subnode_in_node164 = new BitSet(new long[]{0x0000000000385420L});
    public static final BitSet FOLLOW_memberDescription_in_node176 = new BitSet(new long[]{0x0000000000384000L});
    public static final BitSet FOLLOW_field_in_node179 = new BitSet(new long[]{0x0000000000385020L});
    public static final BitSet FOLLOW_RPAREN_in_node190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_extendsNode205 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_extendsNode207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESCRIPTION_in_nodeDescription230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESCRIPTION_in_memberDescription260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode301 = new BitSet(new long[]{0x0000000000000880L});
    public static final BitSet FOLLOW_OPTIONAL_in_subnode303 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_subnode308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode322 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_OPTIONAL_in_subnode324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode339 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_subnode341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode355 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_subnode357 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_subnode361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field380 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field384 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_field404 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field408 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_field428 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field432 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field454 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_field456 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field460 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_field462 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field466 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_field486 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_TYPE_NAME_in_field490 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field494 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field504 = new BitSet(new long[]{0x0000000000000002L});

}