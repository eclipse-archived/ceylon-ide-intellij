import com.intellij.codeInsight.completion {
    CompletionParameters,
    CompletionResultSet,
    CompletionProvider,
    CompletionService {
        completionService
    },
    CompletionInitializationContext {
        dummyIdentifierTrimmed
    }
}
import com.intellij.codeInsight.completion.impl {
    CompletionServiceImpl {
        completionServiceImpl=completionService
    }
}
import com.intellij.codeInsight.lookup {
    LookupElementWeigher,
    LookupElement
}
import com.intellij.openapi.application {
    ApplicationAdapter
}
import com.intellij.openapi.application.ex {
    ApplicationManagerEx {
        application=applicationEx
    }
}
import com.intellij.openapi.progress {
    EmptyProgressIndicator,
    ProcessCanceledException
}
import com.intellij.openapi.ui {
    MessageType
}
import com.intellij.psi.impl.source.tree {
    LeafPsiElement
}
import com.intellij.util {
    ProcessingContext
}
import com.redhat.ceylon.ide.common.completion {
    completionManager
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}

import java.lang {
    JInteger=Integer
}

import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        withAlternateResolution
    },
    getModelManager,
    CeylonModelManager,
    PsiElementGoneException
}
import org.intellij.plugins.ceylon.ide.model.parsing {
    ProgressIndicatorMonitor
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonTokens,
    CeylonLocalAnalyzer
}

shared class IdeaCompletionProvider()
        extends CompletionProvider<CompletionParameters>()  {

    function getAnalysisResult(CeylonModelManager modelManager, CompletionParameters parameters,
            CeylonLocalAnalyzer localAnalyzer, ProgressIndicatorMonitor progressMonitor) {
        value busy = modelManager.busy;
        value autoPopup = parameters.autoPopup;

        if (autoPopup || busy,
            parameters.position.text.indexOf(dummyIdentifierTrimmed)>1, //we're just typing within an identifier
            exists result = localAnalyzer.result, result.lastCompilationUnit exists) {
            if (busy, exists completion = completionServiceImpl.currentCompletion) {
                completion.addAdvertisement(
                    "The results might be incomplete during a Ceylon model update",
                    MessageType.warning.popupBackground);
            }
            return result;
        }

        return if (busy) then null
            else localAnalyzer.ensureTypechecked {
                cancellable = progressMonitor;
                waitForModelInSeconds = autoPopup then 0 else 4;
            };
    }

    shared actual void addCompletions(CompletionParameters parameters,
        ProcessingContext context, variable CompletionResultSet result) {

        assert (exists project = parameters.editor.project,
                exists modelManager = getModelManager(project));
        try {
            modelManager.pauseAutomaticModelUpdate();
        
            value sorter = completionService.emptySorter().weigh(
                object extends LookupElementWeigher("keepInitialOrderWeigher") {
                    variable value i = 0;
                    weigh(LookupElement element) => JInteger(i++);
                }
            );
            
            result = result.withRelevanceSorter(sorter);

            if (is LeafPsiElement position = parameters.position) {
                if (position.elementType == CeylonTokens.astringLiteral) {
                    //TODO: figure out the doc link prefix
                    String text = position.text;
                    if (exists end = text.firstInclusion(dummyIdentifierTrimmed)) {
                        String textBefore = text[0:end];
                        if (exists start = textBefore.lastInclusion("[[")) {
                            String prefix = textBefore[start+2...];
                            result = result.withPrefixMatcher(prefix);
                        }
                        else {
                            result = result.withPrefixMatcher("");
                        }
                    }
                    else {
                        result = result.withPrefixMatcher("");
                    }
                }
                if (position.elementType == CeylonTokens.pidentifier) {
                    // In case of a package identifier like `ceylon.collection`, we compute a reference
                    // on the whole path, which will lead IntelliJ to create prefixes like `ceylon.col`
                    // whereas completionManager will return things like `collection`, which won't match
                    // the prefix. We thus have to change the prefix to what's after the dot.
                    String text = position.text;
                    value loc = text.firstInclusion(dummyIdentifierTrimmed);
                    String prefix = if (exists loc) then text[0:loc] else text;
                    result = result.withPrefixMatcher(prefix);
                }
            }

            if (!application.writeActionPending) {
                value progressMonitor = ProgressIndicatorMonitor.wrap {
                    object monitor extends EmptyProgressIndicator() {
                        // hashCode() seems to be quite slow when used
                        // in CoreProgressManager.threadsUnderIndicator
                        hash => 43;
                    }
                };

                object listener extends ApplicationAdapter() {
                    shared actual void beforeWriteActionStart(Object action) {
                        if (!progressMonitor.cancelled) {
                            progressMonitor.wrapped.cancel();
                        }
                    }
                }
                application.addApplicationListener(listener);

                try {
                    withAlternateResolution(() {
                        if (is CeylonFile ceylonFile = parameters.originalFile,
                            exists localAnalyzer = ceylonFile.localAnalyzer,
                            exists analysisResult = getAnalysisResult {
                                modelManager = modelManager;
                                parameters = parameters;
                                localAnalyzer = localAnalyzer;
                                progressMonitor = progressMonitor;
                            }) {

                            addCompletionsInternal {
                                modelManager = modelManager;
                                parameters = parameters;
                                context = context;
                                result = result;
                                analysisResult = analysisResult;
                                options = completionSettings.options;
                                progressMonitor = progressMonitor;
                            };
                        }
                    });
                } catch (ProcessCanceledException e) {
                    noop(); // for debugging purposes
                } catch (PsiElementGoneException e) {
                    noop(); //noop
                } finally {
                    application.removeApplicationListener(listener);
                }
            }
        } finally {
            modelManager.resumeAutomaticModelUpdate();
        }
    }
    
    void addCompletionsInternal(CeylonModelManager modelManager,
        CompletionParameters parameters,
        ProcessingContext context, CompletionResultSet result,
        LocalAnalysisResult analysisResult, CompletionOptions options,
        ProgressIndicatorMonitor progressMonitor) {
        
        if (exists element = parameters.originalPosition) {
            assert (is CeylonFile ceylonFile = element.containingFile,
                    exists project = parameters.editor.project);

            value isSecondLevel
                    = parameters.invocationCount > 0
                    && parameters.invocationCount % 2 == 0;

            value ctx = IdeaCompletionContext {
                file = ceylonFile;
                localAnalysisResult = analysisResult;
                editor = parameters.editor;
                options = options;
                result = result;
            };
            value doc = parameters.editor.document;
            completionManager.getContentProposals {
                typecheckedRootNode = ctx.lastCompilationUnit;
                ctx = ctx;
                offset = parameters.editor.caretModel.offset;
                line = doc.getLineNumber(element.textOffset);
                secondLevel = isSecondLevel;
                monitor = progressMonitor;
                // The parameters tooltip has nothing to do with code completion, so we bypass it
                returnedParamInfo = true;
            };

            installCustomLookupCellRenderer(project);

            if (!isSecondLevel && !modelManager.busy) {
                result.addLookupAdvertisement("Call again to toggle second-level completions");
            }
        }
    }
}
