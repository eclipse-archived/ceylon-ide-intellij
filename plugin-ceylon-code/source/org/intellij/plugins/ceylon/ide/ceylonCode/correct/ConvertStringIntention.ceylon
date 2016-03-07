import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.correct {
    ConvertStringQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ConvertStringToVerbatimIntention()
        extends GenericIntention()
        satisfies ConvertStringQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> {    
    
    familyName => "Convert string to verbatim";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addConvertToVerbatimProposal(data, file);
}

shared class ConvertVerbatimToStringIntention()
        extends GenericIntention()
        satisfies ConvertStringQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> {    
    
    familyName => "Convert verbatim to string";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addConvertFromVerbatimProposal(data, file);
}

shared class ConvertToInterpolationIntention()
        extends GenericIntention()
        satisfies ConvertStringQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> {    
    
    familyName => "Convert concatenation to interpolation";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addConvertToInterpolationProposal(data, file);
}

shared class ConvertToConcatenationIntention()
        extends GenericIntention()
        satisfies ConvertStringQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> {    
    
    familyName => "Convert interpolation to concatenation";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addConvertToConcatenationProposal(data, file);
}
