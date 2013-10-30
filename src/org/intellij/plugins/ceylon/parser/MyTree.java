package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTree {
    private MyNode root = null;
    private MyNode newNodeParent = new MyNode(null, null); // create a dummy "root parent" just to prevent NPE when adding children to parent
    private Map<ASTNode, MyNode> ideaToMyMap = new HashMap<>();

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

    private boolean containsKey(ASTNode key) {
        return ideaToMyMap.containsKey(key);
    }

    public MyNode getMyNode(ASTNode ideaNode) {
        return ideaToMyMap.get(ideaNode);
    }

    public void bindToRoot(ASTNode astRoot) {
        root.bind(astRoot);
    }

    private void bindToMapped(ASTNode astNode) {
        getMyNode(astNode).bind(astNode);
    }

    public void rebindFirstBoundParent(ASTNode node) {
        ASTNode ancestor = node;
        while (ancestor != null && !containsKey(ancestor)) {
            ancestor = ancestor.getTreeParent();
        }
        if (ancestor == null) {
            throw new IllegalArgumentException("No ancestor of node has been bound: " + node);
        }
        bindToMapped(ancestor);
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

        public Node getNode() {
            return node;
        }

        @Override
        public String toString() {
            return String.format("MyMarker{type=%s, children=%d, hash=%d}", type, children.size(), hashCode());
        }

        void bind(ASTNode ideaNode) {
            ideaToMyMap.put(ideaNode, this);
            assert getType().equals(ideaNode.getElementType()) : getType() + " != " + ideaNode.getElementType();
            final Node specNode = getNode();
            ideaNode.putUserData(CeylonIdeaParser.CEYLON_NODE_KEY, specNode);
            List<MyNode> myChildren = getChildren();
            ASTNode[] ideaChildren = ideaNode.getChildren(CeylonIdeaParser.COMPOSITE_ELEMENTS);
            assert (myChildren.size() == ideaChildren.length) :
                    String.format("error in %s[%s] at %s: %d != %d%n", specNode.getNodeType(), specNode.getMainToken(), specNode.getLocation(), myChildren.size(), ideaChildren.length);
            for (int i = 0; i < myChildren.size(); i++) {
                MyNode myChildNode = myChildren.get(i);
                ASTNode ideaChildNode = ideaChildren[i];
                myChildNode.bind(ideaChildNode);
            }
        }
    }

    public static interface MyMarker {
        void done(IElementType type, Node node);

        void drop();
    }
}
