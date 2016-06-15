import ceylon.collection {
    HashSet
}

import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.redhat.ceylon.ide.common.completion {
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.correct {
    AssignToLocalProposal,
    importProposals,
    assignToLocalQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Type,
    Unit,
    Declaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaProposalsHolder
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaTextChange,
    IdeaLinkedMode,
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

shared class AssignToLocalIntention() extends AbstractIntention() {
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => assignToLocalQuickFix.addProposal(data, offset);
    
    familyName => "Assign to local";
}

class AssignToLocalElement(IdeaQuickFixData data, Project p, Editor e, CeylonFile f)
        satisfies AssignToLocalProposal {
    
    shared actual variable Integer currentOffset = e.caretModel.offset;
    
    shared actual variable Integer exitPos = 0;
    
    shared actual variable {String*} names = empty;
    
    shared actual variable Integer offset = 0;
    
    shared actual variable Type? type = null;
    
    shared void perform() {
        if (exists change = performInitialChange(data, currentOffset)) {
            change.apply();
        }
        assert(is IdeaDocument doc = data.document);
        value lm = IdeaLinkedMode(doc);
        addLinkedPositions(lm, f.phasedUnit.unit);
        // TODO can't we do that in ide-common?
        lm.install(this, 0, 0);
    }
    
    shared actual void toProposals(<String|Type>[] types, ProposalsHolder proposals, 
        Integer offset, Unit unit) {
        
        assert(is IdeaProposalsHolder proposals);
        
        types.each((type) {
            if (is String type) {
                proposals.add(LookupElementBuilder.create(type).withIcon(icons.correction));
            } else {
                value prop = LookupElementBuilder.create(type.asString(unit))
                    .withIcon(icons.forDeclaration(type.declaration))
                    .withInsertHandler(object satisfies InsertHandler<LookupElement> {
                        shared actual void handleInsert(InsertionContext ctx, LookupElement el) {
                            // TODO abstract that
                            value imports = HashSet<Declaration>();
                            importProposals.importType(imports, type, data.rootNode);
                            if (!imports.empty) {
                                value change = IdeaTextChange(data.document);
                                change.initMultiEdit();
                                importProposals.applyImports(change, imports, data.rootNode, data.document);
                                change.apply();
                            }
                        }
                    });
                proposals.add(prop);
            }
        });
    }

    shared actual void toNameProposals(String[] names, ProposalsHolder proposals, 
        Integer offset, Unit unit, Integer seq) {

        assert(is IdeaProposalsHolder proposals);
        
        names.each(
            (n) => proposals.add(LookupElementBuilder.create(n).withIcon(icons.local))
        );
    }
}
