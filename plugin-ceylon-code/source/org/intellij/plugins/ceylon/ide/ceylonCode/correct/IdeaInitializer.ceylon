import com.redhat.ceylon.ide.common.correct {
    AbstractInitializerQuickFix
}
import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode
}
import com.intellij.openapi.editor {
    Document,
    EditorFactory
}
import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Functional
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}
import com.redhat.ceylon.ide.common.completion {
    getProposedName,
    appendPositionalArgs
}

class IdeaInitializer()
        satisfies AbstractInitializerQuickFix<IdeaLinkedMode,Document,LookupElement> {

    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc, 
        Integer start, Integer len, Integer exitSeqNumber, LookupElement[] proposals) {
        
        lm.addEditableRegion(start, len, proposals);
    }
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, 
        Object owner, Integer exitSeqNumber, Integer exitPosition) {
        
        value editors = EditorFactory.instance.getEditors(doc);
        if (editors.size > 0) {
            lm.buildTemplate(editors.get(0));
        }
    }
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode();

    shared actual LookupElement newNestedCompletionProposal(Declaration dec, 
        Integer offset) => LookupElementBuilder.create(getText(dec, false))
                .withPresentableText(getText(dec, true))
                .withIcon(ideaIcons.forDeclaration(dec));

    
    shared actual LookupElement newNestedLiteralCompletionProposal(String val, 
        Integer offset) => LookupElementBuilder.create(val);
    
    String getText(Declaration dec, Boolean description) {
        StringBuilder sb = StringBuilder();
        value unit = dec.unit;
        sb.append(getProposedName(null, dec, unit));
        if (is Functional dec) {
            appendPositionalArgs(dec, null, unit, sb, false, description, false);
        }
        
        return sb.string;
    }
    
}
