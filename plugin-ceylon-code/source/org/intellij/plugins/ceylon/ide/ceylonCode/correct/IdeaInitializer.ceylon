import com.intellij.codeInsight.lookup {
    LookupElementBuilder
}
import com.redhat.ceylon.ide.common.completion {
    getProposedName,
    appendPositionalArgs,
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.correct {
    AbstractInitializerQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Functional
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaProposalsHolder
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaInitializer()
        satisfies AbstractInitializerQuickFix {

    shared actual void newNestedCompletionProposal(ProposalsHolder proposals,
        Declaration dec, Integer offset) {
        
        if (is IdeaProposalsHolder proposals) {
            proposals.add(
                LookupElementBuilder.create(getText(dec, false))
                        .withPresentableText(getText(dec, true))
                        .withIcon(ideaIcons.forDeclaration(dec))
            );
        }
    }

    
    shared actual void newNestedLiteralCompletionProposal(ProposalsHolder proposals,
        String val, Integer offset) {
        
        if (is IdeaProposalsHolder proposals) {
            proposals.add(LookupElementBuilder.create(val));
        }
    }
    
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
