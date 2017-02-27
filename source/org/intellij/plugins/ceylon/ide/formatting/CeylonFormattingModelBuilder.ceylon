import com.intellij.formatting {
    FormattingModel,
    FormattingModelBuilder,
    FormattingModelProvider,
    Indent
}
import com.intellij.lang {
    ASTNode
}
import com.intellij.psi {
    PsiElement,
    PsiFile
}
import com.intellij.psi.codeStyle {
    CodeStyleSettings
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonLanguage
}

shared class CeylonFormattingModelBuilder() satisfies FormattingModelBuilder {

    shared actual FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        value containingFile = element.containingFile.viewProvider.getPsi(CeylonLanguage.instance);
        value fileNode = containingFile.node;
        value spacings = Spacings(settings);
        value block = CeylonBlock(fileNode, Indent.absoluteNoneIndent, spacings);
        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, block, settings);
    }

    getRangeAffectingIndent(PsiFile file, Integer offset, ASTNode elementAtOffset)
            => null;
}
