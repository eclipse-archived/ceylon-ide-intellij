import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.redhat.ceylon.ide.common.completion {
    FunctionCompletionProposal
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.intellij.plugins.ceylon.ide.util {
    icons
}

class IdeaFunctionCompletionProposal
        (Integer offset, String prefix, String desc, String text, Declaration decl, IdeaCompletionContext ctx)
        extends FunctionCompletionProposal(offset, prefix, desc, text, decl, ctx.lastCompilationUnit)
        satisfies IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement
            => newLookup {
                description = desc;
                text = text;
                icon = icons.surround;
            };
}
