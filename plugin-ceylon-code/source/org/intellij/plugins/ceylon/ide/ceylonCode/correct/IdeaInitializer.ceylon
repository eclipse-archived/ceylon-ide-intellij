import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.intellij.openapi.editor {
    Document
}
import com.redhat.ceylon.ide.common.completion {
    getProposedName,
    appendPositionalArgs
}
import com.redhat.ceylon.ide.common.correct {
    AbstractInitializerQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Functional
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode,
    IdeaLinkedModeSupport
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaInitializer()
        satisfies AbstractInitializerQuickFix<IdeaLinkedMode,Document,LookupElement>
                & IdeaLinkedModeSupport {

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
