import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.redhat.ceylon.ide.common.correct {
    DeclareLocalQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode,
    IdeaLinkedModeSupport
}

object ideaDeclareLocalQuickFix 
        satisfies DeclareLocalQuickFix<Document,IdeaLinkedMode,LookupElement>
                & IdeaLinkedModeSupport {
}
