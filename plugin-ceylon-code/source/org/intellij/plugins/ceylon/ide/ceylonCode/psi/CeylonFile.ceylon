import ceylon.interop.java {
    javaClass,
    createJavaObjectArray
}

import com.intellij.concurrency {
    AsyncFutureFactory,
    AsyncFuture,
    SameThreadExecutor,
    ResultConsumer
}
import com.intellij.extapi.psi {
    PsiFileBase
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleUtil
}
import com.intellij.openapi.util {
    Computable,
    Ref
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    FileViewProvider,
    PsiClassOwner,
    PsiClass
}
import com.intellij.psi.text {
    BlockSupport
}
import com.intellij.psi.util {
    PsiTreeUtil {
        getChildrenOfType
    }
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
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit,
    LocalAnalysisResult,
    AnyProjectPhasedUnit,
    ProjectPhasedUnit,
    AnalysisResult
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast
}

import java.lang {
    ObjectArray,
    JBoolean=Boolean,
    JClass=Class
}
import java.util.concurrent {
    ExecutionException,
    TimeUnit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage,
    CeylonFileType
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    getModelManager,
    concurrencyManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    CeylonLogger
}
import org.jetbrains.ide {
    PooledThreadExecutor
}
import com.intellij.openapi.progress {
    ProcessCanceledException
}
import com.intellij.openapi.diagnostic {
    ControlFlowException
}

CeylonLogger<CeylonFile> ceylonFileLogger = CeylonLogger<CeylonFile>();

JClass<CeylonPsi.DeclarationPsi> declarationClass = javaClass<CeylonPsi.DeclarationPsi>();
JClass<CeylonPsi.CompilationUnitPsi> compilationUnitClass = javaClass<CeylonPsi.CompilationUnitPsi>();
JClass<CeylonLocalAnalyzerManager> localAnalyzerManagerClass = javaClass<CeylonLocalAnalyzerManager>();
JClass<IdeaCeylonProjects> ideaCeylonProjectsClass = javaClass<IdeaCeylonProjects>();
ObjectArray<PsiClass> noPsiClasses = ObjectArray<PsiClass>(0);

ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>? retrieveProjectPhasedUnit(CeylonFile file) {
    Module? mod = ApplicationManager.application.runReadAction(
        object satisfies Computable<Module> {
            compute() => ModuleUtil.findModuleForPsiElement(file);
        }
    );

    value ceylonProjects = file.project.getComponent(ideaCeylonProjectsClass);
    if (exists ceylonProject = ceylonProjects.getProject(mod),
        exists vf = file.realVirtualFile(),
        exists ceylonVirtualFile = ceylonProject.projectFileFromNative(vf)) {
        return ceylonProject.getParsedUnit(ceylonVirtualFile);
    }
    return null;
}

ExternalPhasedUnit? retrieveAvailableExternalPhasedUnit(CeylonFile file) {
    if (exists vf = file.realVirtualFile(),
        exists externalPuRef = vf.getUserData(uneditedExternalPhasedUnitRef)) {
        return externalPuRef.get();
    }
    return null;
}


AnalysisResult? returnIt(AnalysisResult? result)
        => result;

AsyncFuture<AnalysisResult> wrapItInFuture(AnalysisResult? result)
        => AsyncFutureFactory.instance.wrap(result);

Null giveUp(AsyncFuture<AnalysisResult>() futureBuilder)
        => null;

AsyncFuture<AnalysisResult> returnTheFuture(AsyncFuture<AnalysisResult>() futureBuilder)
        => futureBuilder();

shared class CannotWaitForAnalysisResultInLockedSection() extends ProcessCanceledException() {
    message => "Waiting for an analysis result on a Ceylon File in a
                locked section (read or write) is dead-lock-prone.\n
                Cancelling the wait.";
}

