package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import org.antlr.runtime.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This class avoids calling a custom parser. Instead, we transform the Ceylon AST generated by
 * the official parser into an ASTNode tree. Comments and whitespaces are not present in the Ceylon AST,
 * but we can retrieve them by synchronizing our transformation with a lexer.
 */
public class IdeaCeylonParser extends IStubFileElementType {

    private static final List<Integer> NODES_ALLOWED_AT_EOF = Arrays.asList(CeylonLexer.EOF,
            CeylonLexer.WS, CeylonLexer.LINE_COMMENT, CeylonLexer.MULTI_COMMENT);

    // Leaves which will be wrapped in a CeylonCompositeElement, for example to allow refactoring them
    private static final List<IElementType> LEAVES_TO_WRAP = Arrays.asList(CeylonTypes.IDENTIFIER,
            CeylonTypes.NATURAL_LITERAL, CeylonTypes.FUNCTION_LITERAL);

    public static final Key<Node> CEYLON_NODE_KEY = new Key<>("CEYLON-SPEC_NODE");
    public static final Key<Tree.CompilationUnit> FORCED_CU_KEY = new Key<>("CEYLON-FORCED_CU");

    public IdeaCeylonParser(Language language) {
        super(language);
    }

    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
//        if (psi instanceof CeylonFile) {
//            return super.doParseContents(chameleon, psi);
//        }

        CeylonFile file = (CeylonFile) psi;
        final Queue<Token> tokens = new LinkedList<>();
        boolean verbose = false;

        CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(file.getText())) {
            @Override
            public Token nextToken() {
                Token token = super.nextToken();

                if (token != null) {
                    tokens.add(token);
                }

                return token;
            }
        };

        CommonTokenStream stream = new CommonTokenStream(lexer);
        stream.fill();

        CeylonParser parser = new CeylonParser(stream);

        try {
            AstVisitor visitor = new AstVisitor(file, tokens, verbose);

            Tree.CompilationUnit cu = parser.compilationUnit();
            Tree.CompilationUnit forcedCu = file.getUserData(FORCED_CU_KEY);
            if (forcedCu != null) {
                cu = forcedCu;
            }

            visitor.visit(cu);

            ASTNode root = visitor.parent;
            if (verbose) {
                dump(root, "");
            }

            if (file.getOriginalFile() instanceof CeylonFile) {
                CeylonFile orig = (CeylonFile) file.getOriginalFile();
                orig.setRootNode(cu);
                orig.setTokens(stream.getTokens());
            }

            for (ParseError error : parser.getErrors()) {
                cu.addParseError(error);
            }
            for (LexError error : lexer.getErrors()) {
                cu.addLexError(error);
            }

            return root;
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
        return null;
    }

    // For debugging purposes only
    private void dump(ASTNode root, String indent) {
        System.out.println(indent + root.getElementType() + (root instanceof LeafPsiElement ? " (" + root.getText() + ")" : ""));
        for (ASTNode child : root.getChildren(null)) {
            dump(child, indent + "  ");
        }
    }

    private class AstVisitor extends Visitor {
        private CompositeElement parent;
        private CeylonFile file;
        private Queue<Token> tokens;
        private boolean verbose;
        private int index = 0;

        public AstVisitor(CeylonFile file, Queue<Token> tokens, boolean verbose) {
            this.file = file;
            this.tokens = tokens;
            this.verbose = verbose;
        }

        @Override
        public void visit(Tree.CompilationUnit that) {
            super.visit(that);

            while (!tokens.isEmpty()) {
                Token token = tokens.remove();

                if (token.getType() != CeylonLexer.EOF) {
                    parent.rawAddChildrenWithoutNotifications(buildLeaf(null, TokenTypes.fromInt(token.getType()), token));
                }

                if (verbose && !NODES_ALLOWED_AT_EOF.contains(token.getType())) {
                    Logger.getInstance(IdeaCeylonParser.class).error("Unexpected token " + token + " in " + file.getName());
                }
            }
        }

        @Override
        public void visitAny(Node that) {
            IElementType type = NodeToIElementTypeMap.get(that);
            boolean parentForced = false;

            if (parent == null) {
                parentForced = true;
                parent = new CompositeElement(type);
            }

            if (that instanceof Tree.DocLink) {
                return;
            }
            index = consumeTokens(that, index, true);

            Token token = that.getMainToken();

            OrderedChildrenVisitor visitor = new OrderedChildrenVisitor();
            that.visitChildren(visitor);

            if (that.getToken() != null && visitor.children.isEmpty()) {
                Token peek = tokens.peek();
                if (getTokenLength(peek) == that.getEndIndex() - that.getStartIndex()) {
                    Token toRemove = tokens.remove();
                    parent.rawAddChildrenWithoutNotifications(buildLeaf(that, type, toRemove));
                    if (verbose) {
                        System.out.println("t \"" + toRemove.getText() + "\"");
                    }
                    index += getTokenLength(toRemove);
                } else {
                    CompositeElement comp = new CompositeElement(type);

                    while (index < that.getEndIndex()) {
                        Token toRemove = tokens.remove();
                        comp.rawAddChildrenWithoutNotifications(buildLeaf(null, TokenTypes.fromInt(token.getType()), toRemove));
                        if (verbose) {
                            System.out.println("t \"" + toRemove.getText() + "\"");
                        }
                        index += getTokenLength(toRemove);
                    }

                    parent.rawAddChildrenWithoutNotifications(comp);
                }

                // TODO should be == but sometimes the tree includes a node that was already included before
                // (see `exists` constructs for example)
                assert index >= that.getEndIndex();
            } else {
                CompositeElement oldParent = parent;
                if (!parentForced) {
                    parent = new CompositeElement(type);
                    oldParent.rawAddChildrenWithoutNotifications(parent);
                }
                parent.putUserData(CEYLON_NODE_KEY, that);

                for (Node child : visitor.getChildren()) {
                    visitAny(child);
                }

                index = consumeTokens(that, index, false);

                parent = oldParent;
            }
        }

        @NotNull
        private TreeElement buildLeaf(Node ceylonNode, IElementType type, Token token) {
            String tokenText = token.getText();
            if (tokenText.length() != getTokenLength(token)) {
                switch (token.getType()) {
                    case CeylonLexer.PIDENTIFIER:
                    case CeylonLexer.AIDENTIFIER:
                    case CeylonLexer.LIDENTIFIER:
                        tokenText = "\\i" + tokenText;
                        break;
                    case CeylonLexer.UIDENTIFIER:
                        tokenText = "\\I" + tokenText;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported token type " + token);
                }
            }
            if (LEAVES_TO_WRAP.contains(type)) {
                CompositeElement comp = new CompositeElement(type);
                LeafPsiElement leaf = new LeafPsiElement(TokenTypes.fromInt(token.getType()), tokenText);
                comp.rawAddChildrenWithoutNotifications(leaf);
                comp.putUserData(CEYLON_NODE_KEY, ceylonNode);
                return comp;
            } else {
                return new LeafPsiElement(TokenTypes.fromInt(token.getType()), tokenText);
            }
        }

        private int consumeTokens(Node that, int index, boolean before) {
            Integer targetIndex = before ? that.getStartIndex() : that.getEndIndex();

            if (targetIndex == null) {
                return index;
            }

            if (index > targetIndex) {
                if (verbose) {
                    System.out.println(String.format("WARN : index (%d) > targetIndex (%d)", index, targetIndex));
                }
                return index;
            }

            while (index < targetIndex) {
                Token token = tokens.remove();
                parent.rawAddChildrenWithoutNotifications(buildLeaf(null, TokenTypes.fromInt(token.getType()), token));
                index += getTokenLength(token);
                if (verbose) {
                    System.out.println("c \"" + token.getText() + "\"");
                }
            }

            assert index == targetIndex;

            return index;
        }

        private int getTokenLength(Token token) {
            if (token instanceof CommonToken) {
                CommonToken commonToken = (CommonToken) token;
                return commonToken.getStopIndex() - commonToken.getStartIndex() + 1;
            } else {
                return token.getText().length();
            }
        }
    }

    private class OrderedChildrenVisitor extends Visitor {
        List<Node> children = new ArrayList<>();

        @Override
        public void visitAny(Node that) {
            children.add(that);
        }

        public List<Node> getChildren() {
            Collections.sort(children, new Comparator<Node>() {
                @Override
                public int compare(@NotNull Node o1, @NotNull Node o2) {
                    Integer idx1 = o1.getStartIndex();
                    Integer idx2 = o2.getStartIndex();

                    if (idx1 == null) {
                        return idx2 == null ? 0 : 1;
                    }
                    if (idx2 == null) {
                        return -1;
                    }

                    return idx1.compareTo(idx2);
                }
            });

            return children;
        }
    }
}