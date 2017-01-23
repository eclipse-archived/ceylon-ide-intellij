import com.intellij.lang {
    CodeDocumentationAwareCommenterEx
}
import com.intellij.psi {
    PsiComment,
    PsiElement
}

import org.intellij.plugins.ceylon.ide.psi {
    TokenTypes
}

shared class CeylonCommenter() satisfies CodeDocumentationAwareCommenterEx {
    isDocumentationCommentText(PsiElement element) => false;
    lineCommentTokenType => TokenTypes.lineComment.tokenType;
    blockCommentTokenType => TokenTypes.multiComment.tokenType;
    documentationCommentTokenType => TokenTypes.multiComment.tokenType;
    documentationCommentPrefix => "/**";
    documentationCommentLinePrefix => "*";
    documentationCommentSuffix => "*/";
    isDocumentationComment(PsiComment element) => false;
    lineCommentPrefix => "//";
    blockCommentPrefix => "/*";
    blockCommentSuffix => "*/";
    commentedBlockCommentPrefix => null;
    commentedBlockCommentSuffix => null;
}
