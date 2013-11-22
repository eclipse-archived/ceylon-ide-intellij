package org.intellij.plugins.ceylon.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper;
import org.jetbrains.annotations.NotNull;

public abstract class InjectableLiteral extends CeylonPsiImpl.PrimaryPsiImpl implements PsiLanguageInjectionHost {

    public InjectableLiteral(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new StringLiteralEscaper<PsiLanguageInjectionHost>(this);
    }
}
