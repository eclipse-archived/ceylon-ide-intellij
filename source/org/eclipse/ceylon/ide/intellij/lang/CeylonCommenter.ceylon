/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.lang {
    CodeDocumentationAwareCommenterEx
}
import com.intellij.psi {
    PsiComment,
    PsiElement
}

import org.eclipse.ceylon.ide.intellij.psi {
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
