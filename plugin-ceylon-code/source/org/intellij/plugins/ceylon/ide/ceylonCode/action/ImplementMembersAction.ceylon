import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.generation {
    ClassMember,
    MemberChooserObjectBase
}
import com.intellij.ide.util {
    MemberChooser
}
import com.intellij.lang {
    LanguageCodeInsightActionHandler
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
    PsiFile
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
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
    ArrayList,
    List
}

import javax.swing {
    JComponent
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsi,
    ceylonDeclarationDescriptionProvider
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared class OverrideMembersAction() extends ImplementMembersAction() {
    proposable(Declaration member) => member.formal || member.default;
}

shared class ImplementMembersAction()
        satisfies LanguageCodeInsightActionHandler {
    
    void apply(CeylonFile file, Editor editor, Tree.Declaration node,
            List<ClassMember> selected, ClassOrInterface ci, Integer offset) {
        value rootNode = file.compilationUnit;
        value doc = IdeaDocument(editor.document);
        value bodyIndent = doc.getIndent(node);
        value delim = doc.defaultLineDelimiter;
        value indent = delim + bodyIndent + platformServices.document.defaultIndent;
        value importProposals = CommonImportProposals(doc, rootNode);
        value result = StringBuilder();
        for (element in selected) {
            assert (is Member element);
            value d = element.declaration;
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

    shared default Boolean proposable(Declaration member)
            => member.formal;

    shared actual void invoke(Project project, Editor editor, PsiFile file) {
        value offset = editor.selectionModel.selectionStart;

        if (is CeylonFile file,
            exists typeDec
                //TODO: fix this:
                = PsiTreeUtil.findElementOfClassAtOffset(file, offset,
                    javaClass<CeylonPsi.ObjectDefinitionPsi>(), false)
                else PsiTreeUtil.findElementOfClassAtOffset(file, offset,
                        javaClass<CeylonPsi.ClassOrInterfacePsi>(), false)) {

            value node =
                if (is CeylonPsi.ClassOrInterfacePsi typeDec)
                then typeDec.ceylonNode
                else typeDec.ceylonNode;
            value ci = switch (node)
                case (is Tree.ClassOrInterface) node.declarationModel
                case (is Tree.ObjectDefinition) node.anonymousClass;
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

            value chooser
                = MemberChooser(list.toArray(ObjectArray<ClassMember>(0)),
                    true, true, project, null, ObjectArray<JComponent>(0));
            chooser.show();
            if (exists selected = chooser.selectedElements, selected.size()>0) {
                value p = project;
                object extends WriteCommandAction<Nothing>(p, "Implement members") {
                    run(Result<Nothing> result)
                            => apply(file, editor, node, selected, ci, offset);
                }.execute();
            }
        }

    }

    isValidFor(Editor editor, PsiFile psiFile) => psiFile is CeylonFile;

    startInWriteAction() => true;

    class Parent(ClassOrInterface container)
            extends MemberChooserObjectBase(
                ceylonDeclarationDescriptionProvider.descriptionForDeclaration {
                    decl = container;
                    includeContainer = false;
                },
                ideaIcons.forDeclaration(container)) {
        hash => container.hash;
        equals(Object that)
                => if (is Parent that)
        then container==that.container
        else false;
    }
    
    class Member(shared Declaration declaration, ClassOrInterface container)
            extends MemberChooserObjectBase(
                ceylonDeclarationDescriptionProvider.descriptionForDeclaration {
                    decl = declaration;
                    includeContainer = false;
                },
                ideaIcons.forDeclaration(declaration))
            satisfies ClassMember {
        parentNodeDelegate = Parent(container);
        hash => declaration.hash;
        equals(Object that)
                => if (is Member that)
                then declaration ==that.declaration
                else false;
    }
}