shared class CeylonFile(FileViewProvider viewProvider)
        extends PsiFileBase(viewProvider, CeylonLanguage.instance)
        satisfies PsiClassOwner {

    putUserData(BlockSupport.treeDepthLimitExceeded, JBoolean.true);

    value compilationUnitPsi {
        assert (exists psi = PsiTreeUtil.findChildOfType(this, compilationUnitClass));
        return psi;
    }

    shared VirtualFile? realVirtualFile() => originalFile.virtualFile;


    "*__Be careful:__* this might be a Ceylon compilation unit that is not typechecked.
     To be sure that the Ceylon AST bridged to this [[CeylonFile]] is typechecked,
     use the other members that return either an [[AnalysisResult]], an up-to-date [[PhasedUnit]],
     or the [[CeylonLocalAnalyzer]] for an edited Ceylon file."
    see(`value availableAnalysisResult`,
        `function waitForAnalysis`,
        `function doWhenAnalyzed`,
        `value availableUpToDatePhasedUnit`,
        `function waitForUpToDatePhasedUnit`,
        `value localAnalyzer`)
    shared Tree.CompilationUnit compilationUnit => compilationUnitPsi.ceylonNode;

    "The [[CeylonLocalAnalyzer]] associated to this file if it is opened
     in at least one editor.
     The current [[LocalAnalysisResult]]
     can be retrieved through the [[result|CeylonLocalAnalyzer.result]] member."
    shared CeylonLocalAnalyzer? localAnalyzer
            => let (localAnalyzerManager = project.getComponent(localAnalyzerManagerClass))
            if (exists vf = realVirtualFile())
            then localAnalyzerManager[vf]
            else null;

    shared TypeChecker? typechecker {
        if (isInSourceArchive(realVirtualFile())) {
            value ceylonProjects = project.getComponent(ideaCeylonProjectsClass);
            if (exists mod = ceylonProjects.findModuleForExternalPhasedUnit(realVirtualFile()),
                exists ceylonProject = mod.ceylonProject) {

                return ceylonProject.typechecker;
            }
            return null;
        } else if (exists projectPhasedUnit = retrieveProjectPhasedUnit(this)) {
            return projectPhasedUnit.typeChecker;
        }

        return availableAnalysisResult?.typeChecker;
    }

    "return the [[AnalysisResult]] if it is available, or [[null]].

     It can be unavailable if:
     - it is an edited Ceylon file that has not been analyzed at least once.
     This is not likely to happen, since an analysis is scheduled as soon as
     an editor is opened.
     - it is a file from a binary Ceylon archive, and the corresponding source
     [[ExternalPhasedUnit]] has not been lazily opened and typechecked at least once.
     In such a case, you can use the [[waitForAnalysis]] or [[doWhenAnalyzed]] methods
     to do your work when the [[ExternalPhasedUnit]] has been lazily typechecked
     and bridged with the [[CeylonFile]] PSI AST.
     - it is a project file not opened in any editor, and a model update is executing.
     In this case there is not much to do."
    shared AnalysisResult? availableAnalysisResult {
        value result = requestAnalysisResult {
            onExistingResult = returnIt;
            onNonExistingResult = giveUp;
        };
        return result;
    }


    "Wait until an up-to-date [[AnalysisResult]] is available with a typechecked
     [[PhasedUnit]] correctly bridged with the Ceylon PSI AST. This might lazily
     trigger the loading / typechecking of an [[ExternalPhasedUnit]] from the
     source archive associated to a Ceylon binary module.

     *__Beware__*: It is *__NOT__* possible to wait for a pending analysis result
     from a read section or write section, since it is highly deadlock-prone.
     In such a case, this method will throw a
     [[CannotWaitForAnalysisResultInLockedSection]] exception (that extends
     [[ProcessCanceledException]]), to avoid lock problems."
    throws(`class CannotWaitForAnalysisResultInLockedSection`)
    shared AnalysisResult? waitForAnalysis(Integer timeout, TimeUnit unit) {
        if (exists analysisResult = waitForAnalysisResult(timeout, unit)) {
            value phasedUnitFuture = analysisResult.phasedUnitWhenTypechecked;
            try {
                phasedUnitFuture.get(timeout, unit);
                return analysisResult;
            } catch (Exception e) {
                if (is ControlFlowException realException = e) {
                    throw realException;
                }
                if (is ControlFlowException realException = e.cause) {
                    throw realException;
                }
                platformUtils.log(Status._WARNING,
                    "Analysis result retrieval triggered the following exception for file ``
                    realVirtualFile() else "<unknown>" ``", e);
            }
        }

        return null;
    }

    "Asynchronously run [[action]] when an up-to-date [[AnalysisResult]] is available
     with a typechecked [[PhasedUnit]] correctly bridged with the Ceylon PSI AST.
     This might lazily trigger the loading / typechecking of an [[ExternalPhasedUnit]] from the
     source archive associated to a Ceylon binary module."
    shared void doWhenAnalyzed(void action(AnalysisResult analysisResult)) {
        void doWithFuture<V>(AsyncFuture<V> future, void action(V? v)) {
            Ref<Boolean> consumerStarted = Ref(false);
            future.addConsumer(SameThreadExecutor.instance, object satisfies ResultConsumer<V> {
                shared actual void onFailure(Throwable throwable) {
                    consumerStarted.set(true);
                    ceylonFileLogger.warnThrowable(throwable);
                }

                shared actual void onSuccess(V? v) {
                    consumerStarted.set(true);
                    if (exists v) {
                        action(v);
                    }
                }
            });
            if (future.done && !(consumerStarted.get() else false)) {
                try {
                    if(exists v = future.get()) {
                        action(v);
                    }
                } catch(ExecutionException ee) {
                    ceylonFileLogger.warnThrowable(ee.cause else ee);
                }
            }
        }

        value resultFuture = requestAnalysisResult {
            onExistingResult = wrapItInFuture;
            onNonExistingResult = returnTheFuture;
        };
        doWithFuture<AnalysisResult>(resultFuture, (AnalysisResult? analysisResult) {
            if(exists analysisResult) {
                value phasedUnitFuture = unsafeCast<AsyncFuture<PhasedUnit>>(
                        analysisResult.phasedUnitWhenTypechecked);
                doWithFuture(phasedUnitFuture, (Anything phasedUnit) {
                    if (exists phasedUnit) {
                        action(analysisResult);
                    }
                });
            }
        });
    }

    shared PhasedUnit? availableUpToDatePhasedUnit =>
            availableAnalysisResult?.typecheckedPhasedUnit;

    shared PhasedUnit? waitForUpToDatePhasedUnit(Integer timeout, TimeUnit unit) =>
            waitForAnalysis(timeout, unit)?.typecheckedPhasedUnit;


    Return requestAnalysisResult<Return>(
            Return onExistingResult(AnalysisResult? result),
            Return onNonExistingResult(AsyncFuture<AnalysisResult>() futureBuilder)) {
        value attachedCompilationUnit
                => concurrencyManager.needReadAccess(() => compilationUnit);

        function checkRootNode(Tree.CompilationUnit toCheck, AnalysisResult() produceResult, String checkedAttributeName) {
            AnalysisResult? resultToReturn;
            value theAttachedCompilationUnit = attachedCompilationUnit;
            if (toCheck === theAttachedCompilationUnit) {
                resultToReturn = produceResult();
            }
            else {
                ceylonFileLogger.warn(() =>
                "`` checkedAttributeName ``(``
                toCheck.hash ``) !== ceylonFile.compilationUnit (``
                theAttachedCompilationUnit.hash `` - `` theAttachedCompilationUnit.nodeType `` - `` theAttachedCompilationUnit.location ``) for file `` originalFile.name ``(``originalFile.hash``)");
                resultToReturn =  null;
            }
            return resultToReturn;
        }

        if (exists localAnalyzer = this.localAnalyzer) {
            function checkAnalysisResult(AnalysisResult result) => checkRootNode {
                toCheck = result.parsedRootNode;
                produceResult() => result;
                checkedAttributeName = "LocalAnalysisResult.parsedRootNode";
            };

            if (exists result = localAnalyzer.result) {
                return onExistingResult(checkAnalysisResult(result));
            } else {
                return onNonExistingResult {
                    function futureBuilder() {
                        value resultFuture = AsyncFutureFactory.instance.createAsyncFutureResult<AnalysisResult>();
                        localAnalyzer.waitForResult().addConsumer(
                            SameThreadExecutor.instance,
                            object satisfies ResultConsumer<LocalAnalysisResult> {
                                shared actual void onFailure(Throwable throwable) => resultFuture.setException(throwable);
                                shared actual void onSuccess(LocalAnalysisResult result) {
                                    try {
                                        value checkedAnalysisResult = checkAnalysisResult(result);
                                        if (is MutableLocalAnalysisResult checkedAnalysisResult) {
                                            resultFuture.set(checkedAnalysisResult.immutable);
                                        } else {
                                            resultFuture.set(checkedAnalysisResult);
                                        }
                                    } catch(Throwable t) {
                                        resultFuture.setException(t);
                                    }
                                }
                            });
                        return resultFuture;
                    }
                };
            }
        }

        alias ExpectedPhasedUnit => ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>|ExternalPhasedUnit;
        function analysisResultFromPhasedUnit(ExpectedPhasedUnit phasedUnit) {
            return object satisfies AnalysisResult {
                assert (exists document = viewProvider.document);

                commonDocument => IdeaDocument(document);
                typeChecker => phasedUnit.typeChecker;
                tokens => phasedUnit.tokens;
                parsedRootNode => attachedCompilationUnit;
                typecheckedPhasedUnit => phasedUnit;

                ceylonProject
                        => if (is AnyProjectPhasedUnit phasedUnit)
                then phasedUnit.ceylonProject
                else phasedUnit.moduleSourceMapper?.ceylonProject;

                phasedUnitWhenTypechecked => AsyncFutureFactory.instance.wrap(phasedUnit);
            };
        }

        value inSourceArchive = isInSourceArchive(realVirtualFile());

        ExpectedPhasedUnit? phasedUnit = if (inSourceArchive)
        then retrieveAvailableExternalPhasedUnit(this)
        else retrieveProjectPhasedUnit(this);

        function checkPhasedUnit(ExpectedPhasedUnit phasedUnit) => checkRootNode {
            toCheck = phasedUnit.compilationUnit;
            produceResult() => analysisResultFromPhasedUnit(phasedUnit);
            checkedAttributeName = "``
            if(phasedUnit is ExternalPhasedUnit)
            then "ExternalPhasedUnit"
            else "ProjectPhasedUnit"
            ``.compilationUnit";
        };

        if (exists phasedUnit,
            phasedUnit.refinementValidated) {
            return onExistingResult(checkPhasedUnit(phasedUnit));
        } else {
            if (inSourceArchive) {
                return onNonExistingResult {
                    function futureBuilder() {
                        value newPhasedUnitFuture = AsyncFutureFactory.instance.createAsyncFutureResult<ExternalPhasedUnit>();
                        assert(exists vf = realVirtualFile());
                        value phasedUnitFuture = vf.putUserDataIfAbsent(uneditedExternalPhasedUnitFuture, newPhasedUnitFuture);
                        if (phasedUnitFuture != newPhasedUnitFuture) {
                            newPhasedUnitFuture.cancel(true);
                        }

                        value resultFuture = AsyncFutureFactory.instance.createAsyncFutureResult<AnalysisResult>();
                        phasedUnitFuture.addConsumer(PooledThreadExecutor.instance, object satisfies ResultConsumer<ExternalPhasedUnit> {
                            shared actual void onFailure(Throwable throwable) => resultFuture.setException(throwable);

                            shared actual void onSuccess(ExternalPhasedUnit phasedUnit) {
                                try {
                                    resultFuture.set(checkPhasedUnit(phasedUnit));
                                } catch(Throwable t) {
                                    resultFuture.setException(t);
                                }
                            }
                        });

                        value localAnalyzerManager = project.getComponent(localAnalyzerManagerClass);
                        if (ApplicationManager.application.dispatchThread) {
                            localAnalyzerManager.retrieveTypecheckedAndBridgedExternalSource(vf);
                        } else {
                            localAnalyzerManager.scheduleExternalSourcePreparation(vf);
                        }

                        return resultFuture;
                    }
                };
            }
        }
        platformUtils.log(Status._DEBUG, "localAnalysisResult requested, but was null");
        return onExistingResult(null);
    }

    AnalysisResult? waitForAnalysisResult(Integer timeout, TimeUnit unit) {
        value resultFuture = requestAnalysisResult {
            onExistingResult = wrapItInFuture;
            onNonExistingResult = returnTheFuture;
        };
        try {
            if (!resultFuture.done) {
                value application = ApplicationManager.application;
                if ((application.readAccessAllowed && !application.dispatchThread)
                || application.writeAccessAllowed ) {
                    throw CannotWaitForAnalysisResultInLockedSection();
                }
            }
            return resultFuture.get(timeout, unit);
        } catch (Exception e) {
            if (is ControlFlowException realException = e) {
                throw realException;
            }
            if (is ControlFlowException realException = e.cause) {
                throw realException;
            }
            platformUtils.log(Status._WARNING,
                "Analysis result retrieval triggered the following exception for file ``
                realVirtualFile() else "<unknown>" ``", e);
            return null;
        }
    }

    fileType => CeylonFileType.instance;

    shared PhasedUnit? forceReparse() {
        onContentReload();

        suppressWarnings("unusedDeclaration")
        value neededToTriggerTheReparse = node.lastChildNode;

        return localAnalyzer?.ensureTypechecked()?.lastPhasedUnit;
    }

    shared actual ObjectArray<PsiClass> classes {
        if (exists mm = getModelManager(project), mm.busy) {
            // To avoid huge performance hits during full model updates, because `getName()` on
            // any compiled PSI element will try to get the name from sources
            return noPsiClasses;
        }

        if (exists decls = getChildrenOfType(compilationUnitPsi, declarationClass)) {
            value classes = ObjectArray<PsiClass>(decls.size);
            variable value i = 0;
            for (decl in decls) {
                classes.set(i++, NavigationPsiClass(decl));
            }
            return classes;
        } else if (is CeylonPsi.ModuleDescriptorPsi mod = compilationUnitPsi.firstChild) {
            return createJavaObjectArray({NavigationPsiClass(mod)});
        } else if (is CeylonPsi.PackageDescriptorPsi pkg = compilationUnitPsi.firstChild) {
            return createJavaObjectArray({NavigationPsiClass(pkg)});
        } else {
            return noPsiClasses;
        }
    }

    shared actual String packageName => "";
    assign packageName {}

}
