package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindReferencedNodeVisitor extends Visitor {

	private final Referenceable declaration;
	private Node declarationNode;

	public FindReferencedNodeVisitor(Referenceable declaration) {
		this.declaration = declaration;
	}

	public Node getDeclarationNode() {
		return declarationNode;
	}

    private boolean isDeclaration(Declaration dec) {
        return dec!=null && dec.equals(declaration);
    }
    
    @Override
    public void visit(Tree.ModuleDescriptor that) {
    	super.visit(that);
    	Referenceable m = that.getImportPath().getModel();
    	if (m!=null && m.equals(declaration)) {
    		declarationNode = that;
    	}
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
    	super.visit(that);
    	Referenceable p = that.getImportPath().getModel();
    	if (p!=null && p.equals(declaration)) {
    		declarationNode = that;
    	}
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
        if (isDeclaration(that.getDeclarationModel()
        		.getTypeDeclaration())) {
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
