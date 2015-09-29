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
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiDocumentManager
}

import java.lang {
    ObjectArray
}
import com.intellij.codeInsight.template.impl {
    TemplateState
}

shared class IdeaLinkedMode(Editor editor) {
    value variables = ArrayList<[TextRange, LookupElement[]]>();
    
    shared void addEditableRegion(Integer start, Integer len, LookupElement[] proposals) {
        variables.add([TextRange.from(start, len), proposals]);
    }
    
    shared void buildTemplate(Editor editor) {
        PsiDocumentManager.getInstance(editor.project).commitDocument(editor.document);
        value file = PsiDocumentManager.getInstance(editor.project).getPsiFile(editor.document);
        value builder = TemplateBuilderImpl(file.firstChild);
        
        for (var in variables) {
            value text = editor.document.getText(var[0]);
            value proposals = object extends Expression() {
                    shared actual ObjectArray<LookupElement> calculateLookupItems(ExpressionContext? expressionContext)
                        => createJavaObjectArray(var[1]);
                    shared actual TemplateResult? calculateQuickResult(ExpressionContext? expressionContext) => TextResult(text);
                    shared actual TemplateResult? calculateResult(ExpressionContext? expressionContext) => TextResult(text);
            };
            builder.replaceRange(var[0], proposals);
        }
        
        editor.caretModel.moveToOffset(0);
        
        value template = builder.buildInlineTemplate();
        value listener = object extends TemplateEditingAdapter() {
            shared actual void currentVariableChanged(TemplateState? templateState, Template? template, Integer int, Integer int1) {
                CustomLookupCellRenderer.install(editor.project);
            }
        };
        TemplateManager.getInstance(editor.project).startTemplate(editor, template, listener);
    }
}
