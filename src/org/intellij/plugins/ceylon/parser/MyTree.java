package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;

import java.util.ArrayList;
import java.util.List;

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

    public Node getRootSpecNode() {
        return root.node;
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
            this.type = type;
            this.node = node;
            psiMarker.done(type);
            parent.addChild(this);
            newNodeParent = this.parent;
        }

        @Override
        public void drop() {
            psiMarker.drop();
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
            if ((myChildren.size() != ideaChildren.length)) {
                System.out.printf("error in %s[%s] at %s: %d != %d%n", node.getNodeType(), node.getMainToken(), node.getLocation(), myChildren.size(), ideaChildren.length);
            } else
            for (int i = 0; i < myChildren.size(); i++) {
                MyNode myChildNode = myChildren.get(i);
                ASTNode ideaChildNode = ideaChildren[i];
                myChildNode.bindSubtree(ideaChildNode);
            }
        }

        /**
         * Just binds this to the single nodes, doesn't bind the subtrees.
         */
        void bindNode(ASTNode ideaNode) {
            assert getType().equals(ideaNode.getElementType()) : getType() + " != " + ideaNode.getElementType();
            ideaNode.putUserData(CeylonIdeaParser.CEYLON_NODE_KEY, node);
        }
    }

    public static interface MyMarker {
        void done(IElementType type, Node node);

        void drop();
    }
}
