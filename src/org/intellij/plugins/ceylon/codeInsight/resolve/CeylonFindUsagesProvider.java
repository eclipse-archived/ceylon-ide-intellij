package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.intellij.plugins.ceylon.parser.CeylonLexer;
import org.intellij.plugins.ceylon.psi.CeylonClassDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonInterfaceDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.intellij.plugins.ceylon.psi.impl.CeylonIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new CeylonLexer(), TokenSet.create(CeylonTypes.TYPE_NAME_DECLARATION),
                TokenSet.create(CeylonTypes.LINE_COMMENT, CeylonTypes.MULTI_LINE_COMMENT), TokenSet.create(CeylonTypes.STRING_LITERAL));
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof CeylonIdentifier;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "WTF Man?";
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element.getParent().getParent() instanceof CeylonClassDeclaration) {
            return "Class";
        } else if (element.getParent().getParent() instanceof CeylonInterfaceDeclaration) {
            return "Interface";
        }
        return "Unknown type";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        return element.getText();
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return element.getText();
    }
}
