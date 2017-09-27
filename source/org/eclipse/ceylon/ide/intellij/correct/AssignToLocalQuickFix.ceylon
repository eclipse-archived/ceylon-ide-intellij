import ceylon.collection {
    HashSet
}

import com.intellij.codeInsight.lookup {
    LookupElementBuilder
}
import com.intellij.openapi.editor {
    Editor
}
import org.eclipse.ceylon.ide.common.completion {
    ProposalsHolder
}
import org.eclipse.ceylon.ide.common.correct {
    AssignToLocalProposal,
    importProposals,
    assignToLocalQuickFix
}
import org.eclipse.ceylon.model.typechecker.model {
    Type,
    Unit,
    Declaration
}

import org.eclipse.ceylon.ide.intellij.completion {
    IdeaProposalsHolder,
    CompletionHandler
}
import org.eclipse.ceylon.ide.intellij.platform {
    IdeaTextChange,
    IdeaLinkedMode,
    IdeaDocument
}
import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}
import org.eclipse.ceylon.ide.intellij.util {
    icons
}

shared class AssignToLocalIntention() extends AbstractIntention() {
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => assignToLocalQuickFix.addProposal(data, offset);
    
    familyName => "Assign to local";
}

class AssignToLocalElement(IdeaQuickFixData data, Editor editor, CeylonFile file)
        satisfies AssignToLocalProposal {
    
    shared actual variable Integer currentOffset = editor.caretModel.offset;
    
    shared actual variable Integer exitPos = 0;
    
    shared actual variable {String*} names = [];
    
    shared actual variable Integer offset = 0;
    
    shared actual variable Type? type = null;
    
    shared void perform() {
        if (exists change = performInitialChange(data, currentOffset)) {
            change.apply();
        }

        assert (is IdeaDocument doc = data.document,
                exists phasedUnit = file.localAnalyzer?.result?.lastPhasedUnit);

        value linkedMode = IdeaLinkedMode(doc);
        addLinkedPositions(linkedMode, phasedUnit.unit);
        // TODO can't we do that in ide-common?
        linkedMode.install(this, 0, 0);
    }
    
    shared actual void toProposals(<String|Type>[] types, ProposalsHolder proposals, 
        Integer offset, Unit unit) {
        
        assert (is IdeaProposalsHolder proposals);
        
        for (type in types) {
            if (is String type) {
                proposals.add(LookupElementBuilder.create(type).withIcon(icons.correction));
            } else {
                value prop = LookupElementBuilder.create(type.asString(unit))
                    .withIcon(icons.forDeclaration(type.declaration))
                    .withInsertHandler(CompletionHandler((context) {
                        // TODO abstract that
                        value imports = HashSet<Declaration>();
                        importProposals.importType(imports, type, data.rootNode);
                        if (!imports.empty) {
                            value change = IdeaTextChange(data.document);
                            change.initMultiEdit();
                            importProposals.applyImports(change, imports, data.rootNode, data.document);
                            change.apply();
                        }
                    }));
                proposals.add(prop);
            }
        }
    }

    shared actual void toNameProposals(String[] names, ProposalsHolder proposals, 
        Integer offset, Unit unit, Integer seq) {

        assert (is IdeaProposalsHolder proposals);
        
        for (name in names) {
            proposals.add(LookupElementBuilder.create(name).withIcon(icons.local));
        }
    }
}
