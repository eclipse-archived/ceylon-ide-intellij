/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import org.eclipse.ceylon.ide.common.platform {
    CommonDocument
}

shared class IdeaDocument(nativeDocument)
        satisfies CommonDocument {

    shared Document nativeDocument;
    
    getLineOfOffset(Integer offset) 
            => nativeDocument.getLineNumber(offset);

    getLineContent(Integer line)
            => let (range = TextRange(getLineStartOffset(line), 
                                      getLineEndOffset(line)))
               nativeDocument.getText(range);

    getLineEndOffset(Integer line) 
            => nativeDocument.getLineEndOffset(line);

    getLineStartOffset(Integer line) 
            => nativeDocument.getLineStartOffset(line);

    getText(Integer offset, Integer length)
            => nativeDocument.getText(TextRange.from(offset, length));

    defaultLineDelimiter => "\n";

    size => nativeDocument.textLength;

    equals(Object that) 
            => if (is IdeaDocument that) 
            then nativeDocument==that.nativeDocument 
            else false;
}
