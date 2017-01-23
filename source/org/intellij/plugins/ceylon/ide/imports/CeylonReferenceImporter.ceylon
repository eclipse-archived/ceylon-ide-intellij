/*
import ceylon.collection {
    ArrayList
}

import com.intellij.codeInsight.daemon {
    ReferenceImporter
}
import com.intellij.codeInsight.daemon.impl {
    CollectHighlightsUtil
}
import com.intellij.openapi.editor {
    Editor,
    Document
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    importProposals,
    QuickFixData,
    QuickFixKind
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    TextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

import org.intellij.plugins.ceylon.ide.correct {
    Resolution,
    showPopup
}
import org.intellij.plugins.ceylon.ide.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi,
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}
shared class CeylonReferenceImporter() satisfies ReferenceImporter {

    void importRef(ArrayList<Resolution> proposals, Document doc,
            CeylonPsi.BaseMemberOrTypeExpressionPsi|CeylonPsi.BaseTypePsi element,
            PsiFile file, Editor editor) {
        object data satisfies QuickFixData {

            shared actual void addQuickFix(String description,
                TextChange|Anything() change,
                DefaultRegion? selection,
                Boolean qualifiedNameIsPath,
                Icons? icon,
                QuickFixKind kind,
                String? hint,
                Boolean asynchronous)
                    => proposals.add(Resolution {
                        description = description;
                        icon = icons.imports;
                        change = change;
                        qualifiedNameIsPath = qualifiedNameIsPath;
                    });

            document => IdeaDocument(doc);

            node => element.ceylonNode;

            errorCode => nothing;
            problemLength => nothing;
            problemOffset => nothing;

            shared actual PhasedUnit phasedUnit {
                assert (is CeylonFile unit = element.containingFile,
                    exists phasedUnit = unit.upToDatePhasedUnit);
                return phasedUnit;
            }

            shared actual Tree.CompilationUnit rootNode {
                assert (is CeylonFile unit = element.containingFile,
                    exists rootNode = unit.upToDatePhasedUnit?.compilationUnit);
                return rootNode;
            }

            shared actual BaseCeylonProject ceylonProject {
                assert (is BaseCeylonProject project = file.project);
                return project;
            }

            shared actual DefaultRegion editorSelection
                    => DefaultRegion {
                start = editor.selectionModel.selectionStart;
                length = editor.selectionModel.selectionEnd
                - editor.selectionModel.selectionStart;
            };

            shared actual void addAssignToLocalProposal(String description) {
                assert (false);
            }

            shared actual void addConvertToClassProposal(String description,
                    Tree.ObjectDefinition declaration) {
                assert (false);
            }

        }
        importProposals.addImportProposals(data);
    }

    shared actual Boolean autoImportReferenceAt(Editor editor, PsiFile file, Integer offset) {
        if (!CeylonLanguage.instance in file.viewProvider.languages) {
            return false;
        } else {
            value element = file.findReferenceAt(offset);
            if (is CeylonPsi.BaseMemberOrTypeExpressionPsi|CeylonPsi.BaseTypePsi element) {
                value proposals = ArrayList<Resolution>();
                importRef(proposals, editor.document, element, file, editor);
                if (!proposals.empty) {
                    showPopup(editor, proposals, "Select a Resolution", file.project);
                    return true;
                }
            }
        }
        return false;
    }

    shared actual Boolean autoImportReferenceAtCursor(Editor editor, PsiFile file) {
        if (!CeylonLanguage.instance in file.viewProvider.languages) {
            return false;
        } else {
            Integer caretOffset = editor.caretModel.offset;
            value doc = editor.document;
            Integer lineNumber = doc.getLineNumber(caretOffset);
            Integer startOffset = doc.getLineStartOffset(lineNumber);
            Integer endOffset = doc.getLineEndOffset(lineNumber);
            value elements = CollectHighlightsUtil.getElementsInRange(file, startOffset, endOffset);
            value proposals = ArrayList<Resolution>();
            for (element in elements) {
                if (is CeylonPsi.BaseMemberOrTypeExpressionPsi|CeylonPsi.BaseTypePsi element) {
                    importRef(proposals, doc, element, file, editor);
                }
            }
            if (!proposals.empty) {
                showPopup(editor, proposals, "Select a Resolution", file.project);
                return true;
            }
        }
        return false;
    }
}*/
