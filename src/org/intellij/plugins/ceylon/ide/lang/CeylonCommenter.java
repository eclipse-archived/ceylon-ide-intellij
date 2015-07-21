package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.lang.CodeDocumentationAwareCommenterEx;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.TokenTypes;
import org.jetbrains.annotations.Nullable;

public class CeylonCommenter implements CodeDocumentationAwareCommenterEx {
    @Override
    public boolean isDocumentationCommentText(PsiElement element) {
        return false;
    }

    @Nullable
    @Override
    public IElementType getLineCommentTokenType() {
        return TokenTypes.LINE_COMMENT.getTokenType();
    }

    @Nullable
    @Override
    public IElementType getBlockCommentTokenType() {
        return TokenTypes.MULTI_COMMENT.getTokenType();
    }

    @Nullable
    @Override
    public IElementType getDocumentationCommentTokenType() {
        return TokenTypes.MULTI_COMMENT.getTokenType();
    }

    @Nullable
    @Override
    public String getDocumentationCommentPrefix() {
        return "/**";
    }

    @Nullable
    @Override
    public String getDocumentationCommentLinePrefix() {
        return "*";
    }

    @Nullable
    @Override
    public String getDocumentationCommentSuffix() {
        return "*/";
    }

    @Override
    public boolean isDocumentationComment(PsiComment element) {
        return false;
    }

    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return "//";
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return "/*";
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return "*/";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
