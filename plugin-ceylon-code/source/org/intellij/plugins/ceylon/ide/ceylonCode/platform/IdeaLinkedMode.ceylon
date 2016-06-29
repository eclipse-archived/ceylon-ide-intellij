import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    createJavaObjectArray
}

import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.codeInsight.template {
    TemplateManager,
    ExpressionContext,
    Expression,
    TemplateResult=Result,
    TextResult,
    TemplateBuilderImpl,
    TemplateEditingAdapter,
    Template
}
import com.intellij.codeInsight.template.impl {
    TemplateState
}
import com.intellij.openapi.editor {
    Editor,
    EditorFactory
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.completion {
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.platform {
    LinkedMode
}

import java.lang {
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    CustomLookupCellRenderer,
    IdeaProposalsHolder
}

shared class IdeaLinkedMode(IdeaDocument document) extends LinkedMode(document) {

    value model = IdeaLinkedModeModel();

    shared actual void addEditableGroup(Integer[3]+ positions) {
        value ranges = positions.map((el) => TextRange.from(el[0], el[1]));
        model.addEditableRegions(ranges);
    }

    shared actual void addEditableRegion(Integer start, Integer length, Integer exitSeqNumber,
        ProposalsHolder holder) {

        if (is IdeaProposalsHolder holder) {
            model.addEditableRegion(start, length, holder.proposals.sequence());
        }
    }

    // TODO maybe we should take the editor from QuickFixData?
    shared actual void install(Object owner, Integer exitSeqNumber, Integer exitPosition) {
        value editors = EditorFactory.instance.getEditors(document.nativeDocument);
        if (editors.size > 0) {
            model.buildTemplate(editors.get(0));
        }
    }
}

class IdeaLinkedModeModel() {
    value variables = ArrayList<[TextRange, LookupElement[]]>();
    value multiVariables = ArrayList<{TextRange+}>();

    shared void addEditableRegion(Integer start, Integer len, LookupElement[] proposals) {
        variables.add([TextRange.from(start, len), proposals]);
    }

    shared void addEditableRegions({TextRange+} ranges) {
        multiVariables.add(ranges);
    }

    shared void buildTemplate(Editor editor) {
        assert(exists project = editor.project);
        PsiDocumentManager.getInstance(project).commitDocument(editor.document);
        assert(exists file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document));
        value builder = TemplateBuilderImpl(file.firstChild);

        for ([range,elements] in variables) {
            value text = editor.document.getText(range);
            value proposals = object extends Expression() {
                shared actual ObjectArray<LookupElement> calculateLookupItems(ExpressionContext? expressionContext)
                        => createJavaObjectArray(elements);
                shared actual TemplateResult? calculateQuickResult(ExpressionContext? expressionContext) => TextResult(text);
                shared actual TemplateResult? calculateResult(ExpressionContext? expressionContext) => TextResult(text);
            };
            builder.replaceRange(range, proposals);
        }

        variable value i = 0;
        for (ranges in multiVariables) {
            value rangeName = "multi``i``";
            value text = editor.document.getText(ranges.first);

            variable value j = 0;
            for (range in ranges) {
                value expr = object extends Expression() {
                    shared actual ObjectArray<LookupElement>? calculateLookupItems(ExpressionContext? expressionContext)
                            => null;
                    shared actual TemplateResult? calculateQuickResult(ExpressionContext ctx) => null;
                    shared actual TemplateResult? calculateResult(ExpressionContext ctx)
                            => TextResult(text);
                };

                if (j == 0) {
                    builder.replaceElement(file, range, rangeName, expr, true);
                } else {
                    builder.replaceElement(file, range, rangeName, rangeName + "Other", false);
                }

                j++;
            }
            i++;
        }

        editor.caretModel.moveToOffset(0);

        value template = builder.buildInlineTemplate();
        value listener = object extends TemplateEditingAdapter() {
            shared actual void currentVariableChanged(TemplateState? templateState, Template? template, Integer int, Integer int1) {
                CustomLookupCellRenderer.install(project);
            }
        };
        TemplateManager.getInstance(project).startTemplate(editor, template, listener);
    }
}
