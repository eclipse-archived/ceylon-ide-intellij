import java.lang {
    ObjectArray
}
import ceylon.interop.java {
    createJavaObjectArray
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.codeInsight.template {
    TemplateManager,
    ExpressionContext,
    Expression,
    Template,
    TemplateResult=Result,
    TextResult
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.codeInsight.template.impl {
    TemplateImpl
}
import ceylon.collection {
    ArrayList
}
//import com.intellij.openapi.editor.markup {
//    HighlighterLayer,
//    HighlighterTargetArea
//}
//import com.intellij.openapi.editor.colors {
//    EditorColors,
//    EditorColorsManager
//}


shared class IdeaLinkedMode(String text, Integer startInDoc) {
    value variables = ArrayList<[String, LookupElement[]]>();
    value templateText = StringBuilder();
    variable Integer startOffset = startInDoc;
    
    shared void addEditableRegion(Integer start, Integer len, LookupElement[] proposals) {
        if (start > startOffset) {
            templateText.append(text.span(startOffset - startInDoc, start - startInDoc - 1));
        }
        templateText.append("$__Variable``variables.size``$");
        startOffset = start + len;
        value varName = len == 0 then "" else text.span(start - startInDoc, start - startInDoc - 1 + len);
        variables.add([varName, proposals]);
    }
    
    shared void buildTemplate(Editor editor, Boolean inline = false) {
        if (startOffset < startInDoc + text.size) {
            templateText.append(text.span(startOffset - startInDoc, startInDoc + text.size));
        }
        
        //print("-``templateText.string``-");
        
        TemplateImpl template = TemplateImpl("", templateText.string, "");
        for (var in variables) {
            template.addVariable(object extends Expression() {
                shared actual ObjectArray<LookupElement> calculateLookupItems(ExpressionContext? expressionContext) => createJavaObjectArray(var[1]);
                shared actual TemplateResult? calculateQuickResult(ExpressionContext? expressionContext) => TextResult(var[0]);
                shared actual TemplateResult? calculateResult(ExpressionContext? expressionContext) => TextResult(var[0]);
            }, true);
        }
        
        template.toReformat = false;
        template.inline = inline;
        
        // TODO replace live templates with this?
        //value marker = editor.markupModel.addRangeHighlighter(5, 10, HighlighterLayer.\iLAST + 1,
        //    EditorColorsManager.instance.globalScheme.getAttributes(EditorColors.\iLIVE_TEMPLATE_ATTRIBUTES),
        //    HighlighterTargetArea.\iEXACT_RANGE);
        
        TemplateManager.getInstance(editor.project).startTemplate(editor, template);
    }
}
