import ceylon.collection {
    ArrayList
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

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class IdeaCompletionContext(file, editor, ceylonProject, options) satisfies CompletionContext {

    shared CeylonFile file;
    shared Editor editor;

    shared actual BaseCeylonProject? ceylonProject;
    shared actual CompletionOptions options;

    shared actual IdeaDocument commonDocument = IdeaDocument(editor.document);
    shared actual IdeaProposalsHolder proposals = IdeaProposalsHolder();

    assert (exists localAnalysisResult = file.localAnalysisResult);

    parsedRootNode => localAnalysisResult.parsedRootNode;
    tokens => localAnalysisResult.tokens;
    typecheckedRootNode => localAnalysisResult.typecheckedRootNode;
    
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

    proposalFilters => empty;
    
}

shared class IdeaProposalsHolder() satisfies ProposalsHolder {
    value _proposals = ArrayList<LookupElement>();
    
    shared List<LookupElement> proposals => _proposals;

    shared void add(LookupElement element) => _proposals.add(element);

    size => _proposals.size;
    
}
