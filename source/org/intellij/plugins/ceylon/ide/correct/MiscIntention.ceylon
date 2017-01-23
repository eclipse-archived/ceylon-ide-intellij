import com.redhat.ceylon.ide.common.correct {
    miscQuickFixes
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class AnonymousFunctionIntention() extends AbstractIntention() {
    familyName => "Convert anonymous function block/specifier";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => miscQuickFixes.addAnonymousFunctionProposals(data);
}

shared class DeclarationIntention() extends AbstractIntention() {
    familyName => "Convert declaration block/specifier";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);
        miscQuickFixes.addDeclarationProposals(data, decl, offset);
    }
}

shared class ConvertArgumentBlockIntention() extends AbstractIntention() {
    familyName => "Convert argument block/specifier";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value arg = nodes.findArgument(data.rootNode, data.node);
        miscQuickFixes.addArgumentBlockProposals(data, arg);
    }
}

shared class FillInArgumentNameIntention() extends AbstractIntention() {
    familyName => "Fill in argument name";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value arg = nodes.findArgument(data.rootNode, data.node);
        miscQuickFixes.addArgumentFillInProposals(data, arg);
    }
}