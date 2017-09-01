import com.intellij.concurrency {
    AsyncFutureResult,
    AsyncFutureFactory
}
import com.intellij.openapi.editor {
    Document
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
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument,
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}

import java.util {
    List
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument
}
import ceylon.interop.java {
    synchronize
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

    value futureMutex = object {};
    variable AsyncFutureResult<PhasedUnit> phasedUnitWhenTypechecked_
            = AsyncFutureFactory.instance.createAsyncFutureResult<PhasedUnit>();

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
        synchronize(futureMutex, () {
            phasedUnitWhenTypechecked_.cancel(true);
            phasedUnitWhenTypechecked_ = AsyncFutureFactory.instance.createAsyncFutureResult<PhasedUnit>();
        });
    }
    
    shared void finishedTypechecking(
        PhasedUnit phasedUnit, 
        TypeChecker? typechecker) {
        lastPhasedUnit_ = phasedUnit;
        typechecker_ = typechecker;
        if (phasedUnit.compilationUnit === parsedRootNode) {
            synchronize(futureMutex, () {
                if (!phasedUnitWhenTypechecked_.done) {
                    phasedUnitWhenTypechecked_.set(phasedUnit);
                } else {
                    try {
                        if (!phasedUnitWhenTypechecked_.cancelled,
                            exists cu = phasedUnitWhenTypechecked_.get()?.compilationUnit,
                            ! (cu === parsedRootNode)) {
                            platformUtils.log(Status._WARNING, "The typechecked phased unit future was already set with a different phased unit in `MutableLocalAnalysisResult.finishTypechecking()` !");
                        }
                    } catch(e) {}
                }
            });
        } else {
            synchronize(futureMutex, () {
                if (!phasedUnitWhenTypechecked_.done) {
                    phasedUnitWhenTypechecked_.cancel(true);
                }
            });
            platformUtils.log(Status._WARNING, "The typechecked phased unit reported by `MutableLocalAnalysisResult.finishTypechecking()` doesn't match the pending root node !");
        }
    }
    
    shared Document document => document_;
    commonDocument => commonDocument_;
    
    shared actual PhasedUnit? lastPhasedUnit => lastPhasedUnit_;
    
    shared actual TypeChecker? typeChecker => typechecker_;
    
    shared actual Tree.CompilationUnit parsedRootNode => parsedRootNode_;
    
    shared actual List<CommonToken> tokens => tokens_;
    
    shared actual PhasedUnit? typecheckedPhasedUnit =>
            if (exists aTypecheckedPhasedUnit = lastPhasedUnit,
                exists lastTypecheckedRootNode = aTypecheckedPhasedUnit.compilationUnit,
                lastTypecheckedRootNode === parsedRootNode)
    then aTypecheckedPhasedUnit
    else null;

    shared actual AsyncFutureResult<out PhasedUnit> phasedUnitWhenTypechecked => phasedUnitWhenTypechecked_;

    shared LocalAnalysisResult immutable => object
            satisfies LocalAnalysisResult {
        value initialLastPhasedUnit = outer.lastPhasedUnit;

        ceylonProject = outer.ceylonProject;
        commonDocument = outer.commonDocument;
        parsedRootNode = outer.parsedRootNode;
        tokens = outer.tokens;
        typeChecker = outer.typeChecker;
        phasedUnitWhenTypechecked = outer.phasedUnitWhenTypechecked;

        shared actual PhasedUnit? lastPhasedUnit {
            if (phasedUnitWhenTypechecked.done) {
                try {
                    return phasedUnitWhenTypechecked.get();
                } catch(e) {
                }
            }
            return initialLastPhasedUnit;
        }

        typecheckedPhasedUnit =>
            if (exists aTypecheckedPhasedUnit = lastPhasedUnit,
                exists lastTypecheckedRootNode = aTypecheckedPhasedUnit.compilationUnit,
                lastTypecheckedRootNode === parsedRootNode)
            then aTypecheckedPhasedUnit
            else null;
    };
}