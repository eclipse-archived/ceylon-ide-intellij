import com.redhat.ceylon.ide.common.correct {
    addAnnotationQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

shared abstract class AddAnnotationIntention() extends AbstractIntention() {

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        addContextualAnnotationProposals(data, offset);
    }

    shared formal void addAnnotation(IdeaQuickFixData data, Tree.Declaration decNode, Declaration d);

    shared void addContextualAnnotationProposals(IdeaQuickFixData data, Integer offset) {

        if (exists decNode = nodes.findDeclaration(data.rootNode, data.node),
            exists idNode = nodes.getIdentifyingNode(decNode),
            exists start = idNode.startIndex) {
            value doc = data.document;
            if (doc.getLineOfOffset(start.intValue())
                != doc.getLineOfOffset(offset)) {

                return;
            }

            if (exists d = decNode.declarationModel) {
                addAnnotation(data, decNode, d);
            }
        }
    }
}

shared class AddVariableAnnotationIntention() extends AddAnnotationIntention() {

    shared actual void addAnnotation(IdeaQuickFixData data, Tree.Declaration decNode, Declaration d) {
        if (is Tree.AttributeDeclaration decNode) {
            addAnnotationQuickFix.addMakeVariableDeclarationProposal(data, decNode);
        }
    }

    familyName => "Add variable annotation";
}

shared class AddSharedAnnotationIntention() extends AddAnnotationIntention() {

    shared actual void addAnnotation(IdeaQuickFixData data, Tree.Declaration decNode, Declaration d) {
        if (d.classOrInterfaceMember || d.toplevel, !d.shared) {
            addAnnotationQuickFix.addMakeSharedDecProposal(decNode, data);
        }
    }

    familyName => "Add shared annotation";
}

shared class AddDefaultAnnotationIntention() extends AddAnnotationIntention() {

    shared actual void addAnnotation(IdeaQuickFixData data, Tree.Declaration decNode, Declaration d) {
        if (d.classOrInterfaceMember, !d.default, !d.formal,
            is Tree.AnyClass|Tree.AnyAttribute|Tree.AnyMethod decNode) {

            addAnnotationQuickFix.addMakeDefaultDecProposal(decNode, data);
        }
    }

    familyName => "Add default annotation";
}

shared class AddFormalAnnotationIntention() extends AddAnnotationIntention() {

    shared actual void addAnnotation(IdeaQuickFixData data, Tree.Declaration decNode, Declaration d) {
        if (d.classOrInterfaceMember, !d.default, !d.formal,
            decNode is Tree.ClassDefinition) {
                addAnnotationQuickFix.addMakeFormalDecProposal(decNode, data);
        }
    }

    familyName => "Add formal annotation";
}
