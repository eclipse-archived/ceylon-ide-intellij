package org.intellij.plugins.ceylon.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import jtermios.windows.WinAPI;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerInvoker;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.completion.ideaCompletionManager_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonCompletionContributor extends CompletionContributor {

    public CeylonCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new MyCompletionProvider());
    }

    private static class MyCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {

            CompletionSorter sorter = CompletionService.getCompletionService()
                    .emptySorter()
                    .weigh(new LookupElementWeigher("keepInitialOrderWeigher", false, false) {
                        int i = 0;

                        @Nullable
                        @Override
                        public Comparable weigh(@NotNull LookupElement element) {
                            i++;
                            return i;
                        }
                    });
            result = result.withRelevanceSorter(sorter);

            if (parameters.getPosition() instanceof LeafPsiElement && ((LeafPsiElement) parameters.getPosition()).getElementType() == CeylonTokens.ASTRING_LITERAL) {
                result = result.withPrefixMatcher("");
            }
            PsiElement element = parameters.getOriginalPosition();

            if (element != null) {
                CeylonFile ceylonFile = (CeylonFile) element.getContainingFile();
                PhasedUnit phasedUnit = TypeCheckerInvoker.invokeTypeChecker(ceylonFile);
                if (phasedUnit != null) {
                    ideaCompletionManager_.get_().addCompletions(parameters, context, result, phasedUnit, TypeCheckerProvider.getFor(element));
                }
            }
        }
    }

}