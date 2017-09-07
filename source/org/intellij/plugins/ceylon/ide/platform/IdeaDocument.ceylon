import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.platform {
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
