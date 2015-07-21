package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.psi.stub.CeylonStubTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyTree {
    private MyNode root = null;
    private MyNode newNodeParent = new MyNode(null, null); // create a dummy "root parent" just to prevent NPE when adding children to parent

    public MyTree() {
    }

    public MyMarker mark(PsiBuilder.Marker psiMarker) {
        final MyNode myMarker = new MyNode(psiMarker, newNodeParent);
        newNodeParent = myMarker;
        if (root == null) {
            root = myMarker;
        }
        return myMarker;
    }

    public void bindToRoot(ASTNode astRoot) {
        root.bindSubtree(astRoot);
    }

    public Tree.CompilationUnit getCompilationUnit() {
        return root.node instanceof Tree.CompilationUnit ? (Tree.CompilationUnit) root.node : null;
    }

    public void error() {
        final MyNode myNode = new MyNode(null, newNodeParent);
        myNode.type = TokenType.ERROR_ELEMENT;
        newNodeParent.addChild(myNode);
    }

    private class MyNode implements MyMarker {
        private PsiBuilder.Marker psiMarker;
        private IElementType type;
        private Node node;
        private MyNode parent;
        private List<MyNode> children = new ArrayList<>();

        MyNode(PsiBuilder.Marker psiMarker, MyNode parent) {
            this.psiMarker = psiMarker;
            this.parent = parent;
        }

        @Override
        public void done(IElementType type, Node node) {
            // flatten the tree somewhat by collapsing single-child chains into a single node
            if (children.size() == 1 && children.get(0).node == node && children.get(0).type == type && type != CeylonStubTypes.CEYLON_FILE) {
                psiMarker.drop();
                final MyNode myOnlyChild = children.get(0);
                myOnlyChild.parent = this.parent;
//                myOnlyChild.type = type; // perhaps this makes sense?
                parent.addChild(myOnlyChild);
                newNodeParent = myOnlyChild.parent;
            } else {
                this.type = type;
                this.node = node;
                psiMarker.done(type);
                parent.addChild(this);
                newNodeParent = this.parent;
            }
        }

        @Override
        public void drop() {
            psiMarker.drop();
            newNodeParent = this.parent;
        }

        @Override
        public void error(String message) {
            psiMarker.error(message);
            this.type = TokenType.ERROR_ELEMENT;
            parent.addChild(this);
            newNodeParent = this.parent;
        }

        void addChild(MyNode child) {
            children.add(child);
        }

        IElementType getType() {
            return type;
        }

        List<MyNode> getChildren() {
            return children;
        }

        @Override
        public String toString() {
            return String.format("MyMarker{type=%s, children=%d, hash=%d}", type, children.size(), hashCode());
        }

        void bindSubtree(ASTNode ideaNode) {
            bindNode(ideaNode);
            List<MyNode> myChildren = getChildren();
            ASTNode[] ideaChildren = ideaNode.getChildren(CeylonIdeaParser.COMPOSITE_ELEMENTS);
            if (myChildren.size() == ideaChildren.length) {
                for (int i = 0; i < myChildren.size(); i++) {
                    MyNode myChildNode = myChildren.get(i);
                    ASTNode ideaChildNode = ideaChildren[i];
                    myChildNode.bindSubtree(ideaChildNode);
                }
            } // else Just ignore the subtree; this happens sometimes when Idea inserts its IntellijIdeaRulezz token
        }

        /**
         * Just binds this to the single nodes, doesn't bind the subtrees.
         */
        void bindNode(ASTNode ideaNode) {
            assert Objects.equals(getType(), ideaNode.getElementType()) : getType() + " != " + ideaNode.getElementType();
            ideaNode.putUserData(CeylonIdeaParser.CEYLON_NODE_KEY, node);
        }
    }

    public static interface MyMarker {
        void done(IElementType type, Node node);

        void drop();

        void error(String message);
    }
}
