import ceylon.collection {
    ArrayList
}

import com.intellij.codeInsight.completion {
    CompletionResultSet
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Editor
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.completion {
    CompletionContext,
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}

import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class IdeaCompletionContext(file, localAnalysisResult, editor, options, result)
        satisfies CompletionContext {

    shared CeylonFile file;
    shared Editor editor;
    LocalAnalysisResult localAnalysisResult;
    CompletionResultSet result;

    shared actual CompletionOptions options;

    shared actual IdeaDocument commonDocument = IdeaDocument(editor.document);
    shared actual IdeaProposalsHolder proposals = IdeaCompletionResultSetProposalsHolder(result);
    shared actual BaseCeylonProject? ceylonProject => localAnalysisResult.ceylonProject;

    parsedRootNode => localAnalysisResult.parsedRootNode;
    tokens => localAnalysisResult.tokens;
    phasedUnitWhenTypechecked => localAnalysisResult.phasedUnitWhenTypechecked;
    typecheckedPhasedUnit => localAnalysisResult.typecheckedPhasedUnit;

    shared actual TypeChecker typeChecker {
        assert (exists existingTypeChecker = localAnalysisResult.typeChecker);
        return existingTypeChecker;
    }

    shared actual Tree.CompilationUnit lastCompilationUnit {
        assert (exists lastCompilationUnit = localAnalysisResult.lastCompilationUnit);
        return lastCompilationUnit;
    }
    
    shared actual PhasedUnit lastPhasedUnit {
        assert (exists lastPhasedUnit = localAnalysisResult.lastPhasedUnit);
        return lastPhasedUnit;
    }

    proposalFilters => [];
}

shared interface IdeaProposalsHolder satisfies ProposalsHolder {

    shared formal void add(LookupElement element);
    
}

shared class IdeaCompletionResultSetProposalsHolder(CompletionResultSet result)
        satisfies IdeaProposalsHolder {

    shared actual variable Integer size = 0;

    shared actual void add(LookupElement element) {
        size++;
        result.addElement(element);
    }

}

shared class IdeaListProposalsHolder()
        satisfies IdeaProposalsHolder {
    value _proposals = ArrayList<LookupElement>();

    shared List<LookupElement> proposals => _proposals;

    size => _proposals.size;

    shared actual void add(LookupElement element) {
        _proposals.add(element);
    }

}