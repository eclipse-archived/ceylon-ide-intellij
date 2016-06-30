import java.util {
    List
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.intellij.openapi.editor {
    Document
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import org.antlr.runtime {
    CommonToken
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}

shared class MutableLocalAnalysisResult(
    Document theDocument,
    List<CommonToken> theTokens,
    Tree.CompilationUnit theParsedRootNode,
    shared actual BaseCeylonProject? ceylonProject) 
        satisfies LocalAnalysisResult {
    
    variable CommonDocument commonDocument_;
    variable Document document_;
    variable List<CommonToken> tokens_;
    variable Tree.CompilationUnit parsedRootNode_;
    
    variable TypeChecker? typechecker_ = null;
    variable PhasedUnit? lastPhasedUnit_ = null;

    document_ = theDocument;
    commonDocument_ = IdeaDocument(theDocument);
    tokens_ = theTokens;
    parsedRootNode_ = theParsedRootNode;

    shared void resetParsedDocument(
            Document theDocument,
            Tree.CompilationUnit theParsedRootNode,
            List<CommonToken> theTokens) {
        document_ = theDocument;
        commonDocument_ = IdeaDocument(theDocument);
        tokens_ = theTokens;
        parsedRootNode_ = theParsedRootNode;
    }
    
    shared void finishedTypechecking(
        PhasedUnit phasedUnit, 
        TypeChecker? typechecker) {
        lastPhasedUnit_ = phasedUnit;
        typechecker_ = typechecker;
    }
    
    shared Document document => document_;
    commonDocument => commonDocument_;
    
    shared actual PhasedUnit? lastPhasedUnit => lastPhasedUnit_;
    
    shared actual Tree.CompilationUnit? lastCompilationUnit => 
            lastPhasedUnit?.compilationUnit;
    
    shared actual TypeChecker? typeChecker => typechecker_;
    
    shared actual Tree.CompilationUnit parsedRootNode => parsedRootNode_;
    
    shared actual List<CommonToken> tokens => tokens_;
    
    shared actual Tree.CompilationUnit? typecheckedRootNode => 
            if (exists lastRootNode = lastCompilationUnit,
        lastRootNode === parsedRootNode)
    then lastRootNode
    else null;

    shared LocalAnalysisResult immutable => object 
            satisfies LocalAnalysisResult {
        ceylonProject = outer.ceylonProject;
        commonDocument = outer.commonDocument;
        lastCompilationUnit = outer.lastCompilationUnit;
        lastPhasedUnit = outer.lastPhasedUnit;
        parsedRootNode = outer.parsedRootNode;
        tokens = outer.tokens;
        typeChecker = outer.typeChecker;
        typecheckedRootNode = outer.typecheckedRootNode;
    };
}