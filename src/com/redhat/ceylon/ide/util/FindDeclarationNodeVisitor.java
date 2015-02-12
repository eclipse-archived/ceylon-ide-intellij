package com.redhat.ceylon.ide.util;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindDeclarationNodeVisitor extends Visitor {

    private final Declaration declaration;
    protected Tree.Declaration declarationNode;

    public FindDeclarationNodeVisitor(Declaration declaration) {
        this.declaration = declaration;
    }

    public Tree.Declaration getDeclarationNode() {
        return declarationNode;
    }

    private boolean isDeclaration(Declaration dec) {
        return dec!=null && dec.equals(declaration);
    }

    @Override
    public void visit(Tree.Declaration that) {
        if (isDeclaration(that.getDeclarationModel())) {
            declarationNode = that;
        }
        super.visit(that);
    }

    @Override
    public void visit(Tree.ObjectDefinition that) {
        if (isDeclaration(that.getDeclarationModel().getTypeDeclaration())) {
            declarationNode = that;
        }
        super.visit(that);
    }

    public void visitAny(Node node) {
        if (declarationNode==null) {
            super.visitAny(node);
        }
    }

}
