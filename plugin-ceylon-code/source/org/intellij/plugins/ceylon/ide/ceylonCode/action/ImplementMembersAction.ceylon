import com.intellij.codeInsight.generation {
    ClassMember,
    MemberChooserObjectBase,
    PsiElementMemberChooserObject
}
import com.intellij.codeInsight.generation.actions {
    PresentableCodeInsightActionHandler
}
import com.intellij.ide.util {
    MemberChooser
}
import com.intellij.lang {
    LanguageCodeInsightActionHandler
}
import com.intellij.openapi.actionSystem {
    Presentation
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiFile,
    PsiElement
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.completion {
    overloads,
    getRefinementTextFor,
    completionManager
}
import com.redhat.ceylon.ide.common.correct {
    CommonImportProposals
}
import com.redhat.ceylon.ide.common.platform {
    InsertEdit,
    platformServices
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Interface,
    Declaration
}

import java.lang {
    ObjectArray
}
import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsi,
    descriptions
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    CeylonReference
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

shared class CeylonOverrideMembersAction() extends AbstractMembersAction() {
    proposable(Declaration member) => member.formal || member.default;
    title => "Select Inherited Members to Refine";
    menuLabel => "Refine Inherited Members";
    selectAll => false;
}

shared class CeylonImplementMembersAction() extends AbstractMembersAction() {
    proposable(Declaration member) => member.formal;
    title => "Select Inherited Formal Members to Implement";
    menuLabel => "Implement Formal Members";
    selectAll => true;
}

shared abstract class AbstractMembersAction()
        satisfies LanguageCodeInsightActionHandler
                & PresentableCodeInsightActionHandler {

    shared formal String menuLabel;

    shared formal Boolean proposable(Declaration member);

    shared formal String title;
    
    shared formal Boolean selectAll;

    update(Editor editor, PsiFile psiFile, Presentation presentation)
            => presentation.setText(menuLabel, false);

    void apply(CeylonFile file, Editor editor, Integer offset, Node node, 
            {Declaration*} selected, ClassOrInterface ci) {
        value rootNode = file.compilationUnit;
        value doc = IdeaDocument(editor.document);
        value bodyIndent = doc.getIndent(node);
        value delim = doc.defaultLineDelimiter;
        value indent = delim + bodyIndent + platformServices.document.defaultIndent;
        value importProposals = CommonImportProposals(doc, rootNode);
        value result = StringBuilder();
        for (d in selected) {
            value rtext = getRefinementTextFor { 
                d = d; 
                pr = completionManager.getRefinedProducedReference(ci, d); 
                unit = rootNode.unit; 
                isInterface = ci is Interface; 
                ci = ci; 
                indent = indent; 
                containsNewline = true; 
                preamble = true; 
                addParameterTypesInCompletions = true;
            };
            result.append(indent).append(rtext).append(indent);
            importProposals.importSignatureTypes(d);
        }
        value change = IdeaTextChange(doc);
        importProposals.apply(change);
        change.addEdit(InsertEdit(offset, result.string));
        change.apply();
    }
    
    alias TypeDecPsi 
            => CeylonPsi.ClassOrInterfacePsi
             | CeylonPsi.ObjectDefinitionPsi
             | CeylonPsi.ObjectExpressionPsi
             | CeylonPsi.ObjectArgumentPsi;
    
    shared actual void invoke(Project project, Editor editor, PsiFile file) {

        //TODO: make this approach work:
        // object chooser extends MemberChooser<ClassMember>
        //      (elements, false, true, project, null, null) {
        //  isContainerNode(MemberChooserObject key) => key is Parent;
        //}
        
        class Parent(ClassOrInterface container)
                extends PsiElementMemberChooserObject(
            //TODO: I hate resoving and passing this in here, but
            //      due to a packaging issue I could not refine
            //      MemberChooser.isContainerNode
            CeylonReference.resolveDeclaration(container, project),
            descriptions.descriptionForDeclaration {
                decl = container;
                includeContainer = false;
                includeKeyword = false;
            },
            icons.forDeclaration(container)) {
            hash => container.hash;
            equals(Object that)
                    => if (is Parent that)
            then container==that.container
            else false;
        }

        class Member(declaration, container)
                extends MemberChooserObjectBase(
            descriptions.descriptionForDeclaration {
                decl = declaration;
                includeContainer = false;
                includeKeyword = false;
            },
            icons.forDeclaration(declaration))
                satisfies ClassMember {

            ClassOrInterface container;
            shared Declaration declaration;

            parentNodeDelegate = Parent(container);
            hash => declaration.hash;
            equals(Object that)
                    => if (is Member that)
            then declaration ==that.declaration
            else false;
        }
        
        value offset = editor.selectionModel.selectionStart;

        if (is CeylonFile file,
            exists psi = file.findElementAt(offset)) {
            variable PsiElement? element = psi;
            while (exists e = element, !e is TypeDecPsi) {
                element = e.parent;
            }
            
            value node = element;
            if (!is TypeDecPsi node) {
                return;
            }
            
            ClassOrInterface? ci = 
                if (is CeylonPsi.ObjectDefinitionPsi node) then node.ceylonNode.anonymousClass
                else if (is CeylonPsi.ObjectExpressionPsi node) then node.ceylonNode.anonymousClass
                else if (is CeylonPsi.ObjectArgumentPsi node) then node.ceylonNode.anonymousClass
                else node.ceylonNode.declarationModel;
            if (!exists ci) {
                return;
            }

            value proposals = ci.getMatchingMemberDeclarations(ci.unit, ci, "", 0, null).values();
            value list = ArrayList<ClassMember>();
            for (dwp in proposals) {
                for (member in overloads(dwp.declaration)) {
                    if (proposable(member) && ci.isInheritedFromSupertype(member)) {
                        assert (is ClassOrInterface container = member.container);
                        list.add(Member(member, container));
                    }
                }
            }
            
            value none = ObjectArray<ClassMember>(0);
            value elements = list.toArray(none);
            value chooser = MemberChooser(elements, false, true, project, null, null);
            chooser.title = title;
            chooser.setCopyJavadocVisible(false);
            chooser.selectElements(selectAll then elements else none);
            chooser.show();
            if (exists selected = chooser.selectedElements, 
                    !selected.empty) {
                value p = project;
                object extends WriteCommandAction<Nothing>
                        (p, "Refine Members", file) {
                    run(Result<Nothing> result) => apply { 
                        file = file; 
                        editor = editor; 
                        node = node.ceylonNode;
                        ci = ci; 
                        offset = offset;
                        for (cm in selected) 
                            if (is Member cm) 
                                cm.declaration
                    };
                }.execute();
            }
        }

    }

    isValidFor(Editor editor, PsiFile psiFile) => psiFile is CeylonFile;

    startInWriteAction() => true;

}
